package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

  private interface DecompressNode {
    /**
     * @return Number of characters in this node and all of its children.
     */
    long length();

    /**
     * @param child Child node to add.
     */
    void addChild(DecompressNode child);

    /**
     * @return This node's parent
     */
    DecompressNode parent();

    /**
     * Appends the given character to this node.
     * @param c Character to append.
     */
    void append(char c);

    boolean isFull();
  }

  private static class RootNode implements DecompressNode {
    private final List<DecompressNode> children = new ArrayList<>();

    @Override
    public long length() {
      return children.stream()
          .mapToLong(DecompressNode::length)
          .sum();
    }

    @Override
    public void addChild(DecompressNode child) {
      this.children.add(child);
    }

    @Override
    public DecompressNode parent() {
      return null;
    }

    @Override
    public void append(char c) {
      // Ignore the character - a child will remember it.
    }

    @Override
    public boolean isFull() {
      return false;
    }
  }

  private static class RepeatNode implements DecompressNode {
    private final DecompressNode parent;
    private final List<DecompressNode> children = new ArrayList<>();

    private final int length;
    private final int times;

    private int seen;

    public RepeatNode(DecompressNode parent, int length, int times) {
      this.parent = parent;
      this.length = length;
      this.times = times;
      this.seen = 0;
    }

    @Override
    public long length() {
      return times * children.stream()
          .mapToLong(DecompressNode::length)
          .sum();
    }

    @Override
    public void addChild(DecompressNode child) {
      children.add(child);
    }

    @Override
    public DecompressNode parent() {
      return parent;
    }

    @Override
    public void append(char c) {
      this.parent.append(c);
      this.seen ++;
    }

    @Override
    public boolean isFull() {
      return this.seen == this.length;
    }

    @Override
    public String toString() {
      return "RepeatNode{" +
          "length=" + length +
          ", times=" + times +
          ", seen=" + seen +
          '}';
    }
  }

  private static class WordNode implements DecompressNode {
    private final DecompressNode parent;
    private final StringBuilder bldr;

    public WordNode(DecompressNode parent) {
      this.parent = parent;
      this.bldr = new StringBuilder();
    }

    @Override
    public long length() {
      return bldr.length();
    }

    @Override
    public void addChild(DecompressNode child) {
      throw new IllegalStateException("Word node cannot cannot contain a child.");
    }

    @Override
    public DecompressNode parent() {
      return parent;
    }

    @Override
    public void append(char c) {
      parent.append(c);
      bldr.append(c);
    }

    @Override
    public boolean isFull() {
      return parent.isFull();
    }

    @Override
    public String toString() {
      return "WordNode{" + bldr + '}';
    }
  }

  /**
   * Calculates the length of decompressed data using v2 of the decompression algorithm described
   * in {@code decompress}.  In version two, the only difference is that markers within decompressed
   * data are decompressed. This, the documentation explains, provides much more substantial compression
   * capabilities, allowing many-gigabyte files to be stored in only a few kilobytes.
   *
   * @param compressed Compressed data
   * @return Number of charactes in the decompressed data.
   */
  public static long decompressedLengthV2(String compressed) {
    Repeat repeat = null;
    ParseMode parseMode = ParseMode.WORD;

    DecompressNode rootNode = new RootNode();
    DecompressNode currentNode = rootNode;

    for (char c : compressed.toCharArray()) {
      switch (parseMode) {
        case WORD:
          if (c == '(') { // Switch from reading normal words to reading repeat instructions
            repeat = new Repeat();
            parseMode = ParseMode.REPEAT_CHARS;

            // Word nodes can't contain children - go up a level.
            if (currentNode instanceof WordNode) {
              currentNode = currentNode.parent();
            }

            currentNode.append(c);
          } else { // Read normal words

            // Normal characters only go into word nodes
            if (! (currentNode instanceof WordNode)) {
              WordNode wordNode = new WordNode(currentNode);
              currentNode.addChild(wordNode);
              currentNode = wordNode;
            }

            currentNode.append(c);

            // If the current node is full, traverse up the tree until you reach
            while (currentNode.isFull()) {
              currentNode = currentNode.parent();
            }
          }
          break;

        case REPEAT_CHARS:
          currentNode.append(c);
          if (c == 'x') { // Switch from the number of characters to repeat to the number of times to repeat them.
            parseMode = ParseMode.REPEAT_TIMES;
          } else { // Parse the number of characters to repeat
            repeat.numCharsBuffer.append(c);
          }
          break;

        case REPEAT_TIMES:
          currentNode.append(c);
          if (c == ')') { // Switch from the number of times to repeat to buffering repeated characters
            parseMode = ParseMode.WORD;

            RepeatNode repeatNode = new RepeatNode(currentNode, repeat.getNumChars(), repeat.getTimes());
            currentNode.addChild(repeatNode);
            currentNode = repeatNode;

          } else { // Parse the number of times to repeat
            repeat.timesBuffer.append(c);
          }
          break;

        default:
          throw new IllegalStateException("Unknown parse mode " + parseMode);
      }
    }

    return rootNode.length();
  }

  public static void main(String[] args) throws IOException {
    String compressed = Resources.toString(Resources.getResource("problem9.txt"), Charsets.UTF_8).trim();

    System.out.println("Part 1: decompressed file has length " + Problem9.decompress(compressed).length());
    System.out.println("Part 2: v2 decompressed file has length " + Problem9.decompressedLengthV2(compressed));
  }
}
