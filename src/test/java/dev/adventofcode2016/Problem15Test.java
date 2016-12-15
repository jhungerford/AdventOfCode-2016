package dev.adventofcode2016;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem15Test {

  @Test
  public void part1Example() {
    Problem15.Machine machine = new Problem15.Machine(
        new Problem15.Disk(1, 5, 4),
        new Problem15.Disk(2, 2, 1)
    );

    assertThat(machine.earliestTime()).isEqualTo(5);
  }

}
