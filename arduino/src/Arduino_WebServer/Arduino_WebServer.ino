/********************
  - Executive Order Corporation - Arduino Tron - Arduino ESP8266 MQTT Telemetry Transport Machine-to-Machine(M2M)/Internet of Things(IoT)
  - Arduino Tron Drools-jBPM :: Executive Order Sensor Processor System - Arduino Tron MQTT AI-IoT Client using AI-IoT Drools-jBPM
  - Arduino Tron AI-IoT :: Internet of Things Drools-jBPM Expert System using Arduino Tron AI-IoT Processing
  - Executive Order Corporation
  - Copyright © 1978, 2018: Executive Order Corporation, All Rights Reserved
********************/

//#include <SimpleDHT.h> <-- uncommit for dht11
//#include <IRrecv.h> <-- uncommit for IR VS1838

#include <ESP8266WiFi.h>
#include <WiFiUdp.h>

#define ADC0 A0 // NodeMCU pin Analog ADC0 (A0)

#define LED0 D0 // NodeMCU pin GPIO16 (D0)
#define LED1 D1 // NodeMCU pin GPIO5 (D1)
#define LED2 D2 // NodeMCU pin GPIO4 (D2)
#define LED3 D3 // NodeMCU pin GPIO0 (D3)
#define LED4 D4 // NodeMCU pin GPIO2 (D4-onboard)
#define LED5 D5 // NodeMCU pin GPIO14 (D5)
#define LED6 D6 // NodeMCU pin GPIO12 (D6)
#define LED7 D7 // NodeMCU pin GPIO13 (D7)
#define LED8 D8 // NodeMCU pin GPIO15 (D8)
#define LED9 D9 // NodeMCU pin GPIO3 (D9-RXD0)
#define LED10 D10 // NodeMCU pin GPIO1 (D10-TXD0)

#define BUTTON0 D0 // NodeMCU pin GPIO16 (D0)
#define BUTTON1 D1 // NodeMCU pin GPIO5 (D1)
#define BUTTON2 D2 // NodeMCU pin GPIO4 (D2)
#define BUTTON3 D3 // NodeMCU pin GPIO0 (D3)
#define BUTTON4 D4 // NodeMCU pin GPIO2 (D4)
#define BUTTON5 D5 // NodeMCU pin GPIO14 (D5)
#define BUTTON6 D6 // NodeMCU pin GPIO12 (D6)
#define BUTTON7 D7 // NodeMCU pin GPIO13 (D7)
#define BUTTON8 D8 // NodeMCU pin GPIO15 (D8)
#define BUTTON9 D9 // NodeMCU pin GPIO3 (D9-RXD0)
#define BUTTON10 D10 // NodeMCU pin GPIO1 (D10-TXD0)

// Update these with WiFi network values
const char* ssid     = "Executive Order"; // "your-ssid"; //  your network SSID (name)
const char* password = "SL550eodas"; // "your-password"; // your network password

WiFiClient client;
WiFiServer webserver(80);

// Update these with Arduino Tron service IP address and unique unit id values
byte server[] = { 10, 0, 0, 166 }; // Set EOSpy server IP address as bytes
String id = "100111"; // Arduino Tron Device unique unit id

const int httpPort = 5055; // Arduino Tron server is running on default port 5055
// OpenStreetMap Automated Navigation Directions is a map and navigation app for Android default port 5055

// Values ?id=334455&timestamp=1521212240&lat=38.888160&lon=-77.019868&speed=0.0&bearing=0.0&altitude=0.0&accuracy=0.0&batt=98.7
String timestamp = "1521212240"; // timestamp
String accuracy = "0.0"; // position accuracy
String batt = "89.7"; // battery value
String light = "53.4"; // photocell value

// Arduino Tron currently supports these additional data fields in the Server Event data model:

