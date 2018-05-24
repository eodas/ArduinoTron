package com.arduinotron.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arduinotron.RulesProcess;
import com.arduinotron.model.ServerEvent;

public class IoTServerThread extends Thread {
	RulesProcess rulesProcessor;

	Socket socket = null;
	IoTServer ws = null;
	PrintStream out;

	private final Logger logger = LoggerFactory.getLogger(IoTServerThread.class);

	public IoTServerThread(Socket socket, IoTServer ws, RulesProcess rulesProcessor) {
		this.ws = ws;
		this.socket = socket;
		this.rulesProcessor = rulesProcessor;
		setName("IoT Server Thread");

		start();
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
			String request = "";
			while (true) {
				request = in.readLine();

				boolean moreLines = true;
				while (moreLines) {
					String reqtmp = in.readLine();

					if ((reqtmp.equals(null)) || (reqtmp.equals(""))) {
						break;
					}
				}

				ServerEvent serverEvent = new ServerEvent();
				String[] req = Pattern.compile(" ").split(request);

				if (req[0].equals("GET")) {

					String arg = req[1].substring(req[1].indexOf('?') + 1);
					String[] tokens = arg.split("&");

					for (String token : tokens) {
						try {
							String key = token.substring(0, token.indexOf('='));
							String value = token.substring(token.indexOf('=') + 1);
							serverEvent.add(key, value);

						} catch (IndexOutOfBoundsException e) {
							System.err.println("=============================================================");
							System.err.println("Unexpected exception caught: " + e.getMessage());
							e.printStackTrace();
						}
					}
					sendHttpTextResp(200, "OK");
					rulesProcessor.receive(serverEvent);
				}
			}

		} catch (Exception localException3) {
			ws.decConnection();
		}
	}

	private void sendHttpTextResp(int status, String response) throws IOException {
		String headerText = "OK";

		switch (status) {
		case 200:
			headerText = "OK";
			break;
		case 404:
			headerText = "File not found";
			break;
		case 401:
			headerText = "Unauthorized";
			break;
		default:
			headerText = "OK";
		}

		out.println("HTTP/1.1 " + status + " " + headerText);
		out.println("Content-Length: 0");
		out.println("");
		// out.println("<!DOCTYPE HTML>");
		// out.println("<html>");

		out.flush();
		out.close();
		socket.close();
	}
}
