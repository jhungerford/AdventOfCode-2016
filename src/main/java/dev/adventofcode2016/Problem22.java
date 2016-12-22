package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import dev.adventofcode2016.util.ImmutableListCollector;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Problem22 {



  public static class Node {
    public final String filesystem;
    public final int x;
    public final int y;
    public final int size;
    public final int used;
    public final int available;
    public final int usePercent;

    public Node(String filesystem, int x, int y, int size, int used, int available, int usePercent) {
      this.filesystem = filesystem;
      this.x = x;
      this.y = y;
      this.size = size;
      this.used = used;
      this.available = available;
      this.usePercent = usePercent;
    }

    @Override
    public String toString() {
      return filesystem +
          ", size=" + size + 'T' +
          ", used=" + used + 'T' +
          ", available=" + available + 'T' +
          ", usePercent=" + usePercent + '%';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Node node = (Node) o;
      return x == node.x &&
          y == node.y &&
          size == node.size &&
          used == node.used &&
          available == node.available &&
          usePercent == node.usePercent &&
          Objects.equals(filesystem, node.filesystem);
    }

    @Override
    public int hashCode() {
      return Objects.hash(filesystem, x, y, size, used, available, usePercent);
    }

    // "/dev/grid/node-x0-y0     94T   72T    22T   76%
    private static final Pattern NODE_PATTERN = Pattern.compile("(/dev/grid/node-x(\\d+)-y(\\d+))\\s+(\\d+)T\\s+(\\d+)T\\s+(\\d+)T\\s+(\\d+)%");

    public static Optional<Node> fromLine(String line) {
      Matcher matcher = NODE_PATTERN.matcher(line);

      if (! matcher.matches()) {
        return Optional.empty();
      }

      return Optional.of(new Node(
          matcher.group(1),
          Integer.parseInt(matcher.group(2)),
          Integer.parseInt(matcher.group(3)),
          Integer.parseInt(matcher.group(4)),
          Integer.parseInt(matcher.group(5)),
          Integer.parseInt(matcher.group(6)),
          Integer.parseInt(matcher.group(7))
      ));
    }
  }

  /**
   * Returns whether it's viable to transfer data from one node to another.  The transfer is viable if:
   *
   * Node A is not empty (its Used is not zero).
   * Nodes A and B are not the same node.
   * The data on node A (its Used) would fit on node B (its Avail).
   *
   * @param from Node to transfer data from
   * @param to   Node to transfer data to
   * @return Whether the transfer is viable
   */
  public static boolean isViable(Node from, Node to) {
    return from.used != 0                      // From node isn't empty
        && !(from.x == to.x && from.y == to.y) // Not the same node
        && from.used <= to.available;          // Space for the transfer to complete

  }

  public static void main(String[] args) throws IOException {
    ImmutableList<Node> nodes = Resources.readLines(Resources.getResource("problem22.txt"), Charsets.UTF_8).stream()
        .map(Node::fromLine)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(new ImmutableListCollector<>());

    long numViable = nodes.stream()
        .mapToLong(fromNode -> nodes.stream().filter(toNode -> isViable(fromNode, toNode)).count())
        .sum();

    System.out.println("Part 1: " + numViable + " viable pairs of nodes.");

  }
}
