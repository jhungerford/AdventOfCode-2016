package dev.adventofcode2016;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem18Test {

  @Test
  public void part1SmallExampleRows() {
    assertThat(Problem18.rows("..^^.", 3)).containsExactly(
        "..^^.",
        ".^^^^",
        "^^..^"
    );
  }

  @Test
  public void part1SmallExampleSafeTiles() {
    assertThat(Problem18.countSafe(Problem18.rows("..^^.", 3))).isEqualTo(6);
  }

  @Test
  public void part1LargeExampleRows() {
    assertThat(Problem18.rows(".^^.^.^^^^", 10)).containsExactly(
        ".^^.^.^^^^",
        "^^^...^..^",
        "^.^^.^.^^.",
        "..^^...^^^",
        ".^^^^.^^.^",
        "^^..^.^^..",
        "^^^^..^^^.",
        "^..^^^^.^^",
        ".^^^..^.^^",
        "^^.^^^..^^"
    );
  }

  @Test
  public void part1LargeExampleSafeTiles() {
    assertThat(Problem18.countSafe(Problem18.rows(".^^.^.^^^^", 10))).isEqualTo(38);
  }
}
