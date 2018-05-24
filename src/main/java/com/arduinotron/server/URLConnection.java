package com.arduinotron.server;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;

public class URLConnection {

	private boolean knowledgeDebug = false;
	private final String USER_AGENT = "Mozilla/5.0";
	private String arduinoURL = "http://10.0.0.2";
	private static URLConnection URLCONNECTION_INSTANCE = null;

	private final Logger logger = LoggerFactory.getLogger(URLConnection.class);

	public URLConnection(String arduinoURL, boolean knowledgeDebug) {
		this.arduinoURL = arduinoURL;
		this.knowledgeDebug = knowledgeDebug;
		URLConnection.URLCONNECTION_INSTANCE = this;
	}

	public static URLConnection getInstance() {
		return URLCONNECTION_INSTANCE;
	}

	// HTTP GET request
	public void sendGet(String command) {
		String urlString = arduinoURL + command;
		if (arduinoURL.indexOf("http://10.0.0.2") != -1) {
			System.err.println("Error: Send Arduino Command no arduinoURL=http:<ip address> defined in arduinotron.properties file.");
			return;
		}
		if (arduinoURL == null && arduinoURL.isEmpty()) {
			System.err.println("Error: Send Arduino Command no arduinoURL=http:<ip address> defined in arduinotron.properties file.");
			return;
		}

		try {
			URL url = new URL(urlString);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			// By default it is GET request
			con.setRequestMethod("GET");

			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
			System.out.println("Send GET request: " + url);
			System.out.println("Response code: " + responseCode);

			// Reading response from input Stream
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer response = new StringBuffer();
			String output;

			while ((output = in.readLine()) != null) {
				response.append(output);
			}
			in.close();

			// printing result from response
			System.out.println(response.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// HTTP Post request
	public void sendPost(String command) {
		String url = arduinoURL + command;
		if (arduinoURL.indexOf("http://10.0.0.2") != -1) {
			System.err.println("Error: Send Arduino Command no arduinoURL=http:<ip address> defined in arduinotron.properties file.");
			return;
		}
		if (arduinoURL == null && arduinoURL.isEmpty()) {
			System.err.println("Error: Send Arduino Command no arduinoURL=http:<ip address> defined in arduinotron.properties file.");
			return;
		}

		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// Setting basic post request
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("Content-Type", "application/json");

			String postJsonData = "arduinotron ai-iot";

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postJsonData);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			if (knowledgeDebug) {
				System.out.println("Send 'POST' request to URL: " + url);
				System.out.println("Post Data: " + postJsonData);
				System.out.println("Response Code: " + responseCode);
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String output;
			StringBuffer response = new StringBuffer();

			while ((output = in.readLine()) != null) {
				response.append(output);
			}
			in.close();

			// printing result from response
			if (knowledgeDebug) {
				System.out.println(response.toString());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
