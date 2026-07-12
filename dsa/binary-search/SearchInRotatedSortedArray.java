// Problem: Search in Rotated Sorted Array - LeetCode #33
// Difficulty: Medium
// Topic: Binary Search
// Approach: At each mid, one half is always sorted. Check which half
//           is sorted, then check if target falls in that sorted range.
//           Move toward target's half each step.
// Time Complexity: O(log n) | Space Complexity: O(1)

class SearchInRotatedSortedArray {
    public int search(int[] nums, int target) {
        int low = 0;
        int high = nums.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (target == nums[mid]) {
                return mid;
            } else if (nums[low] <= nums[mid]) {
                if (target >= nums[low] && target < nums[mid]) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            } else {
                if (target > nums[mid] && target <= nums[high]) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
        }
        return -1;
    }
}
