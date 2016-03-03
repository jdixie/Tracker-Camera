# Recording and Storing Detailed Design Document
## 1. Overview
### Description
This component will be responsible for Retrieving and Playing videos. Videos must be saved to shared memory space for this component to function properly.

### Developer
Benjamin Boudra

### Version
1.0

## 2. Impacted Areas
**System Storage** - This component will Retrieve videos form shared memory space from either internal or external shared memory.

**The File System** - A file navigation fragment will be provided by the component so that the user may scroll through the relevant memory space to find the video they wish to play.

**Video Playback Fragment** - After an onTouchEvent() occurs within the file system fragment wherein the user selects the video that they wish to retrieve, a basic video playback fragment will be provided so that the user can start, stop and pause the video playback.

## 3. Design
### Component Flow


## 3.1 View Component

**fragment_file_system.xml** - An XML file will be needed to configure the view that will display the file system.

**fragment_video_playback.xml** - An XML file will specify the configuration of the video playback view.

## 3.2 Model Component
**File Directory** - The file directory will include the Environment.DIRECTORY_DCIM and the Environment.DIRECTORY_PICTURES Directory's so that the user will have access to any video which was recorded by the application that still exists on the device or SD card.

## 3.3 Controller Component
**FileSystem.java** - This class will provide an object that handles all the functionality used by the file system.
**Playback.java** - This class will provide basic functionality to the video recorder fragment/activity.

## 3.4 Life Cycle Callback Handling
### File System Fragment Lifecycle
**onCreate()** - Initializes the File System Fragment object.

**onStop()** - Returns to the Camera fragment.

### Video Playback Fragment Life Cycle
**onCreate()** Initializes the Playback Fragment object.

**onStop()** - Stops the playback and returns to the File System Fragment.

### FileNameDialog Life Cycle
## 3.5 Exception Handling
**Error Retrieving File** - If the file being retrieved is corrupted, The user is notified and the system will remain in the File System Fragment instead of loading the Video Playback Fragment.

**Video Playback Error** - If an error occurs during video playback, the user is notified and the system will return to the File System Fragment.

## 4. Testing Strategy
**Test Driven Development(TDD)** - TDD with JUnit, Mockito, and PowerMockito will be followed in order to provide high quality code and test coverage.

## 5. References
<ul>
**<li> <a href = http://developer.android.com/guide/components/fragments.html> Android Developers - API Guides - Fragments</a>.</li>**
**<li><a href = http://developer.android.com/training/basics/data-storage/index.html> Android Developers - Training - Saving Files </a></li>**
