// Problem: Contains Duplicate - LeetCode #217
// Difficulty: Easy
// Topic: Arrays, HashSet
// Approach: Add each number to HashSet. If number already exists
//           in set, duplicate found. Return false if set never rejects.
// Time Complexity: O(n) | Space Complexity: O(n)

import java.util.HashSet;

class ContainsDuplicate {
    public boolean containsDuplicate(int[] nums) {
        HashSet<Integer> seen = new HashSet<>();
        for (int num : nums) {
            if (seen.contains(num)) {
                return true;
            }
            seen.add(num);
        }
        return false;
    }
}