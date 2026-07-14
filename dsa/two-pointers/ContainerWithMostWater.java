// Problem: Container With Most Water - LeetCode #11
// Difficulty: Medium
// Topic: Two Pointers
// Approach: Start with widest container. Move the shorter wall inward
//           each step — moving taller wall can never increase area.
//           Track maximum area seen.
// Time Complexity: O(n) | Space Complexity: O(1)

class ContainerWithMostWater {
    public int maxArea(int[] height) {
        int start = 0;
        int end = height.length - 1;
        int maxWater = 0;
        while (start < end) {
            int width = end - start;
            int length = Math.min(height[start], height[end]);
            int currWater = length * width;
            if (currWater > maxWater) {
                maxWater = currWater;
            }
            if (height[start] < height[end]) {
                start++;
            } else {
                end--;
            }
        }
        return maxWater;
    }
}
