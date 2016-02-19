# Tracker-Camera Process

## Overview
This page explains the Tracker-Camera Software Development Process. Our process follows Scrum, such that there are always multiple user story cards iterating through the different phases. Currently, 1 week sprints are followed in order for our team to stay on track with the client's requirements.

## Phases
// INSERT DIAGRAM HERE///
### Requirements
* **User Story Cards** - Any new functionality that needs to be added to our system must be confirmed with all developers before it can be added as a new user story card within the Taiga backlog.

* **Tasks** - Once a developer is assigned to a user story card, they must add a task for that card within the appropriate sprint task board. Each task must represent what the developer is currently working on for their card.

### Design
* **Detailed Design Document** - When a developer is assigned to a user story card, they must write a detailed design document that describes how they are going to implement the user story into the system.
	* *Multiple Developers* - If multiple developers are assigned to one user story card, then they must work in collaboration to write the design document. 

### Design Review
* After a developer has completed a design document for their user story card, they must schedule a design review in person or via video call with at least one other developer.
	* *Failed Review* - If the developer needs to make significant changes to their document, then they must go back to the design phase.
	* *Successful Review* - If the reviewer is satisfied with the design document, then the task can move to the implementation phase.

### Implementation

* **Test Driven Development (TDD)** - TDD is not required, but it is strongly recommended for developers to practice in order to prevent bugs, and code regressions.

* **Code Coverage** - At least 80% unit test coverage must be achieved by the developer before they can move on to the testing phase. If TDD is followed, developer will easily meet this coverage.

### Code Review
* Once the Developer is finished implementing, they must have at least one other developer review the code. The reviewing developer can review the code on there own time and report back when convenient.
	* *Failed Review* - If the code needs changes, then the task is moved back into the implementation phase.
	* *Successful Review* If the code matches the functionality described in the design document, and there are no issues, then the task can move to the QA phase.

### Quality Assurance
* Another developer must be assigned to make at least 5 system tests for the task. The tests must be written out within the Testing Plan section in the corresponding task's user story detailed design document.
	* *Failed Tests* - The tester must write out what tests failed within the Testing Plan Section, and move the task back into the implementation phase.
	* *Successful Tests* - If the code had no bugs after testing, then the task is ready for production.

 