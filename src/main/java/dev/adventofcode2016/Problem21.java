package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import dev.adventofcode2016.util.ImmutableListCollector;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Problem21 {

  public static class Scrambler {
    private final ImmutableList<Step> steps;

    public Scrambler(ImmutableList<Step> steps) {
      this.steps = steps;
    }

    /**
     * Applies all of the scrambling step to the given password, and returns the result.
     *
     * @param password Password to scramble
     * @return Scrambled password
     */
    public String scramble(String password) {
      String scrambled = password;
      for (Step step : steps) {
        scrambled = step.apply(scrambled);
      }

      return scrambled;
    }
  }

  public interface Step {
    /**
     * Applies this scrambling step to the input and returns the result.
     *
     * @param input String to scramble
     * @return Scrambled string
     */
    String apply(String input);
  }

  /**
   * swap position X with position Y means that the letters at indexes X and Y (counting from 0) should be swapped.
   */
  public static class SwapPositionsStep implements Step {
    private final int x;
    private final int y;

    public SwapPositionsStep(int x, int y) {
      if (x < y) {
        this.x = x;
        this.y = y;
      } else {
        this.x = y;
        this.y = x;
      }
    }

    @Override
    public String apply(String input) {
      return input.substring(0, x)
          + input.charAt(y)
          + input.substring(x + 1, y)
          + input.charAt(x)
          + input.substring(y + 1);
    }
  }

  /**
   * swap letter X with letter Y means that the letters X and Y should be swapped
   * (regardless of where they appear in the string).
   */
  public static class SwapLetterStep implements Step {
    private final char x;
    private final char y;

    public SwapLetterStep(char x, char y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public String apply(String input) {
      return new SwapPositionsStep(input.indexOf(x), input.indexOf(y)).apply(input);
    }
  }

  /**
   * rotate left/right X steps means that the whole string should be rotated;
   * for example, one right rotation would turn abcd into dabc.
   */
  public static class RotateStep implements Step {
    private final boolean left;
    private final int steps;

    public RotateStep(boolean left, int steps) {
      this.left = left;
      this.steps = steps;
    }

    @Override
    public String apply(String input) {
      int amount = steps % input.length();

      if (left) {
        return input.substring(amount) + input.substring(0, amount);
      } else {
        return input.substring(input.length() - amount) + input.substring(0, input.length() - amount);
      }
    }
  }

  /**
   * rotate based on position of letter X means that the whole string should be rotated to
   * the right based on the index of letter X (counting from 0) as determined before this
   * instruction does any rotations. Once the index is determined, rotate the string to the
   * right one time, plus a number of times equal to that index, plus one additional time if
   * the index was at least 4.
   */
  public static class RotatePositionStep implements Step {
    private final char letter;

    public RotatePositionStep(char letter) {
      this.letter = letter;
    }

    @Override
    public String apply(String input) {
      int index = input.indexOf(letter);
      int amount = 1 + index + (index < 4 ? 0 : 1);

      return new RotateStep(false, amount).apply(input);
    }
  }

  /**
   * reverse positions X through Y means that the span of letters at indexes X through Y
   * (including the letters at X and Y) should be reversed in order.
   */
  public static class ReverseStep implements Step {
    private final int x;
    private final int y;

    public ReverseStep(int x, int y) {
      if (x < y) {
        this.x = x;
        this.y = y;
      } else {
        this.x = y;
        this.y = x;
      }
    }

    @Override
    public String apply(String input) {
      return input.substring(0, x)
          + new StringBuilder(input.substring(x, y + 1)).reverse().toString()
          + input.substring(y + 1);
    }
  }

  /**
   * move position X to position Y means that the letter which is at index X should be removed
   * from the string, then inserted such that it ends up at index Y.
   */
  public static class MoveStep implements Step {
    private final int x;
    private final int y;

    public MoveStep(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public String apply(String input) {
      if (x > y) {
        return input.substring(0, y)
            + input.charAt(x)
            + input.substring(y, x)
            + input.substring(x + 1);
      } else {
        return input.substring(0, x)
            + input.substring(x + 1, y + 1)
            + input.charAt(x)
            + input.substring(y + 1);
      }
    }
  }

  private static final ImmutableMap<Pattern, Function<Matcher, ? extends Step>> STEP_PATTERNS =
      ImmutableMap.<Pattern, Function<Matcher, ? extends Step>>builder()
          .put(
              Pattern.compile("swap position (\\d+) with position (\\d+)"),
              matcher -> new SwapPositionsStep(
                  Integer.parseInt(matcher.group(1)),
                  Integer.parseInt(matcher.group(2))
              )
          )
          .put(
              Pattern.compile("swap letter ([a-z]) with letter ([a-z])"),
              matcher -> new SwapLetterStep(
                  matcher.group(1).charAt(0),
                  matcher.group(2).charAt(0)
              )
          )
          .put(
              Pattern.compile("rotate ((?:left)|(?:right)) (\\d+) steps?"),
              matcher -> new RotateStep(
                  "left".equals(matcher.group(1)),
                  Integer.parseInt(matcher.group(2))
              )
          )
          .put(
              Pattern.compile("rotate based on position of letter ([a-z])"),
              matcher -> new RotatePositionStep(
                  matcher.group(1).charAt(0)
              )
          )
          .put(
              Pattern.compile("reverse positions (\\d+) through (\\d+)"),
              matcher -> new ReverseStep(
                  Integer.parseInt(matcher.group(1)),
                  Integer.parseInt(matcher.group(2))
              )
          )
          .put(
              Pattern.compile("move position (\\d+) to position (\\d+)"),
              matcher -> new MoveStep(
                  Integer.parseInt(matcher.group(1)),
                  Integer.parseInt(matcher.group(2))
              )
          )
          .build();

  public static Step parseStep(String line) {
    // Find the first pattern that matches the line and use it's function to create the step.
    for (Map.Entry<Pattern, Function<Matcher, ? extends Step>> entry : STEP_PATTERNS.entrySet()) {
      Matcher matcher = entry.getKey().matcher(line);

      if (matcher.matches()) {
        return entry.getValue().apply(matcher);
      }
    }

    throw new IllegalStateException("'" + line + "' is not a valid step");
  }

  public static void main(String[] args) throws IOException {
    Scrambler scrambler = new Scrambler(
        Resources.readLines(Resources.getResource("problem21.txt"), Charsets.UTF_8).stream()
            .map(Problem21::parseStep)
            .collect(new ImmutableListCollector<>())
    );

    System.out.println("Part 1: " + scrambler.scramble("abcdefgh"));
  }
}
