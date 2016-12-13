package dev.adventofcode2016;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem13Test {

  private Problem13.Maze maze = new Problem13.Maze(10);

  @Test
  public void part1ExampleBoard() {
    assertThat(maze.render(new Problem13.Point(9, 6)))
        .containsExactly(
            ".#.####.##",
            "..#..#...#",
            "#....##...",
            "###.#.###.",
            ".##..#..#.",
            "..##....#.",
            "#...##.###"
        );
  }

  @Test
  public void outsideIsClosed() {
    assertThat(maze.isOpen(new Problem13.Point(-1, 0))).isFalse();
    assertThat(maze.isOpen(new Problem13.Point(0, -1))).isFalse();
  }

  @Test
  public void part1ExampleShortestPath() {
    int steps = maze.fewestSteps(new Problem13.Point(1, 1), new Problem13.Point(7, 4));

    assertThat(steps).isEqualTo(11);
  }
}
