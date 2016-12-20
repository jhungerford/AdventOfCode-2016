package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import dev.adventofcode2016.util.ImmutableListCollector;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.stream.Collectors;

public class Problem20 {

  public static class Range {
    public final long start;
    public final long end;

    public Range(long start, long end) {
      this.start = start;
      this.end = end;
    }

    public static Range fromString(String range) {
      String[] parts = range.split("-");
      return new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
    }

    @Override
    public String toString() {
      return start + "-" + end;
    }
  }

  /**
   * Finds the smallest value between 0 and max that doesn't fall into one of the ranges.
   *
   * @param max    Maximum allowed value
   * @param ranges Blacklisted ranges of values
   * @return Smallest allowed value
   */
  public static long smallestAllowed(long max, ImmutableList<Range> ranges) {
    // Collapsed ranges are sorted - one past the first range's end value is the first allowed value.
    return collapseRanges(ranges).get(0).end + 1;
  }

  /**
   * Calculates the number of values between 0 and max (inclusive) that are don't fall into one of the given ranges.
   *
   * @param max    Maximum allowed value
   * @param ranges Blacklisted range of values
   * @return Number of values that are allowed
   */
  public static long numAllowed(long max, ImmutableList<Range> ranges) {
    ImmutableList<Range> collapsed = collapseRanges(ranges);
    long numBlacklisted = collapsed.stream()
        .mapToLong(range -> range.end - range.start + 1)
        .sum();

    return max + 1 - numBlacklisted;
  }

  /**
   * Collapses overlapping ranges in the given list, and returns list of ranges sorted by start value.
   *
   * @param ranges Ranges to collapse
   * @return List of ranges with overlapping or adjacent ranges combined.
   */
  private static ImmutableList<Range> collapseRanges(ImmutableList<Range> ranges) {
    Deque<Range> sorted = ranges.stream()
        .sorted(Comparator.comparingLong(range -> range.start))
        .collect(Collectors.toCollection(ArrayDeque::new));

    ImmutableList.Builder<Range> combined = ImmutableList.builder();

    while (!sorted.isEmpty()) {
      Range first = sorted.removeFirst();

      if (sorted.isEmpty()) {
        combined.add(first);
        return combined.build();
      }

      Range second = sorted.removeFirst();

      if (second.start <= first.end + 1) {
        sorted.addFirst(new Range(
            Math.min(first.start, second.start),
            Math.max(first.end, second.end)
        ));
      } else {
        combined.add(first);
        sorted.addFirst(second);
      }
    }

    return combined.build();
  }

  public static void main(String[] args) throws IOException {
    ImmutableList<Range> ranges =
        Resources.readLines(Resources.getResource("problem20.txt"), Charsets.UTF_8).stream()
        .map(Range::fromString)
        .collect(new ImmutableListCollector<>());

    long max = 4294967295L;

    System.out.println("Part 1: smallest allowed value is " + Problem20.smallestAllowed(max, ranges));
    System.out.println("Part 2: total allowed is " + Problem20.numAllowed(max, ranges));
  }
}
