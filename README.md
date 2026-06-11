# A Simple Team Management System

## Overview
A Simple Team Management System is a JavaFX desktop application for managing rugby-league style teams, managers, and players. The application starts with a manager login screen, validates the manager ID against seeded data, opens a manager dashboard, allows the manager to view and switch teams, and provides a team dashboard for signing or unsigning players and assigning selected players into active-team slots.

## Main Features
- Manager login by numeric manager ID.
- Invalid login handling through a separate error dialog.
- Manager dashboard showing the current team name and jersey image.
- Team switching through a modal swap window.
- Team dashboard with a player table and active-team visual slots.
- Ability to sign a new player into the roster.
- Ability to unsign a selected player from the roster.
- Ability to assign a selected player to an active-team slot by clicking an icon slot.
- Seeded league data containing teams, managers, players, positions, and initial team-manager assignments.
- FXML-based JavaFX views separated from Java controller logic.
- CSS styling and image resources for a consistent interface.

## How to Run
1. Open Eclipse.
2. Choose `File > Import > Existing Projects into Workspace`.
3. Select the project folder.
4. Make sure the project uses a Java 8 JDK that includes JavaFX.
5. Run `ManagerPanelApp.java` as a Java application.

## Important Implementation Notes
- The project is a JavaFX desktop application, not a web application.
- The app uses in-memory seed data only. It does not use a database or file persistence.
- `League` is implemented as a singleton and can only be initialized once.
- `LoginController` uses the real seeded model through `League`.
- `ManagerDashboardController` currently uses a hard-coded current team and hard-coded jersey mapping for visible dashboard behaviour.
- `TeamDashboardController` currently uses an inner `Player` class and hard-coded sample rosters for the visible table, rather than directly using `model.application.Player` and seeded team objects.
- The `withdraw()` method in `ManagerDashboardController` is currently empty.
- The JAR manifest does not define a `Main-Class`, so the JAR is not directly executable with `java -jar` without rebuilding the manifest.
