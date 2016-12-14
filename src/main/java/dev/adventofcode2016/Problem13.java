package dev.adventofcode2016;

import com.google.common.collect.ImmutableList;
import dev.adventofcode2016.algorithms.AStar;
import dev.adventofcode2016.util.ImmutableListCollector;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

  public static class Maze implements AStar<Point> {
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
        return false;
      }

      int value = point.x * point.x
          + 3 * point.x
          + 2 * point.x * point.y
          + point.y
          + point.y * point.y
          + input;

      // Hacker's Delight, Second Edition - Section 5-1 Counting 1-Bits (Page 81)
      // Counts the number of 1 bits in the string by divide-and-conquer
      value = (value & 0x55555555) + ((value >> 1) & 0x55555555);
      value = (value & 0x33333333) + ((value >> 2) & 0x33333333);
      value = (value & 0x0F0F0F0F) + ((value >> 4) & 0x0F0F0F0F);
      value = (value & 0x00FF00FF) + ((value >> 8) & 0x00FF00FF);
      value = (value & 0x0000FFFF) + ((value >> 16) & 0x0000FFFF);

      return value % 2 == 0;
    }

    /**
     * Calculates number of steps in the shortest path between the start and end points in this maze using A*.
     *
     * @param start Starting point
     * @param end Ending point
     * @return Number of steps in the shortest path
     */
    public int fewestSteps(Point start, Point end) {
      return shortestPath(start, end).size() - 1; // Maze doesn't count the first step as a move.
    }

    /**
     * Returns the number of steps (including the starting point) reachable in the given number of steps.
     *
     * @param start Starting position
     * @param maxSteps Number of steps to take
     * @return Number of points reachable in the given number of steps
     */
    public int reachable(Point start, int maxSteps) {
      Map<Point, Integer> steps = new HashMap<>(); // Map of point to the number of steps required to reach the point.
      Set<Point> open = new HashSet<>(); // Points left to visit

      open.add(start);
      steps.put(start, 0);

      while (!open.isEmpty()) {
        Point current = open.stream()
            .sorted(Comparator.comparing(steps::get)) // Compare the score for each point
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Open set is empty."));

        open.remove(current);

        for (Point neighbor : neighbors(current)) {
          if (open.contains(neighbor)) {
            continue; // Already visited.
          }

          int neighborSteps = steps.get(current) + 1;
          if (neighborSteps > maxSteps) {
            continue; // Too far.
          }

          open.add(neighbor);
          steps.put(neighbor, neighborSteps);
        }
      }

      return steps.keySet().size();
    }

    /**
     * Returns the open neighbors of the given point.
     *
     * @param point Point to find neighbors for
     * @return Open (non-wall) neighbors of the given point in this maze.
     */
    public ImmutableList<Point> neighbors(Point point) {
      return Stream.of(
          new Point(point.x - 1, point.y),
          new Point(point.x + 1, point.y),
          new Point(point.x, point.y - 1),
          new Point(point.x, point.y + 1)
      )
          .filter(this::isOpen)
          .collect(new ImmutableListCollector<>());
    }

    /**
     * Calculates the heuristic cost of getting from the 'from' point to the 'end'.  Should
     * be less than the actual cost of getting to end for A* to converge.
     *
     * This heuristic uses the taxi-cab distance between the points, which will pass through walls
     * and be less than or equal to the actual distance.
     *
     * @param from Starting point
     * @param end  Goal point
     * @return Heuristic number of steps between from and end
     */
    public int heuristicCost(Point from, Point end) {
      return Math.abs(end.x - from.x) + Math.abs(end.y - from.y);
    }

    /**
     * Renders the given maze.  The resulting list contains a string for each line.  Open
     * spaces are rendered as '.', and walls are '#'.
     *
     * @param end Lower right corner of the maze, inclusive
     * @return Rendered maze.
     */
    public ImmutableList<String> render(Point end) {
      return IntStream.rangeClosed(0, end.y)
          .mapToObj(y ->
              IntStream.rangeClosed(0, end.x)
                  .mapToObj(x -> isOpen(new Problem13.Point(x, y)) ? '.' : '#')
                  .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                  .toString()
          ).collect(new ImmutableListCollector<>());
    }
  }

  public static void main(String[] args) {
    Maze maze = new Maze(1358);

    maze.render(new Point(40, 50)).forEach(System.out::println);

    System.out.println("Part 1: " + maze.fewestSteps(new Point(1, 1), new Point(31, 39)) + " steps");
    System.out.println("Part 2: " + maze.reachable(new Point(1, 1), 50) + " points reachable in 50 steps");
  }
}
