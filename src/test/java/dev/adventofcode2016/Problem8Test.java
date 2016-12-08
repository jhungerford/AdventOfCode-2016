package dev.adventofcode2016;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem8Test {

  @Test
  public void numPixelsLitNewScreen() {
    Problem8.Screen screen = new Problem8.Screen(3, 2);
    assertThat(screen.numPixelsLit()).isEqualTo(0);
  }

  @Test
  public void part1Example() {
    Problem8.Screen screen = new Problem8.Screen(7, 3);

    screen.instruction("rect 3x2");
    assertThat(screen.toString()).isEqualTo(
        "###....\n" +
        "###....\n" +
        ".......\n"
    );

    screen.instruction("rotate column x=1 by 1");
    assertThat(screen.toString()).isEqualTo(
        "#.#....\n" +
        "###....\n" +
        ".#.....\n"
    );

    screen.instruction("rotate row y=0 by 4");
    assertThat(screen.toString()).isEqualTo(
        "....#.#\n" +
        "###....\n" +
        ".#.....\n"
    );

    screen.instruction("rotate column x=1 by 1");
    assertThat(screen.toString()).isEqualTo(
        ".#..#.#\n" +
        "#.#....\n" +
        ".#.....\n"
    );

    assertThat(screen.numPixelsLit()).isEqualTo(6);
  }
}
