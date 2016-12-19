package dev.adventofcode2016;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem19Test {

  @Test
  public void fiveElves() {
    assertThat(Problem19.elfWithPresents(5)).isEqualTo(3);
  }

  @Test
  public void rightElf() {
    assertThat(Problem19.findRightElf(new boolean[] {true, true, true, true, true}, 0)).isEqualTo(1);
    assertThat(Problem19.findRightElf(new boolean[] {true, false, true, true, true}, 2)).isEqualTo(3);
    assertThat(Problem19.findRightElf(new boolean[] {true, false, true, false, true}, 4)).isEqualTo(0);
    assertThat(Problem19.findRightElf(new boolean[] {false, false, true, false, true}, 2)).isEqualTo(4);
  }
}
