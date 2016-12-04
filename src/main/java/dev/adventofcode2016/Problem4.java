package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Problem4 {

  // A room consists of an encrypted name (lowercase letters separated by dashes
  // followed by a dash, a sector id, and a checksum in square brackets
  // e.g. 'aaaaa-bbb-z-y-x-123[abxyz]'
  private static final Pattern ROOM_PATTERN = Pattern.compile("([a-z-]+)-([0-9]+)\\[([a-z]+)]");

  public static class Room {
    public final String encryptedName;
    public final int sector;
    public final String checksum;

    public Room(String encryptedName, int sector, String checksum) {
      this.encryptedName = encryptedName;
      this.sector = sector;
      this.checksum = checksum;
    }

    /**
     * Constructs a room from a line consisting of an encrypted name (lowercase letters separated by dashes)
     * followed by a dash, a sector ID, and a checksum in square brackets.
     *
     * @param line Line to convert to a room.
     * @return Room from the given line.
     * @throws IllegalArgumentException if the line is improperly formatted
     */
    public static Room fromLine(String line) {
      Matcher matcher = ROOM_PATTERN.matcher(line);
      if (! matcher.matches()) {
        throw new IllegalArgumentException("'" + line + "' does not match");
      }

      return new Room(
          matcher.group(1),
          Integer.parseInt(matcher.group(2)),
          matcher.group(3)
      );
    }

    /**
     * Returns whether the checksum for this room matches the checksum on the line.  A room's
     * checksum consists of the five most common letters in the encrypted name, in order,
     * with ties broken by alphabetization.
     *
     * @return Whether this room's checksum is valid.
     */
    public boolean isValid() {
      String expectedChecksum = encryptedName.chars()

          // Encrypted name includes dashes, but the checksum doesn't count them.
          .filter(c -> c != '-')

          // Convert the name to a 'letter' -> 'count' map
          .mapToObj(c -> (char) c)
          .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))

          // Sort the entries by 'count' descending, then 'letter' ascending
          .entrySet().stream()
          .sorted(
              Map.Entry.<Character, Long>comparingByValue().reversed() // Count descending
              .thenComparing(Map.Entry.comparingByKey()) // Letter ascending
          )

          // Take the top 5 letters
          .map(Map.Entry::getKey)
          .limit(5)

          // Convert the top 5 letters to a String
          .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
          .toString();

      return checksum.equals(expectedChecksum);
    }

    /**
     * Decrypts this room's name.  Dashes become spaces, and each letter is rotated forward through
     * the alphabet for this room's sector id.
     *
     * @return Decrypted room name
     */
    public String decryptedName() {
      return encryptedName.chars()
          .map(this::shift)
          .mapToObj(c -> (char) c)
          .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
          .toString();
    }

    /**
     * Shifts the given character forward through the alphabet for a number of times equal
     * to this room's sector id.  Converts dashes to spaces.
     *
     * @param c Character to shift
     * @return Shifted character
     */
    private int shift(int c) {
      if (c == '-') {
        return ' ';
      }

      return (c - 'a' + sector) % 26 + 'a';
    }

    @Override
    public String toString() {
      return "Room{" +
          "encryptedName='" + encryptedName + '\'' +
          ", sector=" + sector +
          ", checksum='" + checksum + '\'' +
          '}';
    }
  }

  public static int sumValidRoomSectors(Collection<String> lines) {
    return lines.stream()
        .map(Room::fromLine)
        .filter(Room::isValid)
        .mapToInt(room -> room.sector)
        .sum();
  }

  public static void main(String[] args) throws IOException {
    List<String> lines = Resources.readLines(Resources.getResource("problem4.txt"), Charsets.UTF_8);

    int part1 = sumValidRoomSectors(lines);
    System.out.println("Part 1: " + part1 + " is the sum of the sector ids of real rooms.");

    Optional<Room> northPole = lines.stream()
        .map(Room::fromLine)
        .filter(room -> "northpole object storage".equals(room.decryptedName()))
        .findFirst();

    String northPoleSector = northPole
        .map(room -> "" + room.sector)
        .orElse("???");

    System.out.println("Part 2: North Pole objects are in sector " + northPoleSector);
  }
}
