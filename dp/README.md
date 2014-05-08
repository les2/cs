### Dynamic Programming Solution to Maximum Subarray Problem

First, compile everything with:

  ```
  javac MaximumSubarray.java
  ```

There are two main programs:

Generate test data (random integers).

  ```
  java RandomIntegers AMOUNT
  ```

And the solver:

  ```
  java MaximumSubarray 10 20 -15 88
  ```

You can pipe the random integers to the solver like so:

  ```
  java RandomIntegers 100 | java MaximumSubarray brute_force -
  ```

Notice that I specified that the "brute force" O(n^2) algorithm
should be used instead of the "dp" algorithm:

  ```
  java RandomIntegers 100 | java MaximumSubarray dp -
  ```

The trailing "-" (dash) is what causes it to read from the standard input stream.

If an algorithm is not specified, then the dynamic programming one is used.

As expected, the brute force algorithm will take a lot longer to complete
for large inputs.

Also note that we use plain old 'ints' for arithmetic instead of BigInteger
or something. Trying to add too many large numbers will overflow.

Here's a simple test:

  ```
  java RandomIntegers 10000 | java MaximumSubarray dp -
  java RandomIntegers 10000 | java MaximumSubarray brute_force -
  ```
