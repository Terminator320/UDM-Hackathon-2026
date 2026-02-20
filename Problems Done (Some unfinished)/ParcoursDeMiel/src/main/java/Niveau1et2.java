import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Niveau1et2 {
    private static List<Point> points = new ArrayList<>();
    private static double[][] dist;
    private static int n = 0;

    // Tracking for the best path found
    private static double minDistance = Double.MAX_VALUE;
    private static List<Integer> optimalPath = new ArrayList<>();

    private static final class Point {
        final double x;
        final double y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    // This method reads a CSV file containing coordinates, converts each line into a Point, and returns a list of all points.
    // Empty lines are ignored, and strings are converted into doubles.
    private static List<Point> readPoints(String filename) throws IOException {
        List<Point> result = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get(filename))) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;
            String[] parts = trimmed.split(",");
            double x = Double.parseDouble(parts[0].trim());
            double y = Double.parseDouble(parts[1].trim());
            result.add(new Point(x, y));
        }
        return result;
    }

    // This method calculates the Euclidean distance between every pair of points and stores it in a 2D array.
    // This helps avoid recalculating distances repeatedly when checking different paths.
    private static double[][] distanceMatrix(List<Point> pts) {
        int size = pts.size();
        double[][] matrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            Point a = pts.get(i);
            for (int j = 0; j < size; j++) {
                Point b = pts.get(j);
                matrix[i][j] = Math.hypot(b.x - a.x, b.y - a.y);
            }
        }
        return matrix;
    }
    // This method initializes everything for a run: reads points from a file, stores the number of points,
    // builds the distance matrix,
    // and resets the minimum distance in case we run the algorithm multiple times
    private static void initialize(String filename) throws IOException {
        points = readPoints(filename);
        n = points.size();
        dist = distanceMatrix(points);
        minDistance = Double.MAX_VALUE; // Reset for new runs
    }

    //This method calculates the total distance of a path by summing the distances between consecutive points,
    // including returning to the hive to complete the loop.
    private static double calculateLength(List<Integer> parcours) {
        if (parcours == null || parcours.isEmpty()) return -1.0;
        double totalDistance = 0.0;
        int numPoints = parcours.size();
        for (int i = 0; i < numPoints; i++) {
            int u = parcours.get(i);
            // This modulo ensures we always add the distance from last node back to first node (the hive)
            int v = parcours.get((i + 1) % numPoints);
            totalDistance += dist[u][v];
        }
        return totalDistance;
    }

    // Brute force to find the absolute minimum path
    // “This method generates all possible paths starting from the hive and finds the one with the shortest total distance.
    // It returns the indices of points in the optimal order.”
    private static List<Integer> adviseHoneybee() {
        if (n == 0) return new ArrayList<>();

        List<Integer> currentPath = IntStream.range(0, n).boxed().collect(Collectors.toList());

        // Start permuting from index 1, keeping index 0 (the hive) fixed
        generatePermutations(currentPath, 1);

        return optimalPath;
    }

    //This is a recursive method that generates all possible orders of visiting points (except the hive, which stays first). It calculates the total distance for each path and updates the optimal path if a shorter one is found.
    // We swap elements and backtrack to explore all possibilities.
    private static void generatePermutations(List<Integer> path, int k) {
        if (k == path.size()) {
            double currentLen = calculateLength(path);
            if (currentLen < minDistance) {
                minDistance = currentLen;
                optimalPath = new ArrayList<>(path);
            }
        } else {
            for (int i = k; i < path.size(); i++) {
                Collections.swap(path, k, i);
                generatePermutations(path, k + 1);
                Collections.swap(path, k, i); // backtrack
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // 1. Load the points
        initialize("input_level_2.txt");

        // 2. Find the optimal route
        List<Integer> bestIndices = adviseHoneybee();

        // 3. Print the results clearly
        System.out.println("--- Honeybee Route Optimization ---");
        System.out.printf("%-10s | %-15s\n", "Index", "Coordinates");
        System.out.println("-----------------------------------");

        for (int index : bestIndices) {
            Point p = points.get(index);
            String label = (index == 0) ? " (Hive)" : "";
            System.out.printf("%-10d | (%-3.1f, %-3.1f)%s\n", index, p.x, p.y, label);
        }

        // 4. Explicitly show the return to the start to follow the "Loop" rule
        Point hive = points.get(bestIndices.get(0));
        System.out.printf("%-10d | (%-3.1f, %-3.1f) (Return to Hive)\n", bestIndices.get(0), hive.x, hive.y);

        System.out.println("-----------------------------------");
        System.out.printf("Total Optimal Distance: %.4f\n", minDistance);

        // 5. Additional output: list format + distance
        System.out.println(bestIndices);
        System.out.println("distance: " + minDistance);
    }
}