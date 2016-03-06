# Retrieving Detailed Design Document
## 1. Overview
### Description
This component will be responsible for retrieving and playing videos. Videos must be saved to shared memory space for this component to function properly.

### Developer
Benjamin Boudra

### Version
1.0

## 2. Impacted Areas
**System Storage** - This component will retrieve videos from shared memory space within either internal or external shared memory.

**The File System Fragment** - A file navigation fragment will be provided by the component so that the user may search through the relevant memory space to find the video they wish to play.

## 3. Design
### 3.1 View Component

**fragment_file_system.xml** - An XML file will be needed to configure the view that will display the file system.

### 3.2 Model Component
**File Directory** - The file directory will include the Environment.DIRECTORY_DCIM and the Environment.DIRECTORY_PICTURES directories so that the user will have access to any video which was recorded by the application.

### 3.3 Controller Component
**FileSystem.java** - This class will provide an object that handles all the functionality used by the file system.

## 3.4 Life Cycle Callback Handling
### File System Fragment Lifecycle
**onCreate()** - Initializes the File System Fragment object.

**onStop()** - Returns to the Camera fragment.

## 3.5 Exception Handling
**Error Retrieving File** - If the file being retrieved is corrupted, the user is notified and the system will remain in the File System Fragment instead of creating the intent to view the video externally.

## 4. Testing Strategy
**Test Driven Development(TDD)** - TDD with JUnit, Mockito, and PowerMockito will be followed in order to provide high-quality code and test coverage.

## 5. References
<ul>
**<li> <a href = http://developer.android.com/guide/components/fragments.html> Android Developers - API Guides - Fragments</a>.</li>**
**<li><a href = http://developer.android.com/training/basics/data-storage/index.html> Android Developers - Training - Saving Files </a></li>**
**<li><a href =
</ul>
