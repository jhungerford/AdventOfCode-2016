package dev.adventofcode2016;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem20Test {

  private static final ImmutableList<Problem20.Range> EXAMPLE_RANGES = ImmutableList.of(
      new Problem20.Range(5, 8),
      new Problem20.Range(0, 2),
      new Problem20.Range(4, 7)
  );

  @Test
  public void exampleSmallestAllowed() {
    assertThat(Problem20.smallestAllowed(9, EXAMPLE_RANGES)).isEqualTo(3);
  }

  @Test
  public void exampleTotalAllowed() {
    assertThat(Problem20.numAllowed(9, EXAMPLE_RANGES)).isEqualTo(2);
  }
}