// id=6&event=allEvents&protocol=osmand&servertime=<date>&timestamp=<date>&fixtime=<date>&outdated=false&valid=true
// &lat=38.85&lon=-84.35&altitude=27.0&speed=0.0&course=0.0&address=<street address>&accuracy=0.0&network=null
// &batteryLevel=78.3&textMessage=Message_Sent&temp=71.2&ir_temp=0.0&humidity=0.0&mbar=79.9
// &accel_x=-0.01&accel_y=-0.07&accel_z=9.79&gyro_x=0.0&gyro_y=-0.0&gyro_z=-0.0&magnet_x=-0.01&magnet_y=-0.07&magnet_z=9.81
// &light=91.0&keypress=0.0&alarm=Temperature&distance=1.6&totalDistance=3.79&motion=false

// You can add more additional fields to the data model and transmit via any device to the Arduino Tron Drools-jBPM processing

// Values for the DHT11 digital temperature/humidity sensor; &temp= and &humidity= fields
String temp = "0.0";
String humidity = "0.0";

// Values to send in &textMessage= filed
String textMessage = "text_message";

// Values to send in &keypress= field
const String TYPE_ALLEVENTS = "allEvents"; // allEvents
const String TYPE_KEYPRESS_1 = "1.0"; // keypress_1
const String TYPE_KEYPRESS_2 = "2.0"; // keypress_2
const String TYPE_REED_RELAY = "4.0"; // reedRelay
const String TYPE_PROXIMITY = "8.0"; // proximity

// Values to send in &alarm= field
const String ALARM_GENERAL = "general";
const String ALARM_SOS = "sos";
const String ALARM_VIBRATION = "vibration";
const String ALARM_MOVEMENT = "movement";
const String ALARM_LOW_SPEED = "lowspeed";
const String ALARM_OVERSPEED = "overspeed";
const String ALARM_FALL_DOWN = "fallDown";
const String ALARM_LOW_POWER = "lowPower";
const String ALARM_LOW_BATTERY = "lowBattery";
const String ALARM_FAULT = "fault";
const String ALARM_POWER_OFF = "powerOff";
const String ALARM_POWER_ON = "powerOn";
const String ALARM_DOOR = "door";
const String ALARM_GEOFENCE = "geofence";
const String ALARM_GEOFENCE_ENTER = "geofenceEnter";
const String ALARM_GEOFENCE_EXIT = "geofenceExit";
const String ALARM_GPS_ANTENNA_CUT = "gpsAntennaCut";
const String ALARM_ACCIDENT = "accident";
const String ALARM_TOW = "tow";
const String ALARM_ACCELERATION = "hardAcceleration";
const String ALARM_BRAKING = "hardBraking";
const String ALARM_CORNERING = "hardCornering";
const String ALARM_FATIGUE_DRIVING = "fatigueDriving";
const String ALARM_POWER_CUT = "powerCut";
const String ALARM_POWER_RESTORED = "powerRestored";
const String ALARM_JAMMING = "jamming";
const String ALARM_TEMPERATURE = "temperature";
const String ALARM_PARKING = "parking";
const String ALARM_SHOCK = "shock";
const String ALARM_BONNET = "bonnet";
const String ALARM_FOOT_BRAKE = "footBrake";
const String ALARM_OIL_LEAK = "oilLeak";
const String ALARM_TAMPERING = "tampering";
const String ALARM_REMOVING = "removing";

String ver = "1.03E";
int loopCounter = 1; // loop counter
int switchState = 0; // digitalRead value from gpiox button

// Arduino Time Sync from NTP Server using ESP8266 WiFi module
unsigned int localPort = 2390; // local port to listen for UDP packets
IPAddress timeServerIP;
const char* ntpServerName = "time.nist.gov";

const int NTP_PACKET_SIZE = 48;
byte packetBuffer[NTP_PACKET_SIZE];
WiFiUDP udp;

unsigned long milsec = 0;
unsigned long epoch = 0;

// DHT11 digital temperature and humidity sensor pin Vout (sense)
int pinDHT11 = 2;
//SimpleDHT11 dht11; <-- uncommit for dht11

