/**
 * =====================================================
 * Student Name    : Jared Wackyn Laordin
 * Course          : Programming 2 — BSIT-GD-1
 * Assignment      : Programming Assignment 1 — 3x3 Matrix Determinant Solver
 * School          : University of Perpetual Help System DALTA, Molino Campus
 * Date            : April 8, 2026
 * GitHub Repo     : https://github.com/whoopjared/Prog2-9302-AY225-LAORDIN
 * Runtime         : Node.js (run with: node determinant_solver.js)
 *
 * Description:
 *   This script solves the determinant of a fixed 3x3 matrix using
 *   cofactor expansion along the first row and prints every computation step.
 * =====================================================
 */

// Assigned 3x3 matrix for this student.
const matrix = [
    [2, 4, 3],
    [5, 1, 6],
    [3, 2, 4]
];

// Computes a 2x2 determinant from values [a b; c d].
function computeMinor(a, b, c, d) {
    return (a * d) - (b * c);
}

// Prints the matrix in a clean, readable layout.
function printMatrix(m) {
    m.forEach((row) => {
        console.log(`  | ${row[0].toString().padStart(2)}  ${row[1].toString().padStart(2)}  ${row[2].toString().padStart(2)} |`);
    });
}

// Solves determinant with first-row cofactor expansion and prints each step.
function solveDeterminant(m) {
    const line = "=".repeat(55);

    console.log(line);
    console.log("  3x3 MATRIX DETERMINANT SOLVER");
    console.log("  Student: Jared Wackyn Laordin");
    console.log("  Assigned Matrix:");
    console.log(line);
    printMatrix(m);
    console.log(line);
    console.log();
    console.log("Expanding along Row 1 (cofactor expansion):");
    console.log();

    // Minor M11 from rows 2-3 and cols 2-3.
    const m11Ad = m[1][1] * m[2][2];
    const m11Bc = m[1][2] * m[2][1];
    const minor11 = computeMinor(m[1][1], m[1][2], m[2][1], m[2][2]);
    console.log(
        `  Step 1 - Minor M11: det([${m[1][1]},${m[1][2]}],[${m[2][1]},${m[2][2]}]) = (${m[1][1]}*${m[2][2]}) - (${m[1][2]}*${m[2][1]}) = ${m11Ad} - ${m11Bc} = ${minor11}`
    );

    // Minor M12 from rows 2-3 and cols 1 and 3.
    const m12Ad = m[1][0] * m[2][2];
    const m12Bc = m[1][2] * m[2][0];
    const minor12 = computeMinor(m[1][0], m[1][2], m[2][0], m[2][2]);
    console.log(
        `  Step 2 - Minor M12: det([${m[1][0]},${m[1][2]}],[${m[2][0]},${m[2][2]}]) = (${m[1][0]}*${m[2][2]}) - (${m[1][2]}*${m[2][0]}) = ${m12Ad} - ${m12Bc} = ${minor12}`
    );

    // Minor M13 from rows 2-3 and cols 1-2.
    const m13Ad = m[1][0] * m[2][1];
    const m13Bc = m[1][1] * m[2][0];
    const minor13 = computeMinor(m[1][0], m[1][1], m[2][0], m[2][1]);
    console.log(
        `  Step 3 - Minor M13: det([${m[1][0]},${m[1][1]}],[${m[2][0]},${m[2][1]}]) = (${m[1][0]}*${m[2][1]}) - (${m[1][1]}*${m[2][0]}) = ${m13Ad} - ${m13Bc} = ${minor13}`
    );

    // Apply signs (+, -, +) to build cofactor terms for row 1.
    const c11 = m[0][0] * minor11;
    const c12 = -m[0][1] * minor12;
    const c13 = m[0][2] * minor13;

    console.log();
    console.log(`  Cofactor C11 = (+1) * ${m[0][0]} * ${minor11} = ${c11}`);
    console.log(`  Cofactor C12 = (-1) * ${m[0][1]} * ${minor12} = ${c12}`);
    console.log(`  Cofactor C13 = (+1) * ${m[0][2]} * ${minor13} = ${c13}`);

    const det = c11 + c12 + c13;
    console.log();
    console.log(`  det(M) = ${c11} + (${c12}) + ${c13}`);
    console.log();
    console.log(line);
    console.log(`  DETERMINANT = ${det}`);
    if (det === 0) {
        console.log("  The matrix is SINGULAR — it has no inverse.");
    }
    console.log(line);
}

solveDeterminant(matrix);
