// Problem: Group Anagrams - LeetCode #49
// Difficulty: Medium
// Topic: Strings, HashMap
// Approach: Sort each string to generate a key. Group all strings
//           with same key using HashMap. Return all groups.
// Time Complexity: O(n × k log k) | Space Complexity: O(n × k)

import java.util.*;

class GroupAnagrams {
    public List<List<String>> groupAnagrams(String[] strs) {
        HashMap<String, List<String>> map = new HashMap<>();
        for (String each : strs) {
            char[] chars = each.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            if (!map.containsKey(key)) {
                map.put(key, new ArrayList<>());
            }
            map.get(key).add(each);
        }
        return new ArrayList<>(map.values());
    }
}
