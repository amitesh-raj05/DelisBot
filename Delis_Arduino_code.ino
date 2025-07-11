#include <ESP8266WiFi.h>
#include <Firebase_ESP_Client.h>
#include <addons/TokenHelper.h>
#include <addons/RTDBHelper.h>

#define WIFI_SSID "AMITESH"
#define WIFI_PASSWORD "amtu9986"
#define API_KEY "AIzaSyCx7cDk4CQe2K35bEapl9Jqz94Z9xIWYfQ"
#define DATABASE_URL "https://delis-bot-default-rtdb.asia-southeast1.firebasedatabase.app/" 
#define USER_EMAIL "raazamitesh9@gmail.com"
#define USER_PASSWORD "123456789"

#define IN1 D1
#define IN2 D2
#define IN3 D3
#define IN4 D4
#define ENA D0
#define ENB D5

const int tableSpacing = 50;       // Distance between tables in cm
const int speed = 80;             // Motor speed
const int travelTimePerTable = 1000; // Estimated travel time per table (in ms)

int targetTable = -1;              // Target table to navigate to
int currentTable = 1;              // Current table position of the car
int lastTable = -1;                // Last fetched table number
unsigned long travelStartTime = 0; // Time at which movement started
bool moving = false;               // Whether the car is moving
bool movingForward = true;         // Direction of movement

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;

void setup() {
  Serial.begin(115200);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  config.api_key = API_KEY;
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  config.database_url = DATABASE_URL;
  config.token_status_callback = tokenStatusCallback; 
  Firebase.reconnectNetwork(true);

  // Set buffer size and response size
  fbdo.setBSSLBufferSize(4096, 1024);
  fbdo.setResponseSize(2048);

  Firebase.begin(&config, &auth);
  Firebase.setDoubleDigits(5);

  config.timeout.serverResponse = 10 * 1000;  // Set server response timeout to 10 seconds
  
  pinMode(IN1, OUTPUT);
  pinMode(IN2, OUTPUT);
  pinMode(IN3, OUTPUT);
  pinMode(IN4, OUTPUT);
  pinMode(ENA, OUTPUT);
  pinMode(ENB, OUTPUT);
}

void loop() {
  if (Firebase.ready() && (millis() - sendDataPrevMillis > 1000 || sendDataPrevMillis == 0)) {
    sendDataPrevMillis = millis();

    // Fetch table number from Firebase only if not already moving
    if (!moving && Firebase.RTDB.getInt(&fbdo, F("/number/table"))) {
      targetTable = fbdo.intData();
      Serial.print("Table number fetched from Firebase: ");
      Serial.println(targetTable);

      // Check if the target table has changed
      if (targetTable > 0 && targetTable != currentTable) {
        lastTable = targetTable; // Update last table number
        determineDirectionAndStart(targetTable); // Start movement in the right direction
      }
    } else if (!moving) {
      Serial.print("Failed to get number from Firebase: ");
      Serial.println(fbdo.errorReason());
    }
  }

  // Update movement based on travel time
  updateMovement();
}

void determineDirectionAndStart(int target) {
  int travelTime = travelTimePerTable * abs(target - currentTable); // Calculate travel time to reach the table
  travelStartTime = millis();
  moving = true;

  if (target > currentTable) {
    movingForward = true;
    moveForward();
    Serial.println("Moving forward to target table...");
  } else {
    movingForward = false;
    moveBackward();
    Serial.println("Moving backward to target table...");
  }
}

void updateMovement() {
  if (moving) {
    int travelTime = travelTimePerTable * abs(targetTable - currentTable);

    // Stop the car if travel time is reached
    if (millis() - travelStartTime >= travelTime) {
      stopMotors();
      Serial.println("Arrived at target table.");

      // Update current table to the target table reached
      currentTable = targetTable;
      moving = false;
      targetTable = -1; // Reset target table to avoid re-triggering movement
    }
  }
}

void moveForward() { 
  digitalWrite(IN1, HIGH);
  digitalWrite(IN2, LOW);
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, HIGH);
  analogWrite(ENA, speed);
  analogWrite(ENB, speed);
}

void moveBackward() {
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, HIGH);
  digitalWrite(IN3, HIGH);
  digitalWrite(IN4, LOW);
  analogWrite(ENA, speed);
  analogWrite(ENB, speed);
}

void stopMotors() {
  digitalWrite(IN1, LOW);
  digitalWrite(IN2, LOW);
  digitalWrite(IN3, LOW);
  digitalWrite(IN4, LOW);
  analogWrite(ENA, 0);
  analogWrite(ENB, 0);
}
