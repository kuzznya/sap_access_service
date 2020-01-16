package com.alpe.sap_access_service;

import com.alpe.sap_access_service.properties.PropertiesHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

@SpringBootApplication
public class SapAccessServiceApplication {

	public static void main(String[] args) {
		LinkedList<String> otherArgs = new LinkedList<>();

		boolean isConfig = false;
		boolean addSystem = false;
		boolean removeSystem = false;
		boolean setSessionLifetime = false;
		float sessionLifetime;
		String systemName = null;
		String systemAddress = null;

		PropertiesHolder systemsProperties;
		PropertiesHolder paramsProperties;
		try {
			systemsProperties = new PropertiesHolder("systems.properties");
			paramsProperties = new PropertiesHolder("params.properties");
		} catch (IOException ex) {
			ex.printStackTrace();
			System.err.println("Error while trying to load or create properties");
			return;
		}

		try {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-config"))
					isConfig = true;
				else if (args[i].equals("-add"))
					addSystem = true;
				else if (args[i].equals("-name"))
					systemName = args[i + 1];
				else if (args[i].equals("-address"))
					systemAddress = args[i + 1];
				else if (args[i].equals("-remove"))
					removeSystem = true;
				else if (args[i].equals("-set_session_lifetime")) {
					try {
						sessionLifetime = Float.parseFloat(args[i + 1]);
					} catch (NumberFormatException ex) {
						System.err.println("Error while trying to parse new session lifetime value");
						return;
					}

					try {
						paramsProperties.setProperty("SESSION_LIFETIME", Float.toString(sessionLifetime));
					} catch (IOException ex) {
						ex.printStackTrace();
						System.err.println("Error while trying to load or create params properties");
					}

				} else
					otherArgs.add(args[i]);
			}
		} catch (Exception ex) {
			System.err.println("Error while trying to parse the arguments");
			return;
		}

		String[] otherArgsArray = new String[otherArgs.size()];
		for (int i = 0; i < otherArgs.size(); i++) {
			otherArgsArray[i] = otherArgs.get(i);
		}

		if (!isConfig)
			SpringApplication.run(SapAccessServiceApplication.class, otherArgsArray);
		else if (addSystem && !removeSystem) {
			if (systemName == null || systemAddress == null)
				System.err.println("Incorrect parameters to add new system");
			else {
				try {
				systemsProperties.setProperty(systemName, systemAddress);
				} catch (IOException ex) {
					ex.printStackTrace();
					System.err.println("Error while trying to load or create systems properties");
				}
			}
		}



		else if (removeSystem && !addSystem) {
			if (systemName == null)
				System.err.println("Incorrect parameters to remove new system");
			else {
				try {
					systemsProperties.removeProperty(systemName);
				} catch (IOException ex) {
					ex.printStackTrace();
					System.err.println("Error while trying to load or create system properties");
				}
			}
		}

	}

}
