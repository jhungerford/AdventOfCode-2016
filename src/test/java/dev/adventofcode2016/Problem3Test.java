package dev.adventofcode2016;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem3Test {

  @Test
  public void possible_3_4_5() {
    assertThat(new Problem3.Triangle(3, 4, 5).isPossible()).isTrue();
  }

  @Test
  public void impossible_5_10_25() {
    assertThat(new Problem3.Triangle(5, 10, 25).isPossible()).isFalse();
  }
}
