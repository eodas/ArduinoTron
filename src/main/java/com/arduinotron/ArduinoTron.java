package com.arduinotron;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arduinotron.model.AgentsList;
import com.arduinotron.model.DevicesList;
import com.arduinotron.server.IoTServer;
import com.arduinotron.server.AgentConnect;
import com.arduinotron.ui.MainWindow;

/**
 * Executive Order Corporation we make Things Smart
 *
 * Arduino Tron AI-IoT :: Internet of Things Drools-jBPM Expert System using Arduino Tron AI-IoT Processing
 * Arduino Tron Drools-jBPM :: Executive Order Sensor Processor System - Arduino Tron MQTT AI-IoT Client using AI-IoT Drools-jBPM
 * Executive Order Corporation - Arduino Tron - Arduino ESP8266 MQTT Telemetry Transport Machine-to-Machine(M2M)/Internet of Things(IoT)
 *
 * Executive Order Corporation
 * Copyright � 1978, 2018: Executive Order Corporation, All Rights Reserved
 */

/**
 * This is the main class for Arduino Tron AI-IoT Drools-jBPM Expert System
 */
public class ArduinoTron {

	AgentsList agentsList;
	ArduinoTron arduinoTron;
	private static IoTServer iotServer = null;

	private String base_path = "";
	private String appVer = "1.01A";
	private String buildDate = "0304";
	private boolean is64bitJMV = false;
	private static boolean iotrunning = false;

	private int port = 5055;
	private String knowledgeDebug = "none";
	private String kSessionType = ""; // createKieSession
	private String kSessionName = ""; // ksession-movement
	private String processID = ""; // com.TrainMovement

	private final Logger logger = LoggerFactory.getLogger(ArduinoTron.class);

	public ArduinoTron(String[] args) {

		this.arduinoTron = this;
		agentsList = new AgentsList();
		System.out.println("Arduino Tron AI-IoT :: Internet of Things Drools-jBPM Expert System"
				+ " using Arduino Tron AI-IoT Processing -version: " + appVer + " (" + buildDate + ")");

		getIPAddress();
		readProperties();

		if (knowledgeDebug.indexOf("none") == -1) {
			System.out.println("os.name: " + System.getProperty("os.name"));
			System.out.println("os.arch: " + System.getProperty("os.arch"));
			is64bitJMV = (System.getProperty("os.arch").indexOf("64") != -1);
			String result = (is64bitJMV == true) ? "64bit" : "32bit";

			System.out.println("java.home: " + System.getProperty("java.home"));
			System.out.println("java.vendor: " + System.getProperty("java.vendor"));
			System.out.println("java.version: " + System.getProperty("java.version") + " " + result);
			long maxHeapBytes = Runtime.getRuntime().maxMemory();
			System.out.println("Max heap memory: " + maxHeapBytes / 1024L / 1024L + "M");
			System.out.println("java.io.tmpdir: " + System.getProperty("java.io.tmpdir"));
			System.out.println("user.home: " + System.getProperty("user.home"));

			base_path = (System.getProperty("user.home") + File.separator);

			System.out.println("base_path: " + base_path);
			System.out.println("File.separator: " + File.separator);
			System.out.println("Local language: " + Locale.getDefault().getLanguage());
		}
	}

	public void init(final boolean exitOnClose) {
		// set up and show main window
		Locale.setDefault(Locale.US);
		final DevicesList devices = new DevicesList();

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MainWindow window = new MainWindow(devices.getDevices(), exitOnClose);
					window.show(); // .setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		AgentConnect agentConnect = new AgentConnect(agentsList, knowledgeDebug);

		if (kSessionType == "") {
			kSessionType = "createKieSession";
		}
		if (kSessionName == "") {
			System.err.println("Error: Must set a kSessionName == defined in arduinotron.properties file.");
			return;
		}
		if (processID == "") {
			System.err.println("Error: Must set a processID == defined in arduinotron.properties file.");
			return;
		}
		
		ProcessjBPMRules processjBPMRules = new ProcessjBPMRules(devices, kSessionType, kSessionName, processID,
				knowledgeDebug);
		startIoTServer(processjBPMRules);
	}

	public void readProperties() {
		try {
			File file = new File("arduinotron.properties");
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();

			Enumeration<?> enuKeys = properties.keys();
			while (enuKeys.hasMoreElements()) {
				String key = (String) enuKeys.nextElement();
				String value = properties.getProperty(key);

				if (key.indexOf("port") != -1) {
					String portStr = value;
					port = Integer.parseInt(portStr);
				}

				if (key.indexOf("serverEvent") != -1) {
					// serverEvent = value;
				}
				if (key.indexOf("knowledgeDebug") != -1) {
					knowledgeDebug = value;
				}
				if (key.indexOf("kSessionType") != -1) {
					kSessionType = value;
				}
				if (key.indexOf("kSessionName") != -1) {
					kSessionName = value;
				}
				if (key.indexOf("processID") != -1) {
					processID = value;
				}
				if (key.indexOf("arduinoAgent") != -1) {
					agentsList.parseAgents(value);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startIoTServer(ProcessjBPMRules processjBPMRules) {
		iotServer = new IoTServer(processjBPMRules, port);
		iotServer.start();
		iotrunning = true;
	}

	public static void stopIoTServer() {
		iotServer.killServer();
		iotrunning = false;
	}

	public void getIPAddress() {
		// Returns the instance of InetAddress containing local host name and address
		InetAddress localhost = null;
		try {
			localhost = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		System.out.print("System IP: " + (localhost.getHostAddress()).trim());

		// Find public IP address
		String systemipaddress = "";
		try {
			URL url_name = new URL("http://bot.whatismyipaddress.com");
			BufferedReader sc = new BufferedReader(new InputStreamReader(url_name.openStream()));

			// reads system IPAddress
			systemipaddress = sc.readLine().trim();
		} catch (Exception e) {
			systemipaddress = "Cannot Execute Properly";
		}
		System.out.println("  Public IP: " + systemipaddress);
	}

	public static void main(String[] args) {
		System.out.println("Arduino Tron :: Executive Order IoT Sensor Processor System"
				+ " - Arduino Tron MQTT AI-IoT Client using AI-IoT Drools-jBPM");

		new ArduinoTron(args).init(true);
	}
}
