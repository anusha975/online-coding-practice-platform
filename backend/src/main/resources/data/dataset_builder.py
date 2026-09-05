import json
import os

all_problems = []

def add_p(title, category, difficulty, desc, constraints, inp_fmt, out_fmt, s_inp, s_out, test_cases, time_limit=2000, mem_limit=256):
    all_problems.append({
        "title": title,
        "category": category,
        "difficulty": difficulty,
        "description": desc,
        "constraints": constraints,
        "inputFormat": inp_fmt,
        "outputFormat": out_fmt,
        "sampleInput": s_inp,
        "sampleOutput": s_out,
        "timeLimitMs": time_limit,
        "memoryLimitMb": mem_limit,
        "testCases": test_cases
    })

# ==========================================
# 1. ARRAYS & HASHING (25 problems)
# ==========================================
add_p("Two Sum", "Arrays & Hashing", "EASY",
      "Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.\n\nAssume each input has exactly one solution, and do not use the same element twice.\n\nPrint the two 0-based indices separated by space in ascending order.",
      "2 <= nums.length <= 10^4\n-10^9 <= nums[i] <= 10^9\n-10^9 <= target <= 10^9",
      "Line 1: Integer n\nLine 2: n space-separated integers\nLine 3: Integer target",
      "Print the two 0-based indices separated by a space in ascending order.",
      "4\n2 7 11 15\n9", "0 1",
      [{"input": "4\n2 7 11 15\n9", "expectedOutput": "0 1", "hidden": False},
       {"input": "3\n3 2 4\n6", "expectedOutput": "1 2", "hidden": False},
       {"input": "2\n3 3\n6", "expectedOutput": "0 1", "hidden": True},
       {"input": "5\n1 5 3 7 9\n12", "expectedOutput": "1 3", "hidden": True}])

add_p("Contains Duplicate", "Arrays & Hashing", "EASY",
      "Given an integer array nums, return true if any value appears at least twice in the array, and return false if every element is distinct.",
      "1 <= nums.length <= 10^5\n-10^9 <= nums[i] <= 10^9",
      "Line 1: Integer n\nLine 2: n space-separated integers",
      "Print 'true' or 'false'.",
      "4\n1 2 3 1", "true",
      [{"input": "4\n1 2 3 1", "expectedOutput": "true", "hidden": False},
       {"input": "4\n1 2 3 4", "expectedOutput": "false", "hidden": False},
       {"input": "10\n1 1 1 3 3 4 3 2 4 2", "expectedOutput": "true", "hidden": True}])

add_p("Valid Anagram", "Arrays & Hashing", "EASY",
      "Given two strings s and t, return true if t is an anagram of s, and false otherwise.",
      "1 <= s.length, t.length <= 5 * 10^4\ns and t consist of lowercase English letters.",
      "Line 1: String s\nLine 2: String t",
      "Print 'true' or 'false'.",
      "anagram\nnagaram", "true",
      [{"input": "anagram\nnagaram", "expectedOutput": "true", "hidden": False},
       {"input": "rat\ncar", "expectedOutput": "false", "hidden": False},
       {"input": "a\nab", "expectedOutput": "false", "hidden": True}])

add_p("Group Anagrams", "Arrays & Hashing", "MEDIUM",
      "Given an array of strings strs, group the anagrams together. Print the size of each group sorted in ascending order.",
      "1 <= strs.length <= 10^4\n0 <= strs[i].length <= 100\nstrs[i] consists of lowercase English letters.",
      "Line 1: Integer n\nLine 2: n space-separated strings",
      "Print the sizes of all anagram groups sorted in ascending order, separated by space.",
      "6\neat tea tan ate nat bat", "1 2 3",
      [{"input": "6\neat tea tan ate nat bat", "expectedOutput": "1 2 3", "hidden": False},
       {"input": "1\na", "expectedOutput": "1", "hidden": False},
       {"input": "2\nab ba", "expectedOutput": "2", "hidden": True}])

add_p("Top K Frequent Elements", "Arrays & Hashing", "MEDIUM",
      "Given an integer array nums and an integer k, return the k most frequent elements sorted in ascending order.",
      "1 <= nums.length <= 10^5\n-10^4 <= nums[i] <= 10^4\nk is in the range [1, unique elements count].",
      "Line 1: Integer n\nLine 2: n space-separated integers\nLine 3: Integer k",
      "Print the k most frequent elements in ascending order, space-separated.",
      "6\n1 1 1 2 2 3\n2", "1 2",
      [{"input": "6\n1 1 1 2 2 3\n2", "expectedOutput": "1 2", "hidden": False},
       {"input": "1\n1\n1", "expectedOutput": "1", "hidden": False},
       {"input": "7\n4 4 4 4 2 2 1\n2", "expectedOutput": "2 4", "hidden": True}])

add_p("Product of Array Except Self", "Arrays & Hashing", "MEDIUM",
      "Given an integer array nums, return an array answer such that answer[i] is equal to the product of all the elements of nums except nums[i] in O(n) without division.",
      "2 <= nums.length <= 10^5\n-30 <= nums[i] <= 30",
      "Line 1: Integer n\nLine 2: n space-separated integers",
      "Print the resulting array elements separated by space.",
      "4\n1 2 3 4", "24 12 8 6",
      [{"input": "4\n1 2 3 4", "expectedOutput": "24 12 8 6", "hidden": False},
       {"input": "5\n-1 1 0 -3 3", "expectedOutput": "0 0 9 0 0", "hidden": False},
       {"input": "3\n2 3 4", "expectedOutput": "12 8 6", "hidden": True}])

add_p("Longest Consecutive Sequence", "Arrays & Hashing", "MEDIUM",
      "Given an unsorted array of integers nums, return the length of the longest consecutive elements sequence in O(n) time.",
      "0 <= nums.length <= 10^5\n-10^9 <= nums[i] <= 10^9",
      "Line 1: Integer n\nLine 2: n space-separated integers",
      "Print the length of the longest consecutive sequence.",
      "6\n100 4 200 1 3 2", "4",
      [{"input": "6\n100 4 200 1 3 2", "expectedOutput": "4", "hidden": False},
       {"input": "10\n0 3 7 2 5 8 4 6 0 1", "expectedOutput": "9", "hidden": False},
       {"input": "0\n", "expectedOutput": "0", "hidden": True}])

add_p("Encode and Decode Strings", "Arrays & Hashing", "MEDIUM",
      "Design an algorithm to encode a list of strings to a string, then decode it back.",
      "1 <= strs.length <= 200\n0 <= strs[i].length <= 200",
      "Line 1: Integer n\nLine 2..n+1: Each line contains a string",
      "Print decoded strings count on line 1, and space-separated decoded strings on line 2.",
      "2\nHello\nWorld", "2\nHello World",
      [{"input": "2\nHello\nWorld", "expectedOutput": "2\nHello World", "hidden": False},
       {"input": "1\nCodeForge", "expectedOutput": "1\nCodeForge", "hidden": True}])

add_p("Majority Element", "Arrays & Hashing", "EASY",
      "Given an array nums of size n, return the majority element that appears more than ⌊n / 2⌋ times.",
      "1 <= n <= 5 * 10^4\n-10^9 <= nums[i] <= 10^9",
      "Line 1: Integer n\nLine 2: n space-separated integers",
      "Print the majority element.",
      "3\n3 2 3", "3",
      [{"input": "3\n3 2 3", "expectedOutput": "3", "hidden": False},
       {"input": "7\n2 2 1 1 1 2 2", "expectedOutput": "2", "hidden": False},
       {"input": "1\n100", "expectedOutput": "100", "hidden": True}])

add_p("Next Permutation", "Arrays & Hashing", "MEDIUM",
      "Find the next lexicographically greater permutation of numbers.",
      "1 <= nums.length <= 100\n0 <= nums[i] <= 100",
      "Line 1: Integer n\nLine 2: n space-separated integers",
      "Print the next permutation space-separated.",
      "3\n1 2 3", "1 3 2",
      [{"input": "3\n1 2 3", "expectedOutput": "1 3 2", "hidden": False},
       {"input": "3\n3 2 1", "expectedOutput": "1 2 3", "hidden": False},
       {"input": "3\n1 1 5", "expectedOutput": "1 5 1", "hidden": True}])

add_p("Maximum Subarray", "Arrays & Hashing", "MEDIUM",
      "Given an integer array nums, find the subarray with the largest sum, and return its sum (Kadane's Algorithm).",
      "1 <= nums.length <= 10^5\n-10^4 <= nums[i] <= 10^4",
      "Line 1: Integer n\nLine 2: n space-separated integers",
      "Print the maximum subarray sum.",
      "9\n-2 1 -3 4 -1 2 1 -5 4", "6",
      [{"input": "9\n-2 1 -3 4 -1 2 1 -5 4", "expectedOutput": "6", "hidden": False},
       {"input": "1\n1", "expectedOutput": "1", "hidden": False},
       {"input": "5\n5 4 -1 7 8", "expectedOutput": "23", "hidden": True}])

add_p("Pascal's Triangle", "Arrays & Hashing", "EASY",
      "Given an integer numRows, return the first numRows of Pascal's triangle.",
      "1 <= numRows <= 30",
      "Line 1: Integer numRows",
      "Print each row of Pascal's triangle on a new line.",
      "5", "1\n1 1\n1 2 1\n1 3 3 1\n1 4 6 4 1",
      [{"input": "5", "expectedOutput": "1\n1 1\n1 2 1\n1 3 3 1\n1 4 6 4 1", "hidden": False},
       {"input": "1", "expectedOutput": "1", "hidden": False}])

add_p("Pascal's Triangle II", "Arrays & Hashing", "EASY",
      "Given an integer rowIndex, return the rowIndex-th (0-indexed) row of Pascal's triangle.",
      "0 <= rowIndex <= 33",
      "Line 1: Integer rowIndex",
      "Print the elements of the row space-separated.",
      "3", "1 3 3 1",
      [{"input": "3", "expectedOutput": "1 3 3 1", "hidden": False},
       {"input": "0", "expectedOutput": "1", "hidden": False},
       {"input": "1", "expectedOutput": "1 1", "hidden": True}])

add_p("Best Time to Buy and Sell Stock", "Arrays & Hashing", "EASY",
      "Find maximum profit by choosing a single day to buy one stock and choosing a different future day to sell.",
      "1 <= prices.length <= 10^5\n0 <= prices[i] <= 10^4",
      "Line 1: Integer n\nLine 2: n space-separated prices",
      "Print the maximum profit.",
      "6\n7 1 5 3 6 4", "5",
      [{"input": "6\n7 1 5 3 6 4", "expectedOutput": "5", "hidden": False},
       {"input": "5\n7 6 4 3 1", "expectedOutput": "0", "hidden": False},
       {"input": "2\n2 4", "expectedOutput": "2", "hidden": True}])

add_p("Rotate Array", "Arrays & Hashing", "MEDIUM",
      "Rotate an integer array to the right by k steps.",
      "1 <= nums.length <= 10^5\n0 <= k <= 10^5",
      "Line 1: Integer n\nLine 2: n space-separated integers\nLine 3: Integer k",
      "Print the rotated array space-separated.",
      "7\n1 2 3 4 5 6 7\n3", "5 6 7 1 2 3 4",
      [{"input": "7\n1 2 3 4 5 6 7\n3", "expectedOutput": "5 6 7 1 2 3 4", "hidden": False},
       {"input": "4\n-1 -100 3 99\n2", "expectedOutput": "3 99 -1 -100", "hidden": True}])

add_p("Find All Duplicates in an Array", "Arrays & Hashing", "MEDIUM",
      "Given an integer array nums of length n where all integers are in [1, n], return an array of all integers that appear twice sorted in ascending order.",
      "1 <= n <= 10^5\n1 <= nums[i] <= n",
      "Line 1: Integer n\nLine 2: n space-separated integers",
      "Print duplicates space-separated in ascending order.",
      "8\n4 3 2 7 8 2 3 1", "2 3",
      [{"input": "8\n4 3 2 7 8 2 3 1", "expectedOutput": "2 3", "hidden": False},
       {"input": "3\n1 1 2", "expectedOutput": "1", "hidden": False},
       {"input": "1\n1", "expectedOutput": "", "hidden": True}])

add_p("Subarray Sum Equals K", "Arrays & Hashing", "MEDIUM",
      "Given an array of integers nums and an integer k, return the total number of subarrays whose sum equals to k.",
      "1 <= nums.length <= 2 * 10^4\n-1000 <= nums[i] <= 1000",
      "Line 1: Integer n\nLine 2: n space-separated integers\nLine 3: Integer k",
      "Print the count of matching subarrays.",
      "3\n1 1 1\n2", "2",
      [{"input": "3\n1 1 1\n2", "expectedOutput": "2", "hidden": False},
       {"input": "3\n1 2 3\n3", "expectedOutput": "2", "hidden": False},
       {"input": "4\n1 -1 0 0\n0", "expectedOutput": "4", "hidden": True}])

add_p("Move Zeroes", "Arrays & Hashing", "EASY",
      "Move all 0's to the end of array while maintaining relative order of non-zero elements.",
      "1 <= nums.length <= 10^4",
      "Line 1: Integer n\nLine 2: n space-separated integers",
      "Print modified array space-separated.",
      "5\n0 1 0 3 12", "1 3 12 0 0",
      [{"input": "5\n0 1 0 3 12", "expectedOutput": "1 3 12 0 0", "hidden": False},
       {"input": "1\n0", "expectedOutput": "0", "hidden": False}])

add_p("Remove Duplicates from Sorted Array", "Arrays & Hashing", "EASY",
      "Remove duplicates in-place from sorted array such that each unique element appears once. Return count and elements.",
      "1 <= nums.length <= 3 * 10^4",
      "Line 1: Integer n\nLine 2: n space-separated integers",
      "Print k on line 1, unique elements on line 2.",
      "5\n1 1 2 2 3", "3\n1 2 3",
      [{"input": "5\n1 1 2 2 3", "expectedOutput": "3\n1 2 3", "hidden": False},
       {"input": "1\n1", "expectedOutput": "1\n1", "hidden": False}])

add_p("Remove Element", "Arrays & Hashing", "EASY",
      "Remove all occurrences of val in nums in-place.",
      "0 <= nums.length <= 100\n0 <= val <= 100",
      "Line 1: Integer n\nLine 2: n space-separated integers\nLine 3: Integer val",
      "Print k on line 1, remaining elements on line 2.",
      "4\n3 2 2 3\n3", "2\n2 2",
      [{"input": "4\n3 2 2 3\n3", "expectedOutput": "2\n2 2", "hidden": False},
       {"input": "8\n0 1 2 2 3 0 4 2\n2", "expectedOutput": "5\n0 1 3 0 4", "hidden": False}])

add_p("Find Pivot Index", "Arrays & Hashing", "EASY",
      "Calculate pivot index where left sum equals right sum.",
      "1 <= nums.length <= 10^4",
      "Line 1: Integer n\nLine 2: n space-separated integers",
      "Print leftmost pivot index or -1.",
      "6\n1 7 3 6 5 6", "3",
      [{"input": "6\n1 7 3 6 5 6", "expectedOutput": "3", "hidden": False},
       {"input": "3\n1 2 3", "expectedOutput": "-1", "hidden": False},
       {"input": "3\n2 1 -1", "expectedOutput": "0", "hidden": True}])

add_p("Squares of a Sorted Array", "Arrays & Hashing", "EASY",
      "Given sorted array nums, return sorted array of squares in non-decreasing order.",
      "1 <= nums.length <= 10^4",
      "Line 1: Integer n\nLine 2: n space-separated integers",
      "Print sorted squares space-separated.",
      "5\n-4 -1 0 3 10", "0 1 9 16 100",
      [{"input": "5\n-4 -1 0 3 10", "expectedOutput": "0 1 9 16 100", "hidden": False},
       {"input": "5\n-7 -3 2 3 11", "expectedOutput": "4 9 9 49 121", "hidden": True}])

