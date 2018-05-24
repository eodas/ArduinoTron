package com.arduinotron;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.process.instance.event.listeners.RuleAwareProcessEventLister;
import org.jbpm.process.instance.event.listeners.TriggerRulesEventListener;

import com.arduinotron.model.Devices;
import com.arduinotron.model.DevicesList;
import com.arduinotron.model.ServerEvent;
import com.arduinotron.ui.MainWindow;
import com.arduinotron.util.AgendaListener;
import com.arduinotron.util.SystemOutProcessEventListener;
import com.arduinotron.util.WorkingMemoryListener;

/**
 * The Arduino Tron AI-IoT Drools-jBPM application
 */
public class RulesProcess {

	private DevicesList devices;
	private KieSession kSession;
	private KieContainer kContainer;

	private boolean knowledgeDebug = false;
	private String kSessionName = "";
	private String processID = "";

	private final Logger logger = LoggerFactory.getLogger(RulesProcess.class);

	public RulesProcess(DevicesList devices, String kSessionName, String processID, boolean knowledgeDebug) {
		super();
		this.devices = devices;
		this.kSessionName = kSessionName;
		this.processID = processID;
		this.knowledgeDebug = knowledgeDebug;
	}

	public KieSession createSession(String kSessionName) {
		if (kContainer == null) {
			// load up the knowledge base
			KieServices ks = KieServices.Factory.get();
			kContainer = ks.getKieClasspathContainer();
		}
		if (kContainer == null) {
		}

		// kContainer.getKieBase("rules");
		kSession = kContainer.newKieSession(kSessionName);
		if (kSession == null) {
			System.err.println("ERROR: Cannot find <ksession name=" + kSessionName + "> match in kmodule.xml file.");
			return null;
		}

		if (knowledgeDebug) {
			AgendaListener agendaListener = new AgendaListener();
			WorkingMemoryListener memoryListener = new WorkingMemoryListener();
			kSession.addEventListener(agendaListener);
			kSession.addEventListener(memoryListener);
			// ksession.setGlobal("helper", helper);
			// ksession.setGlobal("logger", logger);
			// kSession.setGlobal("busCalendar", busCalendar);
		}
		return kSession;
	}

	public void receive(ServerEvent serverEvent) {
		ProcessInstance instance = null;
		// load up the knowledge base
		this.kSession = createSession(this.kSessionName);

		if (knowledgeDebug) {
			// KieSession ksession = this.createDefaultSession();
			kSession.addEventListener(new SystemOutProcessEventListener());
			kSession.addEventListener(new RuleAwareProcessEventLister());
			kSession.addEventListener(new TriggerRulesEventListener(kSession));
		}

		Devices device = this.devices.getDevice(serverEvent.getId());
		if (device == null) {
			System.out.println("> id " + serverEvent.search("id") + " : Unknown IoT Device");
		} else {
			if (serverEvent.search("name").equals("")) {
				serverEvent.add("name", device.getName());
			}
			if (serverEvent.search("event").equals("")) {
				if (serverEvent.search("keypress").equals("")) {
					serverEvent.add("event", "none");
				} else {
					serverEvent.add("event", "keypress" + serverEvent.search("keypress"));
				}
			}
		}

		if (knowledgeDebug) {
			System.out.println("> TRACE " + serverEvent.getDeviceTime() + " id " + device.getId() + "-"
					+ serverEvent.getName() + " event " + serverEvent.getEvent());
		}
		for (Devices devices : this.devices.getDevices()) {
			kSession.insert(devices);
		}
		kSession.insert(serverEvent);

		try {
			// go! - fire rules
			long noOfRulesFired = this.kSession.fireAllRules();
			if (knowledgeDebug) {
				System.out.println("> TRACE kSession no of Rules Fired: " + noOfRulesFired);
				System.out.println("> TRACE Number of facts in the session: " + kSession.getFactCount());
			}
			if (device != null) {
				MainWindow.getInstance().updateDevice(device.getId());
			}
			MainWindow.getInstance().updateEvent(serverEvent);

			Map<String, Object> params = new HashMap<String, Object>();
			for (String key : serverEvent.map.keySet()) {
				params.put(key, serverEvent.map.get(key));
			}
			params.put("ilight", serverEvent.getLight());

			// go! - start jBPM processID
			if (processID != null && !processID.isEmpty()) {
				// Start the process with knowledge session
				instance = kSession.startProcess(processID, params);
			}
			if (instance.getState() != 2) {
				System.out.println(">>" + instance.getState());
			}
			kSession.dispose();

		} catch (Exception e) {
			System.err.println("=============================================================");
			System.err.println("Unexpected exception caught: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void log(String message) {
		MainWindow.getInstance().log(message);
	}
}