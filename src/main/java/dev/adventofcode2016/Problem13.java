package dev.adventofcode2016;

import java.util.Objects;

public class Problem13 {

  public static class Point {
    public final int x;
    public final int y;

    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Point point = (Point) o;
      return x == point.x &&
          y == point.y;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y);
    }

    @Override
    public String toString() {
      return "Point{" +
          "x=" + x +
          ", y=" + y +
          '}';
    }
  }

  public static class Maze {
    private final int input;

    public Maze(int input) {
      this.input = input;
    }

    /**
     * Returns true if the given point is open space, false if it's a wall.
     * Uses the following algorithm:
     * Find x*x + 3*x + 2*x*y + y + y*y.
     * Add the office designer's favorite number (your puzzle input).
     * Find the binary representation of that sum; count the number of bits that are 1.
     * If the number of bits that are 1 is even, it's an open space.
     * If the number of bits that are 1 is odd, it's a wall.
     *
     * @param point Point to check
     * @return Whether the point is open or a wall.
     */
    public boolean isOpen(Point point) {
      if (point.x < 0 || point.y < 0) {
        return true;
      }

      int value = point.x * point.x
          + 3 * point.x
          + 2 * point.x * point.y
          + point.y
          + point.y * point.y
          + input;

      // Taken from 5-1: Counting 1-Bits in Hacker's Delight, Second Edition (Page 81)
      // Counts the number of 1 bits in the string by divide-and-conquer
      value = (value & 0x55555555) + ((value >> 1) & 0x55555555);
      value = (value & 0x33333333) + ((value >> 2) & 0x33333333);
      value = (value & 0x0F0F0F0F) + ((value >> 4) & 0x0F0F0F0F);
      value = (value & 0x00FF00FF) + ((value >> 8) & 0x00FF00FF);
      value = (value & 0x0000FFFF) + ((value >> 16) & 0x0000FFFF);

      return value % 2 == 0;
    }
  }

}
