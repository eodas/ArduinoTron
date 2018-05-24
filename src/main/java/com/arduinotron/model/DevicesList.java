package com.arduinotron.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class to load and return the list of devices
 */
public class DevicesList {

	private Map<String, Devices> devices;

	private final Logger logger = LoggerFactory.getLogger(DevicesList.class);

	public DevicesList() {

		this.devices = new HashMap<String, Devices>();
		this.devices.put("1001", new Devices("Arduino Tron IoT", "1001"));
		this.devices.put("1002", new Devices("Arduino NodeMCU", "1002"));
		this.devices.put("1003", new Devices("Arduino ESP8266", "1003"));
		this.devices.put("1004", new Devices("Arduino SensorTag", "1004"));
		this.devices.put("1005", new Devices("Arduino TI-BLE Tag", "1005"));
		this.devices.put("1006", new Devices("EOSpy TI-SensorTag", "1006"));
		this.devices.put("1007", new Devices("EOSpy TI-BLE Sensor", "1007"));
		this.devices.put("1008", new Devices("EOSpy AndroidSensor", "1008"));
	}

	public Collection<Devices> getDevices() {
		return Collections.unmodifiableCollection(devices.values());
	}

	public Devices getDevice(String device) {
		return this.devices.get(device);
	}
}
