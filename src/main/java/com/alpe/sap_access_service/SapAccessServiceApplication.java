package com.alpe.sap_access_service;

import com.alpe.sap_access_service.properties.PropertiesHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.*;

@SpringBootApplication
public class SapAccessServiceApplication {

	private static PropertiesHolder systemsProperties;

	static {
		try {
			systemsProperties = new PropertiesHolder("systems.properties");
		} catch (IOException e) {
			e.printStackTrace();
			systemsProperties = null;
		}
	}

	private static PropertiesHolder paramsProperties;

	static {
		try {
			paramsProperties = new PropertiesHolder("params.properties");
		} catch (IOException e) {
			e.printStackTrace();
			paramsProperties = null;
		}
	}

	public static String getSystemAddress(String systemName) {
			return systemsProperties.getProperty(systemName);
	}

	public static Set<String> getSystems() {
		HashSet<String> result = new HashSet<>();
		Set<Object> keySet = systemsProperties.getProperties().keySet();
		for (Object key : keySet) {
			result.add((String) key);
		}
		return result;
	}

	public static boolean isSessionsInfo() {
		return paramsProperties.getProperty("PRINT_SESSIONS_INFO").equals("TRUE");
	}

	public static int getTokenLifetime() {
		return Integer.parseInt(paramsProperties.getProperty("TOKEN_LIFETIME"));
	}

	public static void main(String[] args) {
		LinkedList<String> otherArgs = new LinkedList<>();

		boolean isTest = false;
		boolean isConfig = false;
		boolean addSystem = false;
		boolean removeSystem = false;
		int tokenLifetime;
		String systemName = null;
		String systemAddress = null;

		try {
			paramsProperties.setProperty("PRINT_SESSIONS_INFO", "FALSE");
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		try {
			// Parse args
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-test"))
					isTest = true;
				else if (args[i].equals("-config"))
					isConfig = true;
				else if (args[i].equals("-add"))
					addSystem = true;
				else if (args[i].equals("-name"))
					systemName = args[i + 1];
				else if (args[i].equals("-address"))
					systemAddress = args[i + 1];
				else if (args[i].equals("-remove"))
					removeSystem = true;
				else if (args[i].equals("-set_token_lifetime")) {
					try {
						tokenLifetime = Integer.parseInt(args[i + 1]);
					} catch (NumberFormatException ex) {
						System.err.println("Error while trying to parse new token lifetime value");
						return;
					}

					try {
						paramsProperties.setProperty("TOKEN_LIFETIME", Integer.toString(tokenLifetime));
					} catch (IOException ex) {
						ex.printStackTrace();
						System.err.println("Error accessing to params.properties");
						return;
					}

				}
				else if (args[i].equals("-print_sessions_info")) {
					paramsProperties.setProperty("PRINT_SESSIONS_INFO", "TRUE");

					// Sometimes main is restarted, so the code runs twice
					// This causes the problem with setting PRINT_SESSIONS_INFO param to false at the beginning
					// So this argument needs to be passed to SpringApplication.run(...) method
					otherArgs.add(args[i]);
				}
				else
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

		//TEST section
		if (isTest) {
			System.out.println(paramsProperties.getProperty("PRINT_SESSIONS_INFO"));
			System.out.println(isSessionsInfo());
			return;
		}

		// Run server in normal mode
		if (!isConfig) {
			if (paramsProperties == null || systemsProperties == null) {
				System.out.println("Error: missing params.properties or systems.properties\nTry configuring the program before starting the service");
				return;
			}
			else if (paramsProperties.getProperty("TOKEN_LIFETIME") == null) {
				try {
					System.out.println("No token lifetime parameter found\nSetting default token lifetime (600 secs)");
					paramsProperties.setProperty("TOKEN_LIFETIME", "600");
				} catch (IOException ex) {
					System.out.println("Error setting default token lifetime at params.properties");
					return;
				}
			}
			SpringApplication.run(SapAccessServiceApplication.class, otherArgsArray);
		}

		// Add new system & exit
		else if (addSystem && !removeSystem) {
			if (systemName == null || systemAddress == null)
				System.err.println("Incorrect parameters to add new system");
			else {
				try {
				systemsProperties.setProperty(systemName, systemAddress);
				} catch (IOException ex) {
					ex.printStackTrace();
					System.err.println("Error accessing to systems.properties");
				}
			}
		}

		// Remove system
		else if (removeSystem && !addSystem) {
			if (systemName == null)
				System.err.println("Incorrect parameters to remove new system");
			else {
				try {
					systemsProperties.removeProperty(systemName);
				} catch (IOException ex) {
					ex.printStackTrace();
					System.err.println("Error accessing systems.properties");
				}
			}
		}

		else if (removeSystem && addSystem) {
			System.out.println("Incorrect parameters");
		}

	}

}
