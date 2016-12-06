package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Problem6 {

  /** Sorts a map of character -> occurrences so the least common letter is first. */
  public static final Comparator<Map.Entry<Character, Integer>> LEAST_COMMON_LETTER =
      Map.Entry.comparingByValue();

  /** Sorts a map of character -> occurrences so the most common letter is first. */
  public static final Comparator<Map.Entry<Character, Integer>> MOST_COMMON_LETTER =
      Map.Entry.<Character, Integer>comparingByValue().reversed();

  /**
   * Decodes a message from a list of lines.  The message is encoded using the frequency
   * of letters in each column of the lines.
   *
   * @param lines List of lines containing characters.
   * @param order Order of frequency to use to decode the message.
   *              Use {@code LEAST_COMMON_LETTER} or {@code MOST_COMMON_LETTER}.
   * @return Decoded message.
   */
  public static String errorCorrected(List<String> lines, Comparator<Map.Entry<Character, Integer>> order) {
    int numColumns = lines.get(0).length();

    // Map of character -> occurrences for each column
    List<Map<Character, Integer>> columnFrequencyMaps = IntStream.range(0, numColumns)
        .mapToObj(i -> new HashMap<Character, Integer>())
        .collect(Collectors.toList());

    // Compute the frequency of each character in each column
    for (String line : lines) {
      for (int column = 0; column < numColumns; column ++) {
        char character = line.charAt(column);

        Map<Character, Integer> frequencyMap = columnFrequencyMaps.get(column);
        frequencyMap.compute(character, (key, count) -> count == null ? 1 : count + 1);
      }
    }

    // Take the most or least common character in each column to form the answer, determined by order
    return columnFrequencyMaps.stream()
        .flatMap(map -> map.entrySet().stream()
            // Most or least common character in the column
            .sorted(order)
            .map(Map.Entry::getKey)
            .limit(1)
        )
        // Convert to string
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
        .toString();
  }

  public static void main(String[] args) throws IOException {
    List<String> lines = Resources.readLines(Resources.getResource("problem6.txt"), Charsets.UTF_8);

    System.out.println("Part 1: " + errorCorrected(lines, MOST_COMMON_LETTER));
    System.out.println("Part 2: " + errorCorrected(lines, LEAST_COMMON_LETTER));
  }
}
