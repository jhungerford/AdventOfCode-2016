package dev.adventofcode2016;

import com.google.common.collect.ImmutableList;
import dev.adventofcode2016.util.ImmutableListCollector;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem13Test {

  private Problem13.Maze maze = new Problem13.Maze(10);

  @Test
  public void part1ExampleBoard() {
    // Iterates over the rows and columns, asking if the cells are open.  Builds a string for each row,
    // with . if the cell is open or # if it's a wall.
    ImmutableList<String> actual =
        IntStream.rangeClosed(0, 6)
            .mapToObj(y ->
                IntStream.rangeClosed(0, 9)
                    .mapToObj(x -> maze.isOpen(new Problem13.Point(x, y)) ? '.' : '#')
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                    .toString()
            ).collect(new ImmutableListCollector<>());

    assertThat(actual).containsExactly(
        ".#.####.##",
        "..#..#...#",
        "#....##...",
        "###.#.###.",
        ".##..#..#.",
        "..##....#.",
        "#...##.###"
    );
  }

}
