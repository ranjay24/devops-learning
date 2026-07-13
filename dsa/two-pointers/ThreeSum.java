// Problem: 3Sum - LeetCode #15
// Difficulty: Medium
// Topic: Two Pointers
// Approach: Sort array. Fix one element at index i. Use two pointers
//           low and high to find pairs summing to -nums[i].
//           Skip duplicates at i, low, and high to avoid repeated triplets.
// Time Complexity: O(n²) | Space Complexity: O(1)

import java.util.*;

class ThreeSum {
    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> list = new ArrayList<>();
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            int low = i + 1;
            int high = nums.length - 1;
            while (low < high) {
                int sum = nums[low] + nums[high];
                if (sum == -nums[i]) {
                    list.add(Arrays.asList(nums[i], nums[low], nums[high]));
                    while (low < high && nums[low] == nums[low + 1]) low++;
                    while (low < high && nums[high] == nums[high - 1]) high--;
                    low++;
                    high--;
                } else if (sum < -nums[i]) {
                    low++;
                } else {
                    high--;
                }
            }
        }
        return list;
    }
}
