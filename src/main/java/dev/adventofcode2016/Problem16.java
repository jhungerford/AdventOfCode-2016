package dev.adventofcode2016;

public class Problem16 {

  /**
   * Generates random data using a dragon curve.  The resulting string is the input followed by 0, followed by
   * the input reversed and with 0's swapped with 1's (and vise versa)
   *
   * @param input Input to generate random data from
   * @return Random data
   */
  public static String step(String input) {
    return input + '0' + new StringBuilder(input)
        .reverse().chars()
        .mapToObj(c -> c == '0' ? '1' : '0')
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);
  }

  /**
   * Calculates a checksum for the given data.  The checksum for some given data is created by
   * considering each non-overlapping pair of characters in the input data. If the two characters
   * match (00 or 11), the next checksum character is a 1. If the characters do not match (01 or 10),
   * the next checksum character is a 0. This should produce a new string which is exactly half as long
   * as the original. If the length of the checksum is even, repeat the process until you end up with
   * a checksum with an odd length.
   *
   * @param data Data to generate a checksum from
   * @param length Length of the random data
   * @return Checksum for the data
   */
  public static String checksum(String data, int length) {
    String checksum = data.substring(0, length);

    while (checksum.length() % 2 == 0) {
      StringBuilder bldr = new StringBuilder();

      for (int i = 0; i < checksum.length() - 1; i += 2) {
        bldr.append(checksum.charAt(i) == checksum.charAt(i+1) ? '1' : '0');
      }

      checksum = bldr.toString();
    }

    return checksum;
  }

  public static void main(String[] args) {
    String initialRandomData = "10001001100000001";

    int part1DiskLength = 272;
    String part1RandomData = initialRandomData;

    while (part1RandomData.length() < part1DiskLength) {
      part1RandomData = step(part1RandomData);
    }

    System.out.println("Part 1: checksum is '" + checksum(part1RandomData, part1DiskLength) + "'");

    int part2DiskLength = 35651584;
    String part2RandomData = initialRandomData;

    while (part2RandomData.length() < part2DiskLength) {
      part2RandomData = step(part2RandomData);
    }

    System.out.println("Part 2: checksum is '" + checksum(part2RandomData, part2DiskLength) + "'");

  }
}
