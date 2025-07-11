# Deli's Bot 🤖🍽️  
**An Android App to Automate Food Serving and Ordering in Restaurants**

Deli's Bot is an innovative Android application designed to control an automated food-serving robot. It revolutionizes the dining experience by enabling contactless service and streamlining restaurant operations. The robot can autonomously serve food to a specific table based on its number (assuming linearly aligned tables) and can also be manually controlled when needed.

Alongside automation, the app also provides features for customers to order food from the restaurant directly via the app. Employers/staff can view all orders in real-time, identifying which food item has been ordered by which customer.

---

## 🚀 Features

- 🤖 **Autonomous Robot Control**: Send the robot to a specific table number for automatic serving.
- 📲 **Manual Robot Control**: Manually navigate the robot using direction controls.
- 🛵 **Multi-Directional Movement**: Robot can move forward, backward, left, and right.
- 🔄 **Speed Control**: Adjust the robot's movement speed.
- 🧾 **In-App Ordering**: Customers can browse and order food via the app.
- 👨‍🍳 **Order Tracking for Employers**: View which customer ordered which item.

---

## 📱 Tech Stack

- **Android (Java + XML)** – Frontend user interface and logic
- **Firebase** – Realtime database to store and retrieve orders and robot instructions
- **Arduino + NodeMCU** – Microcontroller for controlling the physical robot

---

## ⚙️ Arduino Integration

The Arduino-based robot is powered by a **NodeMCU ESP8266** module, enabling wireless communication with the Android app. The robot uses:

- **Motor Driver Module (L298N)** for controlling DC motors
- **Direction Commands**: `FORWARD`, `BACKWARD`, `LEFT`, `RIGHT`, `STOP`
- **Speed Control** via PWM


---

## ⚙️  Video Demo
  - Video Link - https://drive.google.com/file/d/1ZGmklxj2gQok77jmwPLNn69N6af9xO3q/view?usp=drive_link

## APK

You can find its apk that is in here.

