package dev.adventofcode2016;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem20Test {

  @Test
  public void exampleSmallestAllowed() {
    long smallest = Problem20.smallestAllowed(9, ImmutableList.of(
        new Problem20.Range(5, 8),
        new Problem20.Range(0, 2),
        new Problem20.Range(4, 7)
        )
    );

    assertThat(smallest).isEqualTo(3);
  }
}
