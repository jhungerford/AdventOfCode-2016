package dev.adventofcode2016;

import org.junit.Test;

import static dev.adventofcode2016.Problem11.MoveableType.GENERATOR;
import static dev.adventofcode2016.Problem11.MoveableType.MICROCHIP;
import static org.assertj.core.api.Assertions.assertThat;

public class Problem11Test {

  @Test
  public void renderBuilding() {
    Problem11.Building building = new Problem11.Building(
        new Problem11.Elevator(1),
        new Problem11.Moveable('H', 2, GENERATOR),
        new Problem11.Moveable('H', 1, MICROCHIP),
        new Problem11.Moveable('L', 3, GENERATOR),
        new Problem11.Moveable('L', 1, MICROCHIP)
    );

    assertThat(building.render()).containsExactly(
        "F4 .  .  .  .  .  ",
        "F3 .  .  .  LG .  ",
        "F2 .  HG .  .  .  ",
        "F1 E  .  HM .  LM "
    );
  }
}
