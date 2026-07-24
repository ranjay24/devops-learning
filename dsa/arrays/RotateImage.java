// LeetCode #48 - Rotate Image (Medium)
// Pattern: In-place matrix manipulation
//
// Problem: rotate an n x n matrix 90 degrees clockwise, IN PLACE
// (no extra O(n^2) matrix allowed).
//
// Key insight: rather than computing the rotated position of every
// element directly (messy index math, easy to get wrong), break it
// into two simpler, well-understood operations:
//
//   1. Transpose the matrix (flip across the main diagonal:
//      matrix[i][j] <-> matrix[j][i])
//   2. Reverse each row
//
// Transpose + reverse-each-row together produce the same result as a
// 90-degree clockwise rotation. Works for any n x n matrix.
//
// Example:
//   1 2 3        1 4 7        7 4 1
//   4 5 6   ->   2 5 8   ->   8 5 2
//   7 8 9        3 6 9        9 6 3
//   (start)     (transpose)  (reverse each row)
//
// Time:  O(n^2) - every element touched once in each pass
// Space: O(1) - all swaps happen directly on the input matrix

class Solution {
    public void rotate(int[][] matrix) {
        int n = matrix.length;

        // Step 1: transpose (swap across the diagonal)
        // j starts at i+1, NOT 0 - starting at 0 would swap every pair
        // TWICE (once as [i][j]/[j][i], again later as [j][i]/[i][j]),
        // which undoes the swap and leaves the matrix unchanged.
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }

        // Step 2: reverse each row (two-pointer reversal per row)
        for (int i = 0; i < n; i++) {
            int left = 0, right = n - 1;
            while (left < right) {
                int temp = matrix[i][left];
                matrix[i][left] = matrix[i][right];
                matrix[i][right] = temp;
                left++;
                right--;
            }
        }
    }
}
