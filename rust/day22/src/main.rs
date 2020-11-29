use day22::Grid;
use day22::SlidingPuzzle;

fn main() {
    let grid = Grid::load("input.txt").unwrap();
    let puzzle = SlidingPuzzle::from_grid(&grid);

    println!("Part 1: {}", grid.viable_pairs().len());
    println!("Part 2: {}", puzzle.steps().unwrap_or_default());

}
