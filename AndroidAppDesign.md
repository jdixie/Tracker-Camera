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