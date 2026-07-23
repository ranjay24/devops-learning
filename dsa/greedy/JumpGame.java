// LeetCode #55 - Jump Game (Medium)
// Pattern: Greedy
//
// Problem: each element in nums represents the MAXIMUM jump length from
// that position. Starting at index 0, determine if the last index is
// reachable.
//
// Key insight: don't need to try every possible sequence of jumps.
// Instead, track a single running value - the farthest index reachable
// so far - updated while scanning left to right. This works because
// "farthest reachable" only ever needs to grow, never shrink or
// backtrack, which is what makes this a valid greedy approach.
//
// At each index i:
//   1. If i is already beyond farthest, we could never have legally
//      arrived here - return false immediately.
//   2. Otherwise, update farthest = max(farthest, i + nums[i]) - can we
//      extend our reach further from this position?
//
// If the loop completes without ever getting stuck, the end is
// reachable - no need to explicitly check farthest >= last index at
// the end, since never returning false along the way already proves it.
//
// Time:  O(n) - single pass
// Space: O(1) - one running variable

class Solution {
    public boolean canJump(int[] nums) {
        int farthest = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > farthest) return false;
            farthest = Math.max(farthest, i + nums[i]);
        }
        return true;
    }
}
