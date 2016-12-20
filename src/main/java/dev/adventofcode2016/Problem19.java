package dev.adventofcode2016;

public class Problem19 {

  /**
   * Returns the elf that ends up with presents after a game of elf white elephant with
   * num elves.
   *
   * Each Elf brings a present. They all sit in a circle, numbered starting with position 1.
   * Then, starting with the first Elf, they take turns stealing all the presents from the
   * Elf to their left. An Elf with no presents is removed from the circle and does not take turns.
   *
   * @param num Number of elves participating
   * @return Number of the elf that ends up with presents
   */
  public static int elfWithPresents(int num, StealFromElf stealFromElf) {
    int numLeft = num;

    boolean[] playing = new boolean[num];
    for (int i = 0; i < num; i ++) {
      playing[i] = true;
    }

    int turn = 0;
    while (numLeft > 1) {
      int from = stealFromElf.from(playing, turn, numLeft);

      playing[from] = false;
      numLeft --;

      turn = findRightElf(playing, turn, numLeft);
    }

    for (int i = 0; i < playing.length; i ++) {
      if (playing[i]) {
        return i + 1; // Elves start at 1.
      }
    }

    throw new IllegalStateException("No elf won.");
  }

  /**
   * Searches through the list of elves who are playing and finds the elf
   * to the right of the given elf.
   *
   * @param playing List of booleans indicating elves who are still in the game
   * @param elf Elf who is taking a turn
   * @return Playing elf to the right of the given elf.
   */
  public static int findRightElf(boolean[] playing, int elf, int numLeft) {
    if (numLeft == 1) {
      return elf;
    }

    for (int i = 1; i < playing.length; i ++) {
      int index = (elf + i) % playing.length;
      if (playing[index]) {
        return index;
      }
    }

    return elf;
  }

  /**
   * Picks the elf that is directly across the circle from the given elf.
   *
   * @param playing Elves who are playing
   * @param elf Elf who is stealing presents
   * @return Elf who presents will be stolen from, or the given elf if only one elf is playing
   */
  public static int acrossCircleElf(boolean[] playing, int elf, int numLeft) {
    if (numLeft == 1) {
      return elf;
    }

    int numToSkip = numLeft / 2; // Integer math rounds fractions down.
    int numFound = 0;

    for (int i = 1; i < playing.length; i ++) {
      int index = (elf + i) % playing.length;

      if (playing[index]) {
        numFound ++;
      }

      if (numFound == numToSkip) {
        return index;
      }
    }

    return elf;
  }

  /**
   * FunctionalInterface that determines which elf should be stolen from.
   */
  @FunctionalInterface
  public interface StealFromElf {
    /**
     * Returns the elf that presents should be stolen from.
     *
     * @param playing Booleans indicating which elves are left in the game
     * @param elf     Index of the elf that is stealing presents
     * @param numLeft Number of elves left in the game
     * @return Elf who presents will be stolen from, or the given elf if only one elf remains
     */
    int from(boolean[] playing, int elf, int numLeft);
  }

  public static int stealAcrossRemaining(int num) {
    // Elf equation (determined by trials)
    // Sequence resets to 1 after 3^n.  Sequence increases monotonically up to 3^n-1, then by twos up to 3^n.

    // F(n) = n - 3^a              if n-3^a <= 3^a
    //        3^a + 2*(n - 2*3^a)  otherwise

    // Find the biggest power of three that's <= num.  (3^a <= num, but 3^(a+1) > num)
    int a = 0;
    while (Math.pow(3, a+1) < num) {
      a++;
    }

    int threeA = (int) Math.pow(3, a); // 3^a

    if (num - threeA <= threeA) {
      return num - threeA; // n - 3^a
    }

    return threeA + 2 * (num - 2 * threeA); // 3^a + 2*(n - 2*3^a)
  }

  public static void main(String[] args) {
    int numElves = 3014603;

    int part1 = Problem19.elfWithPresents(numElves, Problem19::findRightElf);
    System.out.println("Part 1: Elf " + part1 + " ends up with the presents");

    int part2 = Problem19.stealAcrossRemaining(numElves);
    System.out.println("Part 2: Elf " + part2 + " ends up with the presents");
  }
}
