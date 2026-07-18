// LeetCode #3 - Longest Substring Without Repeating Characters
// Pattern: Sliding Window (variable size)
//
// Approach:
// Two pointers (left, right) define the current window.
// Expand right one character at a time.
// If the character at right already exists in the window, shrink left
// forward, removing characters one by one, until the duplicate is gone.
// Track the max window size seen at each step.
//
// Time: O(n) - left and right pointers only ever move forward, never
// reset backward, so total movement across the whole run is bounded by 2n
// even though there's a nested while loop.
// Space: O(min(n, charset size))

class Solution {
    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> freq = new HashMap<>();
        int left = 0;
        int curlen = 0, max = 0;
        int right = 0;

        while (left <= right && right < s.length()) {
            char c = s.charAt(right);

            if (!freq.containsKey(c)) {
                // character not in window - expand
                freq.put(c, freq.getOrDefault(c, 0) + 1);
                curlen = right - left + 1;
                if (curlen > max) {
                    max = curlen;
                }
            } else {
                // duplicate found - shrink window from the left
                // until the duplicate character is removed
                while (left < right) {
                    if (s.charAt(left) == s.charAt(right)) {
                        freq.put(s.charAt(left), 1);
                        left++;
                        break;
                    }
                    // freq.remove(), not freq.put(key, 0) - a value of 0
                    // still leaves the key in the map, so containsKey()
                    // would incorrectly still return true for it
                    freq.remove(s.charAt(left));
                    left++;
                }
            }
            right++;
        }
        return max;
    }
}
