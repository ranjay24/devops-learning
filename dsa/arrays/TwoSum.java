// Problem: Two Sum - LeetCode #1
// Difficulty: Easy
// Topic: Arrays, HashMap
// Approach: For each number check if complement (target - num)
//           exists in HashMap. Store number and index as we go.
// Time Complexity: O(n) | Space Complexity: O(n)

import java.util.HashMap;

class TwoSum {
    public int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> hash = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int remaining = target - nums[i];
            if (hash.containsKey(remaining)) {
                return new int[] { hash.get(remaining), i };
            }
            hash.put(nums[i], i);
        }
        return new int[] {};
    }

    public static void main(String[] args) {
        TwoSum solution = new TwoSum();
        int[] nums = { 2, 7, 11, 15 };
        int target = 9;
        int[] result = solution.twoSum(nums, target);
        System.out.println("Indices: " + result[0] + ", " + result[1]);
    }
}