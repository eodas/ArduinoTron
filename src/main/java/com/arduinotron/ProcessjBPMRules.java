package com.arduinotron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jbpm.test.JBPMHelper;
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
public class ProcessjBPMRules {

	private DevicesList devices;
	private KieSession kSession;
	private KieContainer kContainer;
	private RuntimeManager manager;
	private RuntimeEngine runtime;

	private boolean knowledgeDebug = false;
	private String kSessionType = "";
	private String kSessionName = "";
	private String processID = "";

	private final Logger logger = LoggerFactory.getLogger(ProcessjBPMRules.class);

	public ProcessjBPMRules(DevicesList devices, String kSessionType, String kSessionName, String processID,
			boolean knowledgeDebug) {
		super();
		this.devices = devices;
		this.kSessionType = kSessionType;
		this.kSessionName = kSessionName;
		this.processID = processID;
		this.knowledgeDebug = knowledgeDebug;
	}

	public KieSession createKieSession(String kSessionName) {
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

	private static KieSession getKieSession(String bpmn) throws Exception {
		RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newEmptyBuilder()
				.addAsset(KieServices.Factory.get().getResources().newClassPathResource(bpmn), ResourceType.BPMN2)
				.get();
		return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment).getRuntimeEngine(null)
				.getKieSession();
	}

	private static RuntimeManager getRuntimeManager(String process) {
		// load up the knowledge base
		JBPMHelper.startH2Server();
		JBPMHelper.setupDataSource();
		RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
				.addAsset(KieServices.Factory.get().getResources().newClassPathResource(process), ResourceType.BPMN2)
				.get();
		return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
	}
	
//
	
	public void main1(String[] args) {
		try {
			// load up the knowledge session
			kSession = getKieSession("com/looping/Looping.bpmn2");
			// start a new process instance
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("count", 5);
			kSession.startProcess("com.sample.looping", params);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void main2(String[] args) {
		try {
			manager = getRuntimeManager("com/multipleinstance/multipleinstance.bpmn");
			runtime = manager.getRuntimeEngine(null);
			kSession = runtime.getKieSession();

			// start a new process instance
			Map<String, Object> params = new HashMap<String, Object>();
			List<String> list = new ArrayList<String>();
			list.add("krisv");
			list.add("john doe");
			list.add("superman");
			params.put("list", list);
			kSession.startProcess("com.sample.multipleinstance", params);

			TaskService taskService = runtime.getTaskService();
			List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("sales-rep", "en-UK");
			for (TaskSummary task : tasks) {
				System.out.println("Sales-rep executing task " + task.getName() + "(" + task.getId() + ": "
						+ task.getDescription() + ")");
				taskService.start(task.getId(), "sales-rep");
				taskService.complete(task.getId(), "sales-rep", null);
			}

			manager.disposeRuntimeEngine(runtime);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		System.exit(0);
	}

//
	
	public String receive(ServerEvent serverEvent) {
	    String response = "";
		ProcessInstance instance = null;

		try {
			// load up the knowledge base
			switch (this.kSessionType) {
			case "createKieSession":
				this.kSession = createKieSession(this.kSessionName);
				break;
			case "getKieSession":
				this.kSession = getKieSession(this.kSessionName);
				break;
			case "getRuntimeManager":
				this.manager = getRuntimeManager(this.kSessionName);
				break;
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}

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

			// instance.getParentProcessInstanceId().getVar
			response = (String) kSession.getGlobal("results");
			kSession.dispose();

		} catch (Exception e) {
			System.err.println("=============================================================");
			System.err.println("Unexpected exception caught: " + e.getMessage());
			e.printStackTrace();
		}
		return (response);
	}

	public void log(String message) {
		MainWindow.getInstance().log(message);
	}
}
