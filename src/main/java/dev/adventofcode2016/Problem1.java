package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Resources;

import java.io.IOException;

public class Problem1 {

  private static final Splitter STEP_SPLITTER = Splitter.on(", ");

  public enum Direction {
    NORTH, EAST, SOUTH, WEST;

    public Direction turn(char direction) {
      int offset = direction == 'R' ? 1 : -1;


      int numDirections = Direction.values().length;
      return Direction.values()[(this.ordinal() + offset + numDirections) % numDirections];
    }
  }

  public static class Position {
    public static final Position START = new Position(0, 0, Direction.NORTH);

    public final int xBlock;
    public final int yBlock;
    public final Direction direction;

    public Position(int xBlock, int yBlock, Direction direction) {
      this.xBlock = xBlock;
      this.yBlock = yBlock;
      this.direction = direction;
    }

    public Position move(String step) {
      char direction = step.charAt(0);
      int amount = Integer.valueOf(step.substring(1));

      Direction newDirection = this.direction.turn(direction);

      switch (newDirection) {
        case NORTH:
          return new Position(this.xBlock, this.yBlock + amount, Direction.NORTH);
        case EAST:
          return new Position(this.xBlock + amount, this.yBlock, Direction.EAST);
        case SOUTH:
          return new Position(this.xBlock, this.yBlock - amount, Direction.SOUTH);
        case WEST:
          return new Position(this.xBlock - amount, this.yBlock, Direction.WEST);
        default:
          throw new IllegalStateException("Unknown direction " + newDirection);
      }
    }

    public int blocksFromStart() {
      return Math.abs(xBlock) + Math.abs(yBlock);
    }

    @Override
    public String toString() {
      return "Position{" + xBlock + ", " + yBlock + " - " + direction + '}';
    }
  }

  public static Position follow(String steps) {
    Position position = Position.START;

    for (String step : STEP_SPLITTER.split(steps)) {
      position = position.move(step);
    }

    return position;
  }

  public static void main(String[] args) throws IOException {
    String part1Steps = Resources.toString(Resources.getResource("problem1.txt"), Charsets.UTF_8);
    Position part1 = follow(part1Steps);
    System.out.println("Part1: " + part1.blocksFromStart() + " blocks from start");
  }
}