// LDR Photocell light interface for NodeMCU
int photocellChange = 10; // LDR and 10K pulldown resistor are connected to A0
float photocellLight; // Variable to hold last analog light value

// Arduino valuse for IR sensor connected to GPIO2
uint16_t RECV_PIN = D5;
//IRrecv irrecv(RECV_PIN); <-- uncommit for IR VS1838
//decode_results results; <-- uncommit for IR VS1838
String irkey = "1";

// Required for LIGHT_SLEEP_T delay mode
extern "C" {
#include "user_interface.h"
}

void setup() {
  pinMode(LED0, OUTPUT); // Declaring Arduino LED pin as output
  pinMode(LED1, OUTPUT);
  pinMode(LED2, OUTPUT);
  pinMode(LED3, OUTPUT);
  pinMode(LED4, OUTPUT);

  digitalWrite(LED0, LOW); // turn the LED off
  digitalWrite(LED1, LOW);
  digitalWrite(LED2, LOW);
  digitalWrite(LED3, LOW);
  digitalWrite(LED4, LOW);

  // Arduino IDE Serial Monitor window to emulate what Arduino Tron sensors are reading
  Serial.begin(115200); // Serial connection from ESP-01 via 3.3v console cable

  // Connect to WiFi network
  Serial.println("Executive Order Corporation - Arduino Tron - Arduino ESP8266 MQTT Telemetry Transport Machine-to-Machine(M2M)/Internet of Things(IoT)");
  Serial.println("Arduino Tron Drools-jBPM :: Executive Order Sensor Processor System - Arduino Tron MQTT AI-IoT Client using AI-IoT Drools-jBPM");
  Serial.println("- Arduino Tron Webserver ver " + ver);
  Serial.println("Copyright © 1978, 2018: Executive Order Corporation, All Rights Reserved");
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");

  // Start the arduino tron webserver
  webserver.begin();
  Serial.println("Arduino Tron Webserver started");

  // Print the IP address
  Serial.print("Use this URL to connect: ");
  Serial.print("http://");
  Serial.print(WiFi.localIP());
  Serial.println("/");
}

void loop() {
  // Check if a client has connected
  client = webserver.available();
  if (!client) {
    return;
  }

  // Wait until the client sends some data
  Serial.print("new client connection, loop ");
  Serial.println(loopCounter);
  loopCounter++;

  while (!client.available()) {
    delay(1);
  }
  webclient();
  arduinoTronSend();
}

void arduinoTronSend()
{
  getTimeClock(); // get time clock for event timestamp

  // Explicitly set the ESP8266 to be a WiFi-client
  WiFi.mode(WIFI_STA);

  // Connect to WiFi network
  WiFi.begin(ssid, password);
  Serial.print("\n\r \n\rExecutive Order Corporation - Arduino Tron Webserver - Arduino ESP8266 MQTT Telemetry Transport Machine-to-Machine(M2M)/Internet of Things(IoT) ");
  Serial.println(timestamp);

  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(200);
    Serial.print(".");
  }
  Serial.print("Connected to ");
  Serial.print(ssid);
  Serial.print(" IP address: ");
  Serial.print(WiFi.localIP());
  Serial.print(" ESP8266 Chip Id ");
  Serial.print(ESP.getChipId());
  Serial.print(" gpio ");
  Serial.print(switchState);
  Serial.print(" loop ");
  Serial.println(loopCounter);
  loopCounter++;

  if (!client.connect(server, httpPort)) { // http server is running on default port 5055
    Serial.print("Connection Failed Status: ");
    Serial.println(WiFi.status());
    return;
  }

  Serial.println("Connected");
  client.print("GET /?id=" + id);
  client.print("&timestamp=" + timestamp);
  // client.print("&lat=" + lat); <-- no GPS location needed in demo
  // client.print("&lon=" + lon);
  // client.print("&speed=" + speeds);
  // client.print("&bearing=" + bearing);
  // client.print("&altitude=" + altitude);
  // client.print("&accuracy=" + accuracy);
  client.print("&batt=" + batt);

  // digitalRead GPIO15(D8) send values for textMessage, keypress and alarm
  client.print("&light=" + light);
  textMessage = "Movement_Security_Alarm";
  client.print("&textMessage=" + textMessage);
  client.print("&keypress=" + irkey); // keypress=irkey
  client.print("&alarm=" + ALARM_MOVEMENT);

  client.println(" HTTP/1.1");

  client.println("User-Agent: Arduino Tron ver " + ver);
  client.println("Content-Length: 0");

  client.println(); // empty line for apache server

  int i = 0;
  // Wait up to 5 seconds for server to respond then read response
  while ((!client.available()) && (i < 1000)) {
    delay(5); // was 10 seconds
    i++;
  }

  client.stop();

  Serial.print("Connection Status: ");
  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("Connected Ok");
  } else {
    Serial.print("Error ");
    Serial.println(WiFi.status());
  }
  // WL_NO_SHIELD = 255
  // WL_IDLE_STATUS = 0
  // WL_NO_SSID_AVAIL = 1
  // WL_SCAN_COMPLETED = 2
  // WL_CONNECTED = 3
  // WL_CONNECT_FAILED = 4
  // WL_CONNECTION_LOST = 5
  // WL_DISCONNECTED = 6

  // WiFi.disconnect(); // DO NOT DISCONNECT WIFI IF YOU WANT TO LOWER YOUR POWER DURING LIGHT_SLEEP_T DELLAY !
  // wifi_set_sleep_type(LIGHT_SLEEP_T);
}

