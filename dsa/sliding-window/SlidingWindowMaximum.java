// Problem: Sliding Window Maximum - LeetCode #239
// Approach: Monotonic decreasing deque of INDICES. Front always holds the
// current window's max. Drop expired index from front; drop smaller values
// from back before adding new index. Each index enters and exits once.
// Time: O(n) | Space: O(k)

import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int[] maxSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> dq = new ArrayDeque<>();
        int idx = 0;

        for (int right = 0; right < n; right++) {
            if (!dq.isEmpty() && dq.peekFirst() <= right - k) {
                dq.pollFirst();
            }

            while (!dq.isEmpty() && nums[dq.peek                    Last()] < nums[right]) {
                dq.pollLast();
            }

            dq.offerLast(right);

            if (right >= k - 1) {
                result[idx++] = nums[dq.peekFirst()];
            }
        }
        return result;
    }
}
