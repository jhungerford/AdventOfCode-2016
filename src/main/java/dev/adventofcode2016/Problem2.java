package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.List;


public class Problem2 {

  /*
   * 1 2 3
   * 4 5 6
   * 7 8 9
   */
  public static final char[][] BOARD_1 = new char[][]{
      {'1', '2', '3'},
      {'4', '5', '6'},
      {'7', '8', '9'}
  };

  /*
   *     1
   *   2 3 4
   * 5 6 7 8 9
   *   A B C
   *     D
   */
  public static final char[][] BOARD_2 = new char[][]{
      {'*', '*', '1', '*', '*'},
      {'*', '2', '3', '4', '*'},
      {'5', '6', '7', '8', '9'},
      {'*', 'A', 'B', 'C', '*'},
      {'*', '*', 'D', '*', '*'},
  };

  public static final class Position {
    public final int row;
    public final int column;

    public Position(int row, int column) {
      this.row = row;
      this.column = column;
    }

    public char button(char[][] board) {
      return board[row][column];
    }

    public Position move(char direction, char[][] board) {
      Position next = nextPosition(direction);

      if (next.inBounds(board) && next.button(board) != '*') {
        return next;
      }

      // Don't move past the edge of the buttons - stay in the same spot
      return this;
    }

    private Position nextPosition(char direction) {
      switch (direction) {
        case 'U':
          return new Position(row - 1, column);
        case 'D':
          return new Position(row + 1, column);
        case 'L':
          return new Position(row, column - 1);
        case 'R':
          return new Position(row, column + 1);
        default:
          throw new IllegalStateException("Invalid direction " + direction);
      }
    }

    private boolean inBounds(char[][] board) {
      return row >= 0 && row < board.length && column >= 0 && column < board[0].length;
    }
  }


  private static Position findStart(char[][] board) {
    for (int row = 0; row < board.length; row ++) {
      for (int column = 0; column < board[row].length; column ++) {
        Position position = new Position(row, column);
        if (position.button(board) == '5') {
          return position;
        }
      }
    }

    throw new IllegalStateException("Board does not contain the starting position");
  }

  public static String code(Iterable<String> lines, char[][] board) {
    String code = "";

    Position position = findStart(board);
    for (String line : lines) {

      for (char direction : line.toCharArray()) {
        position = position.move(direction, board);
      }

      code += position.button(board);
    }

    return code;
  }

  public static void main(String[] args) throws IOException {
    List<String> lines = Resources.readLines(Resources.getResource("problem2.txt"), Charsets.UTF_8);

    String part1Code = code(lines, BOARD_1);
    System.out.println("Part 1 code: " + part1Code);

    String part2Code = code(lines, BOARD_2);
    System.out.println("Part 2 code: " + part2Code);
  }
}