void webclient() {
  // Read the first line of the request
  String request = client.readStringUntil('\r');
  Serial.println(request);
  client.flush();

  // Set ledPin according to the request digitalWrite(ledPin, value);
  int value0 = LOW;
  if (request.indexOf("/LED0=ON") != -1)  {
    digitalWrite(LED0, HIGH);
    value0 = HIGH;
  }
  if (request.indexOf("/LED0=OFF") != -1)  {
    digitalWrite(LED0, LOW);
    value0 = LOW;
  }

  // Set ledPin according to the request digitalWrite(ledPin, value);
  int value1 = LOW;
  if (request.indexOf("/LED1=ON") != -1)  {
    digitalWrite(LED1, HIGH);
    value1 = HIGH;
  }
  if (request.indexOf("/LED1=OFF") != -1)  {
    digitalWrite(LED1, LOW);
    value1 = LOW;
  }

  // Set ledPin according to the request digitalWrite(ledPin, value);
  int value2 = LOW;
  if (request.indexOf("/LED2=ON") != -1)  {
    digitalWrite(LED2, HIGH);
    value2 = HIGH;
  }
  if (request.indexOf("/LED2=OFF") != -1)  {
    digitalWrite(LED2, LOW);
    value2 = LOW;
  }

  // Set ledPin according to the request digitalWrite(ledPin, value);
  int value3 = LOW;
  if (request.indexOf("/LED3=ON") != -1)  {
    digitalWrite(LED3, HIGH);
    value3 = HIGH;
  }
  if (request.indexOf("/LED3=OFF") != -1)  {
    digitalWrite(LED3, LOW);
    value3 = LOW;
  }

  // Set ledPin according to the request digitalWrite(ledPin, value);
  int value4 = LOW;
  if (request.indexOf("/LED4=ON") != -1)  {
    digitalWrite(LED4, HIGH);
    value4 = HIGH;
  }
  if (request.indexOf("/LED4=OFF") != -1)  {
    digitalWrite(LED4, LOW);
    value4 = LOW;
  }

  //stopping client
  //client.stop();

  // Return the response
  client.println("HTTP/1.1 200 OK");
  client.println("Content-Type: text/html");
  client.println(""); //  do not forget this one
  client.println("<!DOCTYPE HTML>");
  client.println("<html><head>Arduino Tron Web Server</head><body>");
  client.println();
  client.print("<form method=get>");
  client.println("<br><br>");

  client.print("Led pin is now: ");

  if (value4 == HIGH) {
    client.print("On");
  } else {
    client.print("Off");
  }
  client.println("<br><br>");
  client.println("<a href=\"/LED0=ON\"\"><button>Turn LED-0 On </button></a>");
  client.println("<a href=\"/LED0=OFF\"\"><button>Turn LED-0 Off </button></a><br />");
  client.println("<a href=\"/LED1=ON\"\"><button>Turn LED-1 On </button></a>");
  client.println("<a href=\"/LED1=OFF\"\"><button>Turn LED-1 Off </button></a><br />");
  client.println("<a href=\"/LED2=ON\"\"><button>Turn LED-2 On </button></a>");
  client.println("<a href=\"/LED2=OFF\"\"><button>Turn LED-2 Off </button></a><br />");
  client.println("<a href=\"/LED3=ON\"\"><button>Turn LED-3 On </button></a>");
  client.println("<a href=\"/LED3=OFF\"\"><button>Turn LED-3 Off </button></a><br />");
  client.println("<a href=\"/LED4=ON\"\"><button>Turn LED-4 On </button></a>");
  client.println("<a href=\"/LED4=OFF\"\"><button>Turn LED-4 Off </button></a><br />");
  client.println("<br><br>");

client.println("<select name=carlist>");
  client.println("<option value=volvo>Volvo</option>");
  client.println("<option value=saab>Saab</option>");
  client.println("<option value=opel>Opel</option>");
  client.println("<option value=audi>Audi</option>");
client.println("</select>");
  
//  client.println("<input type=text name=textbox size=25 value=Enter_your_name_here!> Enter your name here!<br>");
//  client.println("<input type=text name=namebox size=25 value=Something> Enter Something here!<br>");

  client.println("<br><input type=submit value=Submit><br>");
  client.println("</body></html>");

  delay(1);
  Serial.println("Client disonnected");
  Serial.println("");
}

