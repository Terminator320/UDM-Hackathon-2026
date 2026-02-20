import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Niveau4et5
 * ------------
 * This class solves a path optimization problem similar to the
 * Traveling Salesman Problem (TSP).
 *
 * The goal:
 * - Start at the hive (point 0)
 * - Visit all points exactly once
 * - Return to the hive
 * - Minimize the total distance traveled
 *
 * The solution uses:
 * 1) Nearest Neighbor heuristic
 * 2) 2-Opt optimization to improve the route
 */
public class Niveau4et5 {

    // List of all points (coordinates read from the file)
    private static List<Point> points = new ArrayList<>();

    // Distance matrix: dist[i][j] = distance between point i and point j
    private static double[][] dist;

    // Number of points
    private static int n = 0;

    // Best (minimum) distance found so far
    private static double minDistance = Double.MAX_VALUE;

    // Indices of the optimal path
    private static List<Integer> optimalPath = new ArrayList<>();

    /*
     * Inner class representing a point in 2D space
     */
    private static final class Point {
        final double x;
        final double y;

        // Constructor for Point
        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    /*
     * Reads points from a file.
     * Each line must be in the form:
     * x, y
     *
     * Example:
     * 2.5, 4.0
     */
    private static List<Point> readPoints(String filename) throws IOException {
        List<Point> result = new ArrayList<>();

        // Read all lines from the file
        for (String line : Files.readAllLines(Paths.get(filename))) {
            String trimmed = line.trim();

            // Skip empty lines
            if (trimmed.isEmpty()) continue;

            // Split the line by comma
            String[] parts = trimmed.split(",");

            // Convert strings to doubles
            double x = Double.parseDouble(parts[0].trim());
            double y = Double.parseDouble(parts[1].trim());

            // Create a new Point and add it to the list
            result.add(new Point(x, y));
        }

        return result;
    }

    /*
     * Builds the distance matrix using Euclidean distance.
     *
     * dist[i][j] = distance between point i and point j
     */
    private static double[][] distanceMatrix(List<Point> pts) {
        int size = pts.size();
        double[][] matrix = new double[size][size];

        for (int i = 0; i < size; i++) {
            Point a = pts.get(i);

            for (int j = 0; j < size; j++) {
                Point b = pts.get(j);

                // Math.hypot computes sqrt((dx)^2 + (dy)^2)
                matrix[i][j] = Math.hypot(b.x - a.x, b.y - a.y);
            }
        }
        return matrix;
    }

    /*
     * Initializes the problem:
     * - Reads points from the file
     * - Stores number of points
     * - Computes the distance matrix
     */
    private static void initialize(String filename) throws IOException {
        points = readPoints(filename);
        n = points.size();
        dist = distanceMatrix(points);
        minDistance = Double.MAX_VALUE;
    }

    /*
     * Calculates the total length of a path.
     * The path is circular: last point returns to the first.
     */
    private static double calculateLength(List<Integer> parcours) {
        if (parcours == null || parcours.isEmpty()) return -1.0;

        double totalDistance = 0.0;
        int numPoints = parcours.size();

        for (int i = 0; i < numPoints; i++) {
            int u = parcours.get(i);
            int v = parcours.get((i + 1) % numPoints); // return to start
            totalDistance += dist[u][v];
        }
        return totalDistance;
    }

    /*
     * Main optimization method:
     * 1) Builds an initial route using Nearest Neighbor
     * 2) Improves it using the 2-Opt algorithm
     */
    private static List<Integer> adviseHoneybee() {

        // Step 1: Initial path using nearest neighbor heuristic
        List<Integer> path = nearestNeighborPath();

        // Step 2: Repeatedly improve the path using 2-Opt
        boolean improved = true;
        while (improved) {
            improved = twoOpt(path);
        }

        // Store best result
        minDistance = calculateLength(path);
        optimalPath = new ArrayList<>(path);

        return optimalPath;
    }

    /*
     * Nearest Neighbor algorithm:
     * - Start at the hive (index 0)
     * - Always go to the closest unvisited point
     */
    private static List<Integer> nearestNeighborPath() {
        List<Integer> path = new ArrayList<>();
        boolean[] visited = new boolean[n];

        int current = 0; // Start at hive
        path.add(current);
        visited[current] = true;

        for (int step = 1; step < n; step++) {
            int next = -1;
            double bestDist = Double.MAX_VALUE;

            // Find the nearest unvisited point
            for (int i = 0; i < n; i++) {
                if (!visited[i] && dist[current][i] < bestDist) {
                    bestDist = dist[current][i];
                    next = i;
                }
            }

            path.add(next);
            visited[next] = true;
            current = next;
        }

        return path;
    }

    /*
     * 2-Opt optimization:
     * - Tries to reduce the path length by reversing segments
     * - If swapping two edges reduces distance, apply the swap
     */
    private static boolean twoOpt(List<Integer> path) {
        boolean improved = false;
        int size = path.size();

        for (int i = 1; i < size - 2; i++) {
            for (int j = i + 1; j < size - 1; j++) {

                int a = path.get(i - 1);
                int b = path.get(i);
                int c = path.get(j);
                int d = path.get((j + 1) % size);

                double currentDist = dist[a][b] + dist[c][d];
                double newDist = dist[a][c] + dist[b][d];

                // If the swap shortens the route, reverse the segment
                if (newDist < currentDist) {
                    Collections.reverse(path.subList(i, j + 1));
                    improved = true;
                }
            }
        }

        return improved;
    }

    /*
     * Program entry point
     */
    public static void main(String[] args) throws IOException {

        // Load points and compute distances
        initialize("input_level_5.txt");

        // Compute best path
        List<Integer> bestIndices = adviseHoneybee();

        // Display results
        System.out.println("--- Honeybee Route Optimization ---");
        System.out.printf("%-10s | %-15s\n", "Index", "Coordinates");
        System.out.println("-----------------------------------");

        for (int index : bestIndices) {
            Point p = points.get(index);
            String label = (index == 0) ? " (Hive)" : "";
            System.out.printf("%-10d | (%-3.1f, %-3.1f)%s\n",
                    index, p.x, p.y, label);
        }

        // Show return to hive
        Point hive = points.get(bestIndices.get(0));
        System.out.printf("%-10d | (%-3.1f, %-3.1f) (Return to Hive)\n",
                bestIndices.get(0), hive.x, hive.y);

        System.out.println("-----------------------------------");
        System.out.printf("Total Optimal Distance (approx): %.4f\n", minDistance);

        System.out.println(optimalPath);
        System.out.println("distance " + minDistance);
    }
}
