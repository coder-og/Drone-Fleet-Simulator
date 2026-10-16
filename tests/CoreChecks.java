package application;
public class CoreChecks {
    private static void near(double actual, double expected) {
        if (Math.abs(actual - expected) > 1e-9) throw new AssertionError(actual + " != " + expected);
    }
    public static void main(String[] args) {
        Drone drone = new Drone(0, 1.2, 0.1, new Vector3D(0, 0, 10));
        drone.getController().setTargetPosition(new Vector3D(0, 0, 10));
        drone.getController().computeControl(drone);
        near(drone.getState().thrustZ, 1.2 * 9.81);
        drone.integrate(0.1);
        near(drone.getState().position.z, 10);
        near(drone.getState().velocity.z, 0);
        Vector3D cross = new Vector3D(1,0,0).cross(new Vector3D(0,1,0));
        near(cross.z, 1);
        System.out.println("PASS: hover balance and vector cross product");
    }
}
