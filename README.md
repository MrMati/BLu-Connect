# BLu Connect

BLu Connect is an application targeting an audience of developers who are new to 
Bluetooth Low Energy. 


Features:
1. [x] manual control of RGB LED and reading a sensor,
2. [x] configuring automatic behaviours which use sensor data for LED control.
3. [ ] bonding with the device for faster reconnections



### Dependencies

* [Android BLE Library](https://github.com/NordicSemiconductor/Android-BLE-Library/) - handles Bluetooth LE connectivity
* [Nordic Common Library for Android](https://github.com/NordicPlayground/Android-Common-Libraries) - theme and common UI components, e.g. scanner screen
* [nRF Logger tree for Timber](https://github.com/NordicSemiconductor/nRF-Logger-API) - logs to nRF Logger app using [Timber](https://github.com/JakeWharton/timber)
* [Android Gradle Plugins](https://github.com/NordicSemiconductor/Android-Gradle-Plugins) - set of Gradle plugins

The gradle script was written in Kotlin Script (*gradle.kts*) and is using version catalog for 
dependency management.

### Modules

The application consists of the following modules:

* **:app** - the main module, contains the application code
* **:scanner** - contains the scanner screen destination
* **:blu:spec** - contains the BLu device specification
* **:blu:ble** - contains the BLE related code
* **:blu:ui** - contains the UI related code

The **:blu:ui** and **:blu:spec** modules are transport agnostic. The Bluetooth LE transport
is set using Hilt `@Binds` dependency injection.

The app is based on **:navigation** module from the Nordic Common Library, which is using 
[`NavHost`](https://developer.android.com/jetpack/compose/navigation) under the hood, and adds 
type-safety to the navigation graph.

Each screen defines a `DestinationId` (with input and output types) and `NavigationDestination`, 
which declares the composable, inner navigation or a dialog target. 


Navigation between destinations is done using [`Navigator`](https://github.com/NordicPlayground/Android-Common-Libraries/blob/d8e60628a877eccf8592da4889cf12afdbc08e44/navigation/src/main/java/no/nordicsemi/android/common/navigation/Navigator.kt) object, 
available using Hilt form a `ViewModel`. When a new destination is selected, the input parameters 
are available from `SavedStateHandle`.

## BLu proprietary Control Service

Service UUID: `0000181C-0000-1000-8000-00805F9B34FB`

Packets sent in both directions are serialized using ProtoBuf library 

Characteristics:

- HOST_OUT
  - UUID: **`00002B31-0000-1000-8000-00805F9B34FB`**
  - Properties: **Write** or **Write Without Response**


- HOST_IN
  - UUID: **`00002B30-0000-1000-8000-00805F9B34FB`**
  - Properties: **Notify**
  

## Requirements

* This application depends on [Android BLE Library](https://github.com/NordicSemiconductor/Android-BLE-Library/).
* Android 4.3 or newer is required.
* A device from BLu family 

## Installation and usage

The device should appear on the scanner screen after granting required permissions.
You can connect and control its functions.

### Required permissions

On Android 6 - 11 BLu Connect will ask for Location Permission and Location services. 
This permission is required on Android in order to obtain Bluetooth LE scan results. The app does not
use location in any way and has no Internet permission so can be used safely.

This permission is not required from Android 12 onwards, where new 
[Bluetooth permissions](https://developer.android.com/guide/topics/connectivity/bluetooth/permissions)
were introduced. The `BLUETOOTH_SCAN` permission can now be requested with 
`usesPermissionFlags="neverForLocation"` parameter, which excludes location related data from the
scan results, making requesting location not needed anymore.