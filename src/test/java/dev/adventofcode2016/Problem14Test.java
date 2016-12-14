package dev.adventofcode2016;

import org.junit.Test;

import java.util.OptionalInt;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem14Test {

  private Problem14.KeyGenerator keyGenerator = new Problem14.KeyGenerator("abc");

  @Test
  public void key18() {
    assertThat(keyGenerator.generateKey(18)).contains("cc38887a5");
  }

  @Test
  public void containsNoTriples() {
    assertThat(keyGenerator.tripleCharacter(keyGenerator.generateKey(10))).isEmpty();
  }

  @Test
  public void containsTriple() {
    assertThat(keyGenerator.tripleCharacter(keyGenerator.generateKey(18))).hasValue('8');
  }

  @Test
  public void containsTripleRepeatedFiveTimes() {
    assertThat(keyGenerator.tripleCharacter(keyGenerator.generateKey(816))).hasValue('e');
  }

  @Test
  public void repeatedFiveTimes() {
    assertThat(keyGenerator.isRepeatedFiveTimes('e', keyGenerator.generateKey(816))).isTrue();
  }

  @Test
  public void notRepeatedFiveTimes() {
    assertThat(keyGenerator.isRepeatedFiveTimes('e', keyGenerator.generateKey(815))).isFalse();
  }

  @Test
  public void isKeyIndex() {
    assertThat(keyGenerator.isKeyIndex(92)).isTrue();
  }

  @Test
  public void example64thIndex() {
    OptionalInt key64Index = IntStream.iterate(1, i -> i + 1)
        .filter(keyGenerator::isKeyIndex)
        .limit(64)
        .max();

    assertThat(key64Index).hasValue(22728);
  }

}
