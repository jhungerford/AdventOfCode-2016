package dev.adventofcode2016;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem5Test {

  @Test
  public void part1Example() {
    String password = Problem5.password("abc", 3);
    assertThat(password).isEqualTo("18f");
  }

}
