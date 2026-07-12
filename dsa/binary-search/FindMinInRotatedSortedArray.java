// Problem: Find Minimum in Rotated Sorted Array - LeetCode #153
// Difficulty: Medium
// Topic: Binary Search
// Approach: If nums[mid] > nums[high], minimum is in right half.
//           Otherwise minimum is in left half including mid.
//           Stop when low == high.
// Time Complexity: O(log n) | Space Complexity: O(1)

class FindMinimumInRotatedSortedArray {
    public int findMin(int[] nums) {
        int low = 0;
        int high = nums.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (low == high) {
                break;
            } else if (nums[mid] > nums[high]) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return nums[low];
    }
}
