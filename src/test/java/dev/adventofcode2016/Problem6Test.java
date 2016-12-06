package dev.adventofcode2016;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem6Test {

  @Test
  public void part1Example() {
    List<String> lines = ImmutableList.of(
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

    String errorCorrected = Problem6.errorCorrected(lines);
    assertThat(errorCorrected).isEqualTo("easter");
  }
}
