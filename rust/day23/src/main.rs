use day23::Computer;
use day23::Reg::A;
use day23::RegisterOrValue::Register;

fn main() {
    let mut part1_comp = Computer::load("input.txt");
    let mut part2_comp = Computer::load("input.txt");

    part1_comp.set(&A, 7);
    part1_comp.run();

    println!("Part 1: {}", part1_comp.get(&Register(A)));

    part2_comp.set(&A, 12);
    part2_comp.run();

    println!("Part 2: {}", part2_comp.get(&Register(A)));
}