add_p("Intersection of Two Arrays", "Arrays & Hashing", "EASY",
      "Return unique intersection of two arrays in ascending sorted order.",
      "1 <= nums1.length, nums2.length <= 1000",
      "Line 1: Integer n1\nLine 2: n1 space-separated integers\nLine 3: Integer n2\nLine 4: n2 space-separated integers",
      "Print unique intersection space-separated in ascending order.",
      "4\n1 2 2 1\n2\n2 2", "2",
      [{"input": "4\n1 2 2 1\n2\n2 2", "expectedOutput": "2", "hidden": False},
       {"input": "3\n4 9 5\n5\n9 4 9 8 4", "expectedOutput": "4 9", "hidden": False}])

add_p("Set Matrix Zeroes", "Arrays & Hashing", "MEDIUM",
      "Given m x n matrix, if an element is 0, set its entire row and column to 0 in-place.",
      "1 <= m, n <= 200",
      "Line 1: Integers m n\nNext m lines: n integers per line",
      "Print resulting matrix rows.",
      "3 3\n1 1 1\n1 0 1\n1 1 1", "1 0 1\n0 0 0\n1 0 1",
      [{"input": "3 3\n1 1 1\n1 0 1\n1 1 1", "expectedOutput": "1 0 1\n0 0 0\n1 0 1", "hidden": False},
       {"input": "3 4\n0 1 2 0\n3 4 5 2\n1 3 1 5", "expectedOutput": "0 0 0 0\n0 4 5 0\n0 3 1 0", "hidden": True}])

add_p("Spiral Matrix", "Arrays & Hashing", "MEDIUM",
      "Return all elements of m x n matrix in spiral order.",
      "1 <= m, n <= 10",
      "Line 1: Integers m n\nNext m lines: n integers per line",
      "Print spiral order space-separated.",
      "3 3\n1 2 3\n4 5 6\n7 8 9", "1 2 3 6 9 8 7 4 5",
      [{"input": "3 3\n1 2 3\n4 5 6\n7 8 9", "expectedOutput": "1 2 3 6 9 8 7 4 5", "hidden": False},
       {"input": "3 4\n1 2 3 4\n5 6 7 8\n9 10 11 12", "expectedOutput": "1 2 3 4 8 12 11 10 9 5 6 7", "hidden": True}])

# ==========================================
# 2. TWO POINTERS & SLIDING WINDOW (20 problems)
# ==========================================
add_p("Valid Palindrome", "Two Pointers", "EASY",
      "Check if string is palindrome after converting uppercase to lowercase and removing non-alphanumeric chars.",
      "1 <= s.length <= 2 * 10^5",
      "Line 1: String s",
      "Print 'true' or 'false'.",
      "A man, a plan, a canal: Panama", "true",
      [{"input": "A man, a plan, a canal: Panama", "expectedOutput": "true", "hidden": False},
       {"input": "race a car", "expectedOutput": "false", "hidden": False},
       {"input": " ", "expectedOutput": "true", "hidden": True}])

add_p("Two Sum II - Input Array Is Sorted", "Two Pointers", "MEDIUM",
      "Given 1-indexed sorted integer array, return indices of two numbers that add up to target.",
      "2 <= nums.length <= 3 * 10^4",
      "Line 1: Integer n\nLine 2: n space-separated integers\nLine 3: Integer target",
      "Print the 1-based indices space-separated.",
      "4\n2 7 11 15\n9", "1 2",
      [{"input": "4\n2 7 11 15\n9", "expectedOutput": "1 2", "hidden": False},
       {"input": "3\n2 3 4\n6", "expectedOutput": "1 3", "hidden": False},
       {"input": "2\n-1 0\n-1", "expectedOutput": "1 2", "hidden": True}])

add_p("3Sum", "Two Pointers", "MEDIUM",
      "Find all unique triplets [nums[i], nums[j], nums[k]] such that i != j, i != k, and j != k, and nums[i] + nums[j] + nums[k] == 0. Print count of triplets on line 1, each sorted triplet on next lines.",
      "3 <= nums.length <= 3000\n-10^5 <= nums[i] <= 10^5",
      "Line 1: Integer n\nLine 2: n space-separated integers",
      "Line 1: Count of triplets. Next lines: space-separated triplets sorted.",
      "6\n-1 0 1 2 -1 -4", "2\n-1 -1 2\n-1 0 1",
      [{"input": "6\n-1 0 1 2 -1 -4", "expectedOutput": "2\n-1 -1 2\n-1 0 1", "hidden": False},
       {"input": "3\n0 1 1", "expectedOutput": "0", "hidden": False},
       {"input": "3\n0 0 0", "expectedOutput": "1\n0 0 0", "hidden": True}])

add_p("3Sum Closest", "Two Pointers", "MEDIUM",
      "Find three integers in nums such that the sum is closest to target. Return sum of the three integers.",
      "3 <= nums.length <= 500",
      "Line 1: Integer n\nLine 2: n space-separated integers\nLine 3: Integer target",
      "Print closest sum.",
      "4\n-1 2 1 -4\n1", "2",
      [{"input": "4\n-1 2 1 -4\n1", "expectedOutput": "2", "hidden": False},
       {"input": "3\n0 0 0\n1", "expectedOutput": "0", "hidden": False}])

add_p("4Sum", "Two Pointers", "MEDIUM",
      "Find all unique quadruplets [nums[a], nums[b], nums[c], nums[d]] that sum to target. Print total count.",
      "1 <= nums.length <= 200",
      "Line 1: Integer n\nLine 2: n space-separated integers\nLine 3: Integer target",
      "Print count of unique quadruplets.",
      "6\n1 0 -1 0 -2 2\n0", "3",
      [{"input": "6\n1 0 -1 0 -2 2\n0", "expectedOutput": "3", "hidden": False},
       {"input": "5\n2 2 2 2 2\n8", "expectedOutput": "1", "hidden": False}])

add_p("Container With Most Water", "Two Pointers", "MEDIUM",
      "Find two vertical lines that together with x-axis form a container holding the most water.",
      "2 <= n <= 10^5",
      "Line 1: Integer n\nLine 2: n space-separated integers",
      "Print maximum water area.",
      "9\n1 8 6 2 5 4 8 3 7", "49",
      [{"input": "9\n1 8 6 2 5 4 8 3 7", "expectedOutput": "49", "hidden": False},
       {"input": "2\n1 1", "expectedOutput": "1", "hidden": False},
       {"input": "4\n4 3 2 1 4", "expectedOutput": "16", "hidden": True}])

add_p("Trapping Rain Water", "Two Pointers", "HARD",
      "Given n non-negative integers representing elevation map where width of each bar is 1, compute how much water it can trap after raining.",
      "n == height.length\n1 <= n <= 2 * 10^4\n0 <= height[i] <= 10^5",
      "Line 1: Integer n\nLine 2: n space-separated integers",
      "Print total trapped water units.",
      "12\n0 1 0 2 1 0 1 3 2 1 2 1", "6",
      [{"input": "12\n0 1 0 2 1 0 1 3 2 1 2 1", "expectedOutput": "6", "hidden": False},
       {"input": "6\n4 2 0 3 2 5", "expectedOutput": "9", "hidden": False}])

add_p("Longest Substring Without Repeating Characters", "Sliding Window", "MEDIUM",
      "Given a string s, find the length of the longest substring without repeating characters.",
      "0 <= s.length <= 5 * 10^4",
      "Line 1: String s",
      "Print length of longest substring.",
      "abcabcbb", "3",
      [{"input": "abcabcbb", "expectedOutput": "3", "hidden": False},
       {"input": "bbbbb", "expectedOutput": "1", "hidden": False},
       {"input": "pwwkew", "expectedOutput": "3", "hidden": True},
       {"input": "", "expectedOutput": "0", "hidden": True}])

add_p("Longest Repeating Character Replacement", "Sliding Window", "MEDIUM",
      "You are given string s and integer k. You can choose any character of string and change it to any other uppercase English character at most k times. Return length of longest substring containing same letter.",
      "1 <= s.length <= 10^5\n0 <= k <= s.length",
      "Line 1: String s\nLine 2: Integer k",
      "Print maximum length.",
      "ABAB\n2", "4",
      [{"input": "ABAB\n2", "expectedOutput": "4", "hidden": False},
       {"input": "AABABBA\n1", "expectedOutput": "4", "hidden": False}])

add_p("Permutation in String", "Sliding Window", "MEDIUM",
      "Given two strings s1 and s2, return true if s2 contains a permutation of s1, or false otherwise.",
      "1 <= s1.length, s2.length <= 10^4",
      "Line 1: String s1\nLine 2: String s2",
      "Print 'true' or 'false'.",
      "ab\neidbaooo", "true",
      [{"input": "ab\neidbaooo", "expectedOutput": "true", "hidden": False},
       {"input": "ab\neidboaoo", "expectedOutput": "false", "hidden": False}])

add_p("Minimum Window Substring", "Sliding Window", "HARD",
      "Given two strings s and t, return minimum window substring of s such that every character in t (including duplicates) is included.",
      "1 <= s.length, t.length <= 10^5",
      "Line 1: String s\nLine 2: String t",
      "Print minimum window substring or empty string.",
      "ADOBECODEBANC\nABC", "BANC",
      [{"input": "ADOBECODEBANC\nABC", "expectedOutput": "BANC", "hidden": False},
       {"input": "a\na", "expectedOutput": "a", "hidden": False},
       {"input": "a\naa", "expectedOutput": "", "hidden": True}])

add_p("Sliding Window Maximum", "Sliding Window", "HARD",
      "Return max sliding window of size k moving from very left of array to very right.",
      "1 <= nums.length <= 10^5\n1 <= k <= nums.length",
      "Line 1: Integer n\nLine 2: n space-separated integers\nLine 3: Integer k",
      "Print max elements space-separated.",
      "8\n1 3 -1 -3 5 3 6 7\n3", "3 3 5 5 6 7",
      [{"input": "8\n1 3 -1 -3 5 3 6 7\n3", "expectedOutput": "3 3 5 5 6 7", "hidden": False},
       {"input": "1\n1\n1", "expectedOutput": "1", "hidden": False}])

add_p("Minimum Size Subarray Sum", "Sliding Window", "MEDIUM",
      "Given an array of positive integers nums and a positive integer target, return minimal length of subarray whose sum is >= target.",
      "1 <= target <= 10^9\n1 <= nums.length <= 10^5",
      "Line 1: Integer target\nLine 2: Integer n\nLine 3: n space-separated integers",
      "Print minimal length or 0.",
      "7\n6\n2 3 1 2 4 3", "2",
      [{"input": "7\n6\n2 3 1 2 4 3", "expectedOutput": "2", "hidden": False},
       {"input": "4\n3\n1 4 4", "expectedOutput": "1", "hidden": False},
       {"input": "11\n8\n1 1 1 1 1 1 1 1", "expectedOutput": "0", "hidden": True}])

add_p("Max Consecutive Ones III", "Sliding Window", "MEDIUM",
      "Given binary array nums and integer k, return max number of consecutive 1's if you can flip at most k 0's.",
      "1 <= nums.length <= 10^5\n0 <= k <= nums.length",
      "Line 1: Integer n\nLine 2: n space-separated binary integers\nLine 3: Integer k",
      "Print maximum consecutive 1's.",
      "11\n1 1 1 0 0 0 1 1 1 1 0\n2", "6",
      [{"input": "11\n1 1 1 0 0 0 1 1 1 1 0\n2", "expectedOutput": "6", "hidden": False},
       {"input": "5\n0 0 1 1 0\n1", "expectedOutput": "3", "hidden": True}])

add_p("Fruit Into Baskets", "Sliding Window", "MEDIUM",
      "You have 2 baskets, each can hold only 1 type of fruit. Return max number of fruits you can pick in consecutive trees.",
      "1 <= fruits.length <= 10^5",
      "Line 1: Integer n\nLine 2: n space-separated integers",
      "Print maximum fruits picked.",
      "3\n1 2 1", "3",
      [{"input": "3\n1 2 1", "expectedOutput": "3", "hidden": False},
       {"input": "4\n0 1 2 2", "expectedOutput": "3", "hidden": False},
       {"input": "5\n1 2 3 2 2", "expectedOutput": "4", "hidden": True}])

add_p("Boats to Save People", "Two Pointers", "MEDIUM",
      "Given people weights and boat weight limit, return min boats to carry everyone (at most 2 people per boat).",
      "1 <= people.length <= 5 * 10^4\n1 <= limit <= 3 * 10^4",
      "Line 1: Integer n\nLine 2: n space-separated integers\nLine 3: Integer limit",
      "Print minimum boats.",
      "4\n3 2 2 1\n3", "3",
      [{"input": "4\n3 2 2 1\n3", "expectedOutput": "3", "hidden": False},
       {"input": "4\n3 5 3 4\n5", "expectedOutput": "4", "hidden": False}])

add_p("Reverse Words in a String", "Two Pointers", "MEDIUM",
      "Given input string s, reverse order of words. Words are separated by at least one space. Return string with single space.",
      "1 <= s.length <= 10^4",
      "Line 1: String s",
      "Print reversed words string.",
      "the sky is blue", "blue is sky the",
      [{"input": "the sky is blue", "expectedOutput": "blue is sky the", "hidden": False},
       {"input": "  hello world  ", "expectedOutput": "world hello", "hidden": False},
       {"input": "a good   example", "expectedOutput": "example good a", "hidden": True}])

add_p("Valid Palindrome II", "Two Pointers", "EASY",
      "Given string s, return true if s can be palindrome after deleting at most one character.",
      "1 <= s.length <= 10^5",
      "Line 1: String s",
      "Print 'true' or 'false'.",
      "aba", "true",
      [{"input": "aba", "expectedOutput": "true", "hidden": False},
       {"input": "abca", "expectedOutput": "true", "hidden": False},
       {"input": "abc", "expectedOutput": "false", "hidden": True}])

add_p("Is Subsequence", "Two Pointers", "EASY",
      "Given two strings s and t, return true if s is a subsequence of t, or false otherwise.",
      "0 <= s.length <= 100\n0 <= t.length <= 10^4",
      "Line 1: String s\nLine 2: String t",
      "Print 'true' or 'false'.",
      "abc\nahbgdc", "true",
      [{"input": "abc\nahbgdc", "expectedOutput": "true", "hidden": False},
       {"input": "axc\nahbgdc", "expectedOutput": "false", "hidden": False},
       {"input": "\nahbgdc", "expectedOutput": "true", "hidden": True}])

add_p("Subarrays with K Different Integers", "Sliding Window", "HARD",
      "Given an integer array nums and integer k, return number of good subarrays having exactly k different integers.",
      "1 <= nums.length <= 2 * 10^4\n1 <= k <= nums.length",
      "Line 1: Integer n\nLine 2: n space-separated integers\nLine 3: Integer k",
      "Print count of matching subarrays.",
      "5\n1 2 1 2 3\n2", "7",
      [{"input": "5\n1 2 1 2 3\n2", "expectedOutput": "7", "hidden": False},
       {"input": "5\n1 2 1 3 4\n3", "expectedOutput": "3", "hidden": False}])

print(f"Total problems defined so far: {len(all_problems)}")

# Helper to batch generate other categories cleanly:
def batch_add_from_specs(specs):
    for spec in specs:
        add_p(spec[0], spec[1], spec[2], spec[3], spec[4], spec[5], spec[6], spec[7], spec[8], spec[9])

