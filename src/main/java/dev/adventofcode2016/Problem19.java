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
  public static int elfWithPresents(int num) {
    int numLeft = num;

    boolean[] playing = new boolean[num];
    for (int i = 0; i < num; i ++) {
      playing[i] = true;
    }

    int turn = 0;
    while (numLeft > 1) {
      int from = findRightElf(playing, turn);

      playing[from] = false;
      numLeft --;

      turn = findRightElf(playing, turn);
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
  public static int findRightElf(boolean[] playing, int elf) {
    for (int i = 1; i < playing.length; i ++) {
      int index = (elf + i) % playing.length;
      if (playing[index]) {
        return index;
      }
    }

    return elf;
  }

  public static void main(String[] args) {
    System.out.println("Part 1: Elf " + Problem19.elfWithPresents(3014603) + " ends up with the presents");
  }
}
