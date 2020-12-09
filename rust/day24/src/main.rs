use std::cmp::Ordering;
use std::collections::{BinaryHeap, HashMap, HashSet};
use std::fs::File;
use std::io::{BufRead, BufReader};
use std::str::FromStr;
use std::usize;

use crate::Square::{Empty, Location, Wall};

#[derive(Debug, Eq, PartialEq)]
struct ParseErr {}

#[derive(Debug, Eq, PartialEq)]
enum Square {
    Wall, Empty, Location(usize),
}

impl FromStr for Square {
    type Err = ParseErr;

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s {
            "#" => Ok(Wall),
            "." => Ok(Empty),
            num => Ok(Location(num.parse::<usize>().unwrap())),
        }
    }
}

#[cfg(test)]
mod square_tests {
    use super::*;

    #[test]
    fn parse() {
        assert_eq!(Ok(Wall), "#".parse());
        assert_eq!(Ok(Empty), ".".parse());
        assert_eq!(Ok(Location(0)), "0".parse());
    }
}

#[derive(Debug, Eq, PartialEq, Copy, Clone, Hash, Ord, PartialOrd)]
struct Position {
    x: usize,
    y: usize,
}

impl Position {
    fn plus(&self, grid: &Grid, x: i32, y: i32) -> Option<Position> {
        let new_x = self.x as i32 + x;
        let new_y = self.y as i32 + y;

        if new_x < 0 || new_y < 0 {
            return None;
        }

        let new_x = new_x as usize;
        let new_y = new_y as usize;

        if new_x >= grid.squares[0].len() || new_y >= grid.squares.len() {
            return None;
        }

        Some(Position { x: new_x, y: new_y })
    }
}

struct Grid {
    squares: Vec<Vec<Square>>,
    locations: Vec<Position>,
}

impl Grid {
    /// Loads a grid from the given file.
    fn load(filename: &str) -> Grid {
        let f = File::open(filename).unwrap();
        let f = BufReader::new(f);

        let squares: Vec<Vec<Square>> = f.lines().map(|line_result| {
            let line = line_result.unwrap();
            (0..line.len()).map(|i| line[i..i+1].parse::<Square>().unwrap()).collect()
        }).collect();

        let mut locations = Vec::new();

        for y in 0..squares.len() {
            for x in 0..squares[y].len() {
                if let Location(num) = squares[y][x] {
                    locations.push((num, Position { x, y }))
                }
            }
        }

        locations.sort_by(|(a_num, a_pos), (b_num, b_pos)| a_num.cmp(b_num));

        let locations = locations.iter()
            .map(|(num, pos)| *pos)
            .collect();

        Grid { squares, locations }
    }

    /// Returns the shortest path from the given location that visits all of the locations
    /// and optionally returns to the starting position.
    fn shortest_path_from(&self, from: usize, back_to_start: bool) -> Option<usize> {
        let edges = self.edges();

        // State contains the current position, total cost so far, and locations visited
        // while walking a graph.
        #[derive(Debug, Clone, Eq, PartialEq)]
        struct State {
            cost: usize,
            pos: Pos,
        }

        #[derive(Debug, Clone, Eq, PartialEq, Hash)]
        struct Pos {
            position: usize,
            visited: Vec<usize>
        }

        impl Ord for State {
            fn cmp(&self, other: &Self) -> Ordering {
                other.cost.cmp(&self.cost).then_with(|| self.pos.position.cmp(&other.pos.position))
            }
        }

        impl PartialOrd for State {
            fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
                Some(self.cmp(other))
            }
        }

        let mut dist = HashMap::new();
        let mut to_visit = BinaryHeap::new();

        let goal: HashSet<usize> = (0..self.locations.len()).collect();

        let from_visited= vec![from];
        let from_pos = Pos { position: from, visited: from_visited };

        dist.insert(from_pos.clone(), 0);
        to_visit.push(State { cost: 0, pos: from_pos });

        while let Some(State { cost, pos }) = to_visit.pop() {
            // println!("position: {}, cost: {}, visited: {:?}, dist: {:?}, to_visit: {:?}",
            //          pos.position, cost, pos.visited, &dist, &to_visit);

            // Once we've visited all of the nodes, we're done.
            let visited: HashSet<usize> = pos.visited.iter().cloned().collect();

            if visited == goal && (!back_to_start || pos.position == from) {
                return Some(cost)
            }

            // Already found a shorter way to visit the same nodes and get to this position.

            if cost > *dist.get(&pos).unwrap_or(&usize::MAX) {
                continue;
            }

            // Explore the adjacent nodes
            let adjacent = edges.get(&pos.position).unwrap();
            for neighbor in adjacent.keys() {
                let mut next_visited = pos.visited.clone();
                next_visited.push(*neighbor);
                next_visited.sort();

                let next_pos = Pos {
                    position: *neighbor,
                    visited: next_visited,
                };

                let next_cost = cost + adjacent.get(neighbor).unwrap();

                let next = State {
                    cost: next_cost,
                    pos: next_pos.clone(),
                };

                if next.cost < *dist.get(&next_pos).unwrap_or(&usize::MAX) {
                    to_visit.push(next);
                    dist.insert(next_pos, next_cost);
                }
            }
        }

