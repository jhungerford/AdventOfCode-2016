package dev.adventofcode2016;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Problem1Test {

  @Test
  public void northTurnLeft() {
    assertThat(Problem1.Direction.NORTH.turn('L')).isEqualTo(Problem1.Direction.WEST);
  }

  @Test
  public void northTurnRight() {
    assertThat(Problem1.Direction.NORTH.turn('R')).isEqualTo(Problem1.Direction.EAST);
  }

  @Test
  public void westTurnRight() {
    assertThat(Problem1.Direction.WEST.turn('R')).isEqualTo(Problem1.Direction.NORTH);
  }

  @Test
  public void right2Left3() {
    assertThat(Problem1.follow("R2, L3").blocksFromStart()).isEqualTo(5);
  }

  @Test
  public void right2right2right2() {
    assertThat(Problem1.follow("R2, R2, R2").blocksFromStart()).isEqualTo(2);
  }

  @Test
  public void fourRights() {
    assertThat(Problem1.follow("R2, R2, R2, R2").blocksFromStart()).isEqualTo(0);
  }

  @Test
  public void right5left5right5right3() {
    assertThat(Problem1.follow("R5, L5, R5, R3").blocksFromStart()).isEqualTo(12);
  }
}
