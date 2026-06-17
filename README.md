# A Simple Team Management System

## Overview

A JavaFX 8 desktop application for managing rugby-league teams, managers,
players, and active-team selections. Data is seeded in memory when the
application starts.

## Features

- Manager login using seeded manager IDs.
- Manager team withdrawal and switching to an available team.
- Team dashboard backed by the shared `League`, `Team`, and `Player` models.
- Player signing and unsigning with position selection.
- Assignment of available players to five active-team slots.
- FXML views, Java controllers, CSS, and bundled image resources.

## Run In VS Code

The repository includes `.vscode/settings.json` and `.vscode/launch.json` for
the installed Java 8 JDK at:

```text
C:\Program Files\Java\jdk1.8.0_351
```

1. Install the VS Code Extension Pack for Java.
2. Open this repository folder in VS Code.
3. Run `Java: Clean Java Language Server Workspace` from the command palette
   after the first import.
4. Select **Run ManagerPanelApp** from the Run and Debug panel.

If JDK 8 is installed elsewhere, update the `path` value in
`.vscode/settings.json`.

## Seeded Login IDs

```text
12345
1
34896
678
912
```
