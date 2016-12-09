package dev.adventofcode2016;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class Problem9Test {

  @Test
  public void advent() {
    assertThat(Problem9.decompress("ADVENT")).isEqualTo("ADVENT");
  }

  @Test
  public void repeatOnce() {
    assertThat(Problem9.decompress("A(1x5)BC")).isEqualTo("ABBBBBC");
  }

  @Test
  public void repeatThreeTimes() {
    assertThat(Problem9.decompress("(3x3)XYZ")).isEqualTo("XYZXYZXYZ");
  }

  @Test
  public void twoRepetitions() {
    assertThat(Problem9.decompress("A(2x2)BCD(2x2)EFG")).isEqualTo("ABCBCDEFEFG");
  }

  @Test
  public void ignoreInstructions() {
    assertThat(Problem9.decompress("(6x1)(1x3)A")).isEqualTo("(1x3)A");
  }

  @Test
  public void repeatInstructions() {
    assertThat(Problem9.decompress("X(8x2)(3x3)ABCY")).isEqualTo("X(3x3)ABC(3x3)ABCY");
  }
}
