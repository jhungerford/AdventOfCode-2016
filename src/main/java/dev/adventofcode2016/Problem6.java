package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Problem6 {

  public static String errorCorrected(List<String> lines) {
    int numColumns = lines.get(0).length();

    // Compute a map of character -> occurrences for each column
    List<Map<Character, Integer>> columnCount = IntStream.range(0, numColumns)
        .mapToObj(i -> new HashMap<Character, Integer>())
        .collect(Collectors.toList());

    for (String line : lines) {
      for (int column = 0; column < numColumns; column ++) {
        char c = line.charAt(column);

        columnCount.get(column).compute(c, (key, count) -> count == null ? 1 : count + 1);
      }
    }

    // Take the most common character in each column to form the answer
    return columnCount.stream()
        .flatMap(map ->
            map.entrySet().stream()
                .sorted(Map.Entry.<Character, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey) // Most common character in the column
                .limit(1)
        ).collect(StringBuilder::new, StringBuilder::append, StringBuilder::append) // Convert to string
        .toString();
  }

  public static void main(String[] args) throws IOException {
    List<String> lines = Resources.readLines(Resources.getResource("problem6.txt"), Charsets.UTF_8);

    System.out.println("Part 1: " + errorCorrected(lines));
  }
}