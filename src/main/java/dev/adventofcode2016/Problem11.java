package dev.adventofcode2016;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import dev.adventofcode2016.algorithms.AStar;
import dev.adventofcode2016.util.ImmutableListCollector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.adventofcode2016.Problem11.MoveableType.GENERATOR;
import static dev.adventofcode2016.Problem11.MoveableType.MICROCHIP;

public class Problem11 implements AStar<Problem11.Building> {

  public static class Building {
    private final int elevatorFloor;
    private final Moveable[] moveables;

    public Building(int elevatorFloor, Moveable... moveables) {
      this.elevatorFloor = elevatorFloor;
      this.moveables = moveables;
    }

    /**
     * Returns whether this Building has a valid configuration.
     *
     * A microchip can't be on a floor with a generator of a different type
     * If a microchip is on a floor with a generator of the same type, it shields the radiation
     *
     * @return
     */
    public boolean isValid() {
      if (elevatorFloor > 4 || elevatorFloor < 1) {
        return false;
      }

      for (int i = 1; i <= 4; i ++) {
        int floor = i;

        // If the floor has a microchip with an unshielded generator, it's invalid.
        Set<Character> floorGenerators = Arrays.stream(moveables)
            .filter(moveable -> moveable.floor == floor && moveable.type == MoveableType.GENERATOR)
            .map(moveable -> moveable.symbol)
            .collect(Collectors.toSet());

        Set<Character> floorMicrochips = Arrays.stream(moveables)
            .filter(moveable -> moveable.floor == floor && moveable.type == MoveableType.MICROCHIP)
            .map(moveable -> moveable.symbol)
            .collect(Collectors.toSet());

        Set<Character> unshieldedMicrochips = Sets.difference(floorMicrochips, floorGenerators);

        if (!unshieldedMicrochips.isEmpty() && !floorGenerators.isEmpty()) {
          return false; // Floor contains a generator and an unshielded microchip.  This fries the microchip.
        }
      }

      return true;
    }

    /**
     * Returns a resulting building where the elevator moves the given materials the number of floors.
     *
     * @param floors    Number of floors to move.  The elevator can move 1 or -1 floors.
     * @param moveables Materials to move
     * @return New building with the elevator and materials moved.
     */
    public Building move(int floors, Collection<Moveable> moveables) {
      Moveable[] movedMaterials = Arrays.stream(this.moveables)
          .map(moveable -> moveables.contains(moveable)
              ? new Moveable(moveable.symbol, moveable.floor + floors, moveable.type)
              : moveable
          )
          .toArray(Moveable[]::new);

      return new Building(this.elevatorFloor + floors, movedMaterials);
    }

    public ImmutableList<String> render() {
      ImmutableList.Builder<String> list = ImmutableList.builder();

      for (int floor = 4; floor >= 1; floor --) {
        String floorString = "F" + floor + ' ';

        floorString += ((elevatorFloor == floor) ? 'E' : '.') + "  ";

        for (Moveable moveable : moveables) {
          floorString += ((moveable.floor == floor) ? moveable.toString() : ". ") + " ";
        }

        list.add(floorString);
      }

      return list.build();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Building building = (Building) o;

      if (elevatorFloor != building.elevatorFloor) {
        return false;
      }

      int[] generatorsPerFloor = new int[4];
      int[] microchipsPerFloor = new int[4];

      for (Moveable moveable : moveables) {
        if (moveable.type == MICROCHIP) {
          microchipsPerFloor[moveable.floor - 1]++;
        } else {
          generatorsPerFloor[moveable.floor - 1]++;
        }
      }

      int[] otherGeneratorsPerFloor = new int[4];
      int[] otherMicrochipsPerFloor = new int[4];

      for (Moveable moveable : building.moveables) {
        if (moveable.type == MICROCHIP) {
          otherMicrochipsPerFloor[moveable.floor - 1]++;
        } else {
          otherGeneratorsPerFloor[moveable.floor - 1]++;
        }
      }

      return Arrays.equals(microchipsPerFloor, otherMicrochipsPerFloor)
          && Arrays.equals(generatorsPerFloor, otherGeneratorsPerFloor);

    }