# Stack (18)
stack_specs = [
    ("Valid Parentheses", "Stack", "EASY", "Given string s containing '()[]{}', determine if input string is valid.", "1 <= s.length <= 10^4", "Line 1: String s", "Print 'true' or 'false'.", "()[]{}", "true", [{"input": "()[]{}", "expectedOutput": "true", "hidden": False}, {"input": "(]", "expectedOutput": "false", "hidden": False}, {"input": "([{}])", "expectedOutput": "true", "hidden": True}]),
    ("Min Stack", "Stack", "MEDIUM", "Design stack that supports push, pop, top, and retrieving minimum element in constant time. Process commands: PUSH x, POP, TOP, GETMIN.", "1 <= commands <= 10^4", "Line 1: Integer q\nNext q lines: command", "Print result of TOP and GETMIN operations.", "6\nPUSH -2\nPUSH 0\nPUSH -3\nGETMIN\nPOP\nTOP", "-3\n0", [{"input": "6\nPUSH -2\nPUSH 0\nPUSH -3\nGETMIN\nPOP\nTOP", "expectedOutput": "-3\n0", "hidden": False}]),
    ("Evaluate Reverse Polish Notation", "Stack", "MEDIUM", "Evaluate the value of an arithmetic expression in Reverse Polish Notation (+, -, *, /).", "1 <= tokens.length <= 10^4", "Line 1: Integer n\nLine 2: n space-separated tokens", "Print evaluated integer result.", "5\n2 1 + 3 *", "9", [{"input": "5\n2 1 + 3 *", "expectedOutput": "9", "hidden": False}, {"input": "5\n4 13 5 / +", "expectedOutput": "6", "hidden": False}]),
    ("Daily Temperatures", "Stack", "MEDIUM", "Given temperatures array, return array answer such that answer[i] is number of days to wait for warmer temperature (0 if none).", "1 <= temperatures.length <= 10^5", "Line 1: Integer n\nLine 2: n space-separated integers", "Print wait days space-separated.", "8\n73 74 75 71 69 72 76 73", "1 1 4 2 1 1 0 0", [{"input": "8\n73 74 75 71 69 72 76 73", "expectedOutput": "1 1 4 2 1 1 0 0", "hidden": False}]),
    ("Car Fleet", "Stack", "MEDIUM", "n cars at target positions with speeds. Return number of car fleets that arrive at destination.", "1 <= n <= 10^5\n1 <= target <= 10^6", "Line 1: Integer target n\nLine 2: n positions\nLine 3: n speeds", "Print fleet count.", "12 5\n10 8 0 5 3\n2 4 1 1 3", "3", [{"input": "12 5\n10 8 0 5 3\n2 4 1 1 3", "expectedOutput": "3", "hidden": False}]),
    ("Largest Rectangle in Histogram", "Stack", "HARD", "Given histogram bars height, find area of largest rectangle in histogram using Monotonic Stack.", "1 <= heights.length <= 10^5", "Line 1: Integer n\nLine 2: n space-separated heights", "Print max rectangle area.", "6\n2 1 5 6 2 3", "10", [{"input": "6\n2 1 5 6 2 3", "expectedOutput": "10", "hidden": False}, {"input": "2\n2 4", "expectedOutput": "4", "hidden": False}]),
    ("Maximal Rectangle", "Stack", "HARD", "Given binary matrix, find largest rectangle containing only 1's and return its area.", "1 <= rows, cols <= 200", "Line 1: Integers rows cols\nNext rows lines: cols chars (0 or 1)", "Print max area.", "4 5\n10100\n10111\n11111\n10010", "6", [{"input": "4 5\n10100\n10111\n11111\n10010", "expectedOutput": "6", "hidden": False}]),
    ("Asteroid Collision", "Stack", "MEDIUM", "Find state of asteroids after all collisions (smaller explodes, equal both explode).", "2 <= asteroids.length <= 10^4", "Line 1: Integer n\nLine 2: n space-separated asteroid sizes", "Print remaining asteroids space-separated.", "3\n5 10 -5", "5 10", [{"input": "3\n5 10 -5", "expectedOutput": "5 10", "hidden": False}, {"input": "2\n8 -8", "expectedOutput": "", "hidden": False}, {"input": "3\n10 2 -5", "expectedOutput": "10", "hidden": True}]),
    ("Next Greater Element I", "Stack", "EASY", "Find next greater element for nums1 subset in nums2.", "1 <= nums1.length <= nums2.length <= 1000", "Line 1: Integer n1\nLine 2: n1 integers\nLine 3: Integer n2\nLine 4: n2 integers", "Print next greater elements space-separated.", "3\n4 1 2\n4\n1 3 4 2", "-1 3 -1", [{"input": "3\n4 1 2\n4\n1 3 4 2", "expectedOutput": "-1 3 -1", "hidden": False}]),
    ("Next Greater Element II", "Stack", "MEDIUM", "Find next greater number for every element in circular array.", "1 <= nums.length <= 10^4", "Line 1: Integer n\nLine 2: n space-separated integers", "Print next greater elements space-separated.", "3\n1 2 1", "2 -1 2", [{"input": "3\n1 2 1", "expectedOutput": "2 -1 2", "hidden": False}]),
    ("Online Stock Span", "Stack", "MEDIUM", "Calculate span of stock price for consecutive previous days with price <= today.", "1 <= prices <= 10^4", "Line 1: Integer n\nLine 2: n space-separated prices", "Print spans space-separated.", "7\n100 80 60 70 60 75 85", "1 1 1 2 1 4 6", [{"input": "7\n100 80 60 70 60 75 85", "expectedOutput": "1 1 1 2 1 4 6", "hidden": False}]),
    ("Simplify Path", "Stack", "MEDIUM", "Simplify Unix-style file path to canonical path.", "1 <= path.length <= 3000", "Line 1: String path", "Print simplified canonical path.", "/home//foo/", "/home/foo", [{"input": "/home//foo/", "expectedOutput": "/home/foo", "hidden": False}, {"input": "/../", "expectedOutput": "/", "hidden": False}, {"input": "/home/user/Documents/../Pictures", "expectedOutput": "/home/user/Pictures", "hidden": True}]),
    ("132 Pattern", "Stack", "MEDIUM", "Find if there is a 132 pattern: nums[i] < nums[k] < nums[j] with i < j < k.", "1 <= nums.length <= 2 * 10^5", "Line 1: Integer n\nLine 2: n space-separated integers", "Print 'true' or 'false'.", "4\n1 2 3 4", "false", [{"input": "4\n1 2 3 4", "expectedOutput": "false", "hidden": False}, {"input": "4\n3 1 4 2", "expectedOutput": "true", "hidden": False}]),
    ("Decode String", "Stack", "MEDIUM", "Given an encoded string, return its decoded string: k[encoded_string].", "1 <= s.length <= 30", "Line 1: String s", "Print decoded string.", "3[a]2[bc]", "aaabcbc", [{"input": "3[a]2[bc]", "expectedOutput": "aaabcbc", "hidden": False}, {"input": "3[a2[c]]", "expectedOutput": "accaccacc", "hidden": False}]),
    ("Remove All Adjacent Duplicates in String", "Stack", "EASY", "Repeatedly remove duplicate adjacent characters until no duplicates remain.", "1 <= s.length <= 10^5", "Line 1: String s", "Print final string.", "abbaca", "ca", [{"input": "abbaca", "expectedOutput": "ca", "hidden": False}, {"input": "azxxzy", "expectedOutput": "ay", "hidden": True}]),
    ("Basic Calculator", "Stack", "HARD", "Evaluate a math expression with +, -, and parentheses.", "1 <= s.length <= 3 * 10^5", "Line 1: String s", "Print computed integer value.", "1 + 1", "2", [{"input": "1 + 1", "expectedOutput": "2", "hidden": False}, {"input": "(1+(4+5+2)-3)+(6+8)", "expectedOutput": "23", "hidden": False}]),
    ("Basic Calculator II", "Stack", "MEDIUM", "Evaluate math expression with +, -, *, and / without parentheses.", "1 <= s.length <= 3 * 10^5", "Line 1: String s", "Print computed integer value.", "3+2*2", "7", [{"input": "3+2*2", "expectedOutput": "7", "hidden": False}, {"input": " 3/2 ", "expectedOutput": "1", "hidden": False}]),
    ("Backspace String Compare", "Stack", "EASY", "Given two strings s and t with '#' representing backspace, return if they are equal.", "1 <= s.length, t.length <= 200", "Line 1: String s\nLine 2: String t", "Print 'true' or 'false'.", "ab#c\nad#c", "true", [{"input": "ab#c\nad#c", "expectedOutput": "true", "hidden": False}, {"input": "a#c\nb", "expectedOutput": "false", "hidden": False}])
]
batch_add_from_specs(stack_specs)

# Binary Search (18)
bs_specs = [
    ("Binary Search", "Binary Search", "EASY", "Search target in sorted array nums. Return index or -1.", "1 <= nums.length <= 10^4", "Line 1: Integer n\nLine 2: n integers\nLine 3: Integer target", "Print index or -1.", "6\n-1 0 3 5 9 12\n9", "4", [{"input": "6\n-1 0 3 5 9 12\n9", "expectedOutput": "4", "hidden": False}, {"input": "6\n-1 0 3 5 9 12\n2", "expectedOutput": "-1", "hidden": False}]),
    ("Search a 2D Matrix", "Binary Search", "MEDIUM", "Search target in m x n matrix where each row is sorted and first integer of row > last integer of previous row.", "1 <= m, n <= 100", "Line 1: Integers m n\nNext m lines: n integers per line\nLine m+2: Integer target", "Print 'true' or 'false'.", "3 4\n1 3 5 7\n10 11 16 20\n23 30 34 60\n3", "true", [{"input": "3 4\n1 3 5 7\n10 11 16 20\n23 30 34 60\n3", "expectedOutput": "true", "hidden": False}, {"input": "3 4\n1 3 5 7\n10 11 16 20\n23 30 34 60\n13", "expectedOutput": "false", "hidden": False}]),
    ("Search a 2D Matrix II", "Binary Search", "MEDIUM", "Search target in m x n matrix where integers in each row and column are sorted ascending.", "1 <= m, n <= 300", "Line 1: Integers m n\nNext m lines: n integers per line\nLine m+2: Integer target", "Print 'true' or 'false'.", "5 5\n1 4 7 11 15\n2 5 8 12 19\n3 6 9 16 22\n10 13 14 17 24\n18 21 23 26 30\n5", "true", [{"input": "5 5\n1 4 7 11 15\n2 5 8 12 19\n3 6 9 16 22\n10 13 14 17 24\n18 21 23 26 30\n5", "expectedOutput": "true", "hidden": False}]),
    ("Koko Eating Bananas", "Binary Search", "MEDIUM", "Return minimum integer k such that Koko can eat all bananas within h hours.", "1 <= piles.length <= 10^4\npiles.length <= h <= 10^9", "Line 1: Integer n\nLine 2: n space-separated integers\nLine 3: Integer h", "Print minimum eating speed k.", "4\n3 6 7 11\n8", "4", [{"input": "4\n3 6 7 11\n8", "expectedOutput": "4", "hidden": False}, {"input": "5\n30 11 23 4 20\n5", "expectedOutput": "30", "hidden": False}]),
    ("Find Minimum in Rotated Sorted Array", "Binary Search", "MEDIUM", "Find minimum element in rotated sorted array of unique elements.", "1 <= nums.length <= 5000", "Line 1: Integer n\nLine 2: n space-separated integers", "Print minimum element.", "5\n3 4 5 1 2", "1", [{"input": "5\n3 4 5 1 2", "expectedOutput": "1", "hidden": False}, {"input": "7\n4 5 6 7 0 1 2", "expectedOutput": "0", "hidden": False}]),
    ("Find Minimum in Rotated Sorted Array II", "Binary Search", "HARD", "Find minimum element in rotated sorted array containing duplicates.", "1 <= nums.length <= 5000", "Line 1: Integer n\nLine 2: n space-separated integers", "Print minimum element.", "5\n2 2 2 0 1", "0", [{"input": "5\n2 2 2 0 1", "expectedOutput": "0", "hidden": False}]),
    ("Search in Rotated Sorted Array", "Binary Search", "MEDIUM", "Search target in rotated sorted array of unique elements. Return index or -1.", "1 <= nums.length <= 5000", "Line 1: Integer n\nLine 2: n integers\nLine 3: Integer target", "Print index or -1.", "7\n4 5 6 7 0 1 2\n0", "4", [{"input": "7\n4 5 6 7 0 1 2\n0", "expectedOutput": "4", "hidden": False}, {"input": "7\n4 5 6 7 0 1 2\n3", "expectedOutput": "-1", "hidden": False}]),
    ("Search in Rotated Sorted Array II", "Binary Search", "MEDIUM", "Search target in rotated sorted array with duplicates. Return true or false.", "1 <= nums.length <= 5000", "Line 1: Integer n\nLine 2: n integers\nLine 3: Integer target", "Print 'true' or 'false'.", "7\n2 5 6 0 0 1 2\n0", "true", [{"input": "7\n2 5 6 0 0 1 2\n0", "expectedOutput": "true", "hidden": False}, {"input": "7\n2 5 6 0 0 1 2\n3", "expectedOutput": "false", "hidden": False}]),
    ("Time Based Key-Value Store", "Binary Search", "MEDIUM", "Design time-based key-value data structure with SET key value timestamp and GET key timestamp.", "1 <= ops <= 10^4", "Line 1: Integer q\nNext q lines: command", "Print GET results.", "4\nSET foo bar 1\nGET foo 1\nGET foo 3\nSET foo bar2 4", "bar\nbar", [{"input": "4\nSET foo bar 1\nGET foo 1\nGET foo 3\nSET foo bar2 4", "expectedOutput": "bar\nbar", "hidden": False}]),
    ("Median of Two Sorted Arrays", "Binary Search", "HARD", "Given two sorted arrays nums1 and nums2, return median of the two sorted arrays in O(log (m+n)).", "0 <= nums1.length, nums2.length <= 1000", "Line 1: Integer n1\nLine 2: n1 integers\nLine 3: Integer n2\nLine 4: n2 integers", "Print median formatted to 1 decimal place.", "2\n1 3\n1\n2", "2.0", [{"input": "2\n1 3\n1\n2", "expectedOutput": "2.0", "hidden": False}, {"input": "2\n1 2\n2\n3 4", "expectedOutput": "2.5", "hidden": False}]),
    ("First Bad Version", "Binary Search", "EASY", "Find first bad version using minimum number of API calls.", "1 <= bad <= n <= 2^31 - 1", "Line 1: Integer n (total)\nLine 2: Integer bad (first bad version)", "Print first bad version.", "5\n4", "4", [{"input": "5\n4", "expectedOutput": "4", "hidden": False}, {"input": "1\n1", "expectedOutput": "1", "hidden": False}]),
    ("Capacity to Ship Packages Within D Days", "Binary Search", "MEDIUM", "Return least weight capacity of ship that will result in all packages being shipped within days.", "1 <= days <= weights.length <= 5 * 10^4", "Line 1: Integer n\nLine 2: n space-separated weights\nLine 3: Integer days", "Print minimum capacity.", "10\n1 2 3 4 5 6 7 8 9 10\n5", "15", [{"input": "10\n1 2 3 4 5 6 7 8 9 10\n5", "expectedOutput": "15", "hidden": False}]),
    ("Single Element in a Sorted Array", "Binary Search", "MEDIUM", "Every element appears twice except one in sorted array. Find single element in O(log n).", "1 <= nums.length <= 10^5", "Line 1: Integer n\nLine 2: n space-separated integers", "Print single element.", "9\n1 1 2 3 3 4 4 8 8", "2", [{"input": "9\n1 1 2 3 3 4 4 8 8", "expectedOutput": "2", "hidden": False}, {"input": "7\n3 3 7 7 10 11 11", "expectedOutput": "10", "hidden": False}]),
    ("Search Insert Position", "Binary Search", "EASY", "Return index if target is found. If not, return index where it would be if inserted in order.", "1 <= nums.length <= 10^4", "Line 1: Integer n\nLine 2: n integers\nLine 3: Integer target", "Print index.", "4\n1 3 5 6\n5", "2", [{"input": "4\n1 3 5 6\n5", "expectedOutput": "2", "hidden": False}, {"input": "4\n1 3 5 6\n2", "expectedOutput": "1", "hidden": False}, {"input": "4\n1 3 5 6\n7", "expectedOutput": "4", "hidden": True}]),
    ("Find Peak Element", "Binary Search", "MEDIUM", "A peak element is an element that is strictly greater than its neighbors. Find a peak and return its index.", "1 <= nums.length <= 1000", "Line 1: Integer n\nLine 2: n space-separated integers", "Print peak index.", "4\n1 2 3 1", "2", [{"input": "4\n1 2 3 1", "expectedOutput": "2", "hidden": False}]),
    ("Peak Index in a Mountain Array", "Binary Search", "MEDIUM", "Find index i such that arr[0] < ... < arr[i] > ... > arr[arr.length - 1] in O(log n).", "3 <= arr.length <= 10^5", "Line 1: Integer n\nLine 2: n space-separated integers", "Print peak index.", "3\n0 1 0", "1", [{"input": "3\n0 1 0", "expectedOutput": "1", "hidden": False}, {"input": "4\n0 2 1 0", "expectedOutput": "1", "hidden": False}]),
    ("Split Array Largest Sum", "Binary Search", "HARD", "Split array into k non-empty subarrays to minimize largest sum among subarrays.", "1 <= nums.length <= 1000\n1 <= k <= min(50, nums.length)", "Line 1: Integer n\nLine 2: n space-separated integers\nLine 3: Integer k", "Print minimized largest sum.", "5\n7 2 5 10 8\n2", "18", [{"input": "5\n7 2 5 10 8\n2", "expectedOutput": "18", "hidden": False}]),
    ("Find First and Last Position of Element in Sorted Array", "Binary Search", "MEDIUM", "Find starting and ending position of given target value in sorted array in O(log n).", "0 <= nums.length <= 10^5", "Line 1: Integer n\nLine 2: n integers\nLine 3: Integer target", "Print start and end indices space-separated.", "6\n5 7 7 8 8 10\n8", "3 4", [{"input": "6\n5 7 7 8 8 10\n8", "expectedOutput": "3 4", "hidden": False}, {"input": "6\n5 7 7 8 8 10\n6", "expectedOutput": "-1 -1", "hidden": False}])
]
batch_add_from_specs(bs_specs)

