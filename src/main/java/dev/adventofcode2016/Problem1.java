package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public List<Position> move(String step) {
      char direction = step.charAt(0);
      int amount = Integer.valueOf(step.substring(1));

      Direction newDirection = this.direction.turn(direction);

      List<Position> positions = new ArrayList<>();
      for (int i = 1; i <= amount; i ++) {
        switch (newDirection) {
          case NORTH:
            positions.add(new Position(this.xBlock, this.yBlock + i, Direction.NORTH));
            break;
          case EAST:
            positions.add(new Position(this.xBlock + i, this.yBlock, Direction.EAST));
            break;
          case SOUTH:
            positions.add(new Position(this.xBlock, this.yBlock - i, Direction.SOUTH));
            break;
          case WEST:
            positions.add(new Position(this.xBlock - i, this.yBlock, Direction.WEST));
            break;
          default:
            throw new IllegalStateException("Unknown direction " + newDirection);
        }
      }

      return positions;
    }

    public int blocksFromStart() {
      return Math.abs(xBlock) + Math.abs(yBlock);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Position position = (Position) o;
      return xBlock == position.xBlock &&
          yBlock == position.yBlock;
    }

    @Override
    public int hashCode() {
      return Objects.hash(xBlock, yBlock);
    }

    @Override
    public String toString() {
      return "Position{" + xBlock + ", " + yBlock + " - " + direction + '}';
    }
  }

  public static Position follow(String steps) {
    Position position = Position.START;

    for (String step : STEP_SPLITTER.split(steps)) {
      List<Position> moves = position.move(step);
      position = moves.get(moves.size() - 1);
    }

    return position;
  }

  public static Position firstVisitedTwice(String steps) {
    Position position = Position.START;

    List<Position> alreadyVisited = new ArrayList<>();
    alreadyVisited.add(position);

    for (String step : STEP_SPLITTER.split(steps)) {
      List<Position> moves = position.move(step);

      for (Position move : moves) {
        if (alreadyVisited.contains(move)) {
          return move;
        }
      }

      alreadyVisited.addAll(moves);
      position = moves.get(moves.size() - 1);
    }

    return position;
  }

  public static void main(String[] args) throws IOException {
    String steps = Resources.toString(Resources.getResource("problem1.txt"), Charsets.UTF_8);
    Position part1 = follow(steps);
    System.out.println("Part1: " + part1.blocksFromStart() + " blocks from start");

    Position part2 = firstVisitedTwice(steps);
    System.out.println("Position visited twice: " + part2.blocksFromStart() + " blocks from start");
  }
}
