package dev.adventofcode2016;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import dev.adventofcode2016.algorithms.AStar;
import dev.adventofcode2016.util.ImmutableListCollector;

public class Problem22 implements AStar<Problem22.Grid> {

  public static class Position {
    public final int x;
    public final int y;

    public Position(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Position)) return false;
      Position position = (Position) o;
      return x == position.x &&
          y == position.y;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y);
    }

    @Override
    public String toString() {
      return "(" + x + ", " + y + ')';
    }
  }

  public static class Node {
    public final int size;
    public final int used;
    public final int available;
    public final int usePercent;

    public Node(int size, int used, int available, int usePercent) {
      this.size = size;
      this.used = used;
      this.available = available;
      this.usePercent = usePercent;
    }

    /**
     * Returns a copy of this node with all of the data gone.
     * @return Empty copy of this node.
     */
    public Node empty() {
      return new Node(size, 0, size, 0);
    }

    /**
     * Returns a copy of this node with data copied from the given node.
     * @param from Node to copy data from
     * @return Copy of this node containing data from the given node
     */
    public Node withData(Node from) {
      return new Node(size, from.used, size - from.used, from.used * 100 / size);
    }

    @Override
    public String toString() {
      return "size=" + size + 'T' +
          ", used=" + used + 'T' +
          ", available=" + available + 'T' +
          ", usePercent=" + usePercent + '%';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Node node = (Node) o;
      return size == node.size &&
          used == node.used &&
          available == node.available &&
          usePercent == node.usePercent;
    }

    @Override
    public int hashCode() {
      return Objects.hash(size, used, available, usePercent);
    }

    // "/dev/grid/node-x0-y0     94T   72T    22T   76%
    private static final Pattern NODE_PATTERN = Pattern.compile("/dev/grid/node-x(\\d+)-y(\\d+)\\s+(\\d+)T\\s+(\\d+)T\\s+(\\d+)T\\s+(\\d+)%");

    public static Optional<Node> fromLine(String line) {
      Matcher matcher = NODE_PATTERN.matcher(line);

      if (! matcher.matches()) {
        return Optional.empty();
      }

      return Optional.of(new Node(
          Integer.parseInt(matcher.group(3)),
          Integer.parseInt(matcher.group(4)),
          Integer.parseInt(matcher.group(5)),
          Integer.parseInt(matcher.group(6))
      ));
    }
  }

  public static class Grid {
    public static final Grid GOAL = new Grid(null, null, new Position(0, 0));

    public final Node[][] nodes;
    public final Position empty;
    public final Position goal;

    public Grid(Node[][] nodes, Position empty, Position goal) {
      this.nodes = nodes;
      this.empty = empty;
      this.goal = goal;
    }

    /**
     * Returns an optional grid with the empty node moved to another location.
     * The result will be present if the move is valid, and empty otherwise.
     *
     * @param position position to move the empty square to
     * @return Grid with data moved if the move is valid, empty otherwise
     */
    public Optional<Grid> moveEmpty(Position position) {
      if (position.x < 0 || position.x >= nodes[0].length || position.y < 0 || position.y >= nodes[0].length) {
        return Optional.empty(); // Position is out of bounds.
      }

      Node from = nodes[empty.y][empty.x];
      Node to = nodes[position.y][position.x];

      if (to.available < from.used) {
        return Optional.empty();
      }

      Node[][] newNodes = new Node[nodes[0].length][nodes.length];
      for (int i = 0; i < nodes.length; i ++) {
        System.arraycopy(nodes[i], 0, newNodes[i], 0, nodes[i].length);
      }

      newNodes[empty.y][empty.x] = newNodes[empty.y][empty.x].withData(newNodes[position.y][position.x]);
      newNodes[position.y][position.x] = newNodes[position.y][position.x].empty();

      Position newGoal = position.equals(goal) ? empty : goal;

      return Optional.of(new Grid(newNodes, position, newGoal));
    }

    /**
     * Renders the nodes for debugging purposes.  Empty nodes are marked as '_', the target is marked as G, and
     * other nodes are marked as '.'.
     *
     * @return Strings that can be passed to System.out.println for rendering
     */
    public ImmutableList<String> render() {
      ImmutableList.Builder<String> lines = ImmutableList.builder();


      for (int y = 0; y < nodes.length; y ++) {
        StringBuilder line = new StringBuilder();

        for (int x = 0; x < nodes[y].length; x ++) {
          Node node = nodes[y][x];

          char nodeChar;
          if (x == goal.x && y == goal.y) {
            nodeChar = 'G';
          } else if (node.usePercent == 0) {
            nodeChar = '_';
          } else {
            nodeChar = '.';
          }

          line.append(nodeChar).append(' ');
        }

        lines.add(line.toString());
      }

      return lines.build();
    }

    /**
     * Constructs a new grid from the given list of lines
     *
     * @param lines Lines describing the nodes
     * @return Grid that represents the lines
     */
    public static Grid fromLines(Collection<String> lines) {
      ImmutableList<Node> nodeList = lines.stream()
          .map(Node::fromLine)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(new ImmutableListCollector<>());

      int sideLength = (int) Math.sqrt(nodeList.size());

      Node[][] nodes = new Node[sideLength][sideLength];

      Position empty = null;

      // Nodes are ordered by x0-y0, x0-y1, ... in the lines
      int index = 0;
      for (int x = 0; x < sideLength; x ++) {
        for (int y = 0; y < sideLength; y ++) {
          nodes[y][x] = nodeList.get(index++);

          if (nodes[y][x].usePercent == 0) {
            empty = new Position(x, y);
          }
        }
      }

      Position goal = new Position(sideLength - 1, 0); // Goal is in the top right corner

      return new Grid(nodes, empty, goal);
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }

      if (!(other instanceof Grid)) {
        return false;
      }

      Grid grid = (Grid) other;
      // Goal is at the end position - empty node position doesn't matter.
      if (goal.equals(new Position(0, 0)) && goal.equals(grid.goal)) {
        return true;
      }

      return goal.equals(grid.goal)
          && empty.equals(grid.empty);
//          && Arrays.deepEquals(nodes, grid.nodes);
    }

    @Override
    public int hashCode() {
      return Objects.hash(goal, empty, Arrays.deepHashCode(nodes));
    }

    @Override
    public String toString() {
      return Joiner.on('\n').join(render());
    }
  }

  /**
   * Calculates the taxicab distance between the goal in from and to.
   *
   * @param from Starting position
   * @param to   Ending position
   * @return Heuristic cost of moving the goal from 'from' to 'to'
   */
  @Override
  public int heuristicCost(Grid from, Grid to) {
    // Taxicab distance from the goal to the end
    return Math.abs(from.goal.x - to.goal.x)
        + Math.abs(from.goal.y - to.goal.y)
        // Weight grids where the empty square is close to the goal more favorably
        + (int) Math.sqrt(
            Math.pow(Math.abs(from.empty.x - from.goal.x), 2)
                + Math.pow(Math.abs(from.empty.y - from.goal.y), 2));
  }

  /**
   * Returns the list of neighbors of the given grid.  The empty space will be moved in all four directions.
   *
   * @param grid Grid to check
   * @return Neighbors of the grid
   */
  @Override
  public ImmutableList<Grid> neighbors(Grid grid) {
    return Stream.of(
        grid.moveEmpty(new Position(grid.empty.x - 1, grid.empty.y)),
        grid.moveEmpty(new Position(grid.empty.x + 1, grid.empty.y)),
        grid.moveEmpty(new Position(grid.empty.x, grid.empty.y - 1)),
        grid.moveEmpty(new Position(grid.empty.x, grid.empty.y + 1))
    )
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(new ImmutableListCollector<>());
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
    return from.used != 0             // From node isn't empty
        && !(from.equals(to))         // Not the same node
        && from.used <= to.available; // Space for the transfer to complete
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

    Grid grid = Grid.fromLines(Resources.readLines(Resources.getResource("problem22.txt"), Charsets.UTF_8));

    System.out.println("Part 1: " + numViable + " viable pairs of nodes.");
    System.out.println("Part 2: fewest number of steps: " + (new Problem22().shortestPath(grid, Grid.GOAL).size() - 1));
  }
}
