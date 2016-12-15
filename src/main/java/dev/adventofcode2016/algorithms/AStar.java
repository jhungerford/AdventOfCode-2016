package dev.adventofcode2016.algorithms;

import com.google.common.collect.ImmutableList;

import java.util.*;

/**
 * A* searching algorithm, for finding shortest paths efficiently.  POSITION
 * should implement equals() and hashCode().
 *
 * https://en.wikipedia.org/wiki/A*_search_algorithm
 */
public interface AStar<POSITION> {

  /**
   * Calculates the shortest path between the starting and ending position.
   *
   * @param start Starting position
   * @param end Ending position
   * @return Shortest path between the two positions
   */
  default ImmutableList<POSITION> shortestPath(POSITION start, POSITION end) {
    Set<POSITION> visited = new HashSet<>(); // Set of points that have already been visited
    Set<POSITION> open = new HashSet<>(); // Set of points to be evaluated
    Map<POSITION, POSITION> from = new HashMap<>(); // Map of point to the point it can most efficiently be reached from

    Map<POSITION, Integer> cost = new HashMap<>(); // Map of point to the cost of getting to it from the start node
    // Map of point to the total cost of getting from the start
    // to the end passing through the node.  Part known, part heuristic.
    Map<POSITION, Integer> score = new HashMap<>();

    open.add(start);
    cost.put(start, 0); // First one is always free.
    score.put(start, heuristicCost(start, end));

    while (!open.isEmpty()) {
      // Current is the node in open with the lowest score.
      POSITION current = open.stream()
          .sorted(Comparator.comparing(score::get)) // Compare the score for each point
          .findFirst()
          .orElseThrow(() -> new IllegalStateException("Open set is empty."));

      if (current.equals(end)) {
        ImmutableList.Builder<POSITION> path = ImmutableList.builder();
        path.add(current);

        POSITION position = current;
        while (!position.equals(start)) {
          position = from.get(position);
          path.add(position);
        }

        return path.build().reverse();
      }

      open.remove(current);
      visited.add(current);

      for (POSITION neighbor : neighbors(current)) {
        if (visited.contains(neighbor)) {
          continue; // Already visited.
        }

        int neighborScore = cost.get(current) + 1; // TODO: expose actual step distance function.

        if (!open.contains(neighbor)) {
          open.add(neighbor);
        } else if (neighborScore >= cost.get(neighbor)) {
          continue; // Not a better path.
        }

        from.put(neighbor, current);
        cost.put(neighbor, neighborScore);
        score.put(neighbor, neighborScore + heuristicCost(neighbor, end));
      }
    }

    throw new IllegalStateException("No path from " + start + " to " + end);
  }

  /**
   * Calculates a naieve cost of getting from the 'from' position to the 'to' position.
   * The cost should be less than the actual cost of traveling between the two positions so
   * A* will converge.  In the classic shortest flights example in Russel/Norvig, crow-flight
   * distance between airports is a good heuristic.
   *
   * @param from Starting position
   * @param to   Ending position
   * @return Cost of traveling between the two positions
   */
  int heuristicCost(POSITION from, POSITION to);

  /**
   * Returns a list of valid neighbors from the given position.  shortestPath
   * will filter positions that have already been visited, so neighbors
   * should return all valid neighbors, even if they've already been visited.
   *
   * @param position Position to calculate neighbors for
   * @return List of valid neighbors of the given position.
   */
  ImmutableList<POSITION> neighbors(POSITION position);
}
