package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;

public class Problem9 {

  private enum ParseMode {
    /** Plain word character - switch on ( */
    WORD,
    /** First number in a repeat block - switch on x */
    REPEAT_CHARS,
    /** Second number in a repeat block - switch on ) */
    REPEAT_TIMES,
    /** After a repeat block - buffer REPEAT_CHARS into a Repeat */
    REPEAT
  }

  private static class Repeat {
    public final StringBuilder numCharsBuffer;
    public final StringBuilder timesBuffer;
    public final StringBuilder buffer;

    public Repeat() {
      this.numCharsBuffer = new StringBuilder();
      this.timesBuffer = new StringBuilder();
      this.buffer = new StringBuilder();
    }

    public int getNumChars() {
      return Integer.parseInt(numCharsBuffer.toString());
    }

    public int getTimes() {
      return Integer.parseInt(timesBuffer.toString());
    }

    public boolean isFull() {
      return buffer.length() == getNumChars();
    }
  }

  /**
   * Decompresses the given string according to the following rules.  To indicate that some
   * sequence should be repeated, a marker is added to the file, like (10x2). To decompress
   * this marker, take the subsequent 10 characters and repeat them 2 times. Then, continue
   * reading the file after the repeated data. The marker itself is not included in the
   * decompressed output.
   *
   * If parentheses or other characters appear within the data referenced by a marker,
   * that's okay - treat it like normal data, not a marker, and then resume looking for markers
   * after the decompressed section.
   *
   * @param compressed Compressed string
   * @return Decompressed string
   */
  public static String decompress(String compressed) {
    StringBuilder bldr = new StringBuilder(compressed.length());

    Repeat repeat = null;
    ParseMode parseMode = ParseMode.WORD;

    for (char c : compressed.toCharArray()) {
      switch (parseMode) {
        case WORD:
          if (c == '(') { // Switch from reading normal words to reading repeat instructions
            repeat = new Repeat();
            parseMode = ParseMode.REPEAT_CHARS;
          } else { // Read normal words
            bldr.append(c);
          }
          break;

        case REPEAT:
          repeat.buffer.append(c);

          if (repeat.isFull()) { // Done buffering characters - repeat them.
            for (int repetition = 0; repetition < repeat.getTimes(); repetition ++) {
              bldr.append(repeat.buffer);
            }

            parseMode = ParseMode.WORD;
          }
          break;

        case REPEAT_CHARS:
          if (c == 'x') { // Switch from the number of characters to repeat to the number of times to repeat them.
            parseMode = ParseMode.REPEAT_TIMES;
          } else { // Parse the number of characters to repeat
            repeat.numCharsBuffer.append(c);
          }
          break;

        case REPEAT_TIMES:
          if (c == ')') { // Switch from the number of times to repeat to buffering repeated characters
            parseMode = ParseMode.REPEAT;
          } else { // Parse the number of times to repeat
            repeat.timesBuffer.append(c);
          }
          break;

        default:
          throw new IllegalStateException("Unknown parse mode " + parseMode);
      }
    }

    return bldr.toString();
  }

  public static void main(String[] args) throws IOException {
    String compressed = Resources.toString(Resources.getResource("problem9.txt"), Charsets.UTF_8).trim();
    String decompressed = Problem9.decompress(compressed);

    System.out.println("Part 1: decompressed file has length " + decompressed.length());
  }
}
