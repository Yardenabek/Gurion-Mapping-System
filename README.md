# GurionRock Pro Max Ultra Over 9000 - Perception and Mapping System

## Overview
This project is an implementation of a multi-threaded microservice framework to simulate the perception and mapping system of a vacuum robot, *GurionRock Pro Max Ultra Over 9000*. The system integrates sensor data from cameras and LiDAR to build an environmental map using Fusion-SLAM.

## Features
- **Java Concurrency and Synchronization**: Implements Java's concurrency principles, including multi-threading and synchronization.
- **Microservices Framework**: Utilizes a custom-built microservices architecture with a `MessageBus` for communication.
- **Sensor Data Processing**: Handles data from cameras and LiDAR, tracking detected objects and converting them into a global coordinate system.
- **Fusion-SLAM**: Integrates sensor data into an environmental map.
- **Configuration-driven Execution**: Reads settings from JSON configuration files.
- **Error Handling**: Implements a crash detection and error-reporting mechanism.

## System Components
### 1. **Microservices**
- **TimeService**: Manages system ticks and synchronizes components.
- **CameraService**: Detects objects and sends detection events.
- **LiDarWorkerService**: Processes object detections and converts them into spatial coordinates.
- **FusionSlamService**: Integrates detected objects into a global map.
- **PoseService**: Provides the robot's current position and orientation.

### 2. **Messages and Events**
- **DetectObjectsEvent**: Sent by `CameraService`, handled by `LiDarWorkerService`.
- **TrackedObjectsEvent**: Sent by `LiDarWorkerService`, handled by `FusionSlamService`.
- **PoseEvent**: Sent by `PoseService`, used by `FusionSlamService`.
- **TickBroadcast**: Sent by `TimeService` to synchronize processes.
- **Termination and Crash Broadcasts**: Ensure safe system shutdown upon errors.

### 3. **MessageBus Implementation**
The `MessageBusImpl` handles message passing between microservices:
- **Round-Robin Event Distribution**
- **Broadcast Messaging**
- **Future-based Event Handling**

## File Structure
```
├── src
│   ├── main/java/bgu/spl/mics/              # Microservices framework
│   ├── main/java/bgu/spl/mics/application/   # Main application logic
│   ├── main/java/bgu/spl/mics/application/objects/  # Data structures
│   ├── main/java/bgu/spl/mics/application/configuration/  # Config parsing
│   ├── test/java/bgu/spl/tests/             # Unit tests
├── resources/
│   ├── configuration_file.json               # System configuration
│   ├── camera_data.json                      # Camera detections
│   ├── lidar_data.json                       # LiDAR detections
│   ├── pose_data.json                        # Robot movement data
│   ├── output_file.json                      # Generated map and statistics
├── pom.xml                                   # Maven dependencies
├── README.md                                 # Project documentation
```

## Setup and Execution
### Prerequisites
- Java 8+
- Maven

### Running the Simulation
1. **Compile the project:**
   ```sh
   mvn clean install
   ```
2. **Run the application:**
   ```sh
   java -jar target/assignment2.jar configuration_file.json
   ```
3. **View Results:**
   - The processed results will be stored in `output_file.json`

## Configuration
The simulation is configured through `configuration_file.json`, specifying:
- Camera and LiDAR settings
- Simulation tick time and duration
- Input data files

## Testing
This project includes JUnit-based testing:
```sh
mvn test
```
The tests validate:
- `MessageBus` functionality (event dispatching, synchronization)
- Sensor data processing
- Fusion-SLAM transformations

## Error Handling
- If an error occurs (e.g., sensor failure), a `CrashedBroadcast` is sent.
- The system halts and generates an error report in `output_file.json`.

## Authors
- **Course:** SPL 225 - Concurrent Programming in Java
- **Instructor:** BGU Computer Science Department
- **Teaching Assistants:** Lotem Sakira, Ahmed Massalha

## License
This project is for educational purposes and is part of the SPL 225 course at BGU.

