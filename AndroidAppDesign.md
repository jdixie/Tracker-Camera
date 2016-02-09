#Android App System Design

##Design Architecture
// Add Image

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
	<tr> <td>Alternative flows and exceptions</td> <td>The user starts the app.</td> </tr>
	<tr> <td>Trigger</td> <td>The user starts the app.</td> </tr>
</table>

### 1.2 Camera Fragment

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

### 1.6 Tracking Calibration
