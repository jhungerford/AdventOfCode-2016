package dev.adventofcode2016;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

import java.util.stream.Stream;

import dev.adventofcode2016.util.ImmutableListCollector;

public class Problem12Test {

  @Test
  public void part1Example() {
    ImmutableList<Problem12.Instruction> instructions = Stream.of(
        "cpy 41 a",
        "inc a",
        "inc a",
        "dec a",
        "jnz a 2",
        "dec a")
        .map(Problem12::parseInstruction)
        .collect(new ImmutableListCollector<>());

    Problem12.Computer computer = new Problem12.Computer(instructions);

    computer.run();

    assertThat(computer.a).isEqualTo(42);
  }
}
