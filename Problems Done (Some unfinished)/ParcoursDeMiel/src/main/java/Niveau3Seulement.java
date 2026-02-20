import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Niveau3Seulement
 *
 * This program solves the Traveling Salesman Problem (TSP)
 * using the Held–Karp dynamic programming algorithm.
 *
 * Context:
 * - Each point is a flower
 * - Point 0 is the hive (start and end)
 * - The bee must visit every flower exactly once and return to the hive
 * - The goal is to minimize the total distance
 */
public class Niveau3Seulement {

    // List of all points (flowers + hive)
    private static List<Point> points;

    // Distance matrix: dist[i][j] = distance from point i to point j
    private static double[][] dist;

    // Number of points
    private static int n;

    // Best total distance found
    private static double minDistance;

    // Optimal visiting order (indices of points)
    private static List<Integer> optimalPath;

    /**
     * Point represents a 2D coordinate (x, y)
     * This class is immutable (values cannot change).
     */
    private static final class Point {
        final double x, y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Reads points from a file.
     * Each line must be: x,y
     *
     * Example:
     * 0,0
     * 3,4
     * 5,1
     */
    private static List<Point> readPoints(String filename) throws IOException {
        List<Point> result = new ArrayList<>();

        for (String line : Files.readAllLines(Paths.get(filename))) {
            if (line.trim().isEmpty()) continue;

            String[] p = line.split(",");
            result.add(new Point(
                    Double.parseDouble(p[0].trim()),
                    Double.parseDouble(p[1].trim())
            ));
        }
        return result;
    }

    /**
     * Initializes:
     * - the list of points
     * - the number of points
     * - the distance matrix
     */
    private static void initialize(String filename) throws IOException {
        points = readPoints(filename);
        n = points.size();

        dist = new double[n][n];

        // Compute Euclidean distance between every pair of points
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = Math.hypot(
                        points.get(i).x - points.get(j).x,
                        points.get(i).y - points.get(j).y
                );
            }
        }
    }

    // =======================
    // HELD–KARP (OPTIMAL TSP)
    // =======================

    /**
     * Solves the Traveling Salesman Problem optimally
     * using dynamic programming (Held–Karp algorithm).
     *
     * Time complexity: O(n^2 * 2^n)
     * Space complexity: O(n * 2^n)
     */
    private static void solveTSP() {

        int N = n;
        int SIZE = 1 << N; // 2^N subsets

        // dp[mask][i] = minimum cost to:
        // - start at 0 (hive)
        // - visit all points in 'mask'
        // - end at point i
        double[][] dp = new double[SIZE][N];

        // parent[mask][i] = previous point before i in the optimal path
        int[][] parent = new int[SIZE][N];

        // Initialize all distances to infinity
        for (double[] row : dp) {
            Arrays.fill(row, Double.MAX_VALUE);
        }

        // Base case:
        // mask = 1 (binary 000...001)
        // only point 0 (hive) is visited
        dp[1][0] = 0;

        // Iterate over all subsets of points
        for (int mask = 1; mask < SIZE; mask++) {

            // We must always include the hive (bit 0)
            if ((mask & 1) == 0) continue;

            // Try ending the path at point u
            for (int u = 0; u < N; u++) {
                if ((mask & (1 << u)) == 0) continue;

                int prev = mask ^ (1 << u); // remove u from mask

                // Skip invalid states
                if (prev == 0 && u == 0) continue;

                // Try coming to u from another point v
                for (int v = 0; v < N; v++) {
                    if ((prev & (1 << v)) == 0) continue;

                    double val = dp[prev][v] + dist[v][u];

                    if (val < dp[mask][u]) {
                        dp[mask][u] = val;
                        parent[mask][u] = v;
                    }
                }
            }
        }

        // Final step: return to hive (point 0)
        int full = SIZE - 1;
        minDistance = Double.MAX_VALUE;
        int last = -1;

        // Try every possible last point before returning to hive
        for (int i = 1; i < N; i++) {
            double val = dp[full][i] + dist[i][0];
            if (val < minDistance) {
                minDistance = val;
                last = i;
            }
        }

        // ===================
        // PATH RECONSTRUCTION
        // ===================

        LinkedList<Integer> path = new LinkedList<>();
        int mask = full;
        int cur = last;

        // Rebuild the path backwards using the parent array
        while (cur != 0) {
            path.addFirst(cur);
            int p = parent[mask][cur];
            mask ^= (1 << cur);
            cur = p;
        }

        // Add the hive at the start
        path.addFirst(0);

        optimalPath = path;
    }

    /**
     * Program entry point
     */
    public static void main(String[] args) throws IOException {

        // Load points and compute distances
        initialize("input_level_3.txt");

        // Solve the TSP optimally
        solveTSP();

        // Display results
        System.out.println("--- OPTIMAL HONEYBEE ROUTE ---");
        for (int i : optimalPath) {
            Point p = points.get(i);
            System.out.printf("%d -> (%.1f, %.1f)%n", i, p.x, p.y);
        }

        System.out.println("Return to hive");
        System.out.printf("TOTAL OPTIMAL DISTANCE = %.4f%n", minDistance);

        System.out.println(optimalPath);
        System.out.println("distance " + minDistance);

    }
}
