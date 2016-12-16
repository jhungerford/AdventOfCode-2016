package dev.adventofcode2016;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem16Test {

  @Test
  public void oneStep() {
    assertThat(Problem16.step("1")).isEqualTo("100");
  }

  @Test
  public void zeroStep() {
    assertThat(Problem16.step("0")).isEqualTo("001");
  }

  @Test
  public void fiveOnesStep() {
    assertThat(Problem16.step("11111")).isEqualTo("11111000000");
  }

  @Test
  public void longStep() {
    assertThat(Problem16.step("111100001010")).isEqualTo("1111000010100101011110000");
  }

  @Test
  public void checksum() {
    assertThat(Problem16.checksum("110010110100", 12)).isEqualTo("100");
  }

}
