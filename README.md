# 2245Project

SWEN-383 Project 2245

## 👥 Team 4 – Team4AndCo.LLC.INC

- Izzy Pedrizco-Carranza (ip7431)
- Derek Kasmark (djk2529)
- Colin Chomas (cwc6640)
- Adhel Siddique (as8758)
- Philip Snyder (pjs7207)

---

## 📌 Projects Overview

This repository contains two separate projects:

### 1. HealthNCare App – Version 1.0

A CLI-based application that allows users to:

- Log their food intake daily
- Add recipes made of other foods
- Track nutrition values
- Set calorie goals and weight
- View nutritional breakdowns

### 2. Weather Station

A Java application demonstrating:

- Temperature sensor implementation
- Barometer readings
- Adapter pattern for temperature conversion
- Real-time weather data processing

---

## 🧪 Project Structure

```
src/
└── main/
    └── java/
        ├── health_app/          # HealthNCare application
        │   ├── FoodItem.java
        │   ├── Recipe.java
        │   ├── DailyLog.java
        │   ├── LogEntry.java
        │   ├── UserProfile.java
        │   ├── LogManager.java
        │   ├── RecipeManager.java
        │   ├── UserAuthenticator.java
        │   ├── DataPersistence.java
        │   └── HealthAppCLI.java
        └── weather_station/     # Weather Station application
            ├── WeatherStation.java
            ├── ITempSensor.java
            ├── KelvinTempSensor.java
            ├── KelvinTempSensorAdapter.java
            ├── IBarometer.java
            ├── Barometer.java
            ├── Launcher.java
            └── Hello.java
```

---

## ▶️ How to Run the Projects

### HealthNCare App

The HealthNCare app can be run in two modes:

#### GUI Mode

```bash
# To compile
mvn clean compile

# To run the GUI version
mvn exec:java@health-app-gui
```

#### CLI Mode

```bash
# To run the command-line interface version
mvn exec:java@health-app-cli
```

### Weather Station

1. Compile and run the weather station:

```bash
# To compile
mvn clean compile

# To run
mvn exec:java@weather-station
```

Note: You only need to compile once unless you make changes to the source code.

---

## 🛠️ Design Patterns Used

### HealthNCare App

- **Composite Pattern** (for `Recipe`/`FoodItem`)
- **Model-View-Controller (MVC)** architecture
- **Separation of concerns** via subsystem interfaces

### Weather Station

- **Adapter Pattern** (for temperature conversion)
- **Interface-based Design** (for sensors)
- **Observer Pattern** (for weather updates)

---

## 📚 Documentation

- HealthNCare Design Document: `doc/SWEN-383-DesignDoc-Team4.pdf`