        None
    }

    /// Returns the shortest edge length between the given location and other locations that are
    /// directly reachable from the location without going through another location.
    fn lengths(&self, location: usize) -> HashMap<usize, usize> {

        #[derive(Debug, Eq, PartialEq, Copy, Clone, Hash)]
        struct PosDist {
            pos: Position,
            dist: usize,
        }

        impl Ord for PosDist {
            fn cmp(&self, other: &Self) -> Ordering {
                self.dist.cmp(&other.dist).reverse().then_with(|| self.pos.cmp(&other.pos))
            }
        }

        impl PartialOrd for PosDist {
            fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
                Some(self.cmp(other))
            }
        }

        let mut lengths = HashMap::new();
        let mut to_visit = BinaryHeap::new();
        let mut dists = HashMap::new();

        let start = self.locations[location];

        dists.insert(start, 0);
        to_visit.push(PosDist {pos: start, dist: 0});

        while let Some(PosDist { pos, dist }) = to_visit.pop() {
            // Ignore positions that we've already found a shorter path to.
            if dist > *dists.get(&pos).unwrap_or(&usize::MAX) {
                continue;
            }

            let square = &self.squares[pos.y][pos.x];

            // At the start and at empty nodes, explore neighbors.
            if square == &Empty || square == &Location(location) {
                // Explore positions we've found a shorter path to or haven't visited.
                let neighbors: Vec<Position> = vec![(-1, 0), (1, 0), (0, -1), (0, 1)].iter()
                    .flat_map(|(plus_x, plus_y)| pos.plus(self, *plus_x, *plus_y))
                    .filter(|p| self.squares[p.y][p.x] != Wall)
                    .collect();

                let neighbor_dist = dist + 1;

                for neighbor in neighbors {
                    if neighbor_dist < *dists.get(&neighbor).unwrap_or(&usize::MAX) {
                        to_visit.push(PosDist { pos: neighbor, dist: neighbor_dist });
                        dists.insert(neighbor, neighbor_dist);
                    }
                }
            } else if let Location(num) = square {
                // Found a connected location - record it's distance and stop.
                if dist < *lengths.get(num).unwrap_or(&usize::MAX) {
                    lengths.insert(*num, dist);
                }
            } else {
                panic!("Exploring a position that isn't a location or empty");
            }
        }

        lengths
    }

    /// Returns a map of edges to their length.
    fn edges(&self) -> HashMap<usize, HashMap<usize, usize>> {
        let mut edges = HashMap::new();

        for from in 0..self.locations.len() {
            edges.insert(from, self.lengths(from));
        }

        edges
    }
}

#[cfg(test)]
mod grid_tests {
    use super::*;

    #[test]
    fn load() {
        let grid = Grid::load("sample.txt");

        assert_eq!(5, grid.squares.len());
        assert_eq!(11, grid.squares[0].len());

        assert_eq!(vec![
            Position { x: 1, y: 1 },
            Position { x: 3, y: 1 },
            Position { x: 9, y: 1 },
            Position { x: 9, y: 3 },
            Position { x: 1, y: 3 },
        ], grid.locations);
    }

    #[test]
    fn shortest_path() {
        let grid = Grid::load("sample.txt");

        assert_eq!(Some(14), grid.shortest_path_from(0, false));
    }

    #[test]
    fn shortest_path_and_return() {
        let grid = Grid::load("sample.txt");

        assert_eq!(Some(20), grid.shortest_path_from(0, true));
    }

    #[test]
    fn lengths() {
        let grid = Grid::load("sample.txt");

        let zero_lengths = grid.lengths(0);
        let expected_zero_lengths: HashMap<usize, usize> = [(1, 2), (4, 2)].iter().cloned().collect();
        assert_eq!(expected_zero_lengths, zero_lengths);

        let one_lengths = grid.lengths(1);
        let expected_one_lengths: HashMap<usize, usize> = [(0, 2), (2, 6)].iter().cloned().collect();
        assert_eq!(expected_one_lengths, one_lengths);
    }
}

fn main() {
    let grid = Grid::load("input.txt");

    println!("Part 1: {}", grid.shortest_path_from(0, false).unwrap());
    println!("Part 2: {}", grid.shortest_path_from(0, true).unwrap());
}
