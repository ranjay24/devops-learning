// LeetCode #42 - Trapping Rain Water (Hard)
// Pattern: Two Pointers
//
// Problem: given an array of bar heights, compute total water trapped
// between the bars after rain.
//
// Core formula for water above any single bar i:
//   water_at_i = min(maxHeightLeftOf(i), maxHeightRightOf(i)) - height[i]
//
// Water can never be held higher than the SHORTER of the two walls on
// either side - the shorter wall is always the limiting factor, same as
// a valley between a tall wall and a short wall: water spills over the
// short side long before reaching the tall side's height.

class Solution {

    // ---------------------------------------------------------------
    // Approach 1: Precomputed left/right max arrays
    // Time:  O(n)
    // Space: O(n) - two extra arrays sized to the input
    //
    // For every index, precompute the tallest bar seen so far scanning
    // from the left, and separately from the right. Then apply the
    // water formula directly using those two arrays.
    // ---------------------------------------------------------------
    public int trapWithArrays(int[] height) {
        if (height.length == 0) return 0;

        int[] left = new int[height.length];
        int[] right = new int[height.length];

        int leftmax = height[0];
        for (int i = 0; i < height.length; i++) {
            left[i] = Math.max(leftmax, height[i]);
            if (height[i] > leftmax) {
                leftmax = height[i];
            }
        }

        int rightmax = height[height.length - 1];
        for (int i = height.length - 1; i >= 0; i--) {
            right[i] = Math.max(rightmax, height[i]);
            if (height[i] > rightmax) {
                rightmax = height[i];
            }
        }

        int totalWater = 0;
        for (int i = 0; i < height.length; i++) {
            totalWater += Math.min(left[i], right[i]) - height[i];
        }
        return totalWater;
    }

    // ---------------------------------------------------------------
    // Approach 2: Two Pointers (optimized)
    // Time:  O(n)
    // Space: O(1) - no extra arrays, just four integers
    //
    // Key insight: whichever pointer sits on the SHORTER of height[left]
    // vs height[right] is guaranteed to be the limiting wall for water
    // at that position - because the other side is already known to
    // have something at least as tall. So we only need leftMax/rightMax
    // (tallest seen so far on each side), never the exact max on the
    // opposite side.
    //
    // This is the version worth using in an interview - same time
    // complexity as the array version, but no extra space.
    // ---------------------------------------------------------------
    public int trap(int[] height) {
        if (height.length == 0) return 0;

        int left = 0, right = height.length - 1;
        int leftMax = 0, rightMax = 0;
        int totalWater = 0;

        while (left < right) {
            if (height[left] < height[right]) {
                // right side has something at least as tall as
                // height[right], which is already taller than
                // height[left] - so left is the limiting wall here
                leftMax = Math.max(leftMax, height[left]);
                totalWater += leftMax - height[left];
                left++;
            } else {
                // mirror case - right is the limiting wall
                rightMax = Math.max(rightMax, height[right]);
                totalWater += rightMax - height[right];
                right--;
            }
        }

        return totalWater;
    }
}
