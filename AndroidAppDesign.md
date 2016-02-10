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

### 1.4 Playing/Retrieving

### 1.5 Editing/Sharing

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
	<tr> <td>Trigger</td> <td></td> </tr>
</table>