    @Override
    public int hashCode() {
      int[] generatorsPerFloor = new int[4];
      int[] microchipsPerFloor = new int[4];

      for (Moveable moveable : moveables) {
        if (moveable.type == MICROCHIP) {
          microchipsPerFloor[moveable.floor - 1]++;
        } else {
          generatorsPerFloor[moveable.floor - 1]++;
        }
      }

      return Objects.hash(elevatorFloor,
          Arrays.hashCode(generatorsPerFloor),
          Arrays.hashCode(microchipsPerFloor)
      );
    }

    @Override
    public String toString() {
      return "\n" + render().stream().collect(Collectors.joining("\n")) + "\n";
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
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Moveable moveable = (Moveable) o;
      return symbol == moveable.symbol &&
          floor == moveable.floor &&
          type == moveable.type;
    }

    @Override
    public int hashCode() {
      return Objects.hash(symbol, floor, type);
    }

    @Override
    public String toString() {
      return "" + symbol + type.symbol;
    }
  }

  @Override
  public int heuristicCost(Building from, Building to) {
    // Heuristic: how far the elevator and materials are from their final positions.

    int sum = Math.abs(from.elevatorFloor - to.elevatorFloor);

    for (int i = 0; i < from.moveables.length; i ++) {
      sum += Math.abs(from.moveables[i].floor - to.moveables[i].floor);
    }

    return sum;
  }

  @Override
  public ImmutableList<Building> neighbors(Building building) {
    // Result is the list of valid moves for the materials in the building.
    // The elevator can move one or two moveables (it must have at least one on it)

    // Get all materials that are on the elevator's floor.
    Set<Moveable> floorMoveables = Arrays.stream(building.moveables)
        .filter(moveable -> moveable.floor == building.elevatorFloor) // Materials on the elevator's floor
        .collect(Collectors.toSet());

    // Get all permutations of one or two moveables, and prune invalid Buildings
    return Sets.powerSet(floorMoveables).stream()
        .filter(permutation -> permutation.size() == 1 || permutation.size() == 2)
        .flatMap(elevatorContents -> Stream.of(
            building.move(1, elevatorContents),
            building.move(-1, elevatorContents)
        ))
        .filter(Building::isValid)
        .collect(new ImmutableListCollector<>());
  }

  public static void main(String[] args) {
    // P: promethium
    // B: cobolt
    // C: curium
    // R: ruthenium
    // L: plutonium

    Building part1 = new Building(
        1,

        // The first floor contains a promethium generator and a promethium-compatible microchip.
        new Moveable('P', 1, GENERATOR),
        new Moveable('P', 1, MICROCHIP),

        // The second floor contains a cobalt generator, a curium generator, a ruthenium generator,
        // and a plutonium generator.
        new Moveable('B', 2, GENERATOR),
        new Moveable('C', 2, GENERATOR),
        new Moveable('R', 2, GENERATOR),
        new Moveable('L', 2, GENERATOR),

        // The third floor contains a cobalt-compatible microchip, a curium-compatible microchip,
        // a ruthenium-compatible microchip, and a plutonium-compatible microchip.
        new Moveable('B', 3, MICROCHIP),
        new Moveable('C', 3, MICROCHIP),
        new Moveable('R', 3, MICROCHIP),
        new Moveable('L', 3, MICROCHIP)

        // The fourth floor contains nothing relevant.
    );

    Building end = new Building(
        4,
        new Moveable('P', 4, GENERATOR),
        new Moveable('P', 4, MICROCHIP),
        new Moveable('B', 4, GENERATOR),
        new Moveable('C', 4, GENERATOR),
        new Moveable('R', 4, GENERATOR),
        new Moveable('L', 4, GENERATOR),
        new Moveable('B', 4, MICROCHIP),
        new Moveable('C', 4, MICROCHIP),
        new Moveable('R', 4, MICROCHIP),
        new Moveable('L', 4, MICROCHIP)

        // The fourth floor contains nothing relevant.
    );

    int numSteps = new Problem11().shortestPath(part1, end).size();

    System.out.println("Part 1: " + numSteps + " steps");
  }
}
