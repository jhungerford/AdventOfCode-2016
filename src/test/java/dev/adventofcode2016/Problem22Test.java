package dev.adventofcode2016;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem22Test {

  @Test
  public void parseNode() {
    assertThat(Problem22.Node.fromLine("/dev/grid/node-x0-y0     94T   72T    22T   76%"))
        .hasValue(new Problem22.Node("/dev/grid/node-x0-y0", 0, 0, 94, 72, 22, 76));
  }

  @Test
  public void parseNonNode() {
    assertThat(Problem22.Node.fromLine("root@ebhq-gridcenter# df -h")).isEmpty();
  }
}
