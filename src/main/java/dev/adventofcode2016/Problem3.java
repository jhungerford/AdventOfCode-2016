package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Problem3 {

  public static class Triangle {

    public final int side1;
    public final int side2;
    public final int side3;

    public Triangle(int side1, int side2, int side3) {
      this.side1 = side1;
      this.side2 = side2;
      this.side3 = side3;

      if (! (side1 <= side2 && side2 <= side3)) {
        throw new IllegalArgumentException("Triangle sides must be sorted in increasing order");
      }
    }

    public boolean isPossible() {
      return (side1 + side2) > side3;
    }

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
  }

  public static void main(String[] args) throws IOException {
    List<String> lines = Resources.readLines(Resources.getResource("problem3.txt"), Charsets.UTF_8);

    long numPossible = lines.stream()
        .map(Triangle::fromLine)
        .filter(Triangle::isPossible)
        .count();

    System.out.println("Part 1: " + numPossible + " possible triangles");
  }
}