print(f"Total problems defined: {len(all_problems)}")

# Linked Lists (18)
ll_specs = [
    ("Reverse Linked List", "Linked List", "EASY", "Given head of singly linked list, reverse list, and return reversed list elements.", "0 <= nodes <= 5000", "Line 1: Integer n\nLine 2: n space-separated node values", "Print reversed list space-separated.", "5\n1 2 3 4 5", "5 4 3 2 1", [{"input": "5\n1 2 3 4 5", "expectedOutput": "5 4 3 2 1", "hidden": False}, {"input": "2\n1 2", "expectedOutput": "2 1", "hidden": False}]),
    ("Merge Two Sorted Lists", "Linked List", "EASY", "Merge two sorted linked lists and return it as a sorted list.", "0 <= n1, n2 <= 50", "Line 1: Integer n1\nLine 2: n1 values\nLine 3: Integer n2\nLine 4: n2 values", "Print merged list space-separated.", "3\n1 2 4\n3\n1 3 4", "1 1 2 3 4 4", [{"input": "3\n1 2 4\n3\n1 3 4", "expectedOutput": "1 1 2 3 4 4", "hidden": False}]),
    ("Reorder List", "Linked List", "MEDIUM", "Reorder list to: L0 -> Ln -> L1 -> Ln-1 -> L2 -> Ln-2 ...", "1 <= nodes <= 5 * 10^4", "Line 1: Integer n\nLine 2: n space-separated node values", "Print reordered list space-separated.", "4\n1 2 3 4", "1 4 2 3", [{"input": "4\n1 2 3 4", "expectedOutput": "1 4 2 3", "hidden": False}, {"input": "5\n1 2 3 4 5", "expectedOutput": "1 5 2 4 3", "hidden": False}]),
    ("Remove Nth Node From End of List", "Linked List", "MEDIUM", "Remove nth node from end of list and return its head in one pass.", "1 <= sz <= 30\n1 <= n <= sz", "Line 1: Integer sz\nLine 2: sz space-separated values\nLine 3: Integer n", "Print remaining list space-separated.", "5\n1 2 3 4 5\n2", "1 2 3 5", [{"input": "5\n1 2 3 4 5\n2", "expectedOutput": "1 2 3 5", "hidden": False}, {"input": "1\n1\n1", "expectedOutput": "", "hidden": False}]),
    ("Copy List with Random Pointer", "Linked List", "MEDIUM", "Construct deep copy of linked list where each node has next and random pointers.", "0 <= n <= 1000", "Line 1: Integer n\nLine 2: n node values", "Print copied node values.", "3\n7 13 11", "7 13 11", [{"input": "3\n7 13 11", "expectedOutput": "7 13 11", "hidden": False}]),
    ("Add Two Numbers", "Linked List", "MEDIUM", "Two non-empty linked lists represent non-negative integers in reverse order. Add two numbers and return sum as linked list.", "1 <= nodes <= 100", "Line 1: Integer n1\nLine 2: n1 digits\nLine 3: Integer n2\nLine 4: n2 digits", "Print sum digits in reverse order.", "3\n2 4 3\n3\n5 6 4", "7 0 8", [{"input": "3\n2 4 3\n3\n5 6 4", "expectedOutput": "7 0 8", "hidden": False}, {"input": "1\n0\n1\n0", "expectedOutput": "0", "hidden": False}]),
    ("Add Two Numbers II", "Linked List", "MEDIUM", "Add two numbers represented by linked lists in normal (most significant first) order.", "1 <= nodes <= 100", "Line 1: Integer n1\nLine 2: n1 digits\nLine 3: Integer n2\nLine 4: n2 digits", "Print sum digits in normal order.", "4\n7 2 4 3\n3\n5 6 4", "7 8 0 7", [{"input": "4\n7 2 4 3\n3\n5 6 4", "expectedOutput": "7 8 0 7", "hidden": False}]),
    ("Linked List Cycle", "Linked List", "EASY", "Determine if linked list has a cycle (Floyd's Tortoise and Hare).", "0 <= nodes <= 10^4", "Line 1: Integer n\nLine 2: n node values\nLine 3: Integer pos (cycle position or -1)", "Print 'true' or 'false'.", "4\n3 2 0 -4\n1", "true", [{"input": "4\n3 2 0 -4\n1", "expectedOutput": "true", "hidden": False}, {"input": "1\n1\n-1", "expectedOutput": "false", "hidden": False}]),
    ("Linked List Cycle II", "Linked List", "MEDIUM", "Return node where cycle begins, or -1 if no cycle exists.", "0 <= nodes <= 10^4", "Line 1: Integer n\nLine 2: n node values\nLine 3: Integer pos", "Print 0-based index of cycle entry node or -1.", "4\n3 2 0 -4\n1", "1", [{"input": "4\n3 2 0 -4\n1", "expectedOutput": "1", "hidden": False}, {"input": "2\n1 2\n-1", "expectedOutput": "-1", "hidden": False}]),
    ("Find the Duplicate Number", "Linked List", "MEDIUM", "Array of n + 1 integers where each integer is between [1, n]. Find duplicate number in O(1) extra space without modifying array.", "1 <= n <= 10^5", "Line 1: Integer n (array length = n+1)\nLine 2: n+1 integers", "Print duplicate number.", "5\n1 3 4 2 2", "2", [{"input": "5\n1 3 4 2 2", "expectedOutput": "2", "hidden": False}, {"input": "5\n3 1 3 4 2", "expectedOutput": "3", "hidden": False}]),
    ("LRU Cache", "Linked List", "MEDIUM", "Design data structure for Least Recently Used (LRU) Cache with GET and PUT in O(1).", "1 <= capacity <= 3000\n1 <= ops <= 10^4", "Line 1: Integer capacity q\nNext q lines: PUT k v or GET k", "Print result of GET operations.", "2 6\nPUT 1 1\nPUT 2 2\nGET 1\nPUT 3 3\nGET 2\nGET 3", "1\n-1\n3", [{"input": "2 6\nPUT 1 1\nPUT 2 2\nGET 1\nPUT 3 3\nGET 2\nGET 3", "expectedOutput": "1\n-1\n3", "hidden": False}]),
    ("LFU Cache", "Linked List", "HARD", "Design Least Frequently Used (LFU) cache in O(1) time.", "1 <= capacity <= 10^4", "Line 1: Integer capacity q\nNext q lines: PUT k v or GET k", "Print result of GET operations.", "2 6\nPUT 1 1\nPUT 2 2\nGET 1\nPUT 3 3\nGET 2\nGET 3", "1\n-1\n3", [{"input": "2 6\nPUT 1 1\nPUT 2 2\nGET 1\nPUT 3 3\nGET 2\nGET 3", "expectedOutput": "1\n-1\n3", "hidden": False}]),
    ("Merge k Sorted Lists", "Linked List", "HARD", "Merge k sorted linked lists and return it as one sorted list.", "0 <= k <= 10^4", "Line 1: Integer k\nNext k pairs of lines: count and space-separated values", "Print merged sorted list space-separated.", "3\n3\n1 4 5\n3\n1 3 4\n2\n2 6", "1 1 2 3 4 4 5 6", [{"input": "3\n3\n1 4 5\n3\n1 3 4\n2\n2 6", "expectedOutput": "1 1 2 3 4 4 5 6", "hidden": False}]),
    ("Reverse Nodes in k-Group", "Linked List", "HARD", "Reverse nodes of linked list k at a time and return modified list.", "1 <= k <= sz <= 5000", "Line 1: Integer sz\nLine 2: sz space-separated values\nLine 3: Integer k", "Print modified list space-separated.", "5\n1 2 3 4 5\n2", "2 1 4 3 5", [{"input": "5\n1 2 3 4 5\n2", "expectedOutput": "2 1 4 3 5", "hidden": False}, {"input": "5\n1 2 3 4 5\n3", "expectedOutput": "3 2 1 4 5", "hidden": False}]),
    ("Palindrome Linked List", "Linked List", "EASY", "Given head of singly linked list, return true if it is a palindrome.", "1 <= nodes <= 10^5", "Line 1: Integer n\nLine 2: n space-separated node values", "Print 'true' or 'false'.", "4\n1 2 2 1", "true", [{"input": "4\n1 2 2 1", "expectedOutput": "true", "hidden": False}, {"input": "2\n1 2", "expectedOutput": "false", "hidden": False}]),
    ("Intersection of Two Linked Lists", "Linked List", "EASY", "Return value of node at which two lists intersect, or null if no intersection.", "1 <= nodes <= 3 * 10^4", "Line 1: Integer intersectVal\nLine 2: Integer n1\nLine 3: n1 values\nLine 4: Integer n2\nLine 5: n2 values", "Print intersectVal or 'null'.", "8\n5\n4 1 8 4 5\n6\n5 6 1 8 4 5", "8", [{"input": "8\n5\n4 1 8 4 5\n6\n5 6 1 8 4 5", "expectedOutput": "8", "hidden": False}]),
    ("Odd Even Linked List", "Linked List", "MEDIUM", "Group all odd nodes together followed by even nodes.", "0 <= nodes <= 10^4", "Line 1: Integer n\nLine 2: n space-separated values", "Print modified list space-separated.", "5\n1 2 3 4 5", "1 3 5 2 4", [{"input": "5\n1 2 3 4 5", "expectedOutput": "1 3 5 2 4", "hidden": False}]),
    ("Swap Nodes in Pairs", "Linked List", "MEDIUM", "Given linked list, swap every two adjacent nodes and return its head.", "0 <= nodes <= 100", "Line 1: Integer n\nLine 2: n space-separated values", "Print swapped list space-separated.", "4\n1 2 3 4", "2 1 4 3", [{"input": "4\n1 2 3 4", "expectedOutput": "2 1 4 3", "hidden": False}])
]
batch_add_from_specs(ll_specs)

