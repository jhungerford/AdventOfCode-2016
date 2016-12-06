package dev.adventofcode2016;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem6Test {

  public static final List<String> INPUT = ImmutableList.of(
      "eedadn",
      "drvtee",
      "eandsr",
      "raavrd",
      "atevrs",
      "tsrnev",
      "sdttsa",
      "rasrtv",
      "nssdts",
      "ntnada",
      "svetve",
      "tesnvt",
      "vntsnd",
      "vrdear",
      "dvrsen",
      "enarar"
  );

  @Test
  public void mostCommonLetterExample() {
    String errorCorrected = Problem6.errorCorrected(INPUT, Problem6.MOST_COMMON_LETTER);
    assertThat(errorCorrected).isEqualTo("easter");
  }

  @Test
  public void leastCommonLetterExample() {
    String errorCorrected = Problem6.errorCorrected(INPUT, Problem6.LEAST_COMMON_LETTER);
    assertThat(errorCorrected).isEqualTo("advent");
  }
}
