// Problem: Valid Anagram - LeetCode #242
// Difficulty: Easy
// Topic: Arrays, Strings
// Approach: Frequency array of size 26. Increment for chars in s,
//           decrement for chars in t. If all zeros - anagram.
// Time Complexity: O(n) | Space Complexity: O(1)

class ValidAnagram {
    public boolean isAnagram(String s, String t) {
        int[] ans = new int[26];
        if (s.length() != t.length()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            ans[s.charAt(i) - 'a']++;
            ans[t.charAt(i) - 'a']--;
        }
        for (int index : ans) {
            if (index != 0) {
                return false;
            }
        }
        return true;


          // for (int i = 0; i < s.length(); i++) {
        //     char c = s.charAt(i);
        //     newS.setCharAt(i, ' ');

        //     for (int j = 0; j < t.length(); j++) {
        //         if (newT.charAt(j) == c) {
        //             newT.setCharAt(j, ' ');
        //             break;
        //         }
        //     }
        // }

        // return newS.toString().equals(newT.toString());
    }
}