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

  @Test
  public void v2PlainString() {
    assertThat(Problem9.decompressedLengthV2("ADVENT")).isEqualTo(6);
  }

  @Test
  public void v2NoMarkers() {
    assertThat(Problem9.decompressedLengthV2("(3x3)XYZ")).isEqualTo(9);
  }

  @Test
  public void v2OneNestedMarker() {
    assertThat(Problem9.decompressedLengthV2("X(8x2)(3x3)ABCY")).isEqualTo(20);
  }

  @Test
  public void v2ManyImmediatelyNestedMarkers() {
    assertThat(Problem9.decompressedLengthV2("(27x12)(20x12)(13x14)(7x10)(1x12)A")).isEqualTo(241920);
  }

  @Test
  public void v2ManyTreeNestedMarkers() {
    assertThat(Problem9.decompressedLengthV2("(25x3)(3x3)ABC(2x3)XY(5x2)PQRSTX(18x9)(3x2)TWO(5x7)SEVEN"))
        .isEqualTo(445);
  }
}
