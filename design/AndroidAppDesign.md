#Android App System Design

## Overview

### Version
1.0

### Devlopers
* John Qualls
* Ben Boudra
* Joshua Dixie

### Description
Android application that tracks a desired tennis player while communicating with a Rasberry Pi for device rotation.

## System Architecture
### Model View Controller
The system follows a Model View Controller (MVC) Design Architecture. The MVC architecture fits nicely with application development on the Android Platform. The following lists where Android components are relavent.
![MVC Diagram](https://media.taiga.io/attachments/1/d/e/7/a6c47173d603a9b6db394ebf0676e6e95d4c71ce2126a73b44b6a07f4870/MVCDiagram.png "MVC Diagram")


**Model**

* Video file storage.
* User preferences.
* XML containing application resources
	* strings.xml, dimen.xml, etc. 

**VIEW**

* Activities and Fragments.
* Android Views.
* XML describing the layout of the Views.

**Controller** 

* Activities and Fragments
	* Part of View and Controller component because they handle application life cycle events (Controller), and display the UI (View).
* Java classes that handle business logic of application.

##1. Use Cases
###1.1 App Startup
<table>
	<tr> <td>Use case name</td> <td>App Startup</td></tr>
	<tr> <td>Use case ID</td> <td>UC-1</td> </tr>
	<tr> <td>Superordinate use case(s)</td> <td>N/A</td> </tr>
	<tr> <td>Actor(S)(s)</td> <td>Tennis Coach, Tennis player, General User</td> </tr>
	<tr>
		<td>Brief description</td>
		<td>The system shall check to see if it is connected to the Rasberry Pi. If it is connected to the Raberry Pi, the system shall continue to the main menu. If not, the system shall present to the user an error menu with the sole option to exit the app.</td>
	</tr>
	<tr> <td>Trigger</td> <td>The user starts the app.</td> </tr>
	<tr>
		<td>Preconditions</td>
		<td>The User starts the app by pressing the app’s icon button.</td>
	</tr>
	<tr> <td>Post-conditions</td> <td>The user starts the app.</td> </tr>
	<tr>
		<td>Trigger</td>
		<td>
			<ol type="A">
				<li>The application should navigate to the main system screen or should have exited the application.</li>
			</ol>
		</td>
	</tr>
	<tr> <td>Flow of events</td>
		 <td>
			<ol type="1">
				<li>System detects that it is already paired to the stand.</li>
				<li>The System connects to the paired stand.</li>
				<li>Allow user to continue to the application home screen.</li>
			</ol>
		</td>
	</tr>
	<tr> <td>Alternative flows and exceptions</td> 
		<td>**Alternate Flow A - The system detects that it is not paired with any bluetooth stand:**
		<ol>
			<li>The system detects that it is not paired with any bluetooth stand.</li>
			<li>Throw no paired device exception and exit application.</li>
		</ol>
		**Alternate Flow B - The System fails to connect with the paired device:**
		<ol>
			<li>Application failed to initiate a connection with the paired stand.</li>
			<li>Throw failed connection exception and exit application.</li>
		</ol>
		</td>
	</tr>
	<tr> <td>Priority</td> <td>1</td> </tr>
	<tr> <td>Assumptions</td> <td>N/A</td> </tr>
	<tr> <td>Issues</td> <td>N/A</td> </tr>
</table>

### 1.2 Camera Fragment
<table> 
	<tr> <td>Use case name</td> <td>Camera Fragment</td></tr>
	<tr> <td>Use case ID</td> <td>UC-2</td> </tr>
	<tr> <td>Superordinate use case(s)</td> <td>N/A</td> </tr>
	<tr> <td>Actor(S)(s)</td> <td>The system or the user of the system.</td> </tr>
	<tr> 
		<td>Brief description</td> 
		<td>The user shall select the operation that they wish to perform.</td> 
	</tr>
		<td>Trigger</td> 
		<td>
			The application reaches the Camera Fragment.
		</td> 
	<tr> 
		<td>Preconditions</td> 
		<td>The application should have initiated a successful Bluetooth connection to the stand.</td> 
	</tr>
	<tr> <td>Post-conditions</td> <td>See post-condition of appropriate flow of event.</td> </tr>
	<tr> <td>Flow of events</td> 
		 <td>
			See use case 1.3.
		</td> 
	</tr>
	<tr> <td>Alternative flows and exceptions</td> 
		<td>**Alternate Flow A - UC-3 Recording/Storing**
		**Alternate Flow B - UC-5 Editing/Sharing**
		</td>
	</tr>
	<tr> <td>Priority</td> <td>1</td> </tr>
	<tr> <td>Assumptions</td> <td>N/A</td> </tr>
	<tr> <td>Issues</td> <td>N/A</td> </tr>
</table>

### 1.3 Recording/Storing

<table>
	<tr> <td>Use case name</td> <td>Recording/Storing Video</td></tr>
	<tr> <td>Use case ID</td> <td>UC-3</td> </tr>
	<tr> <td>Superordinate use case(s)</td> <td>N/A</td> </tr>
	<tr> <td>Actor(s)</td> <td>user, system, memory</td> </tr>
	<tr>
		<td>Brief description</td>
		<td>The phone will track and record the target that it calibrated to follow. While the video is recording, the video will continuously and automatically be saved to a default location and given a default title. Upon the completion of the recording, a menu will be presented to the user with the option to save the video to a different location and/or with a different title. </td>
	</tr>
	<tr>
		<td>Preconditions</td>
		<td>
		<ul style>
		<li>The camera must be functional.</li>
		<li>The camera must be calibrated to follow the object that the user wishes to track.</li>
		<li>The system is connected to the stand.</li>
		</ul>
	</tr>
	<tr> <td>Post-conditions</td> <td>
		The video has been recorded and saved or an error has occurred.
	</td> </tr>
	<tr>
		<td>Trigger</td>
		<td>
			The user begins recording the video.
		</td>
	</tr>
	<tr> <td>Flow of events</td>
		 <td>
			<ol>
				<li>The user records the video and stores that video in a default location using a default header.</li>
				<li>A menu appears prompting the user to either navigate to their desired location and/or change the name to their desired name or click save.</li>
				<li> The user clicks save</li>
				<li> The system displays a message informing the user that the save operation was successful.
		</ol>
		</td>
	</tr>
	<tr> <td>Alternative flows and exceptions</td> <td>
		<ol type="A">
			<li><u>The user wishes to save the video in a different location and/or with a different name.<u></li>
			<ol type= "1">
				<li> Upon completion of step 2, the user does one or more of the following:
					<ol type = "a">
						<li> The user enters the name they wish to give to the video.</li>
						<li> The user navigates to their desired location in the file storage 	system.</li>
					</ol>
				</li>
				<li> The user proceeds to <i>Flow of events: step 3.</i></li>
			</ol>
			<li> <u> The video fails to record<u></li>
			<ol type = "1">
				<li> A message is displayed to the user notifying them of the error.</li>
			</ol>
			<li> <u> The video fails to save <u></li>
			<ol type = "1">
				<li> At the end of the save operation the user is displayed a error message in place of the save successful message notifying them of the error. If applicable, the cause of the error is displayed along with the error message.
			</ol>
		</ol>
	</td> </tr>
	<tr><td>Priority</td> <td>1</td> </tr>
	<tr><td>Assumptions</td> <td>
		<ol type = "1">
			<li> The system was calibrated successfully to the target.</li>
			<li> The contrast between the target and the surrounding environment is sufficient for the camera to be capable of differentiating between them.
		</ol>
	</td> </tr>
	<tr><td>Issues</td> <td>N/A</td> </tr>
</table>
### 1.4 Playing/Retrieving Video

<table>
	<tr> <td>Use case name</td> <td>Playing/Retrieving Files</td></tr>
	<tr> <td>Use case ID</td> <td>UC-4</td> </tr>
	<tr> <td>Superordinate use case(s)</td> <td>N/A</td> </tr>
	<tr> <td>Actor(s)</td> <td>user, system, memory space </td></tr>
	<tr>
		<td>Brief description</td>
		<td>
			The system shall allow the user to navigate through its file system so that they may find video(s) that they wish to view. Once the videos are located, the user shall be able to retrieve those videos and view them from within the application.
		</td>
	</tr>
	<tr>
		<td>Preconditions</td>
		<td>
			<ul>
				<li> The application is currently displaying the main menu.</li>
			</ul>
		</td>
	</tr>
	<tr> <td>Post-conditions</td> <td>
	The user has just finished viewing the video they wished to watch and have returned to the main screen.
	</td> </tr>
	<tr>
		<td>Trigger</td>
		<td>
			The user enters the file system with the intention to watch a saved video.
		</td>
	</tr>
	<tr> <td>Flow of events</td>
		 <td>
		 	<ol type "1">
		 		<li>
					The user navigates through the file system until they find the video that they wish to watch.
				</li>
				<li> The user selects that video.</li>
				<li> The following simultaneously occurs:</li>
				<ol type = "a">
				<li> The video is loaded into main memory from storage.</li>
				<li> The video is displayed to the user. Initially it is paused and at the beginning of the video.</li>
				<li> Basic video controls appear at the bottom of the screen allowing the user to play, pause, or stop the video. </li>
			</ol>
				<li> The user watches the video.</li>
				<li> The user is returned to the file system. </li>
			</ol>
		</td>
	</tr>
	<tr> <td>Alternative flows and exceptions</td> <td>
	<ol type = "A">
		<li><u>The user decides they wish to stop watching the video before it is completed.</u></li>
		<ol type = "1">
			<li> At some point while the video is loaded, the user selects the stop option from the basic video controls.</li>
			<li> The user is presented with an option menu which asks them if the with to stop watching the video.
			<li> The user does one of the following:</li>
			<ol type = "a">
				<li> Selects the yes option and proceeds to <i> Flow of events: step 5.</i></li>
				<li> Selects the no option and continues returns to the video.</li>
			</ol>
		</ol>
		<li> <u> The video fails to load</u></li>
			<ol type = "1">
			<li> After the user selects the video it fails to load. </li>
			<li> The user is displayed a message informing them the the file was not able to load.</li>
			<li> The user selects "OK" and proceeds to <i> Flow of events, Step 5</i></li>
			</ol>
	</ol>
	</td> </tr>
	<tr><td>Priority</td> <td>1</td> </tr>
	<tr><td>Assumptions</td>
	<td>
	<ul>
		<li> The video the user wishes to watch exists and is stored in the file system.</li>
		<li> The video's contents are not corrupted</li>
	</ol>
	</td></tr>
	<tr><td>Issues</td> <td>N/A</td> </tr>
</table>
### 1.5 Editing/Sharing
TODO

### 1.6 Tracking Calibration
<table> 
	<tr> <td>Use case name</td> <td>Tracking Calibration</td></tr>
	<tr> <td>Use case ID</td> <td>UC-6</td> </tr>
	<tr> <td>Superordinate use case(s)</td> <td>N/A</td> </tr>
	<tr> <td>Actor(S)(s)</td> <td>General User</td> </tr>
	<tr> 
		<td>Brief description</td> 
		<td>The user will calibrate the color tracking to focus on a primary target. The camera will further calibrate during operation to enhance tracking accuracy.</td> 
	</tr>
	<tr> <td>Trigger</td> 
		<td>
		<ul type="A">
			<li>Pre-calibration option chosen from menu.</li>
			<li>Recording is started. In this case calibration will be determined from stored data, or live calibration will be attempted on the spot with visual feedback.</li>
		</ul>
		</td> 
	</tr>
	<tr> 
		<td>Preconditions</td> 
		<td>App is running.</td> 
	</tr>
	<tr> <td>Post-conditions</td> 
		<td>
		<ul type="A">
			<li>Calibration is set and stored.</li>
			<li>Recording is started.</li>
			<li>App actively tracks the subject.</li>
		</ul>
		</td>
	</tr>
	<tr> 
		<td>Trigger</td> 
		<td>
			<ul type="A">
				<li>Subject chosen.</li>
			</ul>
		</td> 
	</tr>
	<tr> <td>Flow of events</td> 
		 <td>
			<ol type="1">
				<li>Pre-calibration chosen from menu.</li>
				<li>User selects desired photo to retrieve a subject form.</li>
				<li>User taps the subject desired in the selected photo.</li>
				<li>Confirmation dialog is shown to confirm choice.</li>
				<li>Dialog to set chosen subject as default subject presented, user responds appropriately.</li>
				<li>Countdown to recording start initiated.</li>
				<li>App begins to actively track subject and recording is started. Visual confirmation presented to user.</li>
			</ol>
		</td> 
	</tr>
	<tr> <td>Alternative flow of events</td>
		<td>
			<ol type="1">
				<li>Recording is started with default subject.</li>
				<li>Countdown to recording start initiated.</li>
				<li>App begins to actively track subject and recording is started. Visual confirmation presented to user.</li>
			</ol>
		</td>
	</tr>
	<tr> <td>Alternative flow of events 2</td>
		<td>
			<ol type="1">
				<li>Recording is started with no default subject.</li>
				<li>Countdown to analysis initiated and visual cue given to user.</li>
				<li>App begins analyzing colors within the center ninth of the frame.</li>
				<li>A color or colors that are present for a specified time interval within that ninth are chosen as the subject to track.</li>
				<li>Countdown to recording start initiated.</li>
				<li>App begins to actively track subject and recording is started. Visual confirmation presented to user.</li>
			</ol>
		</td>
	</tr>
	<tr> <td>Exceptions</td> 
		<td>If a subject is not detected at the end of the countdown to recording start, the recording will not be started an error visual cue will be presented to the user.</td>
	</tr>
	<tr> <td>Trigger</td> <td>Subject not detected at record start. </td> </tr>
</table>
