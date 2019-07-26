package com.arduinotron;

import java.util.HashMap;
import java.util.Map;

/**
 * An event informing of a state change due to some operation
 */
public class OsmandProtocol {
	public Map<String, String> map = new HashMap<>();

	public String id;
	public String name;
	public String event;
	public String alarm;
	public String textMessage;

	public String deviceTime;
	public boolean valid;
	public double lat;
	public double lon;
	public double hdop;
	public String cell;
	public String wifi;
	public double altitude; // value in meters
	public double speed; // value in knots
	public double course;
	public String address;
	public double accuracy;
	public double bearing;
	public double batteryLevel;

	public OsmandProtocol() {
	}

	public void add(String key, String value) {
		map.put(key, value);
		EventParser(key, value);
	}

	public int mapSize() {
		return map.size();
	}

	// Clear all values.
	public void mapClear() {
		map.clear();
	}

	public String search(String searchKey) {
		if (map.containsKey(searchKey)) {
			return map.get(searchKey);
		} else {
			return "";
		}
	}

	// Iterate over objects, using the keySet method.
	public void iterate() {
		for (String key : map.keySet())
			System.out.println(key + " - " + map.get(key));
		System.out.println();
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceTime() {
		return deviceTime;
	}

	public void setDeviceTime(String deviceTime) {
		this.deviceTime = deviceTime;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getCell() {
		return cell;
	}

	public void setCell(String cell) {
		this.cell = cell;
	}

	public String getWifi() {
		return wifi;
	}

	public void setWifi(String wifi) {
		this.wifi = wifi;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getCourse() {
		return course;
	}

	public void setCourse(double course) {
		this.course = course;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public double getBearing() {
		return bearing;
	}

	public void setBearing(double bearing) {
		this.bearing = bearing;
	}

	public double getHdop() {
		return hdop;
	}

	public void setHdop(double hdop) {
		this.hdop = hdop;
	}

	public double getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(double batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public void EventParser(String key, String value) {
		try {
			switch (key) {
			case "id":
			case "deviceid":
				setId(value);
				break;
			case "valid":
				setValid(Boolean.parseBoolean(value));
				break;
			case "timestamp":
				setDeviceTime(parseDate(value));
				break;
			case "lat":
				setLat(Double.parseDouble(value));
				break;
			case "lon":
				setLon(Double.parseDouble(value));
				break;
			case "location":
				String[] location = value.split(",");
				setLat(Double.parseDouble(location[0]));
				setLon(Double.parseDouble(location[1]));
				break;
			case "cell":
				setCell(value);
				break;
			case "wifi":
				setWifi(value);
				break;
			case "speed":
				setSpeed(Double.parseDouble(value));
				break;
			case "bearing":
			case "heading":
				setBearing(Double.parseDouble(value));
				break;
			case "altitude":
				setAltitude(Double.parseDouble(value));
				break;
			case "accuracy":
				setAccuracy(Double.parseDouble(value));
				break;
			case "hdop":
				setHdop(Double.parseDouble(value));
				break;
			case "batteryLevel":
			case "batt":
				setBatteryLevel(Double.parseDouble(value));
				break;
			default:
				System.out.println("> Extended Event Token " + key + "=" + value);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private String parseDate(String date) {
		String sdate = "";
		try {
			long ldate = Long.parseLong(date);
			sdate = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(ldate * 1000));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return sdate;
	}

	@Override
	public String toString() {
		String s = name + " - " + event;
		if (alarm != null && !alarm.isEmpty()) {
			return s = s + " - " + alarm;
		}
		if (textMessage != null && !textMessage.isEmpty()) {
			return s = s + " - " + textMessage;
		}
		return s;
	}
}
