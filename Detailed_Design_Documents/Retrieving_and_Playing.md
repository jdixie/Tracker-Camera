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

**fragment_video_playback.xml**

## 3.2 Model Component
**File Directory** - The file directory will include the Environment.DIRECTORY_DCIM and the Environment.DIRECTORY_PICTURS Directory's so that the user will have access to any video which was recorded by the application that still exists on the device or SD card.

## 3.3 Controller Component
**FileSystem.java** - This class will provide an object



**TEMPORARY: GLCamView.java** - As mentioned in section 2. Impacted Areas.

## 3.4 Life Cycle Callback Handling
### CameraFragment Life Cycle
**onCreate()** - Initializes the CameraRecorder object.

**onStop()** - If a video is recording, it is stopped.

### FileNameDialog Life Cycle
## 3.5 Exception Handling
**Error Saving File** - If there was an error saving the file to the designated output path, then return the user to the dialog with an error message, instructing them to try again or choose a different name.

**Error Recording File** - If there was an error recording the file, notify the user that there was an error recording, and return them back to the CameraFragment.

## 4. Testing Strategy
**Test Driven Development(TDD)** - TDD with JUnit, Mockito, and PowerMockito will be followed in order to provide high quality code and test coverage.

## 5. References
- **Fragment Lifecycle** - [http://developer.android.com/reference/android/app/DialogFragment.html#Lifecycle](http://developer.android.com/reference/android/app/DialogFragment.html#Lifecycle)
- **DialogFragment Blog** - [http://android-developers.blogspot.com/2012/05/using-dialogfragments.html](http://android-developers.blogspot.com/2012/05/using-dialogfragments.html)
- **Google API Camera Guide** - [http://developer.android.com/guide/topics/media/camera.html#capture-video](http://developer.android.com/guide/topics/media/camera.html#capture-video)
- **Google API Saving Media Files Guide** - [http://developer.android.com/guide/topics/media/camera.html#saving-media](http://developer.android.com/guide/topics/media/camera.html#saving-media)
- **Google API Storage Options Guide** - [http://developer.android.com/guide/topics/data/data-storage.html](http://developer.android.com/guide/topics/data/data-storage.html)
  - "Saving files that can be shared with other apps" Header.