# Trees & BST (22)
tree_specs = [
    ("Invert Binary Tree", "Trees", "EASY", "Invert a binary tree and return level order traversal.", "0 <= nodes <= 100", "Line 1: Integer n\nLine 2: level order array (null for missing)", "Print inverted tree level order.", "7\n4 2 7 1 3 6 9", "4 7 2 9 6 3 1", [{"input": "7\n4 2 7 1 3 6 9", "expectedOutput": "4 7 2 9 6 3 1", "hidden": False}]),
    ("Maximum Depth of Binary Tree", "Trees", "EASY", "Find maximum depth (longest path from root down to farthest leaf).", "0 <= nodes <= 10^4", "Line 1: Integer n\nLine 2: level order nodes", "Print max depth.", "5\n3 9 20 null null 15 7", "3", [{"input": "5\n3 9 20 null null 15 7", "expectedOutput": "3", "hidden": False}]),
    ("Minimum Depth of Binary Tree", "Trees", "EASY", "Find minimum depth (shortest path from root down to nearest leaf).", "0 <= nodes <= 10^5", "Line 1: Integer n\nLine 2: level order nodes", "Print min depth.", "5\n3 9 20 null null 15 7", "2", [{"input": "5\n3 9 20 null null 15 7", "expectedOutput": "2", "hidden": False}]),
    ("Diameter of Binary Tree", "Trees", "EASY", "Find length of longest path between any two nodes in a tree.", "1 <= nodes <= 10^4", "Line 1: Integer n\nLine 2: level order nodes", "Print diameter.", "5\n1 2 3 4 5", "3", [{"input": "5\n1 2 3 4 5", "expectedOutput": "3", "hidden": False}]),
    ("Balanced Binary Tree", "Trees", "EASY", "Determine if binary tree is height-balanced.", "0 <= nodes <= 5000", "Line 1: Integer n\nLine 2: level order nodes", "Print 'true' or 'false'.", "5\n3 9 20 null null 15 7", "true", [{"input": "5\n3 9 20 null null 15 7", "expectedOutput": "true", "hidden": False}]),
    ("Same Tree", "Trees", "EASY", "Given roots of two binary trees, check if they are identical.", "0 <= nodes <= 100", "Line 1: Tree 1 nodes\nLine 2: Tree 2 nodes", "Print 'true' or 'false'.", "3\n1 2 3\n3\n1 2 3", "true", [{"input": "3\n1 2 3\n3\n1 2 3", "expectedOutput": "true", "hidden": False}]),
    ("Symmetric Tree", "Trees", "EASY", "Check whether a binary tree is a mirror of itself.", "1 <= nodes <= 1000", "Line 1: Integer n\nLine 2: level order nodes", "Print 'true' or 'false'.", "7\n1 2 2 3 4 4 3", "true", [{"input": "7\n1 2 2 3 4 4 3", "expectedOutput": "true", "hidden": False}]),
    ("Subtree of Another Tree", "Trees", "EASY", "Check if tree subRoot is a subtree of root.", "1 <= nodes <= 2000", "Line 1: Root nodes\nLine 2: SubRoot nodes", "Print 'true' or 'false'.", "5\n3 4 5 1 2\n3\n4 1 2", "true", [{"input": "5\n3 4 5 1 2\n3\n4 1 2", "expectedOutput": "true", "hidden": False}]),
    ("Lowest Common Ancestor of BST", "Trees", "MEDIUM", "Find lowest common ancestor (LCA) node of two given nodes in BST.", "2 <= nodes <= 10^5", "Line 1: BST level order\nLine 2: p and q values", "Print LCA value.", "9\n6 2 8 0 4 7 9 null null 3 5\n2 8", "6", [{"input": "9\n6 2 8 0 4 7 9 null null 3 5\n2 8", "expectedOutput": "6", "hidden": False}]),
    ("Lowest Common Ancestor of Binary Tree", "Trees", "MEDIUM", "Find LCA of two nodes in binary tree.", "2 <= nodes <= 10^5", "Line 1: Tree level order\nLine 2: p and q values", "Print LCA value.", "9\n3 5 1 6 2 0 8 null null 7 4\n5 1", "3", [{"input": "9\n3 5 1 6 2 0 8 null null 7 4\n5 1", "expectedOutput": "3", "hidden": False}]),
    ("Binary Tree Level Order Traversal", "Trees", "MEDIUM", "Return level order traversal of nodes values.", "0 <= nodes <= 2000", "Line 1: Integer n\nLine 2: level order nodes", "Print each level on new line.", "5\n3 9 20 null null 15 7", "3\n9 20\n15 7", [{"input": "5\n3 9 20 null null 15 7", "expectedOutput": "3\n9 20\n15 7", "hidden": False}]),
    ("Binary Tree Zigzag Level Order Traversal", "Trees", "MEDIUM", "Return zigzag level order traversal (alternating left-to-right and right-to-left).", "0 <= nodes <= 2000", "Line 1: Integer n\nLine 2: level order nodes", "Print each level on new line.", "5\n3 9 20 null null 15 7", "3\n20 9\n15 7", [{"input": "5\n3 9 20 null null 15 7", "expectedOutput": "3\n20 9\n15 7", "hidden": False}]),
    ("Binary Tree Right Side View", "Trees", "MEDIUM", "Return values of nodes visible when standing on right side of tree.", "0 <= nodes <= 100", "Line 1: Integer n\nLine 2: level order nodes", "Print right view space-separated.", "5\n1 2 3 null 5 null 4", "1 3 4", [{"input": "5\n1 2 3 null 5 null 4", "expectedOutput": "1 3 4", "hidden": False}]),
    ("Count Good Nodes in Binary Tree", "Trees", "MEDIUM", "Count nodes X where no node on path from root to X has value > X.", "1 <= nodes <= 10^5", "Line 1: Integer n\nLine 2: level order nodes", "Print count of good nodes.", "6\n3 1 4 3 null 1 5", "4", [{"input": "6\n3 1 4 3 null 1 5", "expectedOutput": "4", "hidden": False}]),
    ("Validate Binary Search Tree", "Trees", "MEDIUM", "Determine if binary tree is valid BST.", "1 <= nodes <= 10^4", "Line 1: Integer n\nLine 2: level order nodes", "Print 'true' or 'false'.", "3\n2 1 3", "true", [{"input": "3\n2 1 3", "expectedOutput": "true", "hidden": False}, {"input": "5\n5 1 4 null null 3 6", "expectedOutput": "false", "hidden": False}]),
    ("Kth Smallest Element in a BST", "Trees", "MEDIUM", "Find kth smallest element in BST (1-indexed).", "1 <= k <= nodes <= 10^4", "Line 1: BST nodes\nLine 2: Integer k", "Print kth smallest value.", "4\n3 1 4 null 2\n1", "1", [{"input": "4\n3 1 4 null 2\n1", "expectedOutput": "1", "hidden": False}]),
    ("Construct Binary Tree from Preorder and Inorder Traversal", "Trees", "MEDIUM", "Construct binary tree given preorder and inorder traversals.", "1 <= nodes <= 3000", "Line 1: Integer n\nLine 2: preorder\nLine 3: inorder", "Print level order traversal.", "5\n3 9 20 15 7\n9 3 15 20 7", "3 9 20 15 7", [{"input": "5\n3 9 20 15 7\n9 3 15 20 7", "expectedOutput": "3 9 20 15 7", "hidden": False}]),
    ("Construct Binary Tree from Inorder and Postorder Traversal", "Trees", "MEDIUM", "Construct binary tree given inorder and postorder traversals.", "1 <= nodes <= 3000", "Line 1: Integer n\nLine 2: inorder\nLine 3: postorder", "Print level order traversal.", "5\n9 3 15 20 7\n9 15 7 20 3", "3 9 20 15 7", [{"input": "5\n9 3 15 20 7\n9 15 7 20 3", "expectedOutput": "3 9 20 15 7", "hidden": False}]),
    ("Binary Tree Maximum Path Sum", "Trees", "HARD", "Return maximum path sum of any non-empty path in binary tree.", "1 <= nodes <= 3 * 10^4", "Line 1: Integer n\nLine 2: level order nodes", "Print max path sum.", "3\n1 2 3", "6", [{"input": "3\n1 2 3", "expectedOutput": "6", "hidden": False}, {"input": "5\n-10 9 20 null null 15 7", "expectedOutput": "42", "hidden": False}]),
    ("Serialize and Deserialize Binary Tree", "Trees", "HARD", "Design algorithm to serialize binary tree to string and deserialize back to original tree structure.", "0 <= nodes <= 10^4", "Line 1: Tree serialized string", "Print round-trip verified string.", "1,2,3,null,null,4,5", "1,2,3,null,null,4,5", [{"input": "1,2,3,null,null,4,5", "expectedOutput": "1,2,3,null,null,4,5", "hidden": False}]),
    ("Flatten Binary Tree to Linked List", "Trees", "MEDIUM", "Flatten binary tree into pre-order single right-skewed linked list in-place.", "0 <= nodes <= 2000", "Line 1: Integer n\nLine 2: level order nodes", "Print flattened right-child values.", "6\n1 2 5 3 4 null 6", "1 2 3 4 5 6", [{"input": "6\n1 2 5 3 4 null 6", "expectedOutput": "1 2 3 4 5 6", "hidden": False}]),
    ("Path Sum", "Trees", "EASY", "Given root and targetSum, return true if tree has root-to-leaf path summing to targetSum.", "0 <= nodes <= 5000", "Line 1: Level order nodes\nLine 2: Integer targetSum", "Print 'true' or 'false'.", "9\n5 4 8 11 null 13 4 7 2 null null null 1\n22", "true", [{"input": "9\n5 4 8 11 null 13 4 7 2 null null null 1\n22", "expectedOutput": "true", "hidden": False}])
]
batch_add_from_specs(tree_specs)

# Tries & Strings (12)
trie_specs = [
    ("Implement Trie (Prefix Tree)", "Tries", "MEDIUM", "Implement Trie prefix tree with INSERT, SEARCH, and STARTSWITH.", "1 <= ops <= 10^4", "Line 1: Integer q\nNext q lines: command", "Print SEARCH and STARTSWITH boolean results.", "5\nINSERT apple\nSEARCH apple\nSEARCH app\nSTARTSWITH app\nINSERT app", "true\nfalse\ntrue", [{"input": "5\nINSERT apple\nSEARCH apple\nSEARCH app\nSTARTSWITH app\nINSERT app", "expectedOutput": "true\nfalse\ntrue", "hidden": False}]),
    ("Design Add and Search Words Data Structure", "Tries", "MEDIUM", "Design data structure supporting ADDWORD and SEARCH with '.' matching any letter.", "1 <= ops <= 10^4", "Line 1: Integer q\nNext q lines: command", "Print SEARCH boolean results.", "5\nADDWORD bad\nADDWORD dad\nSEARCH pad\nSEARCH bad\nSEARCH .ad", "false\ntrue\ntrue", [{"input": "5\nADDWORD bad\nADDWORD dad\nSEARCH pad\nSEARCH bad\nSEARCH .ad", "expectedOutput": "false\ntrue\ntrue", "hidden": False}]),
    ("Word Search II", "Tries", "HARD", "Given m x n board and list of strings words, return all words found on board sorted alphabetically.", "1 <= m, n <= 12", "Line 1: Integers m n\nNext m lines: board chars\nLine m+2: Integer w (word count)\nLine m+3: w space-separated words", "Print matched words space-separated.", "4 4\no a a n\ne t a e\ni h k r\ni f l v\n4\noath pea eat rain", "eat oath", [{"input": "4 4\no a a n\ne t a e\ni h k r\ni f l v\n4\noath pea eat rain", "expectedOutput": "eat oath", "hidden": False}]),
    ("Longest Common Prefix", "Tries", "EASY", "Find longest common prefix string amongst array of strings.", "1 <= strs.length <= 200", "Line 1: Integer n\nLine 2: n space-separated strings", "Print longest common prefix.", "3\nflower flow flight", "fl", [{"input": "3\nflower flow flight", "expectedOutput": "fl", "hidden": False}, {"input": "3\ndog racecar car", "expectedOutput": "", "hidden": False}]),
    ("String to Integer (atoi)", "Strings", "MEDIUM", "Convert string to 32-bit signed integer with whitespace trimming, signs, and clamping.", "0 <= s.length <= 200", "Line 1: String s", "Print converted 32-bit integer.", "   -42", "-42", [{"input": "   -42", "expectedOutput": "-42", "hidden": False}, {"input": "4193 with words", "expectedOutput": "4193", "hidden": False}]),
    ("Zigzag Conversion", "Strings", "MEDIUM", "Convert string in zigzag pattern on given numRows and read line by line.", "1 <= s.length <= 1000\n1 <= numRows <= 1000", "Line 1: String s\nLine 2: Integer numRows", "Print zigzag read string.", "PAYPALISHIRING\n3", "PAHNAPLSIIGYIR", [{"input": "PAYPALISHIRING\n3", "expectedOutput": "PAHNAPLSIIGYIR", "hidden": False}]),
    ("Count and Say", "Strings", "MEDIUM", "The count-and-say sequence is defined recursively. Generate the nth term.", "1 <= n <= 30", "Line 1: Integer n", "Print nth sequence string.", "4", "1211", [{"input": "4", "expectedOutput": "1211", "hidden": False}, {"input": "1", "expectedOutput": "1", "hidden": False}]),
    ("Multiply Strings", "Strings", "MEDIUM", "Given two non-negative integers as strings num1 and num2, return product without built-in BigInteger.", "1 <= num1.length, num2.length <= 200", "Line 1: String num1\nLine 2: String num2", "Print product string.", "2\n3", "6", [{"input": "2\n3", "expectedOutput": "6", "hidden": False}, {"input": "123\n456", "expectedOutput": "56088", "hidden": False}]),
    ("Compare Version Numbers", "Strings", "MEDIUM", "Compare two version strings version1 and version2. Return 1, -1, or 0.", "1 <= length <= 500", "Line 1: String v1\nLine 2: String v2", "Print 1, -1, or 0.", "1.2\n1.10", "-1", [{"input": "1.2\n1.10", "expectedOutput": "-1", "hidden": False}, {"input": "1.01\n1.001", "expectedOutput": "0", "hidden": False}]),
    ("Repeated DNA Sequences", "Strings", "MEDIUM", "Find all 10-letter-long sequences (substrings) that occur more than once in DNA molecule string.", "0 <= s.length <= 10^5", "Line 1: String s", "Print repeating sequences space-separated.", "AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT", "AAAAACCCCC CCCCCAAAAA", [{"input": "AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT", "expectedOutput": "AAAAACCCCC CCCCCAAAAA", "hidden": False}]),
    ("Longest Duplicate Substring", "Strings", "HARD", "Find longest duplicate substring in s using Rabin-Karp or Suffix Automaton.", "2 <= s.length <= 3 * 10^4", "Line 1: String s", "Print longest duplicate substring.", "banana", "ana", [{"input": "banana", "expectedOutput": "ana", "hidden": False}]),
    ("Maximum XOR of Two Numbers in an Array", "Tries", "MEDIUM", "Find maximum XOR of two numbers in nums using Bitwise Trie in O(n).", "1 <= nums.length <= 2 * 10^5", "Line 1: Integer n\nLine 2: n space-separated integers", "Print maximum XOR.", "6\n3 10 5 25 2 8", "28", [{"input": "6\n3 10 5 25 2 8", "expectedOutput": "28", "hidden": False}])
]
batch_add_from_specs(trie_specs)

# Heaps & Priority Queues (14)
heap_specs = [
    ("Kth Largest Element in a Stream", "Heap", "EASY", "Design class to find kth largest element in stream of integers.", "1 <= k <= 10^4", "Line 1: Integer k n\nLine 2: n integers\nLine 3: Integer adds count\nLine 4: values to add", "Print kth largest after each add.", "3 4\n4 5 8 2\n4\n3 5 10 9", "4 5 5 8", [{"input": "3 4\n4 5 8 2\n4\n3 5 10 9", "expectedOutput": "4 5 5 8", "hidden": False}]),
    ("Last Stone Weight", "Heap", "EASY", "Smash heaviest stones x and y. Return weight of last stone or 0.", "1 <= stones.length <= 30", "Line 1: Integer n\nLine 2: n stone weights", "Print last stone weight.", "6\n2 7 4 1 8 1", "1", [{"input": "6\n2 7 4 1 8 1", "expectedOutput": "1", "hidden": False}]),
    ("K Closest Points to Origin", "Heap", "MEDIUM", "Find k closest points to origin (0, 0) using Euclidean distance.", "1 <= k <= points.length <= 10^4", "Line 1: Integer n k\nNext n lines: x y coordinates", "Print k closest points (x y) per line.", "2 1\n1 3\n-2 2", "-2 2", [{"input": "2 1\n1 3\n-2 2", "expectedOutput": "-2 2", "hidden": False}]),
    ("Kth Largest Element in an Array", "Heap", "MEDIUM", "Find kth largest element in unsorted array in O(n) using QuickSelect or Min-Heap.", "1 <= k <= nums.length <= 10^5", "Line 1: Integer n\nLine 2: n integers\nLine 3: Integer k", "Print kth largest element.", "6\n3 2 1 5 6 4\n2", "5", [{"input": "6\n3 2 1 5 6 4\n2", "expectedOutput": "5", "hidden": False}]),
    ("Task Scheduler", "Heap", "MEDIUM", "Find least number of CPU intervals needed to complete tasks with cooldown n.", "1 <= tasks.length <= 10^4\n0 <= n <= 100", "Line 1: Integer count n\nLine 2: space-separated task chars", "Print minimum intervals.", "6 2\nA A A B B B", "8", [{"input": "6 2\nA A A B B B", "expectedOutput": "8", "hidden": False}]),
    ("Design Twitter", "Heap", "MEDIUM", "Design simplified Twitter with postTweet, getNewsFeed, follow, and unfollow.", "1 <= ops <= 10^4", "Line 1: Integer q\nNext q lines: command", "Print news feed tweet IDs space-separated.", "5\nPOST 1 5\nFEED 1\nFOLLOW 1 2\nPOST 2 6\nFEED 1", "5\n6 5", [{"input": "5\nPOST 1 5\nFEED 1\nFOLLOW 1 2\nPOST 2 6\nFEED 1", "expectedOutput": "5\n6 5", "hidden": False}]),
    ("Find Median from Data Stream", "Heap", "HARD", "Design data structure to find running median from data stream using Two Heaps.", "1 <= ops <= 5 * 10^4", "Line 1: Integer q\nNext q lines: ADD num or FIND", "Print median formatted to 1 decimal place.", "5\nADD 1\nADD 2\nFIND\nADD 3\nFIND", "1.5\n2.0", [{"input": "5\nADD 1\nADD 2\nFIND\nADD 3\nFIND", "expectedOutput": "1.5\n2.0", "hidden": False}]),
    ("Reorganize String", "Heap", "MEDIUM", "Rearrange characters of s so that any two adjacent characters are not same.", "1 <= s.length <= 500", "Line 1: String s", "Print reorganized string or empty string if impossible.", "aab", "aba", [{"input": "aab", "expectedOutput": "aba", "hidden": False}, {"input": "aaab", "expectedOutput": "", "hidden": False}]),
    ("Top K Frequent Words", "Heap", "MEDIUM", "Return k most frequent strings sorted by frequency and lexicographical order.", "1 <= words.length <= 500\n1 <= k <= unique words", "Line 1: Integer n k\nLine 2: n space-separated words", "Print top k words space-separated.", "6 2\ni love leetcode i love coding", "i love", [{"input": "6 2\ni love leetcode i love coding", "expectedOutput": "i love", "hidden": False}]),
    ("Seat Reservation Manager", "Heap", "MEDIUM", "Design seat reservation manager that reserves smallest-numbered unreserved seat.", "1 <= n <= 10^5", "Line 1: Integer n q\nNext q lines: RESERVE or UNRESERVE x", "Print seat numbers reserved.", "5 4\nRESERVE\nRESERVE\nUNRESERVE 2\nRESERVE", "1\n2\n2", [{"input": "5 4\nRESERVE\nRESERVE\nUNRESERVE 2\nRESERVE", "expectedOutput": "1\n2\n2", "hidden": False}]),
    ("Single-Threaded CPU", "Heap", "MEDIUM", "Given n tasks with [enqueueTime, processingTime], return order of task indices processed.", "1 <= n <= 10^5", "Line 1: Integer n\nNext n lines: enqueueTime processingTime", "Print task indices space-separated.", "4\n1 2\n2 4\n3 2\n4 1", "0 2 3 1", [{"input": "4\n1 2\n2 4\n3 2\n4 1", "expectedOutput": "0 2 3 1", "hidden": False}]),
    ("Maximum Subsequence Score", "Heap", "MEDIUM", "Select subsequence of indices of size k to maximize sum(nums1[i]) * min(nums2[i]).", "1 <= k <= n <= 10^5", "Line 1: Integer n k\nLine 2: n integers for nums1\nLine 3: n integers for nums2", "Print maximum score.", "4 3\n1 3 3 2\n2 1 3 4", "12", [{"input": "4 3\n1 3 3 2\n2 1 3 4", "expectedOutput": "12", "hidden": False}]),
    ("Total Cost to Hire K Workers", "Heap", "MEDIUM", "Hire k workers with lowest cost from first candidates or last candidates.", "1 <= k, candidates <= costs.length <= 10^5", "Line 1: Integer n k candidates\nLine 2: n costs", "Print total cost.", "7 3 2\n17 12 10 2 7 2 11", "11", [{"input": "7 3 2\n17 12 10 2 7 2 11", "expectedOutput": "11", "hidden": False}]),
    ("IPO", "Heap", "HARD", "Maximize capital after at most k distinct projects using Max-Heap and Min-Heap.", "1 <= k <= 10^5\n0 <= w <= 10^9", "Line 1: Integer k w n\nLine 2: n profits\nLine 3: n capital requirements", "Print final maximized capital.", "2 0 3\n1 2 3\n0 1 1", "4", [{"input": "2 0 3\n1 2 3\n0 1 1", "expectedOutput": "4", "hidden": False}])
]
batch_add_from_specs(heap_specs)

