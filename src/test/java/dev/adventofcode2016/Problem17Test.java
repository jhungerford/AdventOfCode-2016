package dev.adventofcode2016;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem17Test {

  @Test
  public void neighborsNoSteps() {
    Problem17 problem = new Problem17("hijkl");
    assertThat(problem.neighbors(problem.new Position(0, 0, ""))).containsOnly(
        problem.new Position(0, 1, "D")
    );
  }

  @Test
  public void neighborsOneStep() {
    Problem17 problem = new Problem17("hijkl");
    assertThat(problem.neighbors(problem.new Position(0, 1, "D"))).containsOnly(
        problem.new Position(0, 0, "DU"),
        problem.new Position(1, 1, "DR")
    );
  }

  @Test
  public void shortestPathExample1() {
    assertThat(new Problem17("ihgpwlah").shortestPath()).isEqualTo("DDRRRD");
  }

  @Test
  public void shortestPathExample2() {
    assertThat(new Problem17("kglvqrro").shortestPath()).isEqualTo("DDUDRLRRUDRD");
  }

  @Test
  public void shortestPathExample3() {
    assertThat(new Problem17("ulqzkmiv").shortestPath()).isEqualTo("DRURDRUDDLLDLUURRDULRLDUUDDDRR");
  }
}
