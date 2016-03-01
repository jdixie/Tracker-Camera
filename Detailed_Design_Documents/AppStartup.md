# App Startup Detailed Design Document

## 1. Overview

### Description
The App startup component is responsible for setting up the Bluetooth connection with the Raspberry Pi application when the Android app is initially launched. See [[android-app-system-architecture]] for high level design that this component follows.

### Developer
John Qualls

### Version 
1.0
 
## 2. Impacted Areas
* **Camera Fragment** - The camera fragment will be impacted since it is dependent upon the success of the Bluetooth connection.

* **Tracker-Camera** - The life cycle of the app is also impacted. See 3. Design for more details.

## 3. Design

## 3.1. View Component

## 3.2 Model Component

## 3.3 Controller Component

## 3.4 Exception Handling

## 4. Testing Strategy