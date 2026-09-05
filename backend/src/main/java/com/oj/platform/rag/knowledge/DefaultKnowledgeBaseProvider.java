package com.oj.platform.rag.knowledge;

import com.oj.platform.rag.model.KnowledgeDocument;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provider of trusted, platform-curated educational knowledge documents for RAG indexing.
 */
@Component
public class DefaultKnowledgeBaseProvider {

    public List<KnowledgeDocument> getEducationalDocuments() {
        List<KnowledgeDocument> docs = new ArrayList<>();

        // 1. Binary Search
        docs.add(KnowledgeDocument.builder()
                .id("doc-algo-binary-search")
                .title("Binary Search Invariants, Boundary Mechanics and Predicates")
                .topic("ALGORITHMS")
                .difficulty("BEGINNER")
                .language("General")
                .source("Platform DSA Master Guide v2.1")
                .concept("Binary Search")
                .tags(Arrays.asList("binary search", "divide and conquer", "logarithmic time", "search on answer", "invariants"))
                .content("""
                        # Binary Search Invariants and Mechanics
                        
                        Binary Search is an optimal divide-and-conquer algorithm that searches a sorted collection in O(log N) time complexity and O(1) auxiliary space complexity.
                        
                        ## Core Invariant
                        At every step, the search space is halved by comparing the target with the middle element:
                        1. If `arr[mid] == target`, target is found.
                        2. If `arr[mid] < target`, the target must reside in the right half `[mid + 1, right]`.
                        3. If `arr[mid] > target`, the target must reside in the left half `[left, mid - 1]`.
                        
                        ## Preventing Integer Overflow
                        Never calculate midpoint as `(left + right) / 2` because when `left + right > 2^31 - 1`, it overflows into a negative 32-bit integer.
                        Always use:
                        ```java
                        int mid = left + (right - left) / 2;
                        ```
                        
                        ## Standard Implementation (Java / Python)
                        ```java
                        public int binarySearch(int[] nums, int target) {
                            int left = 0, right = nums.length - 1;
                            while (left <= right) {
                                int mid = left + (right - left) / 2;
                                if (nums[mid] == target) return mid;
                                else if (nums[mid] < target) left = mid + 1;
                                else right = mid - 1;
                            }
                            return -1; // Not found
                        }
                        ```
                        
                        ## Advanced: Search on Answer (Predicate Binary Search)
                        Binary search applies not only to sorted arrays, but to any monotonic boolean predicate function `f(x)` where `f(x) == true` implies `f(y) == true` for all `y >= x` (e.g., Koko Eating Bananas, Capacity To Ship Packages).
                        """)
                .build());

        // 2. HashMap & Hash Tables
        docs.add(KnowledgeDocument.builder()
                .id("doc-ds-hashmap-internals")
                .title("HashMap Architecture, Collisions, Load Factors and When to Use")
                .topic("DATA_STRUCTURES")
                .difficulty("INTERMEDIATE")
                .language("Java")
                .source("Java Core Architecture Handbook")
                .concept("HashMap Internals")
                .tags(Arrays.asList("hashmap", "hashtable", "collisions", "load factor", "equals hashcode", "treemap"))
                .content("""
                        # HashMap Architecture & When to Use
                        
                        A HashMap provides key-value storage with O(1) average-time complexity for `get()`, `put()`, and `containsKey()`, and O(N) worst-case time complexity during severe hash collisions.
                        
                        ## Internal Mechanics in Java
                        - **Array of Buckets**: Internal storage is `Node<K,V>[] table`.
                        - **Hash Calculation**: Hash code of key is spread across bucket array length using bitwise AND: `(n - 1) & hash`.
                        - **Collision Resolution**: Separate chaining. Multiple keys hashing to the same bucket index form a linked list. In Java 8+, when a single bucket exceeds TREEIFY_THRESHOLD (8 items) and table capacity >= 64, the linked list transforms into a Red-Black Tree (TreeNode) improving worst-case search from O(N) to O(log N).
                        - **Load Factor & Rehashing**: Default load factor is 0.75. When size exceeds `capacity * loadFactor` (threshold = 16 * 0.75 = 12), the bucket array doubles in size and existing entries are rehashed.
                        
                        ## When to Use HashMap vs TreeMap vs LinkedHashMap
                        1. **HashMap**: Use for fast O(1) lookups when ordering does not matter.
                        2. **TreeMap**: Use when keys must remain sorted in natural or custom Comparator order (O(log N) operations using Red-Black Tree). Supports range queries like `subMap()`, `floorKey()`, `ceilingKey()`.
                        3. **LinkedHashMap**: Use when insertion-order or access-order (e.g. LRU Cache) iteration must be preserved.
                        
                        ## The equals() and hashCode() Contract
                        If two objects are equal according to `equals(Object)`, they MUST return the exact same `hashCode()`. Failing to override `hashCode()` when overriding `equals()` causes `get()` to fail to retrieve stored keys.
                        """)
                .build());

        // 3. Two Pointers Technique
        docs.add(KnowledgeDocument.builder()
                .id("doc-algo-two-pointers")
                .title("Two Pointers Algorithmic Technique and Space Optimization")
                .topic("ALGORITHMS")
                .difficulty("BEGINNER")
                .language("General")
                .source("Platform DSA Master Guide v2.1")
                .concept("Two Pointers")
                .tags(Arrays.asList("two pointers", "pointers", "sorted array", "space optimization", "container with most water"))
                .content("""
                        # Two Pointers Technique
                        
                        The Two Pointers pattern utilizes two index markers traversing a linear data structure (Array or String) simultaneously to reduce brute-force O(N^2) quadratic checks into a single O(N) linear pass with O(1) memory.
                        
                        ## Primary Two Pointers Variants
                        
                        ### 1. Opposite Direction (Converging Pointers)
                        - Left pointer starts at index `0`, right pointer starts at `nums.length - 1`.
                        - Pointers move toward each other based on comparison conditions.
                        - **Applications**:
                          - Two Sum on Sorted Array (Two Sum II)
                          - Valid Palindrome check
                          - Container With Most Water (Greedily move the shorter height pointer)
                          - Trapping Rain Water
                        
                        ```java
                        int left = 0, right = nums.length - 1;
                        while (left < right) {
                            int sum = nums[left] + nums[right];
                            if (sum == target) return new int[]{left, right};
                            else if (sum < target) left++; // Need larger value
                            else right--; // Need smaller value
                        }
                        ```
                        
                        ### 2. Same Direction (Fast and Slow Pointers)
                        - Slow pointer tracks boundary of valid elements while fast pointer scans ahead.
                        - **Applications**: Remove Duplicates from Sorted Array, Move Zeroes, Floyd's Cycle Detection in Linked Lists.
                        """)
                .build());

        // 4. Sliding Window Pattern
        docs.add(KnowledgeDocument.builder()
                .id("doc-algo-sliding-window")
                .title("Sliding Window Pattern for Subarrays and Substrings")
                .topic("CODING_PATTERNS")
                .difficulty("INTERMEDIATE")
                .language("General")
                .source("Platform DSA Patterns Guide")
                .concept("Sliding Window")
                .tags(Arrays.asList("sliding window", "subarray", "substring", "frequency map", "monotonic window"))
                .content("""
                        # Sliding Window Algorithmic Pattern
                        
                        The Sliding Window technique optimizes contiguous subarray or substring problems by maintaining a window `[left, right]` that expands to include new elements and shrinks from the left when boundary constraints are violated.
                        
                        ## Window Classifications
                        
                        ### 1. Fixed-Size Window (Length = K)
                        - Expand right pointer until window size reaches `K`.
                        - Compute initial window metric (e.g. sum, max).
                        - Slide window forward by 1 step at a time: subtract `nums[left]` and add `nums[right + 1]`.
                        - Time Complexity: O(N), Space Complexity: O(1).
                        
                        ### 2. Variable-Size Window (Dynamic Length)
                        - Expand `right` pointer to include elements and update state (frequency map / sum).
                        - Check if current window violates problem conditions.
                        - While invalid, shrink window from `left` (increment `left` and subtract `nums[left]`).
                        - Update answer with optimal window size `(right - left + 1)`.
                        
                        ```java
                        // Example: Longest Substring Without Repeating Characters
                        public int lengthOfLongestSubstring(String s) {
                            Map<Character, Integer> lastSeen = new HashMap<>();
                            int maxLen = 0, left = 0;
                            for (int right = 0; right < s.length(); right++) {
                                char c = s.charAt(right);
                                if (lastSeen.containsKey(c)) {
                                    left = Math.max(left, lastSeen.get(c) + 1);
                                }
                                lastSeen.put(c, right);
                                maxLen = Math.max(maxLen, right - left + 1);
                            }
                            return maxLen;
                        }
                        ```
                        """)
                .build());

        // 5. Dynamic Programming
        docs.add(KnowledgeDocument.builder()
                .id("doc-algo-dynamic-programming")
                .title("Dynamic Programming: Overlapping Subproblems, Memoization vs Tabulation")
                .topic("ALGORITHMS")
                .difficulty("ADVANCED")
                .language("General")
                .source("Platform DSA Master Guide v2.1")
                .concept("Dynamic Programming")
                .tags(Arrays.asList("dynamic programming", "dp", "memoization", "tabulation", "optimal substructure", "knapsack"))
                .content("""
                        # Dynamic Programming (DP) Core Principles
                        
                        Dynamic Programming solves complex optimization problems by breaking them down into simpler overlapping subproblems and caching intermediate solutions.
                        
                        ## Two Fundamental Prerequisites
                        1. **Optimal Substructure**: The optimal solution to the problem contains within it optimal solutions to its subproblems.
                        2. **Overlapping Subproblems**: The recursive solution visits identical subproblems repeatedly instead of generating brand new subproblems.
                        
                        ## Top-Down Memoization vs Bottom-Up Tabulation
                        
                        ### Top-Down (Memoization)
                        - Implemented with natural recursion from target state down to base cases.
                        - Store results in a cache (`int[] memo` or `Map<State, Integer>`) before returning.
                        - **Pros**: Intuitive formulation, computes only necessary states.
                        - **Cons**: Recursion call-stack memory overhead O(Depth).
                        
                        ### Bottom-Up (Tabulation)
                        - Implemented iteratively with arrays or matrices starting from base cases up to target state.
                        - **Pros**: No recursion overhead, allows rolling variable space optimization (e.g. O(N) to O(1) space).
                        - **Cons**: Computes all table states.
                        
                        ## 4-Step DP Formulation Framework
                        1. Define state variables: `dp[i][j] = meaning in words`.
                        2. Determine Base Cases: `dp[0] = initial value`.
                        3. Formulate State Transition Equation: `dp[i] = min/max/sum(dp[i-1], dp[i-2] + cost)`.
                        4. Identify Final Answer: e.g., `dp[N]`.
                        """)
                .build());

        // 6. Graph Traversals (BFS vs DFS & Dijkstra)
        docs.add(KnowledgeDocument.builder()
                .id("doc-ds-graph-traversals")
                .title("Graph Traversal Strategies: BFS, DFS, Dijkstra and Topological Sort")
                .topic("DATA_STRUCTURES")
                .difficulty("INTERMEDIATE")
                .language("General")
                .source("Platform DSA Master Guide v2.1")
                .concept("Graph Traversals")
                .tags(Arrays.asList("graphs", "bfs", "dfs", "dijkstra", "topological sort", "shortest path", "kahns"))
                .content("""
                        # Graph Traversals and Shortest Path Algorithms
                        
                        Graphs model complex relational networks consisting of Vertices (V) and Edges (E).
                        
                        ## Traversal Paradigms
                        
                        ### 1. Breadth-First Search (BFS)
                        - Explores vertices level-by-level using a `Queue<Integer>` (FIFO).
                        - **Guarantees Shortest Path** in unweighted graphs.
                        - Time Complexity: O(V + E), Space Complexity: O(V).
                        
                        ### 2. Depth-First Search (DFS)
                        - Explores as deep as possible along each branch before backtracking using recursion or an explicit `Stack`.
                        - Ideal for Connected Components, Cycle Detection, Path Finding, and Exhaustive Search.
                        - Time Complexity: O(V + E), Space Complexity: O(V).
                        
                        ### 3. Dijkstra's Algorithm
                        - Finds single-source shortest path in weighted graphs with **non-negative edge weights**.
                        - Employs a Min-Priority Queue `PriorityQueue<Node>` to greedily expand the currently shortest distance vertex.
                        - Time Complexity: O((V + E) log V).
                        
                        ### 4. Topological Sort (Kahn's Algorithm)
                        - Orders vertices in a Directed Acyclic Graph (DAG) such that for every directed edge `u -> v`, `u` comes before `v`.
                        - Calculate in-degrees for all nodes; enqueue all nodes with in-degree = 0; decrement neighbors when popping.
                        - Used for Build Systems, Course Schedules, and Dependency Resolution.
                        """)
                .build());

        // 7. Java Memory Model & Garbage Collection
        docs.add(KnowledgeDocument.builder()
                .id("doc-java-memory-gc")
                .title("Java JVM Memory Model (Stack vs Heap) and Garbage Collection")
                .topic("JAVA_CORE")
                .difficulty("INTERMEDIATE")
                .language("Java")
                .source("Java Core Architecture Handbook")
                .concept("JVM Memory & Garbage Collection")
                .tags(Arrays.asList("jvm", "memory", "stack", "heap", "garbage collection", "metaspace", "memory leak"))
                .content("""
                        # Java JVM Memory Architecture & Garbage Collection
                        
                        Understanding JVM memory management is critical for writing high-performance, leak-free backend applications.
                        
                        ## Stack vs Heap Memory
                        - **Stack Memory**:
                          - Allocated per thread. Stores primitive local variables and object reference pointers.
                          - Fast LIFO allocation/deallocation when stack frames push and pop on method entry/exit.
                          - Exceeding stack depth throws `StackOverflowError`.
                        - **Heap Memory**:
                          - Shared across all JVM threads. Stores all actual object instances and array data.
                          - Managed automatically by the Garbage Collector (GC).
                          - Exhausting heap throws `OutOfMemoryError: Java heap space`.
                        - **Metaspace**: Stores class metadata, bytecode, static variables, and runtime constant pool.
                        
                        ## Garbage Collection Generations
                        1. **Young Generation**:
                           - Divided into Eden Space and two Survivor Spaces (S0, S1).
                           - Minor GC cleans short-lived objects rapidly using copying algorithms.
                        2. **Old / Tenured Generation**:
                           - Holds objects surviving multiple GC aging threshold cycles (default tenuring threshold = 15).
                           - Major / Full GC cleans old generation.
                        
                        ## Common Memory Leaks in Java
                        1. **Static Collections**: Adding elements to `static List` or `static Map` without ever evicting them.
                        2. **Unclosed System Resources**: Forgetting to close Database Connections, Sockets, or Streams (Always use `try-with-resources`).
                        3. **Improper ThreadLocal**: Failing to call `ThreadLocal.remove()` in thread pool environments.
                        """)
                .build());

        // 8. SQL Database Indexing
        docs.add(KnowledgeDocument.builder()
                .id("doc-sql-indexing")
                .title("Database Indexing Architecture: B+Trees, Clustered vs Non-Clustered, Leftmost Prefix")
                .topic("SQL_DATABASES")
                .difficulty("INTERMEDIATE")
                .language("SQL")
                .source("SQL Query Optimization & Database Architecture Guide")
                .concept("Database Indexing")
                .tags(Arrays.asList("sql", "indexes", "b-tree", "clustered index", "composite index", "query optimization", "explain"))
                .content("""
                        # Database Indexing & Query Optimization
                        
                        A database index is an auxiliary data structure that enables the database engine to find rows in O(log N) disk reads rather than performing an O(N) full table scan.
                        
                        ## B+Tree Index Architecture
                        - Most relational databases (PostgreSQL, MySQL InnoDB) use **B+Trees** for indexing.
                        - All actual data pointers reside solely in leaf nodes, linked sequentially for extremely fast range scans (`WHERE age BETWEEN 20 AND 30`).
                        - High fan-out ensures 3 to 4 disk I/O operations can index billions of rows.
                        
                        ## Clustered vs Non-Clustered Indexes
                        - **Clustered Index**: Determines the actual physical sorting order of rows on disk. Only ONE clustered index per table (typically Primary Key).
                        - **Non-Clustered (Secondary) Index**: A separate B+Tree where leaf nodes store the indexed key along with the primary key reference to look up the physical row.
                        
                        ## Leftmost Prefix Rule for Composite Indexes
                        When creating an index on `(department_id, salary, hire_date)`:
                        - Queries filtering by `department_id` or `department_id AND salary` WILL use the index.
                        - Queries filtering only by `salary` or `hire_date` CANNOT use the index because the leftmost prefix is missing.
                        
                        ## When NOT to Index
                        - Small tables (< 1000 rows) where full table scans are faster.
                        - High-write / low-read tables because every `INSERT`, `UPDATE`, and `DELETE` incurs index rebalancing overhead.
                        """)
                .build());

        // 9. SQL ACID Transactions
        docs.add(KnowledgeDocument.builder()
                .id("doc-sql-transactions-acid")
                .title("SQL ACID Transactions and Transaction Isolation Levels")
                .topic("SQL_DATABASES")
                .difficulty("ADVANCED")
                .language("SQL")
                .source("SQL Query Optimization & Database Architecture Guide")
                .concept("ACID Transactions")
                .tags(Arrays.asList("acid", "transactions", "isolation levels", "dirty read", "repeatable read", "serializable"))
                .content("""
                        # ACID Transactions & Isolation Levels
                        
                        A Transaction is a sequence of database operations executed as a single logical unit of work.
                        
                        ## ACID Properties
                        1. **Atomicity**: All operations succeed, or all are rolled back. No partial executions.
                        2. **Consistency**: Transactions transition the database from one valid state to another according to schema constraints.
                        3. **Isolation**: Concurrent transactions execute without interfering with one another.
                        4. **Durability**: Once committed, changes survive system crashes (persisted via Write-Ahead Logging / WAL).
                        
                        ## The 4 ANSI SQL Isolation Levels
                        1. **Read Uncommitted**: Lowest isolation. Transactions can read uncommitted changes from other transactions (**Dirty Reads**).
                        2. **Read Committed**: Reads only committed data. Prevents dirty reads, but allows **Non-Repeatable Reads** (re-reading a row gets updated values).
                        3. **Repeatable Read** (Default in MySQL InnoDB): Guarantees re-reading a row returns identical data using MVCC (Multi-Version Concurrency Control). May permit **Phantom Reads**.
                        4. **Serializable**: Highest isolation. Strict serial ordering via range locks or snapshot isolation. Prevents all anomalies at the cost of concurrency throughput.
                        """)
                .build());

        // 10. Monotonic Stack & Queue
        docs.add(KnowledgeDocument.builder()
                .id("doc-ds-monotonic-stack")
                .title("Monotonic Stack & Queue: Next Greater Element and Range Extremas")
                .topic("CODING_PATTERNS")
                .difficulty("INTERMEDIATE")
                .language("General")
                .source("Platform DSA Patterns Guide")
                .concept("Monotonic Stack")
                .tags(Arrays.asList("monotonic stack", "next greater element", "histogram", "sliding window maximum", "deque"))
                .content("""
                        # Monotonic Stack & Queue Pattern
                        
                        A Monotonic Stack maintains its elements in strictly ascending or descending order, enabling linear O(N) solutions for "Next Greater Element", "Previous Smaller Element", and "Stock Span" problems.
                        
                        ## Key Principle
                        - Each element is pushed and popped at most once, yielding total amortized O(N) time complexity despite nested loops.
                        
                        ## Next Greater Element Template
                        ```java
                        public int[] nextGreaterElements(int[] nums) {
                            int n = nums.length;
                            int[] result = new int[n];
                            Arrays.fill(result, -1);
                            Deque<Integer> stack = new ArrayDeque<>(); // stores indices
                            
                            for (int i = 0; i < n; i++) {
                                while (!stack.isEmpty() && nums[i] > nums[stack.peek()]) {
                                    int prevIndex = stack.pop();
                                    result[prevIndex] = nums[i];
                                }
                                stack.push(i);
                            }
                            return result;
                        }
                        ```
                        
                        ## Classic Monotonic Stack Problems
                        1. **Daily Temperatures**: Find days until a warmer temperature.
                        2. **Largest Rectangle in Histogram**: Using monotonic increasing stack to determine left/right bounding bars.
                        3. **Sliding Window Maximum**: Using Monotonic Deque to track current window maximum in O(1) per step.
                        """)
                .build());

        // 11. Debugging: Recursion & StackOverflow
        docs.add(KnowledgeDocument.builder()
                .id("doc-debug-recursion-stackoverflow")
                .title("Debugging Guide: Why Recursion is Slow, StackOverflowError, and Call Stack Limits")
                .topic("DEBUGGING_GUIDE")
                .difficulty("BEGINNER")
                .language("General")
                .source("Platform Debugging & Diagnostic Handbook")
                .concept("Recursion & Call Stack")
                .tags(Arrays.asList("recursion", "stackoverflow", "call stack", "memoization", "tle", "debugging"))
                .content("""
                        # Debugging Recursion: Why It Fails or Runs Slow
                        
                        Recursion is elegant, but unoptimized or unbounded recursive functions frequently lead to two critical failure modes: `StackOverflowError` and Time Limit Exceeded (`TLE`).
                        
                        ## 1. Why `StackOverflowError` Occurs
                        - Each recursive function call pushes a new **Stack Frame** onto the JVM Thread Stack (storing arguments, local variables, return address).
                        - The JVM thread stack has a default size limit (typically 1024 KB).
                        - If recursion depth exceeds roughly 10,000 calls without hitting a base case, the stack is exhausted, throwing `StackOverflowError`.
                        - **Fixes**:
                          1. Verify base case conditions (e.g. `if (node == null) return;` or `if (left > right) return;`).
                          2. Convert deep recursion into an iterative loop using an explicit heap-allocated `Deque<Frame> stack = new ArrayDeque<>()`.
                        
                        ## 2. Why Naive Recursion is Slow (O(2^N) Exponential Explosion)
                        - In problems like Fibonacci, recursion without caching computes identical subproblems exponentially many times.
                        - Computing `fib(50)` without memoization takes over 1 quadrillion operations ($2^{50}$).
                        - **Fix**: Add Memoization (`Map<Integer, Long> memo` or `long[] memo`) to reduce time complexity to O(N) linear operations.
                        """)
                .build());

        // 12. Debugging: Common DSA Pitfalls & Big-O
        docs.add(KnowledgeDocument.builder()
                .id("doc-debug-dsa-pitfalls")
                .title("Big-O Complexity Analysis & Common DSA Implementation Pitfalls")
                .topic("DEBUGGING_GUIDE")
                .difficulty("BEGINNER")
                .language("General")
                .source("Platform Debugging & Diagnostic Handbook")
                .concept("Big-O Complexity & Pitfalls")
                .tags(Arrays.asList("big o", "time complexity", "space complexity", "integer overflow", "npe", "pitfalls"))
                .content("""
                        # Big-O Complexity & Common Coding Pitfalls
                        
                        Big-O notation describes how execution time or memory consumption scales as input size `N` grows toward infinity.
                        
                        ## Standard Complexity Hierarchy (Fastest to Slowest)
                        1. **O(1) Constant**: Hash map lookup, array indexing, arithmetic operations.
                        2. **O(log N) Logarithmic**: Binary search, balanced BST operations, GCD Euclid algorithm.
                        3. **O(N) Linear**: Single array traversal, two pointers, sliding window.
                        4. **O(N log N) Linearithmic**: Merge sort, Quick sort, Heap sort, sorting arrays.
                        5. **O(N^2) Quadratic**: Nested loops, bubble sort, naive all-pairs comparison.
                        6. **O(2^N) Exponential**: Naive recursive branching (Subsets, Traveling Salesperson).
                        7. **O(N!) Factorial**: Generating all permutations.
                        
                        ## Common Implementation Pitfalls
                        1. **Integer Overflow**: Multiplying two 32-bit `int` values (e.g. `1_000_000 * 1_000_000`) overflows before assignment. Cast to `long` first: `(long) a * b`.
                        2. **Off-by-One Loop Boundary**: Using `i <= arr.length` instead of `i < arr.length` throws `ArrayIndexOutOfBoundsException`.
                        3. **String Concatenation in Loops**: `s += char` creates a new String on every step (O(N^2)). Always use `StringBuilder` for O(N) linear time.
                        """)
                .build());

        return docs;
    }
}
