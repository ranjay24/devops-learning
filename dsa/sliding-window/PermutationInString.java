// Problem: Permutation in String - LeetCode #567
// Approach: Fixed-size sliding window + frequency compare. Build need[] from s1.
// Slide a window of size s1.length() over s2; if window counts == need counts,
// a permutation exists.
// Time: O(n) | Space: O(1) - two fixed 26-length arrays

import java.util.Arrays;

class Solution {
    public boolean checkInclusion(String s1, String s2) {
        if (s1.length() > s2.length())
            return false;

        int[] need = new int[26];
        int[] window = new int[26];

        for (char c : s1.toCharArray()) {
            need[c - 'a']++;
        }

        int left = 0;
        for (int right = 0; right < s2.length(); right++) {
            window[s2.charAt(right) - 'a']++;

            if (right - left + 1 > s1.length()) {
                window[s2.charAt(left) - 'a']--;
                left++;
            }

            if (Arrays.equals(need, window)) {
                return true;
            }
        }
        return false;
    }
}
