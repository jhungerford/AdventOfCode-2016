#[macro_use] extern crate lazy_static;
extern crate regex;

use std::cmp::Ordering;
use std::collections::{BinaryHeap, HashMap, HashSet};
use std::fs::File;
use std::io;
use std::io::{BufRead, BufReader};
use std::str::FromStr;
use std::usize;

use itertools::Itertools;
use regex::Regex;

#[derive(Debug, Eq, PartialEq, Hash, Clone)]
pub struct Grid {
    nodes: Vec<Node>
}

impl Grid {
    /// Loads a Grid from a file.
    pub fn load(filename: &str) -> io::Result<Grid> {
        let f = File::open(filename)?;
        let f = BufReader::new(f);

        let nodes = f.lines()
            .flat_map(|line| Node::parse(line.unwrap().as_str()))
            .collect_vec();

        Ok(Grid { nodes })
    }

    /// Returns the viable pairs of nodes in this current grid.  A viable pair of nodes
    /// is any two nodes (A,B), regardless of whether they are directly connected, such that:
    /// * Node A is not empty (used is non-zero)
    /// * Nodes A and B are not the same node
    /// * Data on node A (used) would fit on node B (avail)
    pub fn viable_pairs(&self) -> Vec<(&Node, &Node)> {
        self.nodes.iter().permutations(2)
            .filter(|c| c[0].used > 0 && c[0].used <= c[1].avail)
            .map(|c| (c[0], c[1]))
            .collect_vec()
    }

    /// Returns the number of steps required to move the data in x=max, y=0 to x=0, y=0.
    /// Data can only be moved to an adjacent node with enough available space to hold it.
    /// In the input data and example, data can only fit on the empty node.
    pub fn steps(self) -> Option<usize> {
        // Map of position -> shortest number of steps to that position.
        let mut pos_steps = HashMap::new();
        // Heap: priority queue of positions to visit, sorted by cost.
        let mut to_visit = BinaryHeap::new();

        let grid: Grid = Grid { nodes: self.nodes.clone() };

        let goal_pos = Position { x: 0, y: 0 };
        let start_pos = Position { x: self.max_x(), y: 0 };
        let start_state = StepsState { pos: start_pos, grid };
        let start_steps = Steps { steps: 0, state: start_state.clone() };

        to_visit.push(start_steps);
        pos_steps.insert(start_state, 0);

        while let Some(Steps { steps, state }) = to_visit.pop() {
            // Once we find the goal, we're done.
            if state.pos == goal_pos {
                return Some(steps);
            }

            // Ignore positions that we've already found a shorter path to.
            if steps > *pos_steps.get(&state).unwrap_or(&usize::MAX) {
                continue;
            }

            // Explore states that we've found a shorter path to or haven't visited.
            for next_state in state.next_states() {
                let next_steps = steps + 1;
                let next = Steps { steps: next_steps, state: next_state.clone() };

                if next_steps < *pos_steps.get(&next.state).unwrap_or(&usize::MAX) {
                    to_visit.push(next);
                    pos_steps.insert(next_state, next_steps);
                }
            }
        };

        None
    }

    pub fn print(&self) {
        let mut viable = HashSet::new();

        for (a, b) in self.viable_pairs() {
            viable.insert(a.pos);
            viable.insert(b.pos);
        }

        let empty = self.empty_node();

        for y in 0..self.max_y() {
            for x in 0..self.max_x() {
                let pos = Position { x, y };
                if pos == empty.pos {
                    print!("_ ");
                } else if viable.contains(&pos) {
                    print!(". ");
                } else {
                    print!("# ");
                }
            }
            println!("");
        }
    }

    /// Returns the highest x value in this grid's nodes.
    fn max_x(&self) -> i32 {
        self.nodes.iter()
            .map(|n| n.pos.x)
            .max()
            .unwrap_or_default()
    }

