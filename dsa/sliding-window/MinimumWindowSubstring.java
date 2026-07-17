// Problem: Minimum Window Substring - LeetCode #76
// Difficulty: Hard
// Topic: Sliding Window (Variable Size)
// Approach: Expand right until window contains all chars of t.
//           Track unique chars satisfied via requiredChars.
//           Shrink left while window stays valid, recording minimum.
//           requiredChars decrements only when window freq matches target freq exactly.
// Time Complexity: O(n + m) | Space Complexity: O(m)

import java.util.HashMap;
import java.util.Map;

class MinimumWindowSubstring {
    public String minWindow(String s, String t) {
        if (s == null || t == null || s.length() == 0 || t.length() == 0) {
            return "";
        }
        Map<Character, Integer> targetFreq = new HashMap<>();
        for (char c : t.toCharArray()) {
            targetFreq.put(c, targetFreq.getOrDefault(c, 0) + 1);
        }
        int left = 0;
        int right = 0;
        int minLen = Integer.MAX_VALUE;
        int minStart = 0;
        int requiredChars = targetFreq.size();
        Map<Character, Integer> windowFreq = new HashMap<>();
        while (right < s.length()) {
            char currentChar = s.charAt(right);
            windowFreq.put(currentChar, windowFreq.getOrDefault(currentChar, 0) + 1);
            if (targetFreq.containsKey(currentChar) &&
                    targetFreq.get(currentChar).equals(windowFreq.get(currentChar))) {
                requiredChars--;
            }
            while (left <= right && requiredChars == 0) {
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    minStart = left;
                }
                char leftChar = s.charAt(left);
                windowFreq.put(leftChar, windowFreq.get(leftChar) - 1);
                if (targetFreq.containsKey(leftChar) &&
                        windowFreq.get(leftChar) < targetFreq.get(leftChar)) {
                    requiredChars++;
                }
                left++;
            }
            right++;
        }
        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
    }
}