# Backtracking (18)
bt_specs = [
    ("Subsets", "Backtracking", "MEDIUM", "Given an integer array nums of unique elements, return all possible subsets (the power set). Print count and sorted subsets.", "1 <= nums.length <= 10", "Line 1: Integer n\nLine 2: n space-separated integers", "Line 1: Total subsets count. Next lines: subset elements space-separated.", "3\n1 2 3", "8\n\n1\n1 2\n1 2 3\n1 3\n2\n2 3\n3", [{"input": "3\n1 2 3", "expectedOutput": "8\n\n1\n1 2\n1 2 3\n1 3\n2\n2 3\n3", "hidden": False}]),
    ("Subsets II", "Backtracking", "MEDIUM", "Return all possible subsets from array with duplicates without duplicate subsets in output.", "1 <= nums.length <= 10", "Line 1: Integer n\nLine 2: n space-separated integers", "Print count of unique subsets.", "3\n1 2 2", "6", [{"input": "3\n1 2 2", "expectedOutput": "6", "hidden": False}]),
    ("Combination Sum", "Backtracking", "MEDIUM", "Find all unique combinations in candidates where candidate numbers sum to target.", "1 <= candidates.length <= 30\n2 <= target <= 40", "Line 1: Integer n\nLine 2: n space-separated integers\nLine 3: Integer target", "Print count of combinations.", "4\n2 3 6 7\n7", "2", [{"input": "4\n2 3 6 7\n7", "expectedOutput": "2", "hidden": False}]),
    ("Combination Sum II", "Backtracking", "MEDIUM", "Find all unique combinations where each number in candidates may only be used once to reach target.", "1 <= candidates.length <= 100\n1 <= target <= 30", "Line 1: Integer n\nLine 2: n integers\nLine 3: Integer target", "Print count of combinations.", "7\n10 1 2 7 6 1 5\n8", "4", [{"input": "7\n10 1 2 7 6 1 5\n8", "expectedOutput": "4", "hidden": False}]),
    ("Combination Sum III", "Backtracking", "MEDIUM", "Find all valid combinations of k numbers that sum up to n using only numbers 1 to 9 once.", "2 <= k <= 9\n1 <= n <= 60", "Line 1: Integer k n", "Print count of valid combinations.", "3 7", "1", [{"input": "3 7", "expectedOutput": "1", "hidden": False}, {"input": "3 9", "expectedOutput": "3", "hidden": False}]),
    ("Combinations", "Backtracking", "MEDIUM", "Given two integers n and k, return all possible combinations of k numbers chosen from [1, n].", "1 <= n <= 20\n1 <= k <= n", "Line 1: Integer n k", "Print combinations count.", "4 2", "6", [{"input": "4 2", "expectedOutput": "6", "hidden": False}]),
    ("Permutations", "Backtracking", "MEDIUM", "Given an array nums of distinct integers, return all the possible permutations count and listings.", "1 <= nums.length <= 6", "Line 1: Integer n\nLine 2: n space-separated integers", "Print count of permutations.", "3\n1 2 3", "6", [{"input": "3\n1 2 3", "expectedOutput": "6", "hidden": False}]),
    ("Permutations II", "Backtracking", "MEDIUM", "Return all unique permutations from sequence containing duplicates.", "1 <= nums.length <= 8", "Line 1: Integer n\nLine 2: n space-separated integers", "Print count of unique permutations.", "3\n1 1 2", "3", [{"input": "3\n1 1 2", "expectedOutput": "3", "hidden": False}]),
    ("Word Search", "Backtracking", "MEDIUM", "Given m x n grid of characters and string word, return true if word exists in grid.", "1 <= m, n <= 6", "Line 1: Integers m n\nNext m lines: grid chars\nLine m+2: String word", "Print 'true' or 'false'.", "3 4\nA B C E\nS F C S\nA D E E\nABCCED", "true", [{"input": "3 4\nA B C E\nS F C S\nA D E E\nABCCED", "expectedOutput": "true", "hidden": False}, {"input": "3 4\nA B C E\nS F C S\nA D E E\nABCB", "expectedOutput": "false", "hidden": False}]),
    ("Palindrome Partitioning", "Backtracking", "MEDIUM", "Partition string s such that every substring of partition is a palindrome.", "1 <= s.length <= 16", "Line 1: String s", "Print count of palindrome partitions.", "aab", "2", [{"input": "aab", "expectedOutput": "2", "hidden": False}, {"input": "a", "expectedOutput": "1", "hidden": False}]),
    ("Letter Combinations of a Phone Number", "Backtracking", "MEDIUM", "Return all possible letter combinations that number digits from 2-9 could represent.", "0 <= digits.length <= 4", "Line 1: String digits", "Print combinations space-separated.", "23", "ad ae af bd be bf cd ce cf", [{"input": "23", "expectedOutput": "ad ae af bd be bf cd ce cf", "hidden": False}]),
    ("N-Queens", "Backtracking", "HARD", "Place n queens on n x n chessboard such that no two queens attack each other. Return distinct solutions count.", "1 <= n <= 9", "Line 1: Integer n", "Print number of distinct solutions.", "4", "2", [{"input": "4", "expectedOutput": "2", "hidden": False}, {"input": "1", "expectedOutput": "1", "hidden": False}]),
    ("N-Queens II", "Backtracking", "HARD", "Return number of distinct solutions to n-queens puzzle.", "1 <= n <= 9", "Line 1: Integer n", "Print number of solutions.", "8", "92", [{"input": "8", "expectedOutput": "92", "hidden": False}]),
    ("Sudoku Solver", "Backtracking", "HARD", "Solve a 9x9 Sudoku board by filling empty cells ('.').", "Board is 9x9 valid Sudoku", "9 lines: 9 chars per line", "Print 9 solved lines.", "5 3 . . 7 . . . .\n6 . . 1 9 5 . . .\n. 9 8 . . . . 6 .\n8 . . . 6 . . . 3\n4 . . 8 . 3 . . 1\n7 . . . 2 . . . 6\n. 6 . . . . 2 8 .\n. . . 4 1 9 . . 5\n. . . . 8 . . 7 9", "5 3 4 6 7 8 9 1 2\n6 7 2 1 9 5 3 4 8\n1 9 8 3 4 2 5 6 7\n8 5 9 7 6 1 4 2 3\n4 2 6 8 5 3 7 9 1\n7 1 3 9 2 4 8 5 6\n9 6 1 5 3 7 2 8 4\n2 8 7 4 1 9 6 3 5\n3 4 5 2 8 6 1 7 9", [{"input": "5 3 . . 7 . . . .\n6 . . 1 9 5 . . .\n. 9 8 . . . . 6 .\n8 . . . 6 . . . 3\n4 . . 8 . 3 . . 1\n7 . . . 2 . . . 6\n. 6 . . . . 2 8 .\n. . . 4 1 9 . . 5\n. . . . 8 . . 7 9", "expectedOutput": "5 3 4 6 7 8 9 1 2\n6 7 2 1 9 5 3 4 8\n1 9 8 3 4 2 5 6 7\n8 5 9 7 6 1 4 2 3\n4 2 6 8 5 3 7 9 1\n7 1 3 9 2 4 8 5 6\n9 6 1 5 3 7 2 8 4\n2 8 7 4 1 9 6 3 5\n3 4 5 2 8 6 1 7 9", "hidden": False}]),
    ("Generate Parentheses", "Backtracking", "MEDIUM", "Given n pairs of parentheses, generate all combinations of well-formed parentheses.", "1 <= n <= 8", "Line 1: Integer n", "Print combinations count on line 1, space-separated combinations on line 2.", "3", "5\n((())) (()()) (())() ()(()) ()()()", [{"input": "3", "expectedOutput": "5\n((())) (()()) (())() ()(()) ()()()", "hidden": False}, {"input": "1", "expectedOutput": "1\n()", "hidden": False}]),
    ("Restore IP Addresses", "Backtracking", "MEDIUM", "Given string s containing only digits, return all possible valid IPv4 addresses.", "1 <= s.length <= 20", "Line 1: String s", "Print count of valid IP addresses.", "25525511135", "2", [{"input": "25525511135", "expectedOutput": "2", "hidden": False}, {"input": "0000", "expectedOutput": "1", "hidden": False}]),
    ("Matchsticks to Square", "Backtracking", "MEDIUM", "Determine if you can make a square using all matchsticks.", "1 <= matchsticks.length <= 15", "Line 1: Integer n\nLine 2: n integers", "Print 'true' or 'false'.", "5\n1 1 2 2 2", "true", [{"input": "5\n1 1 2 2 2", "expectedOutput": "true", "hidden": False}, {"input": "5\n3 3 3 3 4", "expectedOutput": "false", "hidden": False}]),
    ("Partition to K Equal Sum Subsets", "Backtracking", "MEDIUM", "Check if array can be partitioned into k non-empty subsets whose sums are all equal.", "1 <= k <= nums.length <= 16", "Line 1: Integer n k\nLine 2: n integers", "Print 'true' or 'false'.", "7 4\n4 3 2 3 5 2 1", "true", [{"input": "7 4\n4 3 2 3 5 2 1", "expectedOutput": "true", "hidden": False}])
]
batch_add_from_specs(bt_specs)

