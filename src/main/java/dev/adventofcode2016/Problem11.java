package dev.adventofcode2016;

import com.google.common.collect.ImmutableList;

public class Problem11 {

  public static class Building {
    private final Elevator elevator;
    private final Moveable[] moveables;

    public Building(
        Elevator elevator,
        Moveable... moveables) {
      this.elevator = elevator;
      this.moveables = moveables;
    }

    public ImmutableList<String> render() {
      ImmutableList.Builder<String> list = ImmutableList.builder();

      for (int floor = 4; floor >= 1; floor --) {
        String floorString = "F" + floor + ' ';

        floorString += ((elevator.floor == floor) ? 'E' : '.') + "  ";

        for (Moveable moveable : moveables) {
          floorString += ((moveable.floor == floor) ? moveable.toString() : ". ") + " ";
        }

        list.add(floorString);
      }

      return list.build();
    }
  }

  public static class Elevator {
    private final int floor;

    public Elevator(int floor) {
      this.floor = floor;
    }
  }

  public enum MoveableType {
    GENERATOR, MICROCHIP;

    public final char symbol;

    MoveableType() {
      this.symbol = name().charAt(0);
    }
  }

  public static class Moveable {

    private final char symbol;
    private final int floor;
    private final MoveableType type;

    public Moveable(char symbol, int floor, MoveableType type) {
      this.symbol = symbol;
      this.floor = floor;
      this.type = type;
    }

    @Override
    public String toString() {
      return "" + symbol + type.symbol;
    }
  }
}
