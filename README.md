# Arduino Tron AI-IoT :: Internet of Things Drools-jBPM (Business Process Management)

Executive Order Corp - Arduino Tron - Arduino ESP8266 MQTT Telemetry Transport Machine-to-Machine(M2M)/Internet of Things (IoT)
Arduino Tron :: Executive Order Sensor Processor System - Arduino Tron MQTT AI-IoT Client using EOSpy AI-IoT Drools-jBPM

The power of the IoT (Internet of Things) device increases greatly when business process (jBPM) can use them to provide information
about our real-world as well as execute IoT devices as part of our business process. The jBPM-BPMN modular allow us to define 
both the business processes and IoT devices behavior at the same time using one diagram. With Arduino Tron adding Drools-jBPM to IoT,
we make the IoT devices “smart”. Moving beyond just collecting IoT data and transitioning, to leveraging the new wealth of IoT data, 
to improving the smart decision making is the key. The Executive Order Arduino Tron AI-IoT will help these IoT devices, environments, 
and products to self-monitor, self-diagnose and eventually, self-direct.

Arduino Tron allows you to send IoT sensor data and information directly to the AI-IoT Drools-jBPM Expert System from the Arduino device.
This provides a very lite streamline IoT to Drools-jBPM (Business Process Management) application process without GPS positioning information.
Executive Order Arduino Tron — This quick guide will help you install and configure the Arduino Tron – Executive Order Sensor Processor System components.

Executive Order Arduino Tron has several components:
1. The Arduino Tron AI-IoT (Java), the Internet of Things Drools-jBPM Expert System.
2. The Arduino Tron Sensor (Arduino), the application to send sensor MQTT Telemetry Transport.
3. The Arduino Tron Server (Arduino), the software to control external Arduino connected devices.

You can have an unlimited number and combination of Arduino Tron IoT Devices and/or EOSPY GPS Client tracking devices in use with Arduino Tron AI-IoT.
(Optionally, download EOSPY server from our website http://www.eospy.com and Download EOSPY GPS client from the Google Store, standard or TI-SensorTag version.)

(1) Arduino Tron AI-IoT – To install the Arduino Tron AI-IoT program on your windows computer, download and install the "Eclipse IDE for Java Developers."
Use the Eclipse feature to add new software, available on the Eclipse menu “Help -> Install New Software”. Select the “Add” option and install these packages:
Drools + jBPM Update Site 7.7.0 - http://downloads.jboss.org/jbpm/release/7.7.0.Final/updatesite/
BPMN2-Modeler 1.4.2 - http://download.eclipse.org/bpmn2-modeler/updates/oxygen/1.4.2/
GIT the Arduino Tron from the source code repository, and Import Existing Maven project.

(2) Arduino Tron Sensor – To install the Arduino Tron application on your Arduino device, download the Arduino Tron Sensor application from GIT.
Update the with WiFi network values for network SSID (name) and network password. Update the Arduino Tron Server IP address and unique unit ID values.
Also, you may use a DHT11 digital temperature and humidity sensor (see the Arduino Tron Sensor sketch for more details and information).

(3) Arduino Tron Server - To install the Arduino Tron application on your Arduino Device, download the Arduino Tron Server application from GIT.
The Arduino Tron Server AI-IoT software interface allows you to send commands with the Arduino Tron AI-IoT software to control external Arduino connected devices.
The AI-IoT Arduino Tron Server software uses a WiFi wireless transceiver interface to control and interact with module sensors and remote controls devices. You can 
control any device form the Arduino Tron AI-IoT software or stream any interface over the WiFi internet. With the Arduino Tron AI-IoT Arduino Tron server software
you can automatically turn on lights, appliances, cameras, and open doors from the Drools-jBPM Expert System processing model.

Arduino Tron IoT Sensor Emulate - Prototype an Arduino-based low-power WiFi Internet of Things (IoT) device with built-in sensors emulation that could be used to
deliver sensor data from any location in the world, and potentially control connected devices such as thermostats, lights, door locks, and other automation products.
Use a serial monitor to emulate sensor input values to Arduino Tron. This will allow you to prototype the final IoT device before custom-designing PCB-printed circuit board.

[] The Arduino Tron AI-IoT Drools-jBPM Expert System provides sophisticated jBPM and drools processing. i.e. On a monitoring application, take an action if the temperature 
on the server room increases X degrees in Y minutes, where sensor readings are usually denoted by events example drools.drl file:

declare TemperatureThreshold 
        windowTime : String = "30s" 
        max : long = 70 
end 

declare SensorReading 
        @role( event ) 
        temperature : String = "40" 
end 

rule "Sound the alarm in case temperature rises above threshold" 
when 
   TemperatureThreshold( $max : max, $windowTime : windowTime ) 
   Number( doubleValue > $max ) from accumulate( 
   SensorReading( $temp : temperature ) over window:time( $windowTime ), 
   average( $temp ) ) 
then 
   // sound the alarm 
end 

- Executive Order Corporation
- Copyright © 1978, 2018: Executive Order Corporation, All Rights Reserved


