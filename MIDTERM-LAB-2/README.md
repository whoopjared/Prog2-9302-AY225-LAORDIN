# Programming Assignment 1 - 3x3 Matrix Determinant Solver

## Student Information
- Name: Jared Wackyn Laordin
- Section: BSIT-GD-1
- Course: Programming 2
- School: University of Perpetual Help System DALTA, Molino Campus
- Date Completed: April 8, 2026
- Repository: https://github.com/whoopjared/Prog2-9302-AY225-LAORDIN

## Assigned Matrix
The assigned 3x3 matrix is:

| 2 | 4 | 3 |
|---|---|---|
| 5 | 1 | 6 |
| 3 | 2 | 4 |

## Files Included
- DeterminantSolver.java
- determinant_solver.js
- README.md

## How to Run (Java)
From the MIDTERM-LAB-2 folder:

```bash
javac DeterminantSolver.java
java DeterminantSolver
```

## How to Run (JavaScript)
From the MIDTERM-LAB-2 folder:

```bash
node determinant_solver.js
```

## Sample Output (Java)
```text
=======================================================
  3x3 MATRIX DETERMINANT SOLVER
  Student: Jared Wackyn Laordin
  Assigned Matrix:
=======================================================
  |  2   4   3 |
  |  5   1   6 |
  |  3   2   4 |
=======================================================

Expanding along Row 1 (cofactor expansion):

  Step 1 - Minor M11: det([1,6],[2,4]) = (1*4) - (6*2) = 4 - 12 = -8
  Step 2 - Minor M12: det([5,6],[3,4]) = (5*4) - (6*3) = 20 - 18 = 2
  Step 3 - Minor M13: det([5,1],[3,2]) = (5*2) - (1*3) = 10 - 3 = 7

  Cofactor C11 = (+1) * 2 * -8 = -16
  Cofactor C12 = (-1) * 4 * 2 = -8
  Cofactor C13 = (+1) * 3 * 7 = 21

  det(M) = -16 + (-8) + 21

=======================================================
  DETERMINANT = -3
=======================================================
```

## Sample Output (JavaScript)
```text
=======================================================
  3x3 MATRIX DETERMINANT SOLVER
  Student: Jared Wackyn Laordin
  Assigned Matrix:
=======================================================
  |  2   4   3 |
  |  5   1   6 |
  |  3   2   4 |
=======================================================

Expanding along Row 1 (cofactor expansion):

  Step 1 - Minor M11: det([1,6],[2,4]) = (1*4) - (6*2) = 4 - 12 = -8
  Step 2 - Minor M12: det([5,6],[3,4]) = (5*4) - (6*3) = 20 - 18 = 2
  Step 3 - Minor M13: det([5,1],[3,2]) = (5*2) - (1*3) = 10 - 3 = 7

  Cofactor C11 = (+1) * 2 * -8 = -16
  Cofactor C12 = (-1) * 4 * 2 = -8
  Cofactor C13 = (+1) * 3 * 7 = 21

  det(M) = -16 + (-8) + 21

=======================================================
  DETERMINANT = -3
=======================================================
```

## Final Determinant Value
**det(M) = -3**