# Graphs & Union Find (22)
graph_specs = [
    ("Number of Islands", "Graphs", "MEDIUM", "Given m x n 2D binary grid, return number of islands (connected 1's horizontally/vertically).", "1 <= m, n <= 300", "Line 1: Integers m n\nNext m lines: binary grid strings", "Print island count.", "4 5\n11110\n11010\n11000\n00000", "1", [{"input": "4 5\n11110\n11010\n11000\n00000", "expectedOutput": "1", "hidden": False}, {"input": "4 5\n11000\n11000\n00100\n00011", "expectedOutput": "3", "hidden": False}]),
    ("Max Area of Island", "Graphs", "MEDIUM", "Find maximum area of an island in m x n binary grid.", "1 <= m, n <= 50", "Line 1: Integers m n\nNext m lines: space-separated 0/1 integers", "Print maximum island area.", "4 5\n0 0 1 0 0\n0 1 1 1 0\n0 0 0 0 0\n1 1 0 1 1", "4", [{"input": "4 5\n0 0 1 0 0\n0 1 1 1 0\n0 0 0 0 0\n1 1 0 1 1", "expectedOutput": "4", "hidden": False}]),
    ("Clone Graph", "Graphs", "MEDIUM", "Given reference of node in connected undirected graph, return deep copy (clone) of graph.", "0 <= nodes <= 100", "Line 1: Integer n (node count)\nNext n lines: neighbors of node i", "Print cloned graph nodes count.", "4\n2 4\n1 3\n2 4\n1 3", "4", [{"input": "4\n2 4\n1 3\n2 4\n1 3", "expectedOutput": "4", "hidden": False}]),
    ("Walls and Gates", "Graphs", "MEDIUM", "Fill each empty room with distance to nearest gate (2147483647 is empty room, 0 is gate, -1 is wall).", "1 <= m, n <= 250", "Line 1: Integers m n\nNext m lines: space-separated integers", "Print updated grid distances.", "4 4\n2147483647 -1 0 2147483647\n2147483647 2147483647 2147483647 -1\n2147483647 -1 2147483647 -1\n0 -1 2147483647 2147483647", "3 -1 0 1\n2 2 1 -1\n1 -1 2 -1\n0 -1 3 4", [{"input": "4 4\n2147483647 -1 0 2147483647\n2147483647 2147483647 2147483647 -1\n2147483647 -1 2147483647 -1\n0 -1 2147483647 2147483647", "expectedOutput": "3 -1 0 1\n2 2 1 -1\n1 -1 2 -1\n0 -1 3 4", "hidden": False}]),
    ("Rotting Oranges", "Graphs", "MEDIUM", "Return minimum number of minutes that must elapse until no cell has a fresh orange (1), or -1 if impossible.", "1 <= m, n <= 10", "Line 1: Integers m n\nNext m lines: space-separated integers (0, 1, 2)", "Print minimum minutes or -1.", "3 3\n2 1 1\n1 1 0\n0 1 1", "4", [{"input": "3 3\n2 1 1\n1 1 0\n0 1 1", "expectedOutput": "4", "hidden": False}, {"input": "3 3\n2 1 1\n0 1 1\n1 0 1", "expectedOutput": "-1", "hidden": False}]),
    ("Pacific Atlantic Water Flow", "Graphs", "MEDIUM", "Return coordinates where rain water can flow to both Pacific and Atlantic ocean.", "1 <= m, n <= 200", "Line 1: Integers m n\nNext m lines: n matrix heights", "Print count of valid coordinates.", "5 5\n1 2 2 3 5\n3 2 3 4 4\n2 4 5 3 1\n6 7 1 4 5\n5 1 1 2 4", "7", [{"input": "5 5\n1 2 2 3 5\n3 2 3 4 4\n2 4 5 3 1\n6 7 1 4 5\n5 1 1 2 4", "expectedOutput": "7", "hidden": False}]),
    ("Surrounded Regions", "Graphs", "MEDIUM", "Capture all regions that are 4-directionally surrounded by 'X' by replacing 'O' with 'X'.", "1 <= m, n <= 200", "Line 1: Integers m n\nNext m lines: n space-separated chars (X/O)", "Print modified board.", "4 4\nX X X X\nX O O X\nX X O X\nX O X X", "X X X X\nX X X X\nX X X X\nX O X X", [{"input": "4 4\nX X X X\nX O O X\nX X O X\nX O X X", "expectedOutput": "X X X X\nX X X X\nX X X X\nX O X X", "hidden": False}]),
    ("Course Schedule", "Graphs", "MEDIUM", "Determine if you can finish all courses given prerequisites using Topological Sort / Kahn's Algorithm.", "1 <= numCourses <= 2000", "Line 1: Integer numCourses numPrereqs\nNext numPrereqs lines: a b (b before a)", "Print 'true' or 'false'.", "2 1\n1 0", "true", [{"input": "2 1\n1 0", "expectedOutput": "true", "hidden": False}, {"input": "2 2\n1 0\n0 1", "expectedOutput": "false", "hidden": False}]),
    ("Course Schedule II", "Graphs", "MEDIUM", "Return ordering of courses you should take to finish all courses using Topological Sort.", "1 <= numCourses <= 2000", "Line 1: Integer numCourses numPrereqs\nNext numPrereqs lines: a b", "Print valid ordering space-separated or empty string.", "4 4\n1 0\n2 0\n3 1\n3 2", "0 1 2 3", [{"input": "4 4\n1 0\n2 0\n3 1\n3 2", "expectedOutput": "0 1 2 3", "hidden": False}]),
    ("Graph Valid Tree", "Graphs", "MEDIUM", "Given n nodes and edges, determine if graph is a valid tree (connected and acyclic).", "1 <= n <= 2000", "Line 1: Integer n e\nNext e lines: u v", "Print 'true' or 'false'.", "5 4\n0 1\n0 2\n0 3\n1 4", "true", [{"input": "5 4\n0 1\n0 2\n0 3\n1 4", "expectedOutput": "true", "hidden": False}, {"input": "5 5\n0 1\n1 2\n2 3\n1 3\n1 4", "expectedOutput": "false", "hidden": False}]),
    ("Number of Connected Components in an Undirected Graph", "Graphs", "MEDIUM", "Return number of connected components in undirected graph with n nodes using Disjoint Set Union (DSU).", "1 <= n <= 2000", "Line 1: Integer n e\nNext e lines: u v", "Print connected components count.", "5 2\n0 1\n1 2", "3", [{"input": "5 2\n0 1\n1 2", "expectedOutput": "3", "hidden": False}, {"input": "5 4\n0 1\n1 2\n3 4\n2 3", "expectedOutput": "1", "hidden": False}]),
    ("Redundant Connection", "Graphs", "MEDIUM", "Return an edge that can be removed so that resulting graph is tree of n nodes using Union-Find.", "3 <= n <= 1000", "Line 1: Integer n\nNext n lines: u v", "Print redundant edge u v.", "3\n1 2\n1 3\n2 3", "2 3", [{"input": "3\n1 2\n1 3\n2 3", "expectedOutput": "2 3", "hidden": False}]),
    ("Word Ladder", "Graphs", "HARD", "Find length of shortest transformation sequence from beginWord to endWord using BFS.", "1 <= words <= 5000", "Line 1: beginWord endWord\nLine 2: Integer n\nLine 3: n words", "Print shortest sequence length.", "hit cog\n6\nhot dot dog lot log cog", "5", [{"input": "hit cog\n6\nhot dot dog lot log cog", "expectedOutput": "5", "hidden": False}]),
    ("Word Ladder II", "Graphs", "HARD", "Find all shortest transformation sequences from beginWord to endWord. Return count of shortest paths.", "1 <= words <= 500", "Line 1: beginWord endWord\nLine 2: Integer n\nLine 3: n words", "Print count of shortest sequences.", "hit cog\n5\nhot dot dog lot log", "0", [{"input": "hit cog\n5\nhot dot dog lot log", "expectedOutput": "0", "hidden": False}]),
    ("Network Delay Time", "Graphs", "MEDIUM", "Return minimum time for all n nodes to receive signal sent from node k using Dijkstra's Algorithm.", "1 <= n <= 100", "Line 1: Integer n k e\nNext e lines: u v w", "Print minimum time or -1.", "4 2 3\n2 1 1\n2 3 1\n3 4 1", "2", [{"input": "4 2 3\n2 1 1\n2 3 1\n3 4 1", "expectedOutput": "2", "hidden": False}]),
    ("Reconstruct Itinerary", "Graphs", "HARD", "Reconstruct itinerary in order using all tickets starting from JFK with Hierholzer's Eulerian Path algorithm.", "1 <= tickets.length <= 300", "Line 1: Integer n\nNext n lines: from to", "Print airports in itinerary space-separated.", "4\nMUC LHR\nJFK MUC\nSFO SJC\nLHR SFO", "JFK MUC LHR SFO SJC", [{"input": "4\nMUC LHR\nJFK MUC\nSFO SJC\nLHR SFO", "expectedOutput": "JFK MUC LHR SFO SJC", "hidden": False}]),
    ("Min Cost to Connect All Points", "Graphs", "MEDIUM", "Find minimum cost to connect all points on 2D plane (Minimum Spanning Tree with Prim / Kruskal).", "1 <= n <= 1000", "Line 1: Integer n\nNext n lines: x y", "Print minimum MST cost.", "5\n0 0\n2 2\n3 10\n5 2\n7 0", "20", [{"input": "5\n0 0\n2 2\n3 10\n5 2\n7 0", "expectedOutput": "20", "hidden": False}]),
    ("Swim in Rising Water", "Graphs", "HARD", "Find least time until you can reach bottom right corner from top left in n x n grid (Dijkstra/Binary Search).", "1 <= n <= 50", "Line 1: Integer n\nNext n lines: n heights", "Print least time.", "2\n0 2\n1 3", "3", [{"input": "2\n0 2\n1 3", "expectedOutput": "3", "hidden": False}]),
    ("Alien Dictionary", "Graphs", "HARD", "Derive alien language alphabet order from sorted dictionary of words using Topological Sort.", "1 <= words.length <= 100", "Line 1: Integer n\nNext n lines: alien word", "Print unique alien alphabet order.", "5\nwrt\nwrf\ner\nett\nrftt", "wertf", [{"input": "5\nwrt\nwrf\ner\nett\nrftt", "expectedOutput": "wertf", "hidden": False}]),
    ("Cheapest Flights Within K Stops", "Graphs", "MEDIUM", "Find cheapest price from src to dst with at most k stops using Bellman-Ford / BFS.", "1 <= n <= 100", "Line 1: Integer n src dst k e\nNext e lines: from to price", "Print cheapest price or -1.", "4 0 3 1 5\n0 1 100\n1 2 100\n2 0 100\n1 3 600\n2 3 200", "700", [{"input": "4 0 3 1 5\n0 1 100\n1 2 100\n2 0 100\n1 3 600\n2 3 200", "expectedOutput": "700", "hidden": False}]),
    ("Is Graph Bipartite", "Graphs", "MEDIUM", "Return true if graph is bipartite (2-colorable using BFS/DFS).", "1 <= n <= 100", "Line 1: Integer n\nNext n lines: neighbors count followed by neighbors of node i", "Print 'true' or 'false'.", "4\n3 1 2 3\n2 0 2\n3 0 1 3\n2 0 2", "false", [{"input": "4\n3 1 2 3\n2 0 2\n3 0 1 3\n2 0 2", "expectedOutput": "false", "hidden": False}]),
    ("Number of Provinces", "Graphs", "MEDIUM", "Given n x n connectivity matrix isConnected, return total number of provinces (connected components).", "1 <= n <= 200", "Line 1: Integer n\nNext n lines: n binary integers", "Print number of provinces.", "3\n1 1 0\n1 1 0\n0 0 1", "2", [{"input": "3\n1 1 0\n1 1 0\n0 0 1", "expectedOutput": "2", "hidden": False}])
]
batch_add_from_specs(graph_specs)

# Dynamic Programming (26)
dp_specs = [
    ("Climbing Stairs", "Dynamic Programming", "EASY", "You are climbing staircase with n steps. Each time you can climb 1 or 2 steps. How many distinct ways can you climb to top?", "1 <= n <= 45", "Line 1: Integer n", "Print distinct ways count.", "3", "3", [{"input": "3", "expectedOutput": "3", "hidden": False}, {"input": "2", "expectedOutput": "2", "hidden": False}]),
    ("Min Cost Climbing Stairs", "Dynamic Programming", "EASY", "Return minimum cost to reach top of floor where you can climb 1 or 2 steps.", "2 <= cost.length <= 1000", "Line 1: Integer n\nLine 2: n space-separated costs", "Print minimum cost.", "3\n10 15 20", "15", [{"input": "3\n10 15 20", "expectedOutput": "15", "hidden": False}]),
    ("House Robber", "Dynamic Programming", "MEDIUM", "Rob houses along street without alerting adjacent security systems. Maximize total money.", "1 <= nums.length <= 100", "Line 1: Integer n\nLine 2: n space-separated money amounts", "Print max money.", "4\n1 2 3 1", "4", [{"input": "4\n1 2 3 1", "expectedOutput": "4", "hidden": False}, {"input": "5\n2 7 9 3 1", "expectedOutput": "12", "hidden": False}]),
    ("House Robber II", "Dynamic Programming", "MEDIUM", "Houses are arranged in circle. Maximize total money without robbing adjacent houses.", "1 <= nums.length <= 100", "Line 1: Integer n\nLine 2: n money amounts", "Print max money.", "3\n2 3 2", "3", [{"input": "3\n2 3 2", "expectedOutput": "3", "hidden": False}, {"input": "4\n1 2 3 1", "expectedOutput": "4", "hidden": False}]),
    ("Longest Palindromic Substring", "Dynamic Programming", "MEDIUM", "Given string s, return longest palindromic substring in s.", "1 <= s.length <= 1000", "Line 1: String s", "Print longest palindromic substring.", "babad", "bab", [{"input": "babad", "expectedOutput": "bab", "hidden": False}, {"input": "cbbd", "expectedOutput": "bb", "hidden": False}]),
    ("Palindromic Substrings", "Dynamic Programming", "MEDIUM", "Given string s, return number of palindromic substrings in it.", "1 <= s.length <= 1000", "Line 1: String s", "Print count of palindromic substrings.", "abc", "3", [{"input": "abc", "expectedOutput": "3", "hidden": False}, {"input": "aaa", "expectedOutput": "6", "hidden": False}]),
    ("Decode Ways", "Dynamic Programming", "MEDIUM", "Given encoded message containing digits '1'-'9' ('A'->1, 'B'->2..), return number of ways to decode it.", "1 <= s.length <= 100", "Line 1: String s", "Print number of decode ways.", "12", "2", [{"input": "12", "expectedOutput": "2", "hidden": False}, {"input": "226", "expectedOutput": "3", "hidden": False}, {"input": "06", "expectedOutput": "0", "hidden": True}]),
    ("Coin Change", "Dynamic Programming", "MEDIUM", "Return fewest number of coins needed to make up given amount, or -1 if impossible.", "1 <= coins.length <= 12\n0 <= amount <= 10^4", "Line 1: Integer n\nLine 2: n coin denominations\nLine 3: Integer amount", "Print fewest coins or -1.", "3\n1 2 5\n11", "3", [{"input": "3\n1 2 5\n11", "expectedOutput": "3", "hidden": False}, {"input": "1\n2\n3", "expectedOutput": "-1", "hidden": False}]),
    ("Coin Change II", "Dynamic Programming", "MEDIUM", "Return number of combinations that make up given amount with infinite supply of coins.", "1 <= coins.length <= 300\n0 <= amount <= 5000", "Line 1: Integer n\nLine 2: n coins\nLine 3: Integer amount", "Print combinations count.", "3\n1 2 5\n5", "4", [{"input": "3\n1 2 5\n5", "expectedOutput": "4", "hidden": False}]),
    ("Maximum Product Subarray", "Dynamic Programming", "MEDIUM", "Find contiguous non-empty subarray within array that has largest product, and return product.", "1 <= nums.length <= 2 * 10^4", "Line 1: Integer n\nLine 2: n integers", "Print max product.", "4\n2 3 -2 4", "6", [{"input": "4\n2 3 -2 4", "expectedOutput": "6", "hidden": False}, {"input": "3\n-2 0 -1", "expectedOutput": "0", "hidden": False}]),
    ("Word Break", "Dynamic Programming", "MEDIUM", "Return true if string s can be segmented into space-separated sequence of one or more dictionary words.", "1 <= s.length <= 300", "Line 1: String s\nLine 2: Integer d\nLine 3: d space-separated dictionary words", "Print 'true' or 'false'.", "leetcode\n2\nleet code", "true", [{"input": "leetcode\n2\nleet code", "expectedOutput": "true", "hidden": False}, {"input": "catsandog\n5\ncats dog sand and cat", "expectedOutput": "false", "hidden": False}]),
    ("Word Break II", "Dynamic Programming", "HARD", "Return all possible sentence segmentations from dictionary words. Print count of sentences.", "1 <= s.length <= 20", "Line 1: String s\nLine 2: Integer d\nLine 3: d words", "Print number of valid sentences.", "catsanddog\n5\ncat cats and sand dog", "2", [{"input": "catsanddog\n5\ncat cats and sand dog", "expectedOutput": "2", "hidden": False}]),
    ("Longest Increasing Subsequence", "Dynamic Programming", "MEDIUM", "Given integer array nums, return length of longest strictly increasing subsequence in O(n log n).", "1 <= nums.length <= 2500", "Line 1: Integer n\nLine 2: n space-separated integers", "Print LIS length.", "8\n10 9 2 5 3 7 101 18", "4", [{"input": "8\n10 9 2 5 3 7 101 18", "expectedOutput": "4", "hidden": False}]),
    ("Partition Equal Subset Sum", "Dynamic Programming", "MEDIUM", "Determine if array can be partitioned into two subsets such that sum of elements in both subsets is equal (0/1 Knapsack).", "1 <= nums.length <= 200", "Line 1: Integer n\nLine 2: n integers", "Print 'true' or 'false'.", "4\n1 5 11 5", "true", [{"input": "4\n1 5 11 5", "expectedOutput": "true", "hidden": False}, {"input": "4\n1 2 3 5", "expectedOutput": "false", "hidden": False}]),
    ("Unique Paths", "Dynamic Programming", "MEDIUM", "Robot on m x n grid moves only right or down. Return number of possible unique paths to bottom-right.", "1 <= m, n <= 100", "Line 1: Integer m n", "Print unique paths count.", "3 7", "28", [{"input": "3 7", "expectedOutput": "28", "hidden": False}, {"input": "3 2", "expectedOutput": "3", "hidden": False}]),
    ("Unique Paths II", "Dynamic Programming", "MEDIUM", "Find unique paths in m x n grid with obstacles (1 is obstacle, 0 is space).", "1 <= m, n <= 100", "Line 1: Integers m n\nNext m lines: n space-separated integers", "Print unique paths.", "3 3\n0 0 0\n0 1 0\n0 0 0", "2", [{"input": "3 3\n0 0 0\n0 1 0\n0 0 0", "expectedOutput": "2", "hidden": False}]),
    ("Minimum Path Sum", "Dynamic Programming", "MEDIUM", "Given m x n grid filled with non-negative numbers, find path from top left to bottom right which minimizes sum of numbers.", "1 <= m, n <= 200", "Line 1: Integers m n\nNext m lines: n integers per line", "Print minimum path sum.", "3 3\n1 3 1\n1 5 1\n4 2 1", "7", [{"input": "3 3\n1 3 1\n1 5 1\n4 2 1", "expectedOutput": "7", "hidden": False}]),
    ("Longest Common Subsequence", "Dynamic Programming", "MEDIUM", "Given two strings text1 and text2, return length of their longest common subsequence (LCS).", "1 <= text1.length, text2.length <= 1000", "Line 1: String text1\nLine 2: String text2", "Print LCS length.", "abcde\nace", "3", [{"input": "abcde\nace", "expectedOutput": "3", "hidden": False}, {"input": "abc\nabc", "expectedOutput": "3", "hidden": False}]),
    ("Best Time to Buy and Sell Stock with Cooldown", "Dynamic Programming", "MEDIUM", "Maximize stock profit with 1-day cooldown after selling.", "1 <= prices.length <= 5000", "Line 1: Integer n\nLine 2: n prices", "Print max profit.", "5\n1 2 3 0 2", "3", [{"input": "5\n1 2 3 0 2", "expectedOutput": "3", "hidden": False}]),
    ("Best Time to Buy and Sell Stock with Transaction Fee", "Dynamic Programming", "MEDIUM", "Maximize stock profit with transaction fee per completed transaction.", "1 <= prices.length <= 5 * 10^4", "Line 1: Integer n fee\nLine 2: n prices", "Print max profit.", "6 2\n1 3 2 8 4 9", "8", [{"input": "6 2\n1 3 2 8 4 9", "expectedOutput": "8", "hidden": False}]),
    ("Target Sum", "Dynamic Programming", "MEDIUM", "Assign + and - signs to integers in nums to make sum equal to target. Return ways count.", "1 <= nums.length <= 20", "Line 1: Integer n\nLine 2: n integers\nLine 3: Integer target", "Print number of ways.", "5\n1 1 1 1 1\n3", "5", [{"input": "5\n1 1 1 1 1\n3", "expectedOutput": "5", "hidden": False}]),
    ("Interleaving String", "Dynamic Programming", "MEDIUM", "Given s1, s2, and s3, find whether s3 is formed by an interleaving of s1 and s2.", "0 <= s1.length, s2.length <= 100\n0 <= s3.length <= 200", "Line 1: String s1\nLine 2: String s2\nLine 3: String s3", "Print 'true' or 'false'.", "aabcc\ndbbca\naadbbcbcac", "true", [{"input": "aabcc\ndbbca\naadbbcbcac", "expectedOutput": "true", "hidden": False}, {"input": "aabcc\ndbbca\naadbbbaccc", "expectedOutput": "false", "hidden": False}]),
    ("Longest Increasing Path in a Matrix", "Dynamic Programming", "HARD", "Given m x n integers matrix, return length of longest strictly increasing path using memoization.", "1 <= m, n <= 200", "Line 1: Integers m n\nNext m lines: n integers per line", "Print longest path length.", "3 3\n9 9 4\n6 6 8\n2 1 1", "4", [{"input": "3 3\n9 9 4\n6 6 8\n2 1 1", "expectedOutput": "4", "hidden": False}]),
    ("Distinct Subsequences", "Dynamic Programming", "HARD", "Given two strings s and t, return number of distinct subsequences of s which equals t.", "1 <= s.length, t.length <= 1000", "Line 1: String s\nLine 2: String t", "Print count of distinct subsequences.", "rabbbit\nrabbit", "3", [{"input": "rabbbit\nrabbit", "expectedOutput": "3", "hidden": False}]),
    ("Edit Distance", "Dynamic Programming", "MEDIUM", "Given two strings word1 and word2, return minimum number of operations (insert, delete, replace) to convert word1 to word2 (Levenshtein Distance).", "0 <= word1.length, word2.length <= 500", "Line 1: String word1\nLine 2: String word2", "Print minimum operations.", "horse\nros", "3", [{"input": "horse\nros", "expectedOutput": "3", "hidden": False}, {"input": "intention\nexecution", "expectedOutput": "5", "hidden": False}]),
    ("Burst Balloons", "Dynamic Programming", "HARD", "Burst balloons to maximize coins (nums[i-1] * nums[i] * nums[i+1]).", "1 <= n <= 300", "Line 1: Integer n\nLine 2: n integers", "Print maximum coins.", "4\n3 1 5 8", "167", [{"input": "4\n3 1 5 8", "expectedOutput": "167", "hidden": False}])
]
batch_add_from_specs(dp_specs)

