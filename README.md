# Android Bluetooth Application

## Project Overview

This project demonstrates how to build an Android application using the Model-View-ViewModel (MVVM) architecture with Jetpack Compose for the UI, Dagger-Hilt for dependency injection, and Android's Bluetooth APIs for managing Bluetooth connections and data transmission.

## Key Features

- **Bluetooth Discovery:** Scan for nearby Bluetooth devices.
- **Bluetooth Management:** Enable/disable Bluetooth and make the device discoverable.
- **MVVM Architecture:** Organizes the codebase with a clear separation of concerns.
- **Jetpack Compose:** Modern declarative UI toolkit used to build the interface.
- **Dagger-Hilt:** Provides a standard way to incorporate Dagger dependency injection into an Android application.

## Getting Started

### Prerequisites

- Android Studio Arctic Fox | 2020.3.1 or newer
- Kotlin plugin compatible with your Android Studio version
- An Android device or emulator with Bluetooth capabilities

### Setup

1. **Clone the Repository**
   
   Clone this repository to your local machine using Android Studio or your preferred Git tools.

https://github.com/jackbelding1/android-bluetooth


2. **Open the Project in Android Studio**

Open Android Studio, select "Open an Existing Project," and navigate to the directory where you cloned the project.

3. **Sync Gradle**

Allow Android Studio to sync the project with Gradle files. This process might take a few moments.

4. **Configure an Emulator or Connect a Device**

Set up an Android emulator or connect a physical Android device to run the application. Ensure the device or emulator has Bluetooth capabilities.

### Running the Application

1. **Enable Bluetooth & Permissions**

When running the app for the first time, you will be prompted to enable Bluetooth and grant necessary permissions (e.g., location permissions for Bluetooth discovery).

2. **Discover Devices**

Use the app's interface to discover nearby Bluetooth devices. Make sure other devices are discoverable.

3. **Manage Connections**

The application allows enabling/disabling Bluetooth and making your device discoverable to others.

## Architecture & Libraries

- **MVVM Architecture:** Utilizes ViewModel components to separate UI logic from business logic, promoting a more maintainable and testable codebase.
- **Jetpack Compose:** Simplifies UI development with a modern, declarative approach.
- **Dagger-Hilt:** Simplifies dependency injection, automatically handling component lifecycles.
- **Android Bluetooth API:** Manages Bluetooth operations, including scanning for devices, connecting, and data transmission.




