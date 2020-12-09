use day25::Computer;
use day25::Reg::A;

/// Returns the lowest positive integer that can be used to initialize register a and cause
/// the code to output a clock signal of 0, 1, 0, 1, ... forever.
fn lowest_signal(comp: &Computer) -> i32 {
    for i in 0..1000 {
        let mut c = comp.clone();

        c.set(&A, i);

        let still_running = c.run_until_times(28, 100);

        if still_running && output_is_valid(&c.output) {
            return i;
        }
    }

    panic!("Range is too low.")
}

/// Returns whether the given output is 0, 1, 0, 1, ...
fn output_is_valid(output: &Vec<i32>) -> bool {
    (0..output.len()).step_by(2).all(|i| output[i] == 0)
        && (1..output.len()).step_by(2).all(|i| output[i] == 1)
}

fn main() {
    let mut comp = Computer::load("input.txt");

    println!("Part 1: {}", lowest_signal(&comp));
}
