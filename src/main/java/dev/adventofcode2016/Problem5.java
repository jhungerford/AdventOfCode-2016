package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.util.stream.IntStream;

public class Problem5 {

  public static final HashFunction MD5 = Hashing.md5();

  /**
   * Calculates a password with the given length by finding interesting values.
   * doorID and an index are concatenated and hashed.  The sixth value of a hash
   * that starts with five zeros is the next character in the password.
   *
   * @param doorID Door identifier
   * @param length Number of characters in the password
   * @return Door password
   */
  public static String password(String doorID, int length) {
    return IntStream.iterate(0, i -> i + 1)
        .mapToObj(i -> doorID + i) // Hash input is code + index
        .map(code -> MD5.hashString(code, Charsets.UTF_8).toString()) // MD5
        .filter(hash -> hash.startsWith("00000")) // Interesting codes start with five 0's
        .map(hash -> hash.charAt(5)) // 6th character is the next character in the password.  Will box to Character
        .limit(length) // Take 'length' interesting characters
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append) // Convert to string
        .toString();
  }

  /**
   * Calculates an 8 character password by finding interesting values.
   * DoorID and an index are concatinated and md5 hashed.  Hashes that start with
   * five zeros are considered interesting.  The seventh character in the hash
   * is an element of the password.  The sixth character in the hash indicates
   * the position of the seventh character in the password.  For example, a hash
   * that starts with '000001f' means that 'f' is the second character in the password.
   * Positions greater than 7 are ignored, and duplicate positions are ignored.
   *
   * @param doorID Door identifier
   * @return Door password
   */
  public static String positionPassword(String doorID) {
    System.out.println("Decoding password...");

    StringBuilder bldr = new StringBuilder("________");
    System.out.println("  " + bldr);

    for (int i = 0; bldr.toString().indexOf('_') != -1; i ++) {
      String hash = MD5.hashString(doorID + i, Charsets.UTF_8).toString();

      if (hash.startsWith("00000")) {
        char indexChar = hash.charAt(5);
        char passwordChar = hash.charAt(6);

        if (indexChar >= '0' && indexChar <= '7' && bldr.charAt(indexChar - '0') == '_') {
          bldr.setCharAt(indexChar - '0', passwordChar);
          System.out.println("  " + bldr);
        }
      }
    }

    return bldr.toString();
  }

  public static void main(String[] args) {
    String doorID = "reyedfim";

    System.out.println("Part 1: password is '" + password(doorID, 8) + "'");
    System.out.println("Part 2: password is '" + positionPassword(doorID) + "'");
  }
}
