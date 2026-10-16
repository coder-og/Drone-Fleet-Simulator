package application;

import java.io.*;
import java.util.*;

public class Logger implements AutoCloseable {
    private PrintWriter writer;

    public Logger() {
        try {
            writer = new PrintWriter(new FileWriter("positions.csv"));
            writer.println("DroneID,PosX,PosY,PosZ,VelX,VelY,VelZ,ThrustZ");
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create positions.csv", e);
        }
    }

    public void record(Drone d) {
        DroneState s = d.getState();
        writer.printf(Locale.ROOT, "%d,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f%n",
            d.getDroneID(), s.position.x, s.position.y, s.position.z,
            s.velocity.x, s.velocity.y, s.velocity.z, s.thrustZ);
    }

    @Override
    public void close() { writer.close(); }

    public void saveFinalMetrics(List<Drone> drones) {
        try (PrintWriter metricsWriter = new PrintWriter(new FileWriter("metrics.txt"))) {
            metricsWriter.println("Final Simulation Metrics");
            metricsWriter.println("Total Drones: " + drones.size());
            metricsWriter.println("Execution: Successful");
        } catch (IOException e) {
            System.err.println("Error saving metrics.txt.");
        }
        writer.close();
    }
}