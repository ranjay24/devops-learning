class Solution {
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> ans = new ArrayList<>();
        if (s.length() < p.length())
            return ans;
        int[] pFreq = new int[26];
        int[] windowFreq = new int[26];
        for (char c : p.toCharArray()) {
            pFreq[c - 'a']++;
        }
        for (int i = 0; i < s.length(); i++) {
            windowFreq[s.charAt(i) - 'a']++;
            if (i >= p.length()) {
                windowFreq[s.charAt(i - p.length()) - 'a']--;
            }
            if (i >= p.length() - 1) {
                if (Arrays.equals(pFreq, windowFreq)) {
                    ans.add(i - p.length() + 1);
                }
            }
        }
        return ans;
	}
}