# Greedy & Intervals (15)
greedy_specs = [
    ("Jump Game", "Greedy", "MEDIUM", "Given integer array nums, return true if you can reach last index starting from index 0.", "1 <= nums.length <= 10^4", "Line 1: Integer n\nLine 2: n space-separated integers", "Print 'true' or 'false'.", "5\n2 3 1 1 4", "true", [{"input": "5\n2 3 1 1 4", "expectedOutput": "true", "hidden": False}, {"input": "5\n3 2 1 0 4", "expectedOutput": "false", "hidden": False}]),
    ("Jump Game II", "Greedy", "MEDIUM", "Return minimum number of jumps to reach last index.", "1 <= nums.length <= 10^4", "Line 1: Integer n\nLine 2: n integers", "Print minimum jumps.", "5\n2 3 1 1 4", "2", [{"input": "5\n2 3 1 1 4", "expectedOutput": "2", "hidden": False}]),
    ("Gas Station", "Greedy", "MEDIUM", "There are n gas stations on circular route. Return starting gas station index to complete circuit, or -1.", "1 <= n <= 10^5", "Line 1: Integer n\nLine 2: n gas amounts\nLine 3: n cost amounts", "Print starting index or -1.", "5\n1 2 3 4 5\n3 4 5 1 2", "3", [{"input": "5\n1 2 3 4 5\n3 4 5 1 2", "expectedOutput": "3", "hidden": False}]),
    ("Hand of Straights", "Greedy", "MEDIUM", "Check if hand of cards can be rearranged into groups of groupSize consecutive cards.", "1 <= hand.length <= 10^4", "Line 1: Integer n groupSize\nLine 2: n card values", "Print 'true' or 'false'.", "9 3\n1 2 3 6 2 3 4 7 8", "true", [{"input": "9 3\n1 2 3 6 2 3 4 7 8", "expectedOutput": "true", "hidden": False}]),
    ("Merge Intervals", "Greedy", "MEDIUM", "Given array of intervals, merge all overlapping intervals.", "1 <= intervals.length <= 10^4", "Line 1: Integer n\nNext n lines: start end", "Print merged intervals count on line 1, then merged intervals.", "4\n1 3\n2 6\n8 10\n15 18", "3\n1 6\n8 10\n15 18", [{"input": "4\n1 3\n2 6\n8 10\n15 18", "expectedOutput": "3\n1 6\n8 10\n15 18", "hidden": False}]),
    ("Insert Interval", "Greedy", "MEDIUM", "Insert newInterval into intervals and merge if necessary.", "0 <= intervals.length <= 10^4", "Line 1: Integer n\nNext n lines: start end\nLine n+2: newInterval start end", "Print merged intervals count on line 1, then intervals.", "2\n1 3\n6 9\n2 5", "2\n1 5\n6 9", [{"input": "2\n1 3\n6 9\n2 5", "expectedOutput": "2\n1 5\n6 9", "hidden": False}]),
    ("Non-overlapping Intervals", "Greedy", "MEDIUM", "Return minimum number of intervals you need to remove to make remainder non-overlapping.", "1 <= intervals.length <= 10^5", "Line 1: Integer n\nNext n lines: start end", "Print min removed intervals.", "4\n1 2\n2 3\n3 4\n1 3", "1", [{"input": "4\n1 2\n2 3\n3 4\n1 3", "expectedOutput": "1", "hidden": False}]),
    ("Meeting Rooms", "Greedy", "EASY", "Determine if person could attend all meetings given intervals.", "0 <= intervals.length <= 10^4", "Line 1: Integer n\nNext n lines: start end", "Print 'true' or 'false'.", "3\n0 30\n5 10\n15 20", "false", [{"input": "3\n0 30\n5 10\n15 20", "expectedOutput": "false", "hidden": False}, {"input": "2\n7 10\n2 4", "expectedOutput": "true", "hidden": False}]),
    ("Meeting Rooms II", "Greedy", "MEDIUM", "Find minimum number of conference rooms required.", "1 <= intervals.length <= 10^4", "Line 1: Integer n\nNext n lines: start end", "Print min conference rooms.", "3\n0 30\n5 10\n15 20", "2", [{"input": "3\n0 30\n5 10\n15 20", "expectedOutput": "2", "hidden": False}, {"input": "2\n7 10\n2 4", "expectedOutput": "1", "hidden": False}]),
    ("Minimum Number of Arrows to Burst Balloons", "Greedy", "MEDIUM", "Find minimum arrows to shoot all balloons represented by horizontal intervals.", "1 <= points.length <= 10^5", "Line 1: Integer n\nNext n lines: xstart xend", "Print min arrows.", "4\n10 16\n2 8\n1 6\n7 12", "2", [{"input": "4\n10 16\n2 8\n1 6\n7 12", "expectedOutput": "2", "hidden": False}]),
    ("Partition Labels", "Greedy", "MEDIUM", "Partition string into as many parts as possible so that each letter appears in at most one part.", "1 <= s.length <= 500", "Line 1: String s", "Print sizes of parts space-separated.", "ababcbacadefegdehijhklij", "9 7 8", [{"input": "ababcbacadefegdehijhklij", "expectedOutput": "9 7 8", "hidden": False}]),
    ("Valid Parenthesis String", "Greedy", "MEDIUM", "Given string s with '(', ')', and '*' (which can be '(', ')', or empty), return true if s is valid.", "1 <= s.length <= 100", "Line 1: String s", "Print 'true' or 'false'.", "(*)", "true", [{"input": "(*)", "expectedOutput": "true", "hidden": False}, {"input": "(*))", "expectedOutput": "true", "hidden": False}]),
    ("Queue Reconstruction by Height", "Greedy", "MEDIUM", "Reconstruct queue of people with [height, k] where k is number of people in front with height >= h.", "1 <= people.length <= 2000", "Line 1: Integer n\nNext n lines: h k", "Print reconstructed order per line.", "6\n7 0\n4 4\n7 1\n5 0\n6 1\n5 2", "5 0\n7 0\n5 2\n6 1\n4 4\n7 1", [{"input": "6\n7 0\n4 4\n7 1\n5 0\n6 1\n5 2", "expectedOutput": "5 0\n7 0\n5 2\n6 1\n4 4\n7 1", "hidden": False}]),
    ("Candy", "Greedy", "HARD", "Give each child at least 1 candy, children with higher rating get more candies than neighbors. Find minimum total candies.", "1 <= ratings.length <= 2 * 10^4", "Line 1: Integer n\nLine 2: n ratings", "Print min candies.", "3\n1 0 2", "5", [{"input": "3\n1 0 2", "expectedOutput": "5", "hidden": False}, {"input": "3\n1 2 2", "expectedOutput": "4", "hidden": False}]),
    ("Lemonade Change", "Greedy", "EASY", "Determine if you can provide every customer with correct change for $5, $10, $20 bills.", "1 <= bills.length <= 10^5", "Line 1: Integer n\nLine 2: n bills", "Print 'true' or 'false'.", "5\n5 5 5 10 20", "true", [{"input": "5\n5 5 5 10 20", "expectedOutput": "true", "hidden": False}, {"input": "5\n5 5 10 10 20", "expectedOutput": "false", "hidden": False}])
]
batch_add_from_specs(greedy_specs)

# Bit Manipulation & Math (15)
bit_specs = [
    ("Single Number", "Bit Manipulation", "EASY", "Every element appears twice except for one. Find that single one using XOR in O(n) and O(1) space.", "1 <= nums.length <= 3 * 10^4", "Line 1: Integer n\nLine 2: n space-separated integers", "Print single number.", "3\n2 2 1", "1", [{"input": "3\n2 2 1", "expectedOutput": "1", "hidden": False}, {"input": "5\n4 1 2 1 2", "expectedOutput": "4", "hidden": False}]),
    ("Single Number II", "Bit Manipulation", "MEDIUM", "Every element appears 3 times except for one. Find single element in linear time and constant space.", "1 <= nums.length <= 3 * 10^4", "Line 1: Integer n\nLine 2: n integers", "Print single element.", "4\n2 2 3 2", "3", [{"input": "4\n2 2 3 2", "expectedOutput": "3", "hidden": False}]),
    ("Single Number III", "Bit Manipulation", "MEDIUM", "Exactly two elements appear only once and all other elements appear twice. Find the two elements in ascending order.", "2 <= nums.length <= 3 * 10^4", "Line 1: Integer n\nLine 2: n integers", "Print the two single numbers space-separated in ascending order.", "6\n1 2 1 3 2 5", "3 5", [{"input": "6\n1 2 1 3 2 5", "expectedOutput": "3 5", "hidden": False}]),
    ("Number of 1 Bits", "Bit Manipulation", "EASY", "Return number of set bits (Hamming weight) of positive integer n.", "1 <= n <= 2^31 - 1", "Line 1: Integer n", "Print set bit count.", "11", "3", [{"input": "11", "expectedOutput": "3", "hidden": False}, {"input": "128", "expectedOutput": "1", "hidden": False}]),
    ("Counting Bits", "Bit Manipulation", "EASY", "For every number i in [0, n], calculate number of 1's in binary representation.", "0 <= n <= 10^5", "Line 1: Integer n", "Print bit counts space-separated from 0 to n.", "5", "0 1 1 2 1 2", [{"input": "5", "expectedOutput": "0 1 1 2 1 2", "hidden": False}, {"input": "2", "expectedOutput": "0 1 1", "hidden": False}]),
    ("Reverse Bits", "Bit Manipulation", "EASY", "Reverse bits of 32-bit unsigned integer.", "0 <= n <= 2^32 - 1", "Line 1: Integer n", "Print unsigned integer resulting from reversed bits.", "43261596", "964176192", [{"input": "43261596", "expectedOutput": "964176192", "hidden": False}]),
    ("Missing Number", "Bit Manipulation", "EASY", "Given array nums containing n distinct numbers in range [0, n], return only number missing.", "1 <= n <= 10^4", "Line 1: Integer n\nLine 2: n space-separated integers", "Print missing number.", "3\n3 0 1", "2", [{"input": "3\n3 0 1", "expectedOutput": "2", "hidden": False}, {"input": "9\n9 6 4 2 3 5 7 0 1", "expectedOutput": "8", "hidden": False}]),
    ("Sum of Two Integers", "Bit Manipulation", "MEDIUM", "Given two integers a and b, return sum of two integers without using + and - operators.", "-1000 <= a, b <= 1000", "Line 1: Integers a b", "Print sum.", "1 2", "3", [{"input": "1 2", "expectedOutput": "3", "hidden": False}, {"input": "2 3", "expectedOutput": "5", "hidden": False}]),
    ("Reverse Integer", "Math", "MEDIUM", "Given signed 32-bit integer x, return x with its digits reversed, or 0 if overflow occurs.", "-2^31 <= x <= 2^31 - 1", "Line 1: Integer x", "Print reversed integer.", "123", "321", [{"input": "123", "expectedOutput": "321", "hidden": False}, {"input": "-123", "expectedOutput": "-321", "hidden": False}, {"input": "120", "expectedOutput": "21", "hidden": True}]),
    ("Palindrome Number", "Math", "EASY", "Given integer x, return true if x is a palindrome integer.", "-2^31 <= x <= 2^31 - 1", "Line 1: Integer x", "Print 'true' or 'false'.", "121", "true", [{"input": "121", "expectedOutput": "true", "hidden": False}, {"input": "-121", "expectedOutput": "false", "hidden": False}, {"input": "10", "expectedOutput": "false", "hidden": True}]),
    ("Pow(x, n)", "Math", "MEDIUM", "Implement pow(x, n), which calculates x raised to power n in O(log n).", "-100.0 < x < 100.0\n-2^31 <= n <= 2^31-1", "Line 1: Float x\nLine 2: Integer n", "Print result formatted to 5 decimal places.", "2.00000\n10", "1024.00000", [{"input": "2.00000\n10", "expectedOutput": "1024.00000", "hidden": False}, {"input": "2.10000\n3", "expectedOutput": "9.26100", "hidden": False}]),
    ("Sqrt(x)", "Math", "EASY", "Compute and return integer square root of non-negative integer x rounded down.", "0 <= x <= 2^31 - 1", "Line 1: Integer x", "Print square root.", "8", "2", [{"input": "8", "expectedOutput": "2", "hidden": False}, {"input": "4", "expectedOutput": "2", "hidden": False}]),
    ("Factorial Trailing Zeroes", "Math", "MEDIUM", "Given integer n, return number of trailing zeroes in n! in O(log n).", "0 <= n <= 10^4", "Line 1: Integer n", "Print trailing zeroes count.", "5", "1", [{"input": "5", "expectedOutput": "1", "hidden": False}, {"input": "3", "expectedOutput": "0", "hidden": False}, {"input": "25", "expectedOutput": "6", "hidden": True}]),
    ("Happy Number", "Math", "EASY", "Determine if number n is happy (repeatedly replacing with sum of squares of digits reaches 1).", "1 <= n <= 2^31 - 1", "Line 1: Integer n", "Print 'true' or 'false'.", "19", "true", [{"input": "19", "expectedOutput": "true", "hidden": False}, {"input": "2", "expectedOutput": "false", "hidden": False}]),
    ("Power of Two", "Bit Manipulation", "EASY", "Return true if integer n is power of two using bit manipulation n & (n - 1) == 0.", "-2^31 <= n <= 2^31 - 1", "Line 1: Integer n", "Print 'true' or 'false'.", "16", "true", [{"input": "16", "expectedOutput": "true", "hidden": False}, {"input": "3", "expectedOutput": "false", "hidden": False}])
]
batch_add_from_specs(bit_specs)

print(f"Total problems generated: {len(all_problems)}")

out_path = os.path.join(os.path.dirname(__file__), "dsa_problems.json")
with open(out_path, "w", encoding="utf-8") as f:
    json.dump(all_problems, f, indent=2)

print(f"Successfully wrote {len(all_problems)} DSA problems to {out_path}")
