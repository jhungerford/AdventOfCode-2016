package dev.adventofcode2016;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

import java.util.Optional;

public class Problem22Test {

  private static final ImmutableList<String> EXAMPLE_LINES = ImmutableList.of(
      "Filesystem            Size  Used  Avail  Use%",
      "/dev/grid/node-x0-y0   10T    8T     2T   80%",
      "/dev/grid/node-x0-y1   11T    6T     5T   54%",
      "/dev/grid/node-x0-y2   32T   28T     4T   87%",
      "/dev/grid/node-x1-y0    9T    7T     2T   77%",
      "/dev/grid/node-x1-y1    8T    0T     8T    0%",
      "/dev/grid/node-x1-y2   11T    7T     4T   63%",
      "/dev/grid/node-x2-y0   10T    6T     4T   60%",
      "/dev/grid/node-x2-y1    9T    8T     1T   88%",
      "/dev/grid/node-x2-y2    9T    6T     3T   66%"
  );

  private static final Problem22.Grid EXAMPLE = Problem22.Grid.fromLines(EXAMPLE_LINES);


  @Test
  public void parseNode() {
    assertThat(Problem22.Node.fromLine("/dev/grid/node-x0-y0     94T   72T    22T   76%"))
        .hasValue(new Problem22.Node( 94, 72, 22, 76));
  }

  @Test
  public void parseNonNode() {
    assertThat(Problem22.Node.fromLine("root@ebhq-gridcenter# df -h")).isEmpty();
  }

  @Test
  public void visualizeExample() {
    assertThat(EXAMPLE.render()).containsExactly(
        ". . G ",
        ". _ . ",
        ". . . "
    );
  }

  @Test
  public void fewestStepsExample() {
    assertThat(new Problem22().shortestPath(EXAMPLE, Problem22.Grid.GOAL)).hasSize(8);
  }

  @Test
  public void exampleMoveDown() {
    Optional<Problem22.Grid> moved = EXAMPLE.moveEmpty(new Problem22.Position(1, 2));
    assertThat(moved.isPresent());

    Problem22.Grid actual = moved.get();
    Problem22.Grid expected = new Problem22.Grid(new Problem22.Node[][]{
        new Problem22.Node[]{EXAMPLE.nodes[0][0], EXAMPLE.nodes[0][1], EXAMPLE.nodes[0][2]},
        new Problem22.Node[]{EXAMPLE.nodes[1][0], EXAMPLE.nodes[1][1].withData(EXAMPLE.nodes[2][1]), EXAMPLE.nodes[1][2]},
        new Problem22.Node[]{EXAMPLE.nodes[2][0], EXAMPLE.nodes[2][1].empty(), EXAMPLE.nodes[2][2]}
    },
        new Problem22.Position(1, 2),
        new Problem22.Position(2, 0)
    );

    assertThat(actual.nodes).isEqualTo(expected.nodes);
    assertThat(actual.empty).isEqualTo(expected.empty);
    assertThat(actual.goal).isEqualTo(expected.goal);
  }

  @Test
  public void exampleMoveGoal() {
    // Move the empty square up and right
    Optional<Problem22.Grid> up = EXAMPLE.moveEmpty(new Problem22.Position(1, 0));
    assertThat(up.isPresent());

    Optional<Problem22.Grid> right = up.get().moveEmpty(new Problem22.Position(2, 0));
    assertThat(right.isPresent());

    Problem22.Grid actual = right.get();
    Problem22.Grid expected = new Problem22.Grid(new Problem22.Node[][]{
        new Problem22.Node[]{EXAMPLE.nodes[0][0], EXAMPLE.nodes[0][1].withData(EXAMPLE.nodes[0][2]), EXAMPLE.nodes[0][2].empty()},
        new Problem22.Node[]{EXAMPLE.nodes[1][0], EXAMPLE.nodes[1][1].withData(EXAMPLE.nodes[0][1]), EXAMPLE.nodes[1][2]},
        new Problem22.Node[]{EXAMPLE.nodes[2][0], EXAMPLE.nodes[2][1], EXAMPLE.nodes[2][2]}
    },
        new Problem22.Position(2, 0),
        new Problem22.Position(1, 0)
    );

    assertThat(actual.nodes).isEqualTo(expected.nodes);
    assertThat(actual.empty).isEqualTo(expected.empty);
    assertThat(actual.goal).isEqualTo(expected.goal);
  }

}
