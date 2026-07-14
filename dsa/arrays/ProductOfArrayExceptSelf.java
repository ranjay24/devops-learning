// Problem: Product of Array Except Self - LeetCode #238
// Difficulty: Medium
// Topic: Arrays, Prefix/Suffix
// Approach: Step 1 — fill prefix products left to right.
//           Step 2 — multiply suffix products right to left.
//           No division used. O(1) extra space.
// Time Complexity: O(n) | Space Complexity: O(1)

class ProductOfArrayExceptSelf {
    public int[] productExceptSelf(int[] nums) {
        int[] prefix = new int[nums.length];
        prefix[0] = 1;
        for (int i = 1; i < nums.length; i++) {
            prefix[i] = prefix[i - 1] * nums[i - 1];
        }
        int suffix = 1;
        for (int i = nums.length - 1; i >= 0; i--) {
            prefix[i] *= suffix;
            suffix *= nums[i];
        }
        return prefix;
    }
}
