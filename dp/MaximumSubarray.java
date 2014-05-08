import java.io.*;
import java.util.*;

/**
 * Class for printing small inteters (in the range +/- 100)
 * to standard out for test data. You can pipe the output
 * from RandomIntegers to the MaximumSubarray.
 */
class RandomIntegers {
  public static void main(String[] args) {
    if(args.length == 0) {
      System.err.println("usage: java RandomIntegers AMOUNT");
      System.exit(1);
    }
    Random rand = new Random();
    int amount = Integer.parseInt(args[0]);
    for(int i = 0; i < amount; i++) {
      int next = rand.nextInt(100);
      if(rand.nextBoolean()) {
        next = -next;
      }
      System.out.println(next);
    }
  }
}

/**
 * <p>Attempt at solving the "maximum subarray" problem as
 * described in the CLRS algorithms book using dynamic programming.</p>
 *
 * @author Lloyd Smith II
 */
class MaximumSubarray {

  enum Algorithm {
    BRUTE_FORCE("brute_force") {
      @Override
      public Subarray solve(int[] array) {
        return Subarray.maximumBruteForce(array);
      }
    },
    DYNAMIC_PROGRAMMING("dp") {
      @Override
      public Subarray solve(int[] array) {
        return Subarray.maximum(array);
      }
    };

    private final String key;

    private Algorithm(String key) {
      this.key = key;
    }

    public static Algorithm fromString(String input) {
      for(Algorithm a : values()) {
        if(a.key.equals(input) || a.name().equals(input))
          return a;
      }
      return null;
    }

    public static String availableAlgorithms(String separator) {
      StringBuilder buf = new StringBuilder();
      for(Algorithm a : values()) {
        if(buf.length() > 0) {
          buf.append(separator);
        }
        buf.append(a.key);
      }
      return buf.toString();
    }

    /**
     * Gets a maximum Subarray of the given array using this Algorithm.
     *
     * @param array
     *          the array to use
     */
    public abstract Subarray solve(int[] array);
  }

  private static int[] parseStandardIn() throws IOException {
    List<String> input = new ArrayList<>();
    try(BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
      String line = null;
      while((line = in.readLine()) != null) {
        input.add(line);
      }
    }
    return ArrayUtils.parse(input.toArray(new String[input.size()]));
  }

  public static void main(String[] args) throws IOException {

    String help = String.format("usage: java MaximumSubarray [%s] INTEGER INTEGER...", Algorithm.availableAlgorithms(" | "));

    if(args.length == 0) {
      System.err.println(help);
      System.exit(1);
    }

    Algorithm algorithm;
    int startParsingIndex = 1;

    algorithm = Algorithm.fromString(args[0]);
    if(algorithm == null) {
      algorithm = Algorithm.DYNAMIC_PROGRAMMING;
      startParsingIndex = 0;
    }

    if(!(args.length > startParsingIndex)) {
      System.err.println(help);
      System.exit(1);
    }

    final int[] array;
    if("-".equals(args[startParsingIndex])
        && args.length == (startParsingIndex+1)) {
      array = parseStandardIn();
    } else {
      array = ArrayUtils.parse(args, startParsingIndex);
    }

    // System.out.println("== array ==");
    // System.out.println(ArrayUtils.toString(array));

    System.out.println("-- Using algorithm: " + algorithm);
    System.out.println("== maximum subarray ==");
    System.out.println(algorithm.solve(array).toString(array));
  }
}

/**
 * A Subarray is a start and end index (inclusive) of a subarray,
 * the sum of the elements within the range, and the sum of the elements
 * between the end of the subarray and the end of the array.
 */
class Subarray {

  /** The start index in the array. */
  private final int start;

  /** The end index in the array (inclusive). */
  private final int end;

  /** The sum of the elements in the range of this Subarray. */
  private final int sum;

  /** The sum of the elements between end+1 and j (inclusive) for
   * for an some array index j. For example, if start = 3 and end = 6,
   * the debt could be the sum of the elements 7 ... j (inclusive)
   * for the any j &lgt;= 7.
   */
  private final int debt;

  private Subarray(int start, int end, int sum, int debt) {
    if(end < start || start < 0)
      throw new IllegalArgumentException(String.format("end < start or start < 0: start: %d, end: %d", start, end));

    this.start = start;
    this.end = end;
    this.sum = sum;
    this.debt = debt;
  }

  /**
   * Get a Subarray consisting of the single element in the given array.
   * The initial debt is zero.
   *
   * @param array
   *          an array
   * @param index
   *          the start (and end) index of the Subarray
   */
  public static Subarray ofElement(int[] array, int index) {
    return new Subarray(index, index, array[index], 0);
  }

