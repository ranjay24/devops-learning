// Problem: Best Time to Buy and Sell Stock - LeetCode #121
// Difficulty: Easy
// Topic: Arrays
// Approach: Track minimum buy price and maximum profit in single pass.
//           Update buy price when lower value found.
//           Update profit when current profit exceeds stored maximum.
// Time Complexity: O(n) | Space Complexity: O(1)

class BestTimeToBuyAndSellStock {
    public int maxProfit(int[] prices) {
        int buy = prices[0], profit = 0;
        for (int i = 1; i < prices.length; i++) {
            if (buy > prices[i]) {
                buy = prices[i];
            }
            int currProfit = prices[i] - buy;
            if (currProfit > profit) {
                profit = currProfit;
            }
        }
        return profit;
    }
    public static void main(String[] args) {
        BestTimeToBuyAndSellStock solution = new BestTimeToBuyAndSellStock();
        int[] prices = {7, 1, 5, 3, 6, 4};
        System.out.println(solution.maxProfit(prices)); // Output: 5
    }
}