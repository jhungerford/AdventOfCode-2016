package dev.adventofcode2016;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.adventofcode2016.Problem10.OutputType.BOT;
import static dev.adventofcode2016.Problem10.OutputType.OUTPUT;
import static org.assertj.core.api.Assertions.assertThat;

public class Problem10Test {

  public static final List<String> PART1_INSTRUCTIONS = ImmutableList.of(
      "value 5 goes to bot 2",
      "bot 2 gives low to bot 1 and high to bot 0",
      "value 3 goes to bot 1",
      "bot 1 gives low to output 1 and high to bot 0",
      "bot 0 gives low to output 2 and high to output 0",
      "value 2 goes to bot 2"
  );

  @Test
  public void part1Parse() {
    Problem10.Factory factory = Problem10.factoryFromInstructions(PART1_INSTRUCTIONS);

    assertThat(factory.bots).containsExactly(
        new Problem10.Bot(0, new Problem10.OutputTask(OUTPUT, 2), new Problem10.OutputTask(OUTPUT, 0)),
        new Problem10.Bot(1, new Problem10.OutputTask(OUTPUT, 1), new Problem10.OutputTask(BOT, 0)),
        new Problem10.Bot(2, new Problem10.OutputTask(BOT, 1), new Problem10.OutputTask(BOT, 0))
    );

    assertThat(factory.inputs).containsExactly(
        new Problem10.Input(5, 2),
        new Problem10.Input(3, 1),
        new Problem10.Input(2, 2)
    );

    assertThat(factory.outputs).containsExactly(
        new Problem10.Output(0),
        new Problem10.Output(1),
        new Problem10.Output(2)
    );
  }

  @Test
  public void part1Output() {
    Problem10.Factory factory = Problem10.factoryFromInstructions(PART1_INSTRUCTIONS);

    AtomicInteger compareBotId = new AtomicInteger(-1);
    factory.registerComparisonTrace(new Problem10.LowHighTrace(
        (low, high) -> low == 2 && high == 5,
        compareBotId::set
    ));
    factory.run();

    assertThat(factory.outputs).containsExactly(
        new Problem10.Output(0, 5),
        new Problem10.Output(1, 2),
        new Problem10.Output(2, 3)
    );

    assertThat(compareBotId.get()).isEqualTo(2);
  }
}