// Arduino Time Sync from NTP Server using ESP8266 WiFi module
void getTimeClock()
{
  if ((milsec == 0) || (epoch == 0) || ((millis() - milsec) > 3600000)) {
    setNTPServerTime(); // get time clock for event timestamp
    milsec = millis();
    return;
  }
  unsigned long mpoch = epoch + ((millis() - milsec) / 1000);
  timestamp = String(mpoch);
}

void setNTPServerTime()
{
  WiFi.begin(ssid, password);

  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(200);
    Serial.print(".");
  }

  udp.begin(localPort);
  WiFi.hostByName(ntpServerName, timeServerIP);

  sendNTPpacket(timeServerIP);
  delay(1000);

  int cb = udp.parsePacket();
  if (!cb) {
    delay(1);
  }
  else {
    udp.read(packetBuffer, NTP_PACKET_SIZE); // read the packet into the buffer
    unsigned long highWord = word(packetBuffer[40], packetBuffer[41]);
    unsigned long lowWord = word(packetBuffer[42], packetBuffer[43]);
    unsigned long secsSince1900 = highWord << 16 | lowWord;
    const unsigned long seventyYears = 2208988800UL;
    epoch = secsSince1900 - seventyYears; // Unix epoch number of seconds since midnight 1-1-70
    // Serial.print("UNX");
    // Serial.println(epoch);
    timestamp = String(epoch);
  }
  udp.stop();
}

unsigned long sendNTPpacket(IPAddress& address)
{
  Serial.println("Sending NTP packet...");
  memset(packetBuffer, 0, NTP_PACKET_SIZE);
  packetBuffer[0] = 0b11100011; // LI, Version, Mode
  packetBuffer[1] = 0; // Stratum, or type of clock
  packetBuffer[2] = 6; // Polling Interval
  packetBuffer[3] = 0xEC; // Peer Clock Precision
  packetBuffer[12]  = 49;
  packetBuffer[13]  = 0x4E;
  packetBuffer[14]  = 49;
  packetBuffer[15]  = 52;
  udp.beginPacket(address, 123);
  udp.write(packetBuffer, NTP_PACKET_SIZE);
  udp.endPacket();
}
