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

package com.lohika.alp.mailer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.lohika.alp.utils.validator.EmailAddressException;
import com.lohika.alp.utils.validator.EmailValidator;

/**
 * Java class for configuring mailer. Recipients can be described in the 
 * .properties file or in the testng xml file. Default configuration file name is 
 * 'environment.properties'
 * @author Dmitry Irzhov
 *
 */
public class MailerConfigurator {
	
	private Logger logger = Logger.getLogger(MailerConfigurator.class);
	
	private static MailerConfigurator instance = null;

	private String configFilePath = "environment.properties";

	private Boolean autoMail = false;
	private String smtpHost;
	private Integer smtpPort = 25;
	private Boolean smtpSsl = false;
	private String sender;
	private String smtpUser;
	private String smtpPassword;
	private List<String> recipients;
	private List<String> suiteRecipients;
	private String template;
	
	
	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getConfigFilePath() {
		return configFilePath;
	}

	public Boolean getAutoMail() {
		return autoMail;
	}

	public void setAutoMail(Boolean autoMail) {
		this.autoMail = autoMail;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public Integer getSmtpPort() {
		return smtpPort;
	}

	public Boolean getSmtpSsl() {
		return smtpSsl;
	}

	public void setSmtpSsl(Boolean smtpSsl) {
		this.smtpSsl = smtpSsl;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public void setSmtpPort(Integer smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getSmtpUser() {
		return smtpUser;
	}

	public void setSmtpUser(String smtpUser) {
		this.smtpUser = smtpUser;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	public List<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<String> list) {
		this.recipients = list;
	}

	public List<String> getSuiteRecipients() {
		return suiteRecipients;
	}

	public void setSuiteRecipients(List<String> suiteRecipients) {
		this.suiteRecipients = suiteRecipients;
	}

	public MailerConfigurator () {
		configure(configFilePath);
	}
	
	public MailerConfigurator (String filePath) {
		configure(filePath);

	}
	
	public static MailerConfigurator getInstance() {
		if (instance==null)
			instance = new MailerConfigurator();
		return instance;
	}
	
	public static MailerConfigurator getInstance(String filePath) {
		if (instance==null)
			instance = new MailerConfigurator(filePath);
		return instance;
	}

	private void configure(String configFilePath) {
		Properties properties = new Properties();
        try {
			properties.load(new FileInputStream(configFilePath));
			autoMail = Boolean.parseBoolean(properties.getProperty("mail.autoMail"));
			if (!autoMail) return;
			smtpHost = properties.getProperty("mail.smtp.host");
			if (properties.getProperty("mail.smtp.port") != null)
				smtpPort = Integer.parseInt(properties.getProperty("mail.smtp.port"));
			smtpSsl = Boolean.parseBoolean(properties.getProperty("mail.smtp.ssl"));
			sender = properties.getProperty("mail.smtp.sender");
			smtpUser = properties.getProperty("mail.smtp.username");
			smtpPassword = properties.getProperty("mail.smtp.password");
			setRecipients(readRecipients(properties.getProperty("mail.recipients")));
			template = properties.getProperty("mail.template");
			
			if (smtpHost==null) {
	    		setAutoMail(false);
	    		logger.warn(new MailerException("SMTP host is not defined"));
			}
			if (sender!=null) {
				if (!validateSender(sender)) {
		    		setAutoMail(false);
		    		logger.warn(new EmailAddressException(Arrays.asList(sender)));
				}
			} else {
	    		setAutoMail(false);
	    		logger.warn(new MailerException("mail's sender should be defined. Automail is turn off."));
			}
		} catch (IOException e)
        {
            logger.error(new MailerException("unable to open file '"+configFilePath+"'"), e.getCause());
        } catch (NumberFormatException e) {
            logger.warn(new MailerException("unable to read smtp port number. Will use the default port (25)"), e.getCause());
        }
	}
	
	public ArrayList<String> readRecipients(String recipientsStr) {
        if (recipientsStr==null)
        	return null;
		try {
        	String[] r = recipientsStr.split(",");
        	EmailValidator emailValidator = new EmailValidator();
	        ArrayList<String> failedRecipients = new ArrayList<String>();
	        ArrayList<String> recipients = new ArrayList<String>();
	        for (String recipient : r) {
	        	if (!emailValidator.validate(recipient))
	        		failedRecipients.add(recipient);
	        	else
	        		recipients.add(recipient);
	        }
	        if (failedRecipients.size()>0)
        		throw new EmailAddressException(failedRecipients);
	        if (recipients.size()>0)
	        	return recipients;
	        else
	        	return null;
	    } catch (EmailAddressException e) {
	    	logger.warn(e.getMessage());
	    	return null;
		}
	}
	
	private boolean validateSender(String sender) {
    	EmailValidator emailValidator = new EmailValidator(); 
    	return emailValidator.validate(sender);
	}
}
