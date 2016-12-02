package dev.adventofcode2016;

import static dev.adventofcode2016.Problem2.BOARD_1;
import static dev.adventofcode2016.Problem2.BOARD_2;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

public class Problem2Test {

  private static final ImmutableList<String> EXAMPLE_LINES = ImmutableList.of(
      "ULL",
      "RRDDD",
      "LURDL",
      "UUUUD"
  );

  @Test
  public void part1Example() {
    String code = Problem2.code(EXAMPLE_LINES, BOARD_1);

    assertThat(code).isEqualTo("1985");
  }

  @Test
  public void part2Example() {
    String code = Problem2.code(EXAMPLE_LINES, BOARD_2);

    assertThat(code).isEqualTo("5DB3");
  }
}
