package dev.adventofcode2016;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem21Test {

  @Test
  public void swapPosition() {
    assertThat(Problem21.parseStep("swap position 4 with position 0").apply("abcde")).isEqualTo("ebcda");
  }

  @Test
  public void swapLetter() {
    assertThat(Problem21.parseStep("swap letter d with letter b").apply("ebcda")).isEqualTo("edcba");
  }

  @Test
  public void reversePositions() {
    assertThat(Problem21.parseStep("reverse positions 0 through 4").apply("edcba")).isEqualTo("abcde");
  }

  @Test
  public void reverseSubstring() {
    assertThat(Problem21.parseStep("reverse positions 1 through 3").apply("edcba")).isEqualTo("ebcda");
  }

  @Test
  public void rotateLeft() {
    assertThat(Problem21.parseStep("rotate left 1 step").apply("abcde")).isEqualTo("bcdea");
  }

  @Test
  public void rotateRight() {
    assertThat(Problem21.parseStep("rotate right 1 step").apply("bcdea")).isEqualTo("abcde");
  }

  @Test
  public void movePositionForward() {
    assertThat(Problem21.parseStep("move position 1 to position 4").apply("bcdea")).isEqualTo("bdeac");
  }

  @Test
  public void movePositionBackward() {
    assertThat(Problem21.parseStep("move position 3 to position 0").apply("bdeac")).isEqualTo("abdec");
  }

  @Test
  public void rotatePositionBeginningOfString() {
    assertThat(Problem21.parseStep("rotate based on position of letter b").apply("abdec")).isEqualTo("ecabd");
  }

  @Test
  public void rotatePositionEndOfString() {
    assertThat(Problem21.parseStep("rotate based on position of letter d").apply("ecabd")).isEqualTo("decab");
  }


  @Test
  public void reverseSwapPosition() {
    assertThat(Problem21.parseStep("swap position 4 with position 0").reverse("ebcda")).isEqualTo("abcde");
  }

  @Test
  public void reverseSwapLetter() {
    assertThat(Problem21.parseStep("swap letter d with letter b").reverse("edcba")).isEqualTo("ebcda");
  }

  @Test
  public void reverseReversePositions() {
    assertThat(Problem21.parseStep("reverse positions 0 through 4").reverse("abcde")).isEqualTo("edcba");
  }

  @Test
  public void reverseReverseSubstring() {
    assertThat(Problem21.parseStep("reverse positions 1 through 3").reverse("ebcda")).isEqualTo("edcba");
  }

  @Test
  public void reverseRotateLeft() {
    assertThat(Problem21.parseStep("rotate left 1 step").reverse("bcdea")).isEqualTo("abcde");
  }

  @Test
  public void reverseRotateRight() {
    assertThat(Problem21.parseStep("rotate right 1 step").reverse("abcde")).isEqualTo("bcdea");
  }

  @Test
  public void reverseMovePositionForward() {
    assertThat(Problem21.parseStep("move position 1 to position 4").reverse("bdeac")).isEqualTo("bcdea");
  }

  @Test
  public void reverseMovePositionBackward() {
    assertThat(Problem21.parseStep("move position 3 to position 0").reverse("abdec")).isEqualTo("bdeac");
  }

  @Test
  public void reverseRotatePositionBeginningOfString() {
    assertThat(Problem21.parseStep("rotate based on position of letter a").reverse("habcdefg")).isEqualTo("abcdefgh");
  }

  @Test
  public void reverseRotatePositionEndOfString() {
    assertThat(Problem21.parseStep("rotate based on position of letter h").reverse("habcdefg")).isEqualTo("abcdefgh");
  }
}
