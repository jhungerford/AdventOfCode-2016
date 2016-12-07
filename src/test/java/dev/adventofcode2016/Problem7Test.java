package dev.adventofcode2016;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem7Test {

  @Test
  public void abbaOutsideBrackets() {
    assertThat(Problem7.IPv7.fromAddress("abba[mnop]qrst").supportsTLS()).isTrue();
  }

  @Test
  public void abbaInsideBrackets() {
    assertThat(Problem7.IPv7.fromAddress("abcd[bddb]xyyx").supportsTLS()).isFalse();
  }

  @Test
  public void abbaWithSameCharacters() {
    assertThat(Problem7.IPv7.fromAddress("aaaa[qwer]tyui").supportsTLS()).isFalse();
  }

  @Test
  public void longSegment() {
    assertThat(Problem7.IPv7.fromAddress("ioxxoj[asdfgh]zxcvbn").supportsTLS()).isTrue();
  }

  @Test
  public void multipleHypernetSequences() {
    assertThat(Problem7.IPv7.fromAddress("abcde[asdfgh]zxcvbn[abcde]abba").supportsTLS()).isTrue();
  }

  @Test
  public void longSegmentHasAbbaAtStart() {
    assertThat(Problem7.IPv7.hasAbba("ijabba")).isTrue();
  }

  @Test
  public void longSegmentHasAbbaAtEnd() {
    assertThat(Problem7.IPv7.hasAbba("abbaij")).isTrue();
  }

  @Test
  public void longSegmentHasAbbaAndNotAbba() {
    assertThat(Problem7.IPv7.hasAbba("xxxxabba")).isTrue();
  }
}
