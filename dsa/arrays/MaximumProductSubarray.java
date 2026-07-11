// Problem: Maximum Product Subarray - LeetCode #152
// Difficulty: Medium
// Topic: Arrays
// Approach: Track both max and min product at each step.
//           Min needed because negative x negative = positive.
//           Use temp variables to avoid overwriting before both are calculated.
// Time Complexity: O(n) | Space Complexity: O(1)

class MaximumProductSubarray {
    public int maxProduct(int[] nums) {
        int ans = nums[0];
        int currMax = nums[0];
        int currMin = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int tempMax = Math.max(nums[i], Math.max(currMax * nums[i], currMin * nums[i]));
            int tempMin = Math.min(nums[i], Math.min(currMax * nums[i], currMin * nums[i]));
            currMax = tempMax;
            currMin = tempMin;
            ans = Math.max(ans, currMax);
        }
        return ans;
    }
}
