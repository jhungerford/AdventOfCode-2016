package dev.adventofcode2016;

import com.google.common.collect.ImmutableList;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Problem15 {

  public static class Machine {
    public final ImmutableList<Disk> disks;

    public Machine(Disk... disks) {
      this.disks = ImmutableList.copyOf(disks);
    }

    /**
     * Returns the earliest time that the capsule will fall through all of the disks.
     * It takes one second for the capsule to travel from one disk to the next, and
     * the disks rotate at a rate of one position per second.
     *
     * @return Earliest time the capsule will fall through the disks.
     */
    public int earliestTime() {
      return IntStream.iterate(0, (time) -> time + 1)
          .filter(time -> {
                Set<Integer> positions = disks.stream()
                    .map(disk -> disk.position(time))
                    .collect(Collectors.groupingBy(Function.identity()))
                    .keySet();
                return positions.size() == 1 && positions.iterator().next() == 0; // All slots align at position 0.
              }
          )
          .findFirst()
          .getAsInt(); // Infinite stream - won't be present if it's missing.
    }
  }

  public static class Disk {
    public final int num;
    public final int positions;
    public final int start;

    public Disk(int num, int positions, int start) {
      this.num = num;
      this.positions = positions;
      this.start = start;
    }

    public int position(int time) {
      return (num + start + time) % positions;
    }
  }

  public static void main(String[] args) {
    Machine part1Machine = new Machine(
        new Disk(1, 7, 0),
        new Disk(2, 13, 0),
        new Disk(3, 3, 2),
        new Disk(4, 5, 2),
        new Disk(5, 17, 0),
        new Disk(6, 19, 7)
    );

    System.out.println("Part 1: push the button at time " + part1Machine.earliestTime());
  }
}
