package alp.lohika.com.utils.json.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * 
 * @author "Anton Smorodsky"
 * 
 */

public class ALPJSONValidator {

	private boolean				Json_jump;
	private boolean				callPass;
	private LinkedList<String>	streamName;
	private LinkedList<byte[]>	fileBArray;
	private LinkedList<String>	JsonSequence;
	private LinkedList<String>	ObjectsList;
	private LinkedList<String>	ParentsList;
	private LinkedList<String>	error;
	private JsonFactory			JSONFactory;
	private ObjectMapper		mapper;

	/**
	 * 
	 * @param JSONroot
	 *            - represent path to folder with all *.json
	 * @throws IOException
	 */
	public ALPJSONValidator(String JSONroot) throws IOException {
		JSONFactory = new JsonFactory();
		JsonSequence = new LinkedList<String>();
		ObjectsList = new LinkedList<String>();
		ParentsList = new LinkedList<String>();
		streamName = new LinkedList<String>();
		error = new LinkedList<String>();
		fileBArray = new LinkedList<byte[]>();
		Json_jump = false;
		mapper = new ObjectMapper();
		File dir = new File(JSONroot);
		File[] flist = dir.listFiles();
		for (int i = 0; i < flist.length; i++)
			if (flist[i].getName().endsWith("json")) {
				fileBArray.add(new byte[(int) flist[i].length()]);
				FileInputStream fis = new FileInputStream(flist[i]);
				fis.read(fileBArray.getLast());
				fis.close();
				streamName.add(flist[i].getName()); // name of json schema without '.json'				
			}
	}

	public String getError() {
		String buf = "";
		for (int i = 0; i < error.size(); i++)
			buf += error.get(i);
		return buf;
	}

	public boolean ValidateJSON(String json_path, String schema_path) throws IOException {
		try {
			Reader in = new InputStreamReader(new FileInputStream(json_path), "UTF-8");
			JsonNode respObj = mapper.readTree(in);
			LinkedList<String> schemaDirection = new LinkedList<String>();
			schemaDirection.add(schema_path);
			boolean is_valid = validateObject(respObj, "JSON", schemaDirection);
			for (int i = 0; i < ObjectsList.size(); i++)
				error.add("'" + ObjectsList.get(i) + "' not belongs to '" + ParentsList.get(i)
						+ "'");
			ObjectsList.clear();
			ParentsList.clear();
			JsonSequence.clear();
			return is_valid;
		} catch (IllegalStateException e) {
			return false;
		}

	}

	private boolean SearchInErrors(String objName, String parentName) {
		for (int i = 0; i < ObjectsList.size(); i++)
			if (ObjectsList.get(i).equals(objName) && ParentsList.get(i).equals(parentName)) return true;
		return false;
	}

	private boolean validateObject(JsonNode feedObj, String parentName,
			LinkedList<String> schemaDirection) throws JsonParseException, FileNotFoundException,
			IOException {
		Iterator<JsonNode> Inode = feedObj.getElements();
		Iterator<String> Istring = feedObj.getFieldNames();
		boolean NotEmptyString = true;
		while (Inode.hasNext()) {
			String str = Istring.next();
			JsonSequence.add(str);
			JsonNode nod = Inode.next();
			LinkedList<JSONTypes> test;
			LinkedList<String> inst1 = new LinkedList<String>();
			LinkedList<String> l_tmp = new LinkedList<String>();
			l_tmp.addAll(schemaDirection);
			if (SearchInErrors(GeneratePath(), parentName)) {
				JsonSequence.removeLast();
				continue;
			} else {
				Json_jump = false;
				test = recursiveValidateAgainstSchemaWhile(str, parentName, inst1, l_tmp);
			}
			if (test != null) {
				callPass = false;
				String realType = "";
				int recursive_choice = -1;
				for (int l = 0; l < test.size() && (!callPass); l++) {
					JSONTypes type1 = test.get(l);
					if (realType.isEmpty()) realType = "'" + type1.toString() + "'";
					else
						realType += "  or '" + type1.toString() + "'";
					switch (type1) {
						case object:
							callPass = nod.isObject();
							recursive_choice = 1;
						break;
						case array:
							callPass = nod.isArray();
							recursive_choice = 2;
						break;
						case string:
							if (nod.isTextual()) {
								if (nod.getTextValue().isEmpty()) {
									error.add("Item '" + GeneratePath() + "' is empty");
									NotEmptyString = false;
								} else
									callPass = true;
							}
						break;
						case integer:
							callPass = nod.isNumber();
						break;
						case any:
							callPass = true;
						break;
						default:
							error.add("schema type - '" + type1.toString()
									+ "' is not defined in schema");
					}
				}

				if (callPass == true) {
					if (recursive_choice == 1) {
						recursive_choice = -1;
						LinkedList<String> l_tmp2 = new LinkedList<String>();
						l_tmp2.addAll(l_tmp);
						validateObject(nod, str, l_tmp2);
					}
					if (recursive_choice == 2) {
						recursive_choice = -1;
						LinkedList<String> l_tmp2 = new LinkedList<String>();
						l_tmp2.addAll(l_tmp);
						validateArray(nod, str, l_tmp2);
					}
				} else if (NotEmptyString == true) {
					JsonToken tok = nod.asToken();
					error.add("'" + GeneratePath() + "' should be " + realType + "  but nod is -'"
							+ tok.name() + "' in a fact.");
				}
				NotEmptyString = true;
			} else {
				error.add("'" + GeneratePath() + "is not defined in schema");
				return false;
			}
			JsonSequence.removeLast();
		}
		if (error.isEmpty()) return true;
		else
			return false;
	}