    /// Returns the highest y value in this grid's nodes.
    fn max_y(&self) -> i32 {
        self.nodes.iter()
            .map(|n| n.pos.y)
            .max()
            .unwrap_or_default()
    }

    /// Returns the node at the given position if it's inside this grid, or empty.
    fn node_at(&self, pos: Position) -> Option<&Node> {
        self.nodes.iter()
            .find(|node| node.pos == pos)
    }

    /// Returns the empty node in this grid.
    fn empty_node(&self) -> &Node {
        self.nodes.iter().find(|n| n.used == 0).unwrap()
    }

    /// Returns the grid formed by moving data from a node to a node.
    fn move_node(&self, from: &Node, to: &Node) -> Grid {
        Grid {
            nodes: self.nodes.iter()
                .map(|node| if node == from {
                    Node {
                        pos: from.pos,
                        size: from.size,
                        used: 0,
                        avail: from.size,
                    }
                } else if node == to {
                    let used = to.used + from.used;
                    Node {
                        pos: to.pos,
                        size: to.size,
                        used,
                        avail: to.size - used,
                    }
                } else {
                    node.clone()
                })
                .collect_vec()
        }
    }
}

#[derive(Debug, Eq, PartialEq)]
struct Steps {
    steps: usize,
    state: StepsState,
}

impl Ord for Steps {
    fn cmp(&self, other: &Self) -> Ordering {
        self.steps.cmp(&other.steps).reverse()
            .then_with(|| self.state.cmp(&other.state))
    }
}

impl PartialOrd for Steps {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        return Some(self.cmp(other))
    }
}

#[derive(Debug, Eq, PartialEq, Hash, Clone)]
struct StepsState {
    pos: Position,
    grid: Grid,
}

impl StepsState {
    /// Returns a list of states that are reachable with one move on this grid.
    fn next_states(&self) -> Vec<StepsState> {
        // Data can only fit on the empty node - find it and return the states that can fit.
        let empty = self.grid.empty_node();

        let around = vec![
            Position { x: -1, y: 0 },
            Position { x: 1, y: 0 },
            Position { x: 0, y: -1 },
            Position { x: 0, y: 1 },
        ];

        around.iter()
            .flat_map(|dist| self.grid.node_at(Position {
                x: empty.pos.x + dist.x,
                y: empty.pos.y + dist.y
            }))
            .filter(|node| node.used <= empty.avail)
            .map(|node| StepsState {
                pos: if node.pos == self.pos { empty.pos } else { self.pos },
                grid: self.grid.move_node(node, empty),
            })
            .collect_vec()
    }
}

impl Ord for StepsState {
    fn cmp(&self, other: &Self) -> Ordering {
        self.pos.cmp(&other.pos)
    }
}

impl PartialOrd for StepsState {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some(self.cmp(other))
    }
}

pub struct SlidingPuzzle {
    width: i32,
    height: i32,
    empty: Position,
    walls: HashSet<Position>,
}

impl SlidingPuzzle {
    /// Day 22 is a sliding puzzle with walls - from_grid turns a Grid into a SlidingPuzzle
    pub fn from_grid(grid: &Grid) -> SlidingPuzzle {

        let mut viable = HashSet::new();

        for (a, b) in grid.viable_pairs() {
            viable.insert(a.pos);
            viable.insert(b.pos);
        }

        let width = grid.max_x() + 1;
        let height = grid.max_y() + 1;
        let empty = grid.empty_node();

        let mut walls = HashSet::new();

        for y in 0..height {
            for x in 0..width {
                let pos = Position { x, y };
                if !viable.contains(&pos) {
                    walls.insert(pos);
                }
            }
        }

        SlidingPuzzle { width, height, empty: empty.pos, walls }
    }

