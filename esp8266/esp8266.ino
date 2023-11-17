/*
    This sketch establishes a TCP connection to a "quote of the day" service.
    It sends a "hello" message, and then prints received data.
*/

#include <ESP8266WiFi.h>

#ifndef STASSID
#define STASSID "Akira"
#define STAPSK "Cubin159753"
#endif

const char* ssid = STASSID;
const char* password = STAPSK;

const char* host = "192.168.1.6";
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

}

void loop() {
  static bool wait = false;
  int temp = 34; 
  int humidity = 60;
  WiFiClient client;

  if (!client.connect(host, port)) {
    Serial.println("connection failed");
    delay(5000);
    return;
  }
  Serial.println("Connection Ok");
  client.write(lowByte(temp));
  client.write(highByte(temp));
  client.write(lowByte(humidity));
  client.write(highByte(humidity));
  client.flush();
  delay(1000);
}
