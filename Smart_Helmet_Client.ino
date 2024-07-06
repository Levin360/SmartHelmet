#include <Wire.h>
#include <MPU6050.h>
#include "WiFi.h"
#include "PubSubClient.h"

// WiFi
const char* ssid = "WIFI_NAME"; // Enter your Wifi Name
const char* wifi_password = "WIFI_PASSWORD"; // Enter your Wifi Password

// MQTT
const char* mqtt_server = "IP_ADDRESS_OF_THE_RASP_PI  ";  // IP of the MQTT broker (Raspberry Pi)
const char* mqtt_username = "SET_YOUR_MQTT_USERNAME"; // MQTT username
const char* mqtt_password = "SET_YOUR_MQTT_PASSWORD"; // MQTT password
const char* clientID = "MQTT_CLIENT_ID"; // MQTT client ID
const char* topic = "MQTT_TOPIC"; // MQTT topic

// Initialise the WiFi and MQTT Client objects
WiFiClient wifiClient;
// 1883 is the listener port for the Broker
PubSubClient client(mqtt_server, 1883, wifiClient); 

MPU6050 mpu;

const int relayPin = 14;
const float threshold_acceleration = 1.8;
const float threshold_gyro = 250.0;

#define BUZZER_PIN 4   // Buzzer pin
#define BUTTON_PIN 17  // Button pin
#define RED_LED_PIN 23 // Red LED pin
#define GREEN_LED_PIN 19 // Green LED pin
#define BLUE_LED_PIN 16 // Blue LED pin

bool accidentTriggered = false;
unsigned long accidentTime = 0;
unsigned long lastBlinkTime = 0;
bool blueLedState = false;
bool messageSent = false; // Flag to indicate if the message was sent

void setup() {
  pinMode(BUZZER_PIN, OUTPUT);       // Buzzer pin as output
  digitalWrite(BUZZER_PIN, HIGH);    // Set buzzer off initially (for active-low buzzer)
  
  pinMode(BUTTON_PIN, INPUT_PULLUP);  // Assuming the button uses internal pull-up

  pinMode(RED_LED_PIN, OUTPUT);      // Set Red LED pin as output
  pinMode(GREEN_LED_PIN, OUTPUT);    // Set Green LED pin as output
  pinMode(BLUE_LED_PIN, OUTPUT);     // Set Blue LED pin as output
  
  digitalWrite(RED_LED_PIN, LOW);    // Red LED off initially
  digitalWrite(GREEN_LED_PIN, HIGH); // Green LED on initially
  digitalWrite(BLUE_LED_PIN, LOW);   // Blue LED off initially

  Serial.begin(115200);
  Wire.begin(21, 22);
  mpu.initialize();
  
  pinMode(relayPin, OUTPUT);
  digitalWrite(relayPin, LOW);
  
  if (!mpu.testConnection()) {
    Serial.println("MPU6050 connection failed");
    while (1);
  }
  Serial.println("MPU6050 connection successful");
}

void connect_MQTT(){
  Serial.print("Connecting to ");
  Serial.println(ssid);

  // Connect to the WiFi
  WiFi.begin(ssid, wifi_password);

  // Wait until the connection has been confirmed before continuing
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  // Debugging - Output the IP Address of the ESP32
  Serial.println("WiFi connected");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());

  // Connect to MQTT Broker
  while (!client.connected()) {
    Serial.print("Connecting to MQTT Broker...");
    if (client.connect(clientID, mqtt_username, mqtt_password)) {
      Serial.println("connected");
    } else {
      Serial.print("failed with state ");
      Serial.print(client.state());
      delay(2000);
    }
  }
}

void handleAccidentTriggered() {
  unsigned long currentTime = millis();
  if (currentTime - accidentTime >= 10000) {
    digitalWrite(RED_LED_PIN, LOW); // Turn off red LED
    digitalWrite(GREEN_LED_PIN, LOW); // Turn off green LED
    // Blink blue LED every 2 seconds
    if (currentTime - lastBlinkTime >= 1000) {
      blueLedState = !blueLedState;
      digitalWrite(BLUE_LED_PIN, blueLedState ? HIGH : LOW);
      lastBlinkTime = currentTime;
    }
    digitalWrite(BUZZER_PIN, HIGH);  // Turn off buzzer (active-low)

    // Send the message once after 10 seconds
    if (!messageSent) {
      Serial.println("10 seconds passed. Sending User info to the Authorities.");
      client.publish(topic, "Accident Detected");
      messageSent = true;
    }
  }

  if (currentTime - accidentTime >= 30000 || digitalRead(BUTTON_PIN) == HIGH) {
    digitalWrite(BUZZER_PIN, HIGH);  // Turn off buzzer (active-low)
    digitalWrite(RED_LED_PIN, LOW);  // Turn off red LED
    digitalWrite(GREEN_LED_PIN, HIGH); // Turn on green LED
    digitalWrite(BLUE_LED_PIN, LOW);  // Turn off blue LED
    accidentTriggered = false;
    messageSent = false; // Reset the flag for the next accident
    Serial.println("Buzzer turned off");
  }
}

void checkAccident(float ax_g, float ay_g, float gx_dps, float gy_dps) {
  if ((abs(ax_g) > threshold_acceleration || abs(ay_g) > threshold_acceleration) &&
      (abs(gx_dps) > threshold_gyro || abs(gy_dps) > threshold_gyro)) {
    digitalWrite(relayPin, HIGH);      // Activate relay
    delay(1000);
    digitalWrite(relayPin, LOW);       // Deactivate relay
    digitalWrite(BUZZER_PIN, LOW);     // Turn on buzzer (active-low)
    digitalWrite(RED_LED_PIN, HIGH);   // Turn on red LED
    digitalWrite(GREEN_LED_PIN, LOW);  // Turn off green LED
    digitalWrite(BLUE_LED_PIN, LOW);   // Turn off blue LED
    Serial.println("ACCIDENT DETECTED!");
    accidentTriggered = true;
    accidentTime = millis();           // Record the time when the accident was detected
    lastBlinkTime = millis();          // Initialize blink time
    messageSent = false;               // Reset the message sent flag
  }
}

void loop() {
  if (!client.connected()) {
    connect_MQTT(); // Reconnect to MQTT if disconnected
  }
  client.loop();
  
  if (accidentTriggered) {
    handleAccidentTriggered();
    return;
  }
  
  int16_t ax, ay, az;
  int16_t gx, gy, gz;
  
  mpu.getAcceleration(&ax, &ay, &az);
  mpu.getRotation(&gx, &gy, &gz);
  
  float ax_g = ax / 16384.0;
  float ay_g = ay / 16384.0;
  
  float gx_dps = gx / 131.0;
  float gy_dps = gy / 131.0;
  
  Serial.print(ax_g);
  Serial.print(",");
  Serial.print(ay_g);
  Serial.print(",");
  Serial.print(gx_dps);
  Serial.print(",");
  Serial.println(gy_dps);
  
  checkAccident(ax_g, ay_g, gx_dps, gy_dps);
  
  delay(100);
}
