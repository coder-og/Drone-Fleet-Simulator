# Drone Fleet Simulator

Java/JavaFX coursework simulation of a fleet travelling to a target, spraying for ten simulated seconds, and returning. It combines a mission state machine, simplified translational dynamics, neighbor-based cohesion, collision repulsion, and a 3D view.

## Run

Install **JDK 17** and **Maven 3.8+**, then run from this repository root:

```sh
mvn clean compile
mvn javafx:run
```

The entry point is `application.Main`, which initializes the fleet before launching `DroneGUI`. Do not launch `DroneGUI` directly. Maven downloads JavaFX; the GUI needs a desktop display and graphics support. Close the window to stop and flush the CSV log.

## Architecture

| Source | Responsibility |
|---|---|
| `Main.java` | GO → SPRAY → RETURN → DONE; directly adjusts horizontal velocity |
| `Simulator.java` | Loads config, finds neighbors, computes forces, advances physics and logs |
| `Controller.java` | Vertical thrust from position/velocity error and gravity compensation |
| `Drone.java`, `DroneState.java`, `Vector3D.java` | State, forces, vector operations and integration |
| `FormationManager.java` | Position/velocity consensus force |
| `CommunicationModule.java` | Range-limited neighbor selection with random packet loss |
| `CollisionAvoidance.java` | Repulsion within a minimum separation threshold |
| `Environment.java` | Position bounds and velocity clamping |
| `DroneGUI.java` | JavaFX view, mission timing and spray particles |
| `Logger.java` | Position, velocity and thrust CSV |

Forces are computed before drones are integrated. Velocity is updated before position (semi-implicit Euler). The GUI mission clock uses the configured physics timestep, not wall-clock time.

## Configuration

`config.txt` is read from the working directory.

| Key | Included value | Meaning |
|---|---:|---|
| `numDrones` | 5 | Fleet size |
| `timeStep` | 0.1 | Simulated seconds per step |
| `width`, `height` | 100 | Horizontal environment dimensions |
| `mass` | 1.2 | Mass per drone |
| `kd` | 0.1 | Linear drag coefficient |

The existing `kp` entry is **not read**. Controller gains, communication range (18), packet-loss probability (0.2), target location and spray duration are currently source constants.

## Output and verification

`positions.csv` is overwritten on each launch. It has `DroneID,PosX,PosY,PosZ,VelX,VelY,VelZ,ThrustZ`; rows are grouped by step and contain no timestamp. Save previous logs before rerunning. `saveFinalMetrics()` exists but is not called by the normal GUI path; it does not measure collisions or communication success.

Run the focused physics checks without opening a window:

```sh
mvn compile
javac -cp target/classes -d target/test-classes tests/CoreChecks.java
java -cp target/classes:target/test-classes application.CoreChecks
```

On Windows use `;` instead of `:` in the last classpath. CI compiles the GUI and runs these checks. They verify hover gravity compensation and a vector operation, not full mission stability.

## Model limits

- This is a teaching simulator, not a flight controller or validated UAV dynamics model.
- Horizontal mission motion is assigned by `Main`, rather than produced by tilt/attitude control.
- `Drone.integrate()` updates orientation using angular velocity but does not integrate angular acceleration from torque/inertia. Full rotational dynamics are not implemented.
- Random packet loss is unseeded. Failed neighbor discovery also removes a drone from collision-repulsion inputs; avoidance is not guaranteed.
- No measured accuracy, collision-free guarantee, or hardware performance is claimed.

The included project PDFs provide coursework context. A real GUI recording and measured mission results remain useful additions.
