package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class Problem14 {

  public static class KeyGenerator {
    private static final HashFunction MD5 = Hashing.md5();

    // Problem14 uses lookahead to determine if a key is valid (one of the next 1000 keys must
    // match a condition), so keyCache caches keys.
    private final LoadingCache<Integer, String> keyCache = CacheBuilder.newBuilder()
        .maximumSize(2000)
        .build(
            new CacheLoader<Integer, String>() {
              @Override
              public String load(Integer index) throws Exception {
                return MD5.hashString(salt + index, Charsets.UTF_8).toString();
              }
            }
        );

    private final String salt;

    /**
     * Constructs a new KeyGenerator with the given salt.  Numbers will be appended to the salt.
     *
     * @param salt Salt to use to generate keys
     */
    public KeyGenerator(String salt) {
      this.salt = salt;
    }

    /**
     * Returns the key for the given index by computing md5(salt + index)
     *
     * @param index Key index
     * @return Key for the given index
     */
    public String generateKey(int index) {
      return keyCache.getUnchecked(index);
    }

    /**
     * Returns the first character that appears three times in a row in the given key.
     * For example, this function will return ['8'] for ...cc38887a5... because 8 appears
     * three times in a row.  Empty means no characters appeared three times in a row.
     *
     * @param key Key to check.
     * @return Optional character that appeared three times in a row in the key.
     */
    public Optional<Character> tripleCharacter(String key) {
      for (int i = 0; i < key.length() - 2; i ++) {
        if (key.charAt(i) == key.charAt(i + 1) && key.charAt(i + 1) == key.charAt(i + 2)) {
          return Optional.of(key.charAt(i));
        }
      }

      return Optional.empty();
    }


    /**
     * Returns whether the given character is repeated five times in a row in the given key.
     *
     * @param c   Character that should be repeated
     * @param key Key to check
     * @return Whether the character appears five times in a row in the key
     */
    public boolean isRepeatedFiveTimes(char c, String key) {
      for (int i = 0; i < key.length() - 5; i ++) {
        if (key.charAt(i) == c
            && key.charAt(i + 1) == c
            && key.charAt(i + 2) == c
            && key.charAt(i + 3) == c
            && key.charAt(i + 4) == c) {
          return true;
        }
      }

      return false;
    }

    /**
     * Returns whether the given index generates a valid key.  A valid key's hash contains three of the
     * same character in a row.  One fo the next 1000 hashes in the stream must contain that same character
     * five times in a row for the hash to be valid.
     *
     * @param index Index to check
     * @return Whether the index represents a valid key.
     */
    public boolean isKeyIndex(int index) {
      Optional<Character> tripleCharacter = tripleCharacter(generateKey(index));

      return tripleCharacter.map(character ->
          IntStream.rangeClosed(1, 1000)
              .anyMatch(i -> isRepeatedFiveTimes(character, generateKey(index + i)))
      ).orElse(false);
    }
  }

  public static void main(String[] args) {
    KeyGenerator keyGenerator = new KeyGenerator("yjdafjpo");

    OptionalInt key64Index = IntStream.iterate(1, i -> i + 1)
        .filter(keyGenerator::isKeyIndex)
        .limit(64)
        .max();

    System.out.println("Part 1: " + key64Index + " produces the 64th key.");
  }
}
