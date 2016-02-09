#Android App System Design

## Overview

### Description
TODO

### Version
1.0

### Devlopers
* John Qualls
* Ben Boudra
* Joshua Dixie


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

### 1.4 Playing/Retrieving

### 1.5 Editing/Sharing
<table> 
	<tr> <td>Use case name</td> <td>Editing/Sharing</td></tr>
	<tr> <td>Use case ID</td> <td>UC-5</td> </tr>
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
		<td>**Alternate Flow A - See use case 1.**
		**Alternate Flow B - See use case 1.5**
		</td>
	</tr>
	<tr> <td>Priority</td> <td>1</td> </tr>
	<tr> <td>Assumptions</td> <td>N/A</td> </tr>
	<tr> <td>Issues</td> <td>N/A</td> </tr>
</table>

### 1.6 Tracking Calibration