  public static Subarray fromRange(int[] array, int start, int end) {
    return new Subarray(start, end, ArrayUtils.sum(array, start, end+1), 0);
  }

  /**
   * Get a new Subarray with the given "debt" (positive or negative) added.
   * The start, end, and sum will be the same after the adjustment.
   *
   * @param debtAdjustment
   *            the amount to add to the debt
   */
  public Subarray adjust(int debtAdjustment) {
    return new Subarray(start, end, sum, debt+debtAdjustment);
  }

  /**
   * Get a Subarray starting at this Subarray's start index and extending
   * to the new end index given by 'j'. The given value and the existing
   * debt are added to the existing sum. The new debt will be zero.
   *
   * @param value
   *          the value of element j
   * @param j
   *          the index to which this Subarray is extended
   */
  public Subarray extend(int value, int j) {
    if(!(j > end)) {
      throw new IllegalArgumentException(String.format("must extend Subarray beyond current end. end: %d, j: %d", end, j));
    }

    return new Subarray(start, j, sum+debt+value, 0);
  }

  public int size() {
    return (end-start)+1;
  }

  public String toString(final int[] array) {
    StringBuilder buf = new StringBuilder(ArrayUtils.toString(array, start, end+1));
    buf.append('\n').append("\tstart: ").append(start);
    buf.append('\n').append("\tend: ").append(end);
    buf.append('\n').append("\tsize: ").append(size());
    buf.append('\n').append("\tsum: ").append(sum);
    return buf.toString();
  }

  private static Subarray max(Subarray a, Subarray b, Subarray c) {
    if (a.sum > b.sum)
      return a.sum > c.sum ? a : c;
    return b.sum > c.sum ? b : c;
  }
  private static Subarray max(Subarray a, Subarray b) {
    return a.sum > b.sum ? a : b;
  }

  // this is the slowest algorithm; it should take a while
  // to complete for large values arrays of length 10,000 or greater
  public static Subarray maximumBruteForce(final int[] array) {
    if(array.length == 0)
      throw new IllegalArgumentException("Empty array has no maximum subarray.");

    // note: this algorithm is O(n^3) -- it can be optimized
    // to O(n^2) (and still be brute force)
    Subarray max = null;
    for(int start = 0; start < array.length; start++) {
      for(int end = start; end < array.length; end++) {
        if(max == null) {
          max = Subarray.fromRange(array, start, end);
        } else {
          Subarray test = Subarray.fromRange(array, start, end);
          if(test.sum > max.sum) {
            max = test;
          }
        }
      }
    }

    return max;
  }

  // this is the best algorithm: fast and no stack overflow risk
  // note: this could be made to work in a streaming fashion as well
  public static Subarray maximum(final int[] array) {
    if(array.length == 0)
      throw new IllegalArgumentException("Empty array has no maximum subarray.");

    // the maximum subarray seen so far
    Subarray max = Subarray.ofElement(array, 0);

    // the maximum subarray seen so far ending at n
    Subarray maxEndingAtN = Subarray.ofElement(array, 0);
    for(int n = 1; n < array.length; n++) {

        maxEndingAtN = max(Subarray.ofElement(array, n),
                               maxEndingAtN.extend(array[n], n));

        max = max(max.adjust(array[n]),
                     max.extend(array[n], n),
                     maxEndingAtN);
    }

    return max;
  }
}

class ArrayUtils {

  private ArrayUtils() { /* utility class */ }

  public static int[] parse(String[] array, int start) {
    // length = 10
    // start = 2 (3rd element)
    // result = 8 elements (2, 3, 4, 5, 6, 7, 8, 9)
    int[] result = new int[array.length - start];

    for(int i = start; i < array.length; i++) {
      result[i - start] = Integer.parseInt(array[i]);
    }
    return result;
  }

  public static int[] parse(String[] array) {
    return parse(array, 0);
  }

  public static String toString(final int[] array) {
    return toString(array, 0, array.length);
  }

  public static String toString(final int[] array, final int start, final int end) {
    if(array.length == 0) return "[]";

    StringBuilder buf = new StringBuilder("[");
    for(int i = start; i < end; i++) {
      if(i > start) buf.append(", ");
      buf.append(array[i]);
    }

    return buf.append("]").toString();
  }

  public static int sum(final int[] array) {
    return sum(array, 0, array.length);
  }

  public static int sum(final int[] array, final int start, final int end) {
    int sum = 0;
    for(int i = start; i < end; i++) {
      sum += array[i];
    }
    return sum;
  }
}

