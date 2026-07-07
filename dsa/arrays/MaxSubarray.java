import java.util.*;

class MaxSubarray {
    public int maxSubArray(int[] nums) {
        int max = Integer.MIN_VALUE;
        int curr = 0;
        int start = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] < 0) {
                max = Math.max(max, nums[i]);
                if (i == (nums.length - 1)) {
                    return max;
                }
            } else {
                start = i;
                break;
            }
        }

        for (int i = start; i < nums.length; i++) {
            curr += nums[i];
            if (curr < 0) {
                curr = 0;
            }
            max = Math.max(curr, max);
        }

        return max;
    }
}