	private LinkedList<JSONTypes> recursiveValidateAgainstSchemaWhile(String objName,
			String parentName, LinkedList<String> objects, LinkedList<String> schemaPath)
			throws JsonParseException, FileNotFoundException, IOException {
		try {
			JsonParser schemaParser = getParser(schemaPath.getLast());
			String tmp, tmp2;
			LinkedList<JSONTypes> to_ret = new LinkedList<JSONTypes>();
			JsonToken tok;
			while (schemaParser.nextToken() != null) {
				tok = schemaParser.getCurrentToken();
				tmp = schemaParser.getCurrentName();
				switch (tok) {
					case START_OBJECT:
						if (tmp != null) objects.add(tmp);
					break;
					case END_OBJECT:
						if (tmp != null) {
							tmp2 = objects.removeLast();
							if (!tmp2.equals(tmp)) throw new IllegalStateException(
									"Wrong structure in schema '" + schemaPath.getLast()
											+ "'. Removing object='" + tmp2 + "'. While have '"
											+ tmp + "' at the end.");
						}
					break;
					case FIELD_NAME:
						tmp = schemaParser.getCurrentName();
						if (tmp != null && tmp.equals(objName)) {
							boolean b1 = false;
							if (objects.isEmpty()) {
								tmp = "JSON";
								b1 = true;
							} else if (objects.size() != 1) {
								tmp = objects.getLast();
								if (tmp.equals("properties") || tmp.equals("items")) {
									tmp = objects.get(objects.size() - 2);
									if (tmp.equals("properties") || tmp.equals("items")) tmp = objects
											.get(objects.size() - 3);
									b1 = true;
								}
							}
							if (parentName.equals(tmp) && b1) {
								schemaParser.nextToken();
								schemaParser.nextToken();
								schemaParser.nextToken();
								tok = schemaParser.getCurrentToken();
								if (tok == JsonToken.VALUE_STRING) {
									to_ret.add(JSONTypes.valueOf(schemaParser.getText()));
									return to_ret;
								} else if (tok == JsonToken.START_ARRAY) {
									while (schemaParser.nextToken() != null) {
										tok = schemaParser.getCurrentToken();
										if (tok == JsonToken.VALUE_STRING) to_ret.add(JSONTypes
												.valueOf(schemaParser.getText()));
										else if (tok == JsonToken.END_ARRAY) return to_ret;
									}
								} else if (tok == JsonToken.START_OBJECT) {
									error.add("Using START_OBJECT skip!!! for token :'"
											+ schemaParser.getText() + "'");
									continue;
								}
								tmp = "/";
								for (int i = 0; i < objects.size(); i++)
									tmp += objects.get(i) + "/";
								tmp += objName;
								throw new IllegalStateException(
										"Unexpected content after 'type' record under '" + tmp
												+ "' in schema");
							}
						} else if (tmp.equals("$ref") && !Json_jump) {
							schemaParser.nextToken();
							tmp = schemaParser.getText();
							schemaPath.add(tmp);
							Json_jump = true;
							LinkedList<JSONTypes> recursive_resp = recursiveValidateAgainstSchemaWhile(
									objName, parentName, objects, schemaPath);
							Json_jump = false;
							if (recursive_resp != null) return recursive_resp;
						}
					break;
				}

			}
			schemaParser.close();
			if (schemaPath.isEmpty()) return null;
			schemaPath.removeLast();
		} catch (JsonParseException jp) {
			error.add(jp.getMessage());
			throw jp;
		} catch (FileNotFoundException fe) {
			error.add(fe.getMessage());
			throw fe;
		} catch (IllegalArgumentException ee) {
			error.add(ee.getMessage());
			throw ee;
		}
		return null;
	}

	private String GeneratePath() {
		String out = "/";
		for (int i = 0; i < JsonSequence.size(); i++)
			out += JsonSequence.get(i) + "/";
		return out;
	}

	private void validateArray(JsonNode feedArray, String parentName,
			LinkedList<String> schemaDirection) throws JsonParseException, FileNotFoundException,
			IOException {
		Iterator<JsonNode> Inode = feedArray.getElements();
		while (Inode.hasNext()) {
			LinkedList<String> tmp = new LinkedList<String>();
			tmp.addAll(schemaDirection);
			validateObject(Inode.next(), parentName, tmp);
		}
	}

	/**
	 * 
	 * @param parserName
	 *            - name of certain json schema without '.json'
	 * @return - object JsonParser
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public JsonParser getParser(String parserName) throws JsonParseException, IOException {
		for (int i = 0; i < streamName.size(); i++)
			if (streamName.get(i).equals(parserName)) return JSONFactory
					.createJsonParser(fileBArray.get(i));
		throw new IllegalArgumentException("JSON Schema with name '" + parserName
				+ "' does not exists");
	}
}
