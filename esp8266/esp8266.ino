/*
    This sketch establishes a TCP connection to a "quote of the day" service.
    It sends a "hello" message, and then prints received data.
*/

#include <ESP8266WiFi.h>
#include "DHTesp.h"
DHTesp dht;
#ifndef STASSID
#define STASSID "Akira"
#define STAPSK "Cubin159753"
#endif

const char* ssid = STASSID;
const char* password = STAPSK;

const char* host = "192.168.1.120";
const uint16_t port = 80;

void setup() {
  Serial.begin(115200);

  // We start by connecting to a WiFi network

  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  /* Explicitly set the ESP8266 to be a WiFi-client, otherwise, it by default,
     would try to act as both a client and an access-point and could cause
     network-issues with your other WiFi-devices on your WiFi-network. */
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
  WiFiClient client;
  Serial.begin(115200);
  dht.setup(D2, DHTesp::DHT22);

}

void loop() {
  float temp = dht.getTemperature();
  float humidity = dht.getHumidity();

  Serial.print("humidity: ");
  Serial.println(humidity);
  Serial.print("temperature: ");
  Serial.println(temp);
  Serial.print("}\n");


  byte* bytePointer = (byte*) &temp;
  WiFiClient client;

  if (!client.connect(host, port)) {
    Serial.println("connection failed");
    delay(5000);  
    return;
  }
  for (int i = 0; i <=3; i++) {
    client.write(bytePointer[i]);
  }
  bytePointer = (byte*) &humidity;
  for(int i = 0 ; i<=3;i++){
    client.write(bytePointer[i]);
  }
  Serial.println("Connection Ok");
  
  client.flush();
  client.stop();
  delay(1000);
}



