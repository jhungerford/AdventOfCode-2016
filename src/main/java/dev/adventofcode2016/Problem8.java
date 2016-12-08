package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Problem8 {

  public static class Screen {
    private static final Pattern RECT_PATTERN = Pattern.compile("rect (\\d+)x(\\d+)");
    private static final Pattern ROTATE_COLUMN_PATTERN = Pattern.compile("rotate column x=(\\d+) by (\\d+)");
    private static final Pattern ROTATE_ROW_PATTERN = Pattern.compile("rotate row y=(\\d+) by (\\d+)");

    private final int width;
    private final int height;
    private boolean[][] pixels;

    /**
     * Constructs a new screen that is {@code width} pixels wide and {@code height} pixels tall.
     * @param width  Width of the display
     * @param height Height of the display
     */
    public Screen(int width, int height) {
      this.width = width;
      this.height = height;
      this.pixels = new boolean[height][width];
    }

    /**
     * Executes the given instruction on this screen.
     *
     * @param instruction Instruction to execute
     */
    public void instruction(String instruction) {
      Matcher rectMatcher = RECT_PATTERN.matcher(instruction);
      if (rectMatcher.matches()) {
        this.rect(
            Integer.parseInt(rectMatcher.group(1)),
            Integer.parseInt(rectMatcher.group(2))
        );
      }

      Matcher rotateColumnMatcher = ROTATE_COLUMN_PATTERN.matcher(instruction);
      if (rotateColumnMatcher.matches()) {
        this.rotateColumn(
            Integer.parseInt(rotateColumnMatcher.group(1)),
            Integer.parseInt(rotateColumnMatcher.group(2))
        );
      }

      Matcher rotateRowMatcher = ROTATE_ROW_PATTERN.matcher(instruction);
      if (rotateRowMatcher.matches()) {
        this.rotateRow(
            Integer.parseInt(rotateRowMatcher.group(1)),
            Integer.parseInt(rotateRowMatcher.group(2))
        );
      }
    }

    /**
     * Turns on all of the pixels in a rectangle at the top-left of the screen which is
     * {@code width} pixels wide and {@code height} pixels tall.
     *
     * @param width  Width of the rectangle
     * @param height Height of the rectangle
     */
    public void rect(int width, int height) {
      for (int row = 0; row < height; row ++) {
        for (int column = 0; column < width; column ++) {
          pixels[row][column] = true;
        }
      }
    }

    /**
     * Shifts all of the pixels in the given row (0 is the top row) right by amount pixels.
     * Pixels that would fall off the right end appear at the left end of the row.
     *
     * @param row    Row to shift
     * @param amount Number of pixels to shift the row
     */
    public void rotateRow(int row, int amount) {
      boolean[] buffer = new boolean[width];

      for (int i = 0; i < width; i ++) {
        buffer[(i + amount) % width] = pixels[row][i];
      }

      System.arraycopy(buffer, 0, pixels[row], 0, width);
    }

    /**
     * Shifts all of the pixels in the given column (0 is the left column) down by amount pixels.
     * Pixels that would fall off the bottom appear at the top of the column.
     *
     * @param column Column to shift
     * @param amount Number of pixels to shift the column
     */
    public void rotateColumn(int column, int amount) {
      boolean[] buffer = new boolean[height];

      for (int i = 0; i < height; i ++) {
        buffer[(i + amount) % height] = pixels[i][column];
      }

      for (int i = 0; i < height; i ++) {
        pixels[i][column] = buffer[i];
      }
    }

    /**
     * Calculates the number of pixels that are illuminated on the screen.
     *
     * @return Number of pixels that are on.
     */
    public int numPixelsLit() {
      int sum = 0;

      for (boolean[] row : this.pixels) {
        for (boolean column : row) {
          if (column) {
            sum++;
          }
        }
      }

      return sum;
    }

    public String toString() {
      StringBuilder bldr = new StringBuilder(width * height + height);

      for (boolean[] row : this.pixels) {
        for (boolean column : row) {
          bldr.append(column ? '#' : '.');
        }

        bldr.append('\n');
      }

      return bldr.toString();
    }
  }

  public static void main(String[] args) throws IOException {
    List<String> lines = Resources.readLines(Resources.getResource("problem8.txt"), Charsets.UTF_8);

    Screen screen = new Screen(50, 6);
    lines.forEach(screen::instruction);

    System.out.println("Part 1: " + screen.numPixelsLit() + " pixels lit");
    System.out.println(screen);
  }
}
