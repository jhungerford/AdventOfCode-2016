package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;

import java.io.IOException;

public class Problem18 {

  private static final ImmutableSet<String> TRAP_CONDITIONS = ImmutableSet.of(
      "^^.",
      ".^^",
      "^..",
      "..^"
  );

  /**
   * Generates num rows of safe (.) and trap (^) tiles based on rules from the first row.
   *
   * A new tile is a trap only in one of the following situations:
   *
   * Its left and center tiles are traps, but its right tile is not.
   * Its center and right tiles are traps, but its left tile is not.
   * Only its left tile is a trap.
   * Only its right tile is a trap.
   *
   * Walls are considered safe.
   *
   * @param firstRow First row consisting of safe tiles (.) and traps (^)
   * @param num Number of rows (including the first) to generate.
   * @return num rows (including the first)
   */
  public static ImmutableList<String> rows(String firstRow, int num) {
    ImmutableList.Builder<String> list = ImmutableList.builder();
    list.add(firstRow);

    String previousRow = firstRow;

    // Num includes firstRow which is already in the list, so generate num - 1 rows.
    for (int rowNum = 1; rowNum < num; rowNum ++) {
      StringBuilder row = new StringBuilder(firstRow.length());

      for (int tile = 0; tile < firstRow.length(); tile ++) {
        String above = ""
            + (tile == 0 ? '.' : previousRow.charAt(tile - 1))
            + previousRow.charAt(tile)
            + (tile == firstRow.length() - 1 ? '.' : previousRow.charAt(tile + 1));

        row.append(TRAP_CONDITIONS.contains(above) ? '^' : '.');
      }

      list.add(row.toString());
      previousRow = row.toString();
    }

    return list.build();
  }

  /**
   * Counts the number of safe (.) tiles in the given rows.
   *
   * @param rows Rows containing traps and safe tiles.
   * @return Number of safe tiles in the given rows.
   */
  public static int countSafe(ImmutableList<String> rows) {
    return rows.stream()
        .mapToInt(row -> row.chars().map(tile -> tile == '.' ? 1 : 0).sum())
        .sum();
  }

  public static void main(String[] args) throws IOException {
    String firstRow = Resources.toString(Resources.getResource("problem18.txt"), Charsets.UTF_8).trim();

    System.out.println("Part 1: " + countSafe(rows(firstRow, 40)) + " safe tiles");
  }
}