    /// Returns the number of steps required to move the data in x=max, y=0 to x=0, y=0.
    /// Data can only be moved by sliding the empty square to an adjacent node.
    pub fn steps(&self) -> Option<usize> {
        // Map of empty position -> shortest number of steps to that position.
        let mut state_steps = HashMap::new();

        // Empty position, target position.
        let mut to_visit = BinaryHeap::new();

        let goal = Position { x: 0, y: 0 };
        let start_state = SlidingPuzzleState {
            empty: self.empty,
            target: Position { x: self.width - 1, y: 0 },
        };
        let start_steps = SlidingPuzzleSteps { steps: 0, state: start_state.clone() };

        to_visit.push(start_steps);
        state_steps.insert(start_state, 0);

        while let Some(SlidingPuzzleSteps {steps, state}) = to_visit.pop() {
            // Once we've found the goal, we're done.
            if state.target == goal {
                return Some(steps);
            }

            // Ignore positions that we've already found a shorter path to.
            if steps > *state_steps.get(&state).unwrap_or(&usize::MAX) {
                continue;
            }

            // Explore states that we haven't visited or have found a shorter path to.
            for next_state in state.next_states(&self) {
                let next_steps = steps + 1;
                let next = SlidingPuzzleSteps { steps: next_steps, state: next_state.clone() };

                if next_steps < *state_steps.get(&next.state).unwrap_or(&usize::MAX) {
                    to_visit.push(next);
                    state_steps.insert(next_state, next_steps);
                }
            }
        }

        None
    }
}

#[derive(Debug, Eq, PartialEq)]
struct SlidingPuzzleSteps {
    steps: usize,
    state: SlidingPuzzleState,
}

impl Ord for SlidingPuzzleSteps {
    fn cmp(&self, other: &Self) -> Ordering {
        self.steps.cmp(&other.steps).reverse()
            .then_with(|| self.state.cmp(&other.state))
    }
}

impl PartialOrd for SlidingPuzzleSteps {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        return Some(self.cmp(other))
    }
}

#[derive(Debug, Eq, PartialEq, Hash, Clone)]
struct SlidingPuzzleState {
    empty: Position,
    target: Position,
}

impl SlidingPuzzleState {
    fn next_states(&self, puzzle: &SlidingPuzzle) -> Vec<SlidingPuzzleState> {
        let around = vec![
            Position { x: -1, y: 0 },
            Position { x: 1, y: 0 },
            Position { x: 0, y: -1 },
            Position { x: 0, y: 1 },
        ];

        around.iter()
            .map(|dist| Position {
                x: self.empty.x + dist.x,
                y: self.empty.y + dist.y,
            })
            .filter(|pos| pos.x >= 0 && pos.y >= 0 && pos.x < puzzle.width && pos.y < puzzle.height)
            .filter(|pos| !puzzle.walls.contains(pos))
            .map(|pos| SlidingPuzzleState {
                empty: pos,
                target: if pos == self.target { self.empty } else { self.target },
            })
            .collect_vec()
    }
}

impl Ord for SlidingPuzzleState {
    fn cmp(&self, other: &Self) -> Ordering {
        self.empty.cmp(&other.empty).then_with(|| self.target.cmp(&other.target))
    }
}

impl PartialOrd for SlidingPuzzleState {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some(self.cmp(other))
    }
}

#[derive(Debug, Eq, PartialEq, Copy, Clone, Hash, Ord, PartialOrd)]
pub struct Position {
    x: i32,
    y: i32,
}

impl Position {
    /// Returns the Manhattan distance between this position and the other position.
    fn distance(&self, other: &Position) -> i32 {
        i32::abs(self.x as i32 - other.x as i32) + i32::abs(self.y as i32 - other.y as i32)
    }
}

#[derive(Debug, Eq, PartialEq, Copy, Clone, Hash)]
pub struct Node {
    pos: Position,
    size: i32,
    used: i32,
    avail: i32,
}

impl Node {
    fn new(x: i32, y: i32, size: i32, used: i32, avail: i32) -> Node {
        Node { pos: Position { x, y }, size, used, avail }
    }

