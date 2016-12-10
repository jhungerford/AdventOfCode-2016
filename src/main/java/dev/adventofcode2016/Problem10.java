package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import dev.adventofcode2016.util.ImmutableListCollector;

import java.io.IOException;
import java.util.*;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Problem10 {

  private static final Pattern VALUE_PATTERN = Pattern.compile("value (\\d+) goes to bot (\\d+)");
  private static final Pattern COMPARE_PATTERN = Pattern.compile("bot (\\d+) " +
      "gives low to ((?:bot)|(?:output)) (\\d+) and high to ((?:bot)|(?:output)) (\\d+)");

  public static class Factory {

    public final ImmutableList<Bot> bots;
    public final ImmutableList<Input> inputs;
    public final ImmutableList<Output> outputs;

    public Factory(ImmutableList<Bot> bots, ImmutableList<Input> inputs, ImmutableList<Output> outputs) {
      this.bots = bots;
      this.inputs = inputs;
      this.outputs = outputs;
    }

    public void registerComparisonTrace(LowHighTrace trace) {
      bots.forEach(bot -> bot.registerTrace(trace));
    }

    /**
     * Runs the instructions in the inputs until the bots in the factory stop moving.
     */
    public void run() {
      for (Input input : inputs) {
        bots.get(input.bot).take(input.value, this);
      }
    }
  }

  /** Comparison trace predicate that says whether the combination of low and high values are interesting. */
  @FunctionalInterface
  public interface LowHighPredicate {
    boolean test(int low, int high);
  }

  public static class LowHighTrace {
    public final LowHighPredicate predicate;
    public final IntConsumer callback;

    public LowHighTrace(LowHighPredicate predicate, IntConsumer callback) {
      this.predicate = predicate;
      this.callback = callback;
    }
  }

  /** Interface for classes that move chips */
  public interface ChipConsumer {
    void take(int value, Factory factory);
  }

  /** Output type - either a dedicated output bin or a bot. */
  public enum OutputType { OUTPUT, BOT }

  /** Output task that bots use to determine where to place chips after comparing them. */
  public static class OutputTask implements ChipConsumer {
    public final OutputType outputType;
    public final int id;

    public OutputTask(OutputType outputType, int id) {
      this.outputType = outputType;
      this.id = id;
    }

    /**
     * Executes this OutputTask, passing the chip with the given value to the appropriate
     * place in the factory.
     *
     * @param value   Chip value
     * @param factory Factory containing bots and outputs
     */
    public void take(int value, Factory factory) {
      ImmutableList<? extends ChipConsumer> consumers = outputType == OutputType.BOT ? factory.bots : factory.outputs;

      consumers.get(id).take(value, factory);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      OutputTask that = (OutputTask) o;
      return id == that.id &&
          outputType == that.outputType;
    }

    @Override
    public int hashCode() {
      return Objects.hash(outputType, id);
    }

    @Override
    public String toString() {
      return "OutputTask{" +
          "outputType=" + outputType +
          ", id=" + id +
          '}';
    }
  }

  /**
   * Bot.  Collects two chips in the mutable chips list, compares them,
   * and places them in the low and high output locations.
   */
  public static class Bot implements ChipConsumer {
    public final int id;
    public final OutputTask lowOutputTask;
    public final OutputTask highOutputTask;

    public final List<LowHighTrace> traces;
    public Optional<Integer> chipBuffer;

    public Bot(int id, OutputTask lowOutputTask, OutputTask highOutputTask) {
      this.id = id;
      this.lowOutputTask = lowOutputTask;
      this.highOutputTask = highOutputTask;
      this.traces = new ArrayList<>();
      this.chipBuffer = Optional.empty();
    }

    public void registerTrace(LowHighTrace trace) {
      this.traces.add(trace);
    }

    /**
     * Accepts the given chip value, moving chips along the factory if this Bot now has two chips.
     *
     * @param value   Chip value
     * @param factory Factory to pass chips to
     */
    public void take(int value, Factory factory) {
      if (chipBuffer.isPresent()) {
        // This bot already has a chip - compare the new one and the one in the buffer and execute the output tasks.
        int low = chipBuffer.get() < value ? chipBuffer.get() : value;
        int high = chipBuffer.get() < value ? value : chipBuffer.get();

        // Tell any interested traces that we compared the chips.
        traces.stream()
            .filter(trace -> trace.predicate.test(low, high))
            .forEach(trace -> trace.callback.accept(this.id));

        // Move the chips
        chipBuffer = Optional.empty();
        lowOutputTask.take(low, factory);
        highOutputTask.take(high, factory);

      } else {
        // This bot doesn't have a chip yet - store it in the buffer.
        chipBuffer = Optional.of(value);
      }
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Bot bot = (Bot) o;
      return id == bot.id &&
          Objects.equals(lowOutputTask, bot.lowOutputTask) &&
          Objects.equals(highOutputTask, bot.highOutputTask) &&
          Objects.equals(chipBuffer, bot.chipBuffer);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, lowOutputTask, highOutputTask, chipBuffer);
    }

    @Override
    public String toString() {
      return "Bot{" +
          "id=" + id +
          ", lowOutputTask=" + lowOutputTask +
          ", highOutputTask=" + highOutputTask +
          ", chipBuffer=" + chipBuffer +
          '}';
    }

    private static Bot fromMatcher(Matcher matcher) {
      return new Bot(
          Integer.parseInt(matcher.group(1)),
          new OutputTask(
              OutputType.valueOf(matcher.group(2).toUpperCase()),
              Integer.parseInt(matcher.group(3))
          ),
          new OutputTask(
              OutputType.valueOf(matcher.group(4).toUpperCase()),
              Integer.parseInt(matcher.group(5))
          )
      );
    }
  }

  /** Input instruction - instructs a bot to pick up a chip. */
  public static class Input {
    public final int value;
    public final int bot;

    public Input(int value, int bot) {
      this.value = value;
      this.bot = bot;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Input input = (Input) o;
      return value == input.value &&
          bot == input.bot;
    }

    @Override
    public int hashCode() {
      return Objects.hash(value, bot);
    }

    @Override
    public String toString() {
      return "Input{" +
          "value=" + value +
          ", bot=" + bot +
          '}';
    }

    public static Input fromMatcher(Matcher matcher) {
      return new Input(
          Integer.parseInt(matcher.group(1)),
          Integer.parseInt(matcher.group(2))
      );
    }
  }

  /** Output bin.  Contains a mutable value that a bot can place a chip into.  */
  public static class Output implements ChipConsumer {
    public final int id;
    public Optional<Integer> value;

    public Output(int id) {
      this.id = id;
      this.value = Optional.empty();
    }

    public Output(int id, int value) {
      this.id = id;
      this.value = Optional.of(value);
    }

    public void take(int value, Factory factory) {
      this.value = Optional.of(value);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Output output = (Output) o;
      return id == output.id &&
          Objects.equals(value, output.value);
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, value);
    }

    @Override
    public String toString() {
      return "Output{" +
          "id=" + id +
          ", value=" + value +
          '}';
    }
  }

  public static Factory factoryFromInstructions(List<String> instructions) {
    ImmutableList<Bot> bots = instructions.stream()
        .map(COMPARE_PATTERN::matcher)
        .filter(Matcher::matches)
        .map(Bot::fromMatcher)
        .sorted(Comparator.comparingInt(bot -> bot.id))
        .collect(new ImmutableListCollector<>());

    ImmutableList<Input> inputs = instructions.stream()
        .map(VALUE_PATTERN::matcher)
        .filter(Matcher::matches)
        .map(Input::fromMatcher)
        .collect(new ImmutableListCollector<>());


    // Outputs are numbered sequentially - look at the highest numbered bot output to determine how many there are.
    int numOutputs = bots.stream()
        .flatMapToInt(bot -> Stream.of(bot.lowOutputTask, bot.highOutputTask)
            .filter(outputTask -> outputTask.outputType == OutputType.OUTPUT)
            .mapToInt(outputTask -> outputTask.id)
        )
        .max()
        .orElseThrow(() -> new IllegalStateException("Bots contain no outputs"));

    ImmutableList<Output> outputs = IntStream.rangeClosed(0, numOutputs)
        .mapToObj(Output::new)
        .collect(new ImmutableListCollector<>());

    return new Factory(bots, inputs, outputs);
  }

  public static void main(String[] args) throws IOException {
    List<String> lines = Resources.readLines(Resources.getResource("problem10.txt"), Charsets.UTF_8);

    System.out.print("Part 1: chip that compares 17 and 61: ");
    Factory factory = factoryFromInstructions(lines);
    factory.registerComparisonTrace(new LowHighTrace(
        (low, high) -> low == 17 && high == 61,
        System.out::println
    ));

    factory.run();

    int product = factory.outputs.get(0).value.orElse(0)
        * factory.outputs.get(1).value.orElse(0)
        * factory.outputs.get(2).value.orElse(0);

    System.out.println("Part 2 - product of output 0, 1, and 2: " + product);
  }
}
