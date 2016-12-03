package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Problem3 {

  /**
   * Triangle contains three sides of a triangle, in increasing order.
   */
  public static class Triangle {

    public final int side1;
    public final int side2;
    public final int side3;

    /**
     * Constructs a triangle from the three positive sides.
     * The sides don't need to be sorted - this constructor will sort them.
     *
     * @param side1 First side
     * @param side2 Second side
     * @param side3 Third side
     */
    public Triangle(int side1, int side2, int side3) {
      int[] sides = new int[]{side1, side2, side3};
      Arrays.sort(sides);

      this.side1 = sides[0];
      this.side2 = sides[1];
      this.side3 = sides[2];
    }

    /**
     * Returns whether this triangle is possible (i.e. if the sum of two sides are larger than the third)
     *
     * @return whether this triangle is possible.
     */
    public boolean isPossible() {
      return (side1 + side2) > side3;
    }
  }

  /**
   * Converts the given untrimmed line to a single Triangle.  The three values on the line make up the sides.
   *
   * @param line Line of sides to convert to a triangle
   * @return Triangle
   */
  public static Triangle fromLine(String line) {
    int[] values = Arrays.stream(line.trim().split(" +"))
        .map(String::trim)
        .mapToInt(Integer::valueOf)
        .sorted()
        .toArray();

    if (values.length != 3) {
      throw new IllegalArgumentException("Line must contain three sides: '" + line + "'");
    }

    return new Triangle(values[0], values[1], values[2]);
  }

  /**
   * Converts the given list of three rows to three Triangles.  Forms triangles from the columns.
   *
   * @param rows Three rows to convert to triangles
   * @return Triangles from the columns of the rows.
   */
  public static Stream<Triangle> fromColumns(List<String> rows) {
    // Convert rows of raw strings to a 3x3 int array
    int[][] values = rows.stream()
        .map(String::trim)
        .map(row ->
            Arrays.stream(row.split(" +"))
                .mapToInt(Integer::valueOf)
                .toArray()
        ).toArray(int[][]::new);

    // Triangle per column
    return IntStream.range(0, 3)
        .mapToObj(column -> new Triangle(
            values[0][column],
            values[1][column],
            values[2][column]
        ));
  }

  public static void main(String[] args) throws IOException {
    List<String> lines = Resources.readLines(Resources.getResource("problem3.txt"), Charsets.UTF_8);

    long numPossiblePart1 = lines.parallelStream()
        .map(Problem3::fromLine)
        .filter(Triangle::isPossible)
        .count();

    System.out.println("Part 1: " + numPossiblePart1 + " possible triangles");

    // Triangles are in vertical columns across three rows.  Collect batches of 3 rows, and convert them to triangles.
    List<String> batch = new ArrayList<>(3);
    long numPossiblePart2 = lines.stream()
        .flatMap(line -> {
          batch.add(line);

          if (batch.size() == 3) {
            Stream<Triangle> triangles = fromColumns(batch);
            batch.clear();
            return triangles;
          }

          return Stream.of();
        })
        .filter(Triangle::isPossible)
        .count();

    System.out.println("Part 2: " + numPossiblePart2 + " possible triangles");
  }
}