    fn parse(line: &str) -> Option<Node> {
        // Filesystem            Size  Used  Avail  Use%
        // /dev/grid/node-x0-y0   10T    8T     2T   80%
        lazy_static! {
            static ref LINE_RE: Regex = Regex::new(
            r"^/dev/grid/node-x(\d+)-y(\d+)\s+(\d+)T\s+(\d+)T\s+(\d+)T\s+(\d+)%$"
            ).unwrap();
        }

        LINE_RE.captures(line).map(|captures| Node {
            pos: Position {
                x: i32::from_str(&captures[1]).unwrap(),
                y: i32::from_str(&captures[2]).unwrap(),
            },
            size: i32::from_str(&captures[3]).unwrap(),
            used: i32::from_str(&captures[4]).unwrap(),
            avail: i32::from_str(&captures[5]).unwrap(),
        })
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn load_grid() {
        let grid = Grid::load("example.txt").unwrap();

        let expected_grid = Grid {
            nodes: vec![
                Node::new(0, 0, 10,  8, 2),
                Node::new(0, 1, 11,  6, 5),
                Node::new(0, 2, 32, 28, 4),
                Node::new(1, 0,  9,  7, 2),
                Node::new(1, 1,  8,  0, 8),
                Node::new(1, 2, 11,  7, 4),
                Node::new(2, 0, 10,  6, 4),
                Node::new(2, 1,  9,  8, 1),
                Node::new(2, 2,  9,  6, 3),
            ]
        };

        assert_eq!(grid, expected_grid);
    }

    #[test]
    fn grid_viable_pairs() {
        // x, y, size, used, avail
        let zero_zero = Node::new(0, 0, 10, 2, 8);
        let zero_one = Node::new(0, 1, 5, 0, 5);
        let one_zero = Node::new(1, 0, 5, 1, 4);
        let one_one = Node::new(1, 1, 8, 6, 2);

        let grid = Grid {
            nodes: vec![zero_zero.clone(), zero_one, one_zero, one_one]
        };

        assert_eq!(grid.viable_pairs(), vec![
            (&zero_zero, &zero_one),
            (&zero_zero, &one_zero),
            (&zero_zero, &one_one),

            (&one_zero, &zero_zero),
            (&one_zero, &zero_one),
            (&one_zero, &one_one),

            (&one_one, &zero_zero),
        ]);
    }

    #[test]
    fn grid_steps() {
        let grid = Grid::load("example.txt").unwrap();
        let puzzle = SlidingPuzzle::from_grid(&grid);

        assert_eq!(puzzle.walls.len(), 1);

        assert_eq!(grid.steps(), Some(7));
        assert_eq!(puzzle.steps(), Some(7));
    }

    #[test]
    fn parse_line() {
        assert_eq!(Node::parse("root@ebhq-gridcenter# df -h"), None);
        assert_eq!(Node::parse("Filesystem              Size  Used  Avail  Use%"), None);

        assert_eq!(
            Node::parse("/dev/grid/node-x0-y0     94T   72T    22T   76%"),
            Some(Node {
                pos: Position {
                    x: 0,
                    y: 0,
                },
                size: 94,
                used: 72,
                avail: 22,
            }));

        assert_eq!(
            Node::parse("/dev/grid/node-x1-y22    87T   67T    20T   77%"),
            Some(Node {
                pos: Position {
                    x: 1,
                    y: 22,
                },
                size: 87,
                used: 67,
                avail: 20,
            }));

        assert_eq!(Node::parse(""), None);
    }

    #[test]
    fn position_distance() {
        let zero = &Position {x: 0, y: 0};
        let twos = &Position {x: 2, y: 2};
        let three_one = &Position {x: 3, y: 1};

        assert_eq!(zero.distance(zero), 0);

        assert_eq!(zero.distance(twos), 4);
        assert_eq!(twos.distance(zero), 4);

        assert_eq!(zero.distance(three_one), 4);
        assert_eq!(three_one.distance(zero), 4);

        assert_eq!(twos.distance(three_one), 2);
        assert_eq!(three_one.distance(twos), 2);
    }
}