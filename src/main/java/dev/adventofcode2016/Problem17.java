package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import dev.adventofcode2016.algorithms.AStar;

import java.util.Objects;

public class Problem17 implements AStar<Problem17.Position> {
  public static final HashFunction MD5 = Hashing.md5();
  private final String passcode;
  private final Position start = new Position(0, 0, "");
  private final Position end = new Position(3, 3, "");

  public Problem17(String passcode) {
    this.passcode = passcode;
  }

  /**
   * Returns the heuristic cost of traveling between from and to.  This function
   * uses the taxicab distance between the points as the heuristic cost,
   * which ignores locked doors and will always be less than or equal to the real cost.
   *
   * @param from Starting position
   * @param to   Ending position
   * @return Heuristic cost of traveling between the points.
   */
  @Override
  public int heuristicCost(Position from, Position to) {
    return Math.abs(to.x - from.x) + Math.abs(to.y - from.y);
  }

  /**
   * Returns a list of valid neighbors from the given position.
   *
   * @param position Position to calculate neighbors for
   * @return Neighbors of the given position.
   */
  @Override
  public ImmutableList<Position> neighbors(Position position) {
    String md5 = MD5.hashString(passcode + position.pathSoFar, Charsets.UTF_8).toString();

    // First four characters are the unlocked doors - Up, Down, Left, and Right
    // are unlocked if the characters are b, c, d, e, or f

    ImmutableList.Builder<Position> list = ImmutableList.builder();

    if (isUnlocked(md5.charAt(0)) && position.y > 0) {
      list.add(new Position(position.x, position.y - 1, position.pathSoFar + 'U'));
    }

    if (isUnlocked(md5.charAt(1)) && position.y < 3) {
      list.add(new Position(position.x, position.y + 1, position.pathSoFar + 'D'));
    }

    if (isUnlocked(md5.charAt(2)) && position.x > 0) {
      list.add(new Position(position.x - 1, position.y, position.pathSoFar + 'L'));
    }

    if (isUnlocked(md5.charAt(3)) && position.x < 3) {
      list.add(new Position(position.x + 1, position.y, position.pathSoFar + 'R'));
    }

    return list.build();
  }

  /**
   * Returns whether the given character indicates that a door is unlocked.  Characters b, c, d, e, and f
   * indicate an unlocked door.  All other characters (including a) indicate that the door is locked.
   *
   * @param c Character to check
   * @return Whether the door is unlocked
   */
  private boolean isUnlocked(char c) {
    return c >= 'b' && c <= 'f';
  }

  /**
   * Calculates the shortest path from the top left position on the floor to the bottom right
   * in a 4x4 grid of small rooms connected by doors.
   *
   * The doors in your current room are either open or closed (and locked) based on the hexadecimal
   * MD5 hash of a passcode (your puzzle input) followed by a sequence of uppercase characters representing
   * the path you have taken so far (U for up, D for down, L for left, and R for right).

   Only the first four characters of the hash are used; they represent, respectively, the doors
   up, down, left, and right from your current position. Any b, c, d, e, or f means that the
   corresponding door is open; any other character (any number or a) means that the corresponding
   door is closed and locked.
   *
   * @return Sequence of doors opened to reach the lower right room.
   */
  public String shortestPath() {
    return shortestPath(start, end).reverse().get(0).pathSoFar;
  }

  public final class Position {
    public final int x;
    public final int y;
    public final String pathSoFar;

    public Position(int x, int y, String pathSoFar) {
      this.x = x;
      this.y = y;
      this.pathSoFar = pathSoFar;
    }

    @Override
    public String toString() {
      return "Position{" +
          "x=" + x +
          ", y=" + y +
          ", pathSoFar='" + pathSoFar + '\'' +
          '}';
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }

      if (other == null || getClass() != other.getClass()) {
        return false;
      }

      Position position = (Position) other;
      if (x != position.x || y != position.y) {
        return false;
      }

      // All doors unlock once you reach the vault, so the path doesn't matter.
      if (x == end.x && y == end.y) {
        return true;
      }

      // At all other positions, the path determines which doors are open, so the same position counts
      // as a different place in A* if the paths are different
      return Objects.equals(pathSoFar, position.pathSoFar);
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y, passcode, pathSoFar);
    }
  }

  public static void main(String[] args) {
    Problem17 part1 = new Problem17("mmsxrhfx");
    System.out.println("Part 1: shortest path is " + part1.shortestPath());
  }
}
