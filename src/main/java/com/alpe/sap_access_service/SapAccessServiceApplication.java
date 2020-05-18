package com.alpe.sap_access_service;

import com.alpe.sap_access_service.util.Args;
import com.alpe.sap_access_service.util.PropertiesHolder;
import com.beust.jcommander.JCommander;
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
			System.exit(1);
		}
	}

	private static PropertiesHolder paramsProperties;

	static {
		try {
			paramsProperties = new PropertiesHolder("params.properties");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
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

	public static int getTokenLifetime() {
		return Integer.parseInt(paramsProperties.getProperty("TOKEN_LIFETIME"));
	}

	public static void main(String[] args) {
		Args myArgs = new Args();

		var commander = JCommander.newBuilder()
				.addObject(myArgs)
				.build();

		commander.parse(args);

		if (myArgs.isHelp()) {
			commander.usage();
			return;
		}

		if (myArgs.getNewSystem() != null) {
			String[] newSystem = myArgs.getNewSystem().split("=");
			if (newSystem.length != 2) {
				System.err.println("Invalid parameter to add new system, format -s <NAME>=<address>");
				return;
			}
			try {
				systemsProperties.setProperty(newSystem[0], newSystem[1]);
			} catch (IOException ex) {
				ex.printStackTrace();
				System.err.println("Error accessing systems.properties");
				return;
			}
		}

		if (myArgs.getSystemToBeRemoved() != null) {
			try {
				systemsProperties.removeProperty(myArgs.getSystemToBeRemoved());
			} catch (IOException ex) {
				ex.printStackTrace();
				System.err.println("Error accessing systems.properties");
				return;
			}
		}

		if (myArgs.getTokenLifetime() != null) {
			try {
				paramsProperties.setProperty("TOKEN_LIFETIME", myArgs.getTokenLifetime().toString());
			} catch (IOException ex) {
				ex.printStackTrace();
				System.err.println("Error accessing params.properties");
				return;
			}
		}

		String[] otherArgsArray = new String[myArgs.getParameters().size()];
		for (int i = 0; i < myArgs.getParameters().size(); i++) {
			otherArgsArray[i] = myArgs.getParameters().get(i);
		}

		SpringApplication.run(SapAccessServiceApplication.class, otherArgsArray);

	}

}
