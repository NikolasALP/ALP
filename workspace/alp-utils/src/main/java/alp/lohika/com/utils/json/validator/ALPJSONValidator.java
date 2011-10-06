//Copyright 2011 Lohika .  This file is part of ALP.
//
//    ALP is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    ALP is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with ALP.  If not, see <http://www.gnu.org/licenses/>.

package alp.lohika.com.utils.json.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * 
 * @author "Anton Smorodsky" Validate JSON data against given JSON Schema
 */

public class ALPJSONValidator {

	private boolean				first_try;		// holds us from dig into JSON schema dipper then	
	private File[]				schemasList;	// Array of all JSON schemas 
	private LinkedList<String>	JSONStack;		// List of all JSON objects that stand before current object  
	private LinkedList<String>	ErrorsStack;	// Collect found errors at this List  
	private JsonFactory			JSONFactory;
	private ObjectMapper		JSONMapper;
	private String				schemaName;

	/**
	 * 
	 * @param JSONSchemasPath
	 *            - represent path to folder with all schemas
	 * @param schemaName
	 *            - schema from which validation should start
	 * @throws IOException
	 */
	public ALPJSONValidator(String JSONSchemasPath, String in_schemaName) throws IOException {
		schemaName = in_schemaName;
		JSONFactory = new JsonFactory();
		JSONStack = new LinkedList<String>();
		ErrorsStack = new LinkedList<String>();
		first_try = true;
		JSONMapper = new ObjectMapper();
		File dir = new File(JSONSchemasPath);
		FilenameFilter jsonFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".json");
			}
		};
		schemasList = dir.listFiles(jsonFilter);
	}

	// TODO - Need to think how better output found problems . This not expected way for sure
	public String ErrorsToString() {
		String buf = "";
		for (int i = 0; i < ErrorsStack.size(); i++) {
			buf += ErrorsStack.get(i);
			buf += ";";
		}
		return buf;
	}

	public void WriteToLog(Logger lg, Level lvl) {
		for (int i = 0; i < ErrorsStack.size(); i++)
			lg.log(lvl, ErrorsStack.get(i));
	}

	public LinkedList<String> getErrorsList() {
		return ErrorsStack;
	}

	/**
	 * 
	 * Main function used for validations
	 * 
	 * @param pathToJSON
	 *            - where to find file with json to validate
	 * @return true if schema valid and false if wrong
	 * @throws IOException
	 */
	public boolean ValidateJSON(String pathToJSON) throws IOException {

		JSONStack.clear();
		ErrorsStack.clear();
		first_try = true;
		Reader in = new InputStreamReader(new FileInputStream(pathToJSON), "UTF-8");
		JsonNode rootObj = JSONMapper.readTree(in);
		LinkedList<String> schemaStack = new LinkedList<String>();
		schemaStack.add(schemaName);
		return validateObject(rootObj, "JSON", schemaStack);
	}

	public boolean ValidateJSON(File JSONFile) throws IOException {

		JSONStack.clear();
		ErrorsStack.clear();
		first_try = true;
		Reader in = new InputStreamReader(new FileInputStream(JSONFile), "UTF-8");
		JsonNode rootObj = JSONMapper.readTree(in);
		LinkedList<String> schemaStack = new LinkedList<String>();
		schemaStack.add(schemaName);
		return validateObject(rootObj, "JSON", schemaStack);
	}

	/**
	 * 
	 * This function recursively goes over all elements in JSON and answer if
	 * each one is correct.
	 * 
	 * @param feedObj
	 *            - JsonNode object that currently under verification
	 * @param parentName
	 *            - name of parent of current element
	 * @param schemaStack
	 *            - Linked List that store list of currently read schemas
	 * @return
	 * @throws JsonParseException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private boolean validateObject(JsonNode feedObj, String parentName,
			LinkedList<String> schemaStack) throws JsonParseException, FileNotFoundException,
			IOException {
		Iterator<JsonNode> Inode = feedObj.getElements();
		Iterator<String> Istring = feedObj.getFieldNames();
		while (Inode.hasNext()) {
			String str = Istring.next();
			JsonNode nod = Inode.next();
			JSONStack.add(str);
			LinkedList<String> t_schemaStack = new LinkedList<String>();
			t_schemaStack.addAll(schemaStack);
			LinkedList<JSONTypes> TypesList = getItemTypeFromSchema(str, parentName,
					new LinkedList<String>(), t_schemaStack);
			if (TypesList == null) ErrorsStack.add("'" + GeneratePath() + "' not belongs to '"
					+ parentName + "'");
			else {
				JSONTypes realType = detectType(TypesList, nod);
				if (realType == JSONTypes.object) {
					LinkedList<String> t_SchemaStack2 = new LinkedList<String>();
					t_SchemaStack2.addAll(t_schemaStack);
					validateObject(nod, str, t_SchemaStack2);
				} else if (realType == JSONTypes.array) {
					LinkedList<String> t_SchemaStack2 = new LinkedList<String>();
					t_SchemaStack2.addAll(t_schemaStack);
					validateArray(nod, str, t_SchemaStack2);
				} else if (realType == JSONTypes.string && nod.getTextValue().isEmpty()) ErrorsStack
						.add("Item '" + GeneratePath() + "' is empty");
			}
			JSONStack.removeLast();
		}
		if (ErrorsStack.isEmpty()) return true;
		else
			return false;
	}

	private JSONTypes detectType(LinkedList<JSONTypes> JSONTypeList, JsonNode nod) {
		for (int i = 0; i < JSONTypeList.size(); i++) {
			JSONTypes type = JSONTypeList.get(i);
			switch (type) {
				case object:
					if (nod.isObject()) return type;
				break;
				case array:
					if (nod.isArray()) return type;
				break;
				case string:
					if (nod.isTextual()) return type;
				break;
				case integer:
					if (nod.isNumber()) return type;
				break;
				case any:
				case jboolean: // !!!! need to remove jboolean it is not supported in upstream
					return type;
				default:
					ErrorsStack.add("schema type - '" + type.toString()
							+ "' is not defined in schema");
					return null;
			}
		}
		addError(nod, JSONTypesToString(JSONTypeList));
		return null;
	}

	private String JSONTypesToString(LinkedList<JSONTypes> JSONTypeList) {
		String str = "";
		for (int i = 0; i < JSONTypeList.size(); i++)
			if (i == 0) str = "'" + JSONTypeList.get(0).toString() + "'";
			else
				str += "  or '" + JSONTypeList.get(i).toString() + "'";
		return str;
	}

	private void addError(JsonNode nod, String realType) {
		ErrorsStack.add("'" + GeneratePath() + "' should be " + realType + "  but nod is -'"
				+ nod.asToken().name() + "' in a fact.");
	}

	/**
	 * 
	 * @param elementName
	 *            - name of element to find in JSON Schema
	 * @param parentName
	 *            - name of parent of element to find in JSON Schema
	 * @param schemaObjects
	 *            - List of object that was verified before
	 * @param schemaStack
	 *            - List of schemas that was read before
	 * @return null if object not found in schema and List of types if found
	 * @throws JsonParseException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */

	private LinkedList<JSONTypes> getItemTypeFromSchema(String elementName, String parentName,
			LinkedList<String> schemaObjects, LinkedList<String> schemaStack)
			throws JsonParseException, FileNotFoundException, IOException {
		try {
			JsonParser schemaParser = getParser(schemaStack.getLast());
			String tmp, tmp2;
			LinkedList<JSONTypes> to_ret = new LinkedList<JSONTypes>();
			JsonToken tok;
			while (schemaParser.nextToken() != null) {
				tok = schemaParser.getCurrentToken();
				tmp = schemaParser.getCurrentName();
				switch (tok) {
					case START_OBJECT:
						if (tmp != null) schemaObjects.add(tmp);
					break;
					case END_OBJECT:
						if (tmp != null) {
							tmp2 = schemaObjects.removeLast();
							if (!tmp2.equals(tmp)) throw new IllegalStateException(
									"Wrong structure in schema '" + schemaStack.getLast()
											+ "'. Removing object='" + tmp2 + "'. While have '"
											+ tmp + "' at the end.");
						}
					break;
					case FIELD_NAME:
						tmp = schemaParser.getCurrentName();
						if (tmp != null && tmp.equals(elementName)) {
							boolean b1 = false;
							if (schemaObjects.isEmpty()) {
								tmp = "JSON";
								b1 = true;
							} else if (schemaObjects.size() != 1) {
								tmp = schemaObjects.getLast();
								if (tmp.equals("properties") || tmp.equals("items")) {
									tmp = schemaObjects.get(schemaObjects.size() - 2);
									if (tmp.equals("properties") || tmp.equals("items")) tmp = schemaObjects
											.get(schemaObjects.size() - 3);
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
									ErrorsStack.add("Using START_OBJECT skip!!! for token :'"
											+ schemaParser.getText() + "'");
									continue;
								}
								tmp = "/";
								for (int i = 0; i < schemaObjects.size(); i++)
									tmp += schemaObjects.get(i) + "/";
								tmp += elementName;
								throw new IllegalStateException(
										"Unexpected content after 'type' record under '" + tmp
												+ "' in schema");
							}
						} else if (tmp.equals("$ref") && first_try) {
							schemaParser.nextToken();
							tmp = schemaParser.getText();
							schemaStack.add(tmp);
							first_try = false;
							LinkedList<JSONTypes> recursive_resp = getItemTypeFromSchema(
									elementName, parentName, schemaObjects, schemaStack);
							first_try = true;
							if (recursive_resp != null) return recursive_resp;
						}
					break;
				}

			}
			schemaParser.close();
			if (schemaStack.isEmpty()) return null;
			schemaStack.removeLast();
		} catch (JsonParseException jp) {
			ErrorsStack.add(jp.getMessage());
			throw jp;
		}
		return null;
	}

	private String GeneratePath() {
		String out = "/";
		for (int i = 0; i < JSONStack.size(); i++)
			out += JSONStack.get(i) + "/";
		return out;
	}

	private void validateArray(JsonNode feedArray, String parentName, LinkedList<String> schemaStack)
			throws JsonParseException, FileNotFoundException, IOException {
		Iterator<JsonNode> Inode = feedArray.getElements();
		while (Inode.hasNext()) {
			LinkedList<String> t_schemaStack = new LinkedList<String>();
			t_schemaStack.addAll(schemaStack);
			validateObject(Inode.next(), parentName, t_schemaStack);
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
		for (int i = 0; i < schemasList.length; i++)
			if (schemasList[i].getName().equals(parserName)) return JSONFactory
					.createJsonParser(schemasList[i]);
		throw new IllegalArgumentException("JSON Schema with name '" + parserName
				+ "' does not exists");
	}
}
