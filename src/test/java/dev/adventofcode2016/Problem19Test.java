package dev.adventofcode2016;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem19Test {

  @Test
  public void fiveElvesStealToTheRight() {
    assertThat(Problem19.elfWithPresents(5, Problem19::findRightElf)).isEqualTo(3);
  }

  @Test
  public void rightElf() {
    assertThat(Problem19.findRightElf(new boolean[] {true, true, true, true, true}, 0, 5)).isEqualTo(1);
    assertThat(Problem19.findRightElf(new boolean[] {true, false, true, true, true}, 2, 4)).isEqualTo(3);
    assertThat(Problem19.findRightElf(new boolean[] {true, false, true, false, true}, 4, 3)).isEqualTo(0);
    assertThat(Problem19.findRightElf(new boolean[] {false, false, true, false, true}, 2, 2)).isEqualTo(4);
    assertThat(Problem19.findRightElf(new boolean[] {false, false, true, false, false}, 2, 1)).isEqualTo(2);
  }

  @Test
  public void fiveElvesStealAcrossTheCircle() {
    assertThat(Problem19.elfWithPresents(5, Problem19::acrossCircleElf)).isEqualTo(2);
  }

  @Test
  public void acrossCircleElf() {
    assertThat(Problem19.acrossCircleElf(new boolean[] {true, true, true, true, true}, 0, 5)).isEqualTo(2);
    assertThat(Problem19.acrossCircleElf(new boolean[] {true, true, false, true, true}, 1, 4)).isEqualTo(4);
    assertThat(Problem19.acrossCircleElf(new boolean[] {true, true, false, true, false}, 3, 3)).isEqualTo(0);
    assertThat(Problem19.acrossCircleElf(new boolean[] {false, true, false, true, false}, 1, 2)).isEqualTo(3);
    assertThat(Problem19.acrossCircleElf(new boolean[] {false, true, false, false, false}, 1, 1)).isEqualTo(1);
  }

  @Test
  public void fiveElvesStealAcrossTheCircleEquation() {
    assertThat(Problem19.stealAcrossRemaining(5)).isEqualTo(2);
    assertThat(Problem19.stealAcrossRemaining(56)).isEqualTo(31);
    assertThat(Problem19.stealAcrossRemaining(81)).isEqualTo(81);
    assertThat(Problem19.stealAcrossRemaining(82)).isEqualTo(1);
    assertThat(Problem19.stealAcrossRemaining(199)).isEqualTo(155);
  }
}
