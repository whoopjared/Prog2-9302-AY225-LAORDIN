/**
 * =====================================================
 * Student Name    : Jared Wackyn Laordin
 * Course          : Programming 2 — BSIT-GD-1
 * Assignment      : Programming Assignment 1 — 3x3 Matrix Determinant Solver
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : April 8, 2026
 * GitHub Repo     : https://github.com/whoopjared/Prog2-9302-AY225-LAORDIN
 *
 * Description:
 *   This program solves the determinant of a fixed 3x3 matrix using
 *   cofactor expansion along the first row and prints every computation step.
 * =====================================================
 */
public class DeterminantSolver {

    // Assigned 3x3 matrix for this student.
    private static final int[][] MATRIX = {
        {2, 4, 3},
        {5, 1, 6},
        {3, 2, 4}
    };

    // Computes a 2x2 determinant from values [a b; c d].
    private static int computeMinor(int a, int b, int c, int d) {
        return (a * d) - (b * c);
    }

    // Prints the matrix in a clean, readable layout.
    private static void printMatrix(int[][] m) {
        for (int[] row : m) {
            System.out.printf("  | %2d  %2d  %2d |%n", row[0], row[1], row[2]);
        }
    }

    // Solves determinant with first-row cofactor expansion and prints each step.
    private static void solveDeterminant(int[][] m) {
        String line = "=".repeat(55);

        System.out.println(line);
        System.out.println("  3x3 MATRIX DETERMINANT SOLVER");
        System.out.println("  Student: Jared Wackyn Laordin");
        System.out.println("  Assigned Matrix:");
        System.out.println(line);
        printMatrix(m);
        System.out.println(line);
        System.out.println();
        System.out.println("Expanding along Row 1 (cofactor expansion):");
        System.out.println();

        // Minor M11 from rows 2-3 and cols 2-3.
        int m11Ad = m[1][1] * m[2][2];
        int m11Bc = m[1][2] * m[2][1];
        int minor11 = computeMinor(m[1][1], m[1][2], m[2][1], m[2][2]);
        System.out.printf("  Step 1 - Minor M11: det([%d,%d],[%d,%d]) = (%d*%d) - (%d*%d) = %d - %d = %d%n",
            m[1][1], m[1][2], m[2][1], m[2][2],
            m[1][1], m[2][2], m[1][2], m[2][1], m11Ad, m11Bc, minor11);

        // Minor M12 from rows 2-3 and cols 1 and 3.
        int m12Ad = m[1][0] * m[2][2];
        int m12Bc = m[1][2] * m[2][0];
        int minor12 = computeMinor(m[1][0], m[1][2], m[2][0], m[2][2]);
        System.out.printf("  Step 2 - Minor M12: det([%d,%d],[%d,%d]) = (%d*%d) - (%d*%d) = %d - %d = %d%n",
            m[1][0], m[1][2], m[2][0], m[2][2],
            m[1][0], m[2][2], m[1][2], m[2][0], m12Ad, m12Bc, minor12);

        // Minor M13 from rows 2-3 and cols 1-2.
        int m13Ad = m[1][0] * m[2][1];
        int m13Bc = m[1][1] * m[2][0];
        int minor13 = computeMinor(m[1][0], m[1][1], m[2][0], m[2][1]);
        System.out.printf("  Step 3 - Minor M13: det([%d,%d],[%d,%d]) = (%d*%d) - (%d*%d) = %d - %d = %d%n",
            m[1][0], m[1][1], m[2][0], m[2][1],
            m[1][0], m[2][1], m[1][1], m[2][0], m13Ad, m13Bc, minor13);

        // Apply signs (+, -, +) to build cofactor terms for row 1.
        int c11 = m[0][0] * minor11;
        int c12 = -m[0][1] * minor12;
        int c13 = m[0][2] * minor13;

        System.out.println();
        System.out.printf("  Cofactor C11 = (+1) * %d * %d = %d%n", m[0][0], minor11, c11);
        System.out.printf("  Cofactor C12 = (-1) * %d * %d = %d%n", m[0][1], minor12, c12);
        System.out.printf("  Cofactor C13 = (+1) * %d * %d = %d%n", m[0][2], minor13, c13);

        int det = c11 + c12 + c13;
        System.out.printf("%n  det(M) = %d + (%d) + %d%n", c11, c12, c13);
        System.out.println();
        System.out.println(line);
        System.out.printf("  DETERMINANT = %d%n", det);
        if (det == 0) {
            System.out.println("  The matrix is SINGULAR — it has no inverse.");
        }
        System.out.println(line);
    }

    public static void main(String[] args) {
        solveDeterminant(MATRIX);
    }
}
