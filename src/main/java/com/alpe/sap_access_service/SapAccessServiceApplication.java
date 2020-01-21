package com.alpe.sap_access_service;

import com.alpe.sap_access_service.properties.PropertiesHolder;
import com.alpe.sap_access_service.sessions.Session;
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
		}
	}

	private static PropertiesHolder paramsProperties;

	static {
		try {
			paramsProperties = new PropertiesHolder("params.properties");
		} catch (IOException e) {
			e.printStackTrace();
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
		try {
			return paramsProperties.getProperty("PRINT_SESSIONS_INFO").equals("TRUE");
		} catch (Exception ex) {
			return false;
		}
	}

	public static int getSessionLifetime() {
		return Integer.parseInt(paramsProperties.getProperty("SESSION_LIFETIME"));
	}

	public static void main(String[] args) {
		LinkedList<String> otherArgs = new LinkedList<>();

		boolean isTest = false;
		boolean isConfig = false;
		boolean addSystem = false;
		boolean removeSystem = false;
		float sessionLifetime;
		String systemName = null;
		String systemAddress = null;

		try {
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
				else if (args[i].equals("-set_session_lifetime")) {
					try {
						sessionLifetime = Integer.parseInt(args[i + 1]);
					} catch (NumberFormatException ex) {
						System.err.println("Error while trying to parse new session lifetime value");
						return;
					}

					try {
						paramsProperties.setProperty("SESSION_LIFETIME", Float.toString(sessionLifetime));
					} catch (IOException ex) {
						ex.printStackTrace();
						System.err.println("Error while trying to load or create params properties");
						return;
					}

				}
				else if (args[i].equals("-print_sessions_info")) {
					paramsProperties.setProperty("PRINT_SESSIONS_INFO", "TRUE");
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
			Session session = new Session("BL0",
					"K.GACHECHILA", "Welcome1!", 0);
			System.out.println(session.auth());
			return;
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

		else if (removeSystem && addSystem) {
			System.out.println("Incorrect parameters");
		}

	}

}
