package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Problem7 {

  public static class IPv7 {
    /** Portions of the IPv7 address outside of square brackets */
    public final ImmutableList<String> segments;
    /** Portions of IPv7 addresses inside square brackets */
    public final ImmutableList<String> hypernetSequences;

    public IPv7(ImmutableList<String> segments, ImmutableList<String> hypernetSequences) {
      this.segments = segments;
      this.hypernetSequences = hypernetSequences;
    }

    /**
     * Returns whether this IPv7 address supports TLS (transport-layer snooping).
     * An IP supports TLS if it has a four-character sequence that consists of a pair
     * of two different characters followed by the reverse of that pair, like 'xyyx'.
     * The IP must not have a sequence inside of any square brackets
     *
     * @return Whether this address supports TLS.
     */
    public boolean supportsTLS() {
      return hypernetSequences.stream().noneMatch(IPv7::hasAbba)
          && segments.stream().anyMatch(IPv7::hasAbba);
    }

    /**
     * Returns whether the given string contains an Autonomous Bridge Bypass Annotation, or abba.
     * An ABBA is any four-character sequence which consists of a pair of two different characters
     * followed by the reverse of that pair, such as xyyx or abba.
     *
     * @param str String to check
     * @return Whether the given string contains an abba
     */
    public static boolean hasAbba(String str) {
      return IntStream.rangeClosed(0, str.length() - 4)
          .mapToObj(index -> str.substring(index, index + 4))
          .anyMatch(s ->
              s.charAt(0) == s.charAt(3)
                  && s.charAt(1) == s.charAt(2)
                  && s.charAt(0) != s.charAt(1)
          );
    }

    /**
     * Returns whether this IPv7 address supports SSL (super-secret listening).
     * An IP supports SSL if it has an Area-Broadcast Accessor, or ABA, anywhere
     * in the supernet sequences (outside any square bracketed sections), and a
     * corresponding Byte Allocation Block, or BAB, anywhere in the hypernet sequences.
     * An ABA is any three-character sequence which consists of the same character twice
     * with a different character between them, such as xyx or aba. A corresponding BAB
     * is the same characters but in reversed positions: yxy and bab, respectively.
     *
     * @return Whether this address supports SSL.
     */
    public boolean supportsSSL() {
      // Extract all possible ABAs from the segments, and invert them to BABs
      List<String> segmentBabs = segments.stream()
          .flatMap(Problem7.IPv7::extractABAOrBABs)
          .map(Problem7.IPv7::invertABAOrBAB)
          .collect(Collectors.toList());

      // Extract BABs from hypernets
      List<String> hypernetBabs = hypernetSequences.stream()
          .flatMap(Problem7.IPv7::extractABAOrBABs)
          .collect(Collectors.toList());

      // If one of the segment BABs matches a hypernet BAB, this address supports ssl.
      return segmentBabs.stream()
          .anyMatch(hypernetBabs::contains);
    }

    /**
     * Extracts all subsequences that look like 'aba', where the first and last character match and the
     * middle character does not.
     *
     * @param str String to extract ABA or BABs from.
     * @return All ABA or BAB sequences in the string.
     */
    public static Stream<String> extractABAOrBABs(String str) {
      return IntStream.rangeClosed(0, str.length() - 3)
          .mapToObj(index -> str.substring(index, index + 3))
          .filter(s -> s.charAt(0) == s.charAt(2) && s.charAt(0) != s.charAt(1));
    }

    /**
     * Inverts an ABA into a BAB, or a BAB into an ABA.  For example, invertABAOrBAB('aba') will return 'bab'.
     *
     * @param aba String to invert
     * @return Inverted string
     */
    public static String invertABAOrBAB(String aba) {
      return "" + aba.charAt(1) + aba.charAt(0) + aba.charAt(1);
    }

    /**
     * Constructs an IPv7 address from an address containing segments and hypernet sequences contained
     * in square brackets.
     *
     * @param address Address to convert to an IPv7 address
     * @return IPv7 address
     */
    public static IPv7 fromAddress(String address) {
      ImmutableList.Builder<String> segments = ImmutableList.builder();
      ImmutableList.Builder<String> hypernetSequences = ImmutableList.builder();

      int previousIndex = 0;
      for (int index = address.indexOf('['); index != -1; index = address.indexOf('[', previousIndex)) {
        segments.add(address.substring(previousIndex, index));

        int closingIndex = address.indexOf(']', index);
        hypernetSequences.add(address.substring(index + 1, closingIndex));

        previousIndex = closingIndex + 1;
      }

      segments.add(address.substring(previousIndex));

      return new IPv7(segments.build(), hypernetSequences.build());
    }
  }

  public static void main(String[] args) throws IOException {
    List<String> lines = Resources.readLines(Resources.getResource("problem7.txt"), Charsets.UTF_8);

    long tlsCount = lines.stream()
        .map(Problem7.IPv7::fromAddress)
        .filter(Problem7.IPv7::supportsTLS)
        .count();

    long sslCount = lines.stream()
        .map(Problem7.IPv7::fromAddress)
        .filter(Problem7.IPv7::supportsSSL)
        .count();

    System.out.println("Part 1: " + tlsCount + " addresses support tls.");
    System.out.println("Part 2: " + sslCount + " addresses support ssl.");
  }
}
