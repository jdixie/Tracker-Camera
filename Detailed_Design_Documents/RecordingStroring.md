# Recording and Storing Detailed Design Document

## 1. Overview

### Description
This component is focused on recording and storing the video. Calibration must be finished before recording can initiate.

### Developer
John Qualls

### Version 
1.0
 
## 2. Impacted Areas
**System Storage** - This component will save the recorded video to the external or local system storage. See section 3.2 Model Component for more details.

**CameraFragment.java** - Code will be added in the record button listener to trigger the necessary CameraRecorder methods along with triggering the FileNameDialog fragment. onCreate() will also be modified to get the Camera resource, and setup the camera preview.

**TEMPORARY** - The onTouchEvent() method within GLCamView will trigger the necessary CameraRecorder methods to demo the component until the CameraFragment button listener method is implemented.

**GLCamView.java** - The camera preview will need to be handled here for future tracking calibration.

## 3. Design

### Component Flow
1. **User File Name Choice** - FileNameDialog.java will be used to allow the user to specify a name for the video file. 
2. **Filename Already Exists** - If the user chooses a filename that already exists, then give the user the choice to overwrite it, or press cancel to choose a different name.
3. **Camera Preview** - This will be handled by the [Tracking Component.](https://tree.taiga.io/project/alton09-tracker-camera/wiki/tracking)
4. **Record Button Press** - The flow will follow the configuration and playing steps within the [Google guide document.](http://developer.android.com/guide/topics/media/camera.html#capture-video)
5. **Stop Button Press** - The component will follow the stop and release resources steps specified in the [Google guide document.](http://developer.android.com/guide/topics/media/camera.html#capture-video) 

## 3.1 View Component
**fragment_file_name_dialog.xml** - An XML file will need to be included in the res folder in order to configure the view that will be displayed for the user file name choice flow. 

## 3.2 Model Component
**File Directory** - The recorded files will be added to the Environment.DIRECTORY_DCIM directory if an external SD card does not exist, otherwise, the Environment.DIRECTORY_PICTURES will be used. A sub directory named tracker_camera will be created to help separate other app videos.

## 3.3 Controller Component
**CameraRecorder.java** - This class will be used to provide an object that will handle all of the functions associated with the video camera.

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
* **Fragment Lifecycle** - [http://developer.android.com/guide/components/fragments.html#Lifecycle](http://developer.android.com/guide/components/fragments.html#Lifecycle)
* **DialogFragment Blog** - [http://android-developers.blogspot.com/2012/05/using-dialogfragments.html](http://android-developers.blogspot.com/2012/05/using-dialogfragments.html)
* **Google API Camera Guide** - [http://developer.android.com/guide/topics/media/camera.html#capture-video](http://developer.android.com/guide/topics/media/camera.html#capture-video)
* **Google API Saving Media Files Guide** - [http://developer.android.com/guide/topics/media/camera.html#saving-media](http://developer.android.com/guide/topics/media/camera.html#saving-media)
* **Google API Storage Options Guide** - [http://developer.android.com/guide/topics/data/data-storage.html](http://developer.android.com/guide/topics/data/data-storage.html)
    * "Saving files that can be shared with other apps" Header.