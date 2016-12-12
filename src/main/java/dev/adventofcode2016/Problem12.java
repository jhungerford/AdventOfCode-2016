package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.adventofcode2016.util.ImmutableListCollector;

public class Problem12 {
  /**
   * Computer that contains four registers (a, b, c, and d) that start at 0
   * and can hold any integer.
   *
   * Supports the following instructions:
   *   cpy x y  copies x (either an integer or the value of a register) into register y.
   *   inc x    increases the value of register x by one.
   *   dec x    decreases the value of register x by one.
   *   jnz x y  jumps to an instruction y away (positive means forward; negative means backward),
   *            but only if x is not zero.
   *
   * The jnz instruction moves relative to itself: an offset of -1 would continue at the previous
   * instruction, while an offset of 2 would skip over the next instruction.
   */
  public static class Computer {
    public int a;
    public int b;
    public int c;
    public int d;

    public int programCounter;
    public final ImmutableList<Instruction> instructions;

    public Computer(ImmutableList<Instruction> instructions) {
      this.a = 0;
      this.b = 0;
      this.c = 0;
      this.d = 0;
      this.programCounter = 0;
      this.instructions = instructions;
    }

    /**
     * Runs the instructions in this Computer until the program counter advances past the last instruction.
     */
    public void run() {
      while (programCounter < instructions.size()) {
        instructions.get(programCounter).execute(this);
      }
    }

    /**
     * Sets the value of the given register to the given value.
     *
     * @param register a, b, c, or d
     * @param value Value to set in the register
     * @return New computer with the given value
     */
    public void set(char register, int value) {
      switch (register) {
        case 'a':
          this.a = value;
          break;
        case 'b':
          this.b = value;
          break;
        case 'c':
          this.c = value;
          break;
        case 'd':
          this.d = value;
          break;
        default:
          throw new IllegalArgumentException(register + " is not a valid register");
      }
    }

    /**
     * Returns the value of the given register.
     *
     * @param register a, b, c, or d
     * @return Value of the given register
     */
    public int get(char register) {
      switch (register) {
        case 'a':
          return a;
        case 'b':
          return b;
        case 'c':
          return c;
        case 'd':
          return d;
        default:
          throw new IllegalArgumentException(register + " is not a valid register");
      }
    }

    /**
     * Increments the program counter by 1.
     *
     * @return New computer with the incremented program counter
     */
    public void incrementProgramCounter() {
      this.programCounter++;
    }

    /**
     * Moves the program counter relative to it's current position (e.g. -1 means the previous instruction,
     * 1 means the next instruction).
     *
     * @param amount Positive or negative amount to move the program counter.
     * @return New computer with the moved program counter
     */
    public void moveProgramCounter(int amount) {
      this.programCounter = programCounter + amount;
    }
  }

  public interface Instruction {
    void execute(Computer computer);
  }

  /**
   * cpy x y - copies x (either an integer or a value of a register) into register y.
   */
  public static class CopyInstruction implements Instruction {
    private final ToIntFunction<Computer> value;
    private final char register;

    public CopyInstruction(ToIntFunction<Computer> value, char register) {
      this.value = value;
      this.register = register;
    }

    @Override
    public void execute(Computer computer) {
      computer.set(register, value.applyAsInt(computer));
      computer.incrementProgramCounter();
    }

    public static CopyInstruction fromMatcher(Matcher matcher) {
      return new CopyInstruction(
          parseRegisterOrValue(matcher.group(1)),
          matcher.group(2).charAt(0)
      );
    }
  }

  /**
   * inc x - increments the value of register x by one.
   */
  public static class IncrementInstruction implements Instruction {
    private final char register;

    public IncrementInstruction(char register) {
      this.register = register;
    }

    @Override
    public void execute(Computer computer) {
      computer.set(register, computer.get(register) + 1);
      computer.incrementProgramCounter();
    }

    public static IncrementInstruction fromMatcher(Matcher matcher) {
      return new IncrementInstruction(
          matcher.group(1).charAt(0)
      );
    }
  }

  /**
   * dec x - decrements the value of register x by one.
   */
  public static class DecrementInstruction implements Instruction {
    private final char register;

    public DecrementInstruction(char register) {
      this.register = register;
    }

    @Override
    public void execute(Computer computer) {
      computer.set(register, computer.get(register) - 1);
      computer.incrementProgramCounter();
    }

    public static DecrementInstruction fromMatcher(Matcher matcher) {
      return new DecrementInstruction(
          matcher.group(1).charAt(0)
      );
    }
  }

  /**
   * jnz x y - jumps to an instruction y away if x is not zero
   */
  public static class JumpIfNotZeroInstruction implements Instruction {
    private final ToIntFunction<Computer> value;
    private final int amount;

    public JumpIfNotZeroInstruction(ToIntFunction<Computer> value, int amount) {
      this.value = value;
      this.amount = amount;
    }

    @Override
    public void execute(Computer computer) {
      if (value.applyAsInt(computer) == 0) {
        computer.incrementProgramCounter();
      } else {
        computer.moveProgramCounter(amount);
      }
    }

    public static JumpIfNotZeroInstruction fromMatcher(Matcher matcher) {
      return new JumpIfNotZeroInstruction(
          parseRegisterOrValue(matcher.group(1)),
          Integer.parseInt(matcher.group(2))
      );
    }
  }

  private static ToIntFunction<Computer> parseRegisterOrValue(String str) {
    try {
      int value = Integer.parseInt(str);
      return (computer) -> value;
    } catch (NumberFormatException ex) {
      return (computer) -> computer.get(str.charAt(0));
    }
  }


  private static final ImmutableMap<Pattern, Function<Matcher, ? extends Instruction>> INSTRUCTION_MAP =
      ImmutableMap.<Pattern, Function<Matcher, ? extends Instruction>>builder()
          .put(Pattern.compile("cpy ([0-9a-d]+) ([a-d])"), CopyInstruction::fromMatcher)
          .put(Pattern.compile("inc ([a-d])"), IncrementInstruction::fromMatcher)
          .put(Pattern.compile("dec ([a-d])"), DecrementInstruction::fromMatcher)
          .put(Pattern.compile("jnz ([0-9a-d]+) (-?[0-9]+)"), JumpIfNotZeroInstruction::fromMatcher)
          .build();

  /**
   * Parses the given line into an instruction
   *
   * @param line Line to parse
   * @return Instruction
   */
  public static Instruction parseInstruction(String line) {
    for (Map.Entry<Pattern, Function<Matcher, ? extends Instruction>> entry : INSTRUCTION_MAP.entrySet()) {
      Matcher matcher = entry.getKey().matcher(line);
      if (matcher.matches()) {
        return entry.getValue().apply(matcher);
      }
    }

    throw new IllegalArgumentException("'" + line + "' is not a valid instruction");
  }

  public static void main(String[] args) throws IOException {
    List<String> lines = Resources.readLines(Resources.getResource("problem12.txt"), Charsets.UTF_8);

    ImmutableList<Instruction> instructions = lines.stream()
        .map(Problem12::parseInstruction)
        .collect(new ImmutableListCollector<>());

    Computer computer = new Computer(instructions);
    computer.run();

    System.out.println("Part 1: value of register a is " + computer.a);
  }
}
