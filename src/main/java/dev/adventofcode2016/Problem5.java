package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.util.stream.IntStream;

public class Problem5 {

  /**
   * Calculates a password with the given length by finding interesting values.
   * doorId and an index are concatenated and hashed.  The sixth value of a hash
   * that starts with five zeros is the next character in the password.
   *
   * @param doorID Door identifier
   * @param length Number of characters in the password
   * @return Door password
   */
  public static String password(String doorID, int length) {
    HashFunction md5 = Hashing.md5();

    return IntStream.iterate(0, i -> i + 1)
        .mapToObj(i -> doorID + i) // Hash input is code + index
        .map(code -> md5.hashString(code, Charsets.UTF_8).toString()) // MD5
        .filter(hash -> hash.startsWith("00000")) // Interesting codes start with five 0's
        .map(hash -> hash.charAt(5)) // 6th character is the next character in the password.  Will box to Character
        .limit(length) // Take 'length' interesting characters
        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append) // Convert to string
        .toString();
  }

  public static void main(String[] args) {
    String passwordPart1 = password("reyedfim", 8);
    System.out.println("Part 1: password is '" + passwordPart1 + "'");
  }
}
