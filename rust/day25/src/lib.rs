use std::convert::Infallible;
use std::error::Error;
use std::fs::File;
use std::io::{BufRead, BufReader};
use std::str::FromStr;

use crate::Instruction::{Cpy, Inc, Dec, Skip, Out};
use RegisterOrValue::{Register, Value};
use Reg::{A, B, C, D};
use Instruction::{Jnz, Tgl};
use core::fmt;
use std::fmt::Formatter;

#[derive(Debug)]
pub struct ParseError {}

#[derive(Eq, PartialEq, Debug, Copy, Clone)]
pub enum Reg {
    A, B, C, D,
}

impl Reg {
    fn ordinal(&self) -> usize {
        match self {
            A => 0,
            B => 1,
            C => 2,
            D => 3,
        }
    }
}

impl FromStr for Reg {
    type Err = ParseError;

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s {
            "a" => Ok(A),
            "b" => Ok(B),
            "c" => Ok(C),
            "d" => Ok(D),
            _ => Err(ParseError {})
        }
    }
}

#[cfg(test)]
mod register_tests {
    use super::*;

    #[test]
    fn parse() {
        assert_eq!("a".parse::<Reg>().unwrap(), A);
    }

    #[test]
    fn ordinal() {
        assert_eq!(A.ordinal(), 0);
    }
}

#[derive(Eq, PartialEq, Debug, Copy, Clone)]
pub enum RegisterOrValue {
    Register(Reg),
    Value(i32),
}

impl FromStr for RegisterOrValue {
    type Err = ParseError;

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s.parse::<Reg>() {
            Ok(r) => Ok(Register(r)),
            Err(_) => match s.parse::<i32>() {
                Ok(v) => Ok(Value(v)),
                Err(_) => Err(ParseError {}),
            }
        }
    }
}

#[cfg(test)]
mod register_or_value_tests {
    use super::*;
    use RegisterOrValue::{Register, Value};

    #[test]
    fn parse() {
        assert_eq!("a".parse::<RegisterOrValue>().unwrap(), Register(A));
        assert_eq!("d".parse::<RegisterOrValue>().unwrap(), Register(D));
        assert_eq!("20".parse::<RegisterOrValue>().unwrap(), Value(20));

        assert!("invalid".parse::<RegisterOrValue>().is_err());
    }
}

#[derive(Eq, PartialEq, Debug, Copy, Clone)]
enum Instruction {
    /// cpy x y copies x (either an integer or the value of a register) into register y
    Cpy {x: RegisterOrValue, y: Reg },
    /// inc x increases the value of register x by one.
    Inc {x: Reg },
    /// dec x decreases the value of register x by one.
    Dec {x: Reg },
    /// jnz x y jumps to an instruction y away (positive means forward; negative means backward), but only if x is not zero.
    Jnz {x: RegisterOrValue, y: RegisterOrValue },
    /// tgl x toggles the instruction x away (pointing at instructions like jnz does: positive means forward; negative means backward):
    ///
    /// * For one-argument instructions, inc becomes dec, and all other one-argument instructions become inc.
    /// * For two-argument instructions, jnz becomes cpy, and all other two-instructions become jnz.
    /// * The arguments of a toggled instruction are not affected.
    /// * If an attempt is made to toggle an instruction outside the program, nothing happens.
    /// * If toggling produces an invalid instruction (like cpy 1 2) and an attempt is later made to execute that instruction, skip it instead.
    /// * If tgl toggles itself (for example, if a is 0, tgl a would target itself and become inc a), the resulting instruction is not executed until the next time it is reached.
    Tgl {x: RegisterOrValue },
    /// Skip is a no-op, and comes from toggling to an invalid instruction.
    Skip,
    /// out transmits x as the next value for the clock signal
    Out {x: RegisterOrValue},
}

impl Instruction {
    /// Runs this instruction, returning whether the computer should keep running.
    fn run(&self, computer: &mut Computer) -> bool {
        match self {
            Cpy { x, y } => {
                computer.set(y, computer.get(x));
                computer.pc += 1;
            }

            Inc { x } => {
                computer.registers[x.ordinal()] += 1;
                computer.pc += 1;
            }

            Dec { x } => {
                computer.registers[x.ordinal()] -= 1;
                computer.pc += 1;
            }

            Jnz { x, y } => {
                if computer.get(x) != 0 {
                    let new_pc = computer.get(y) + computer.pc as i32;
                    if new_pc < 0 {
                        computer.pc = computer.instructions.len();
                    } else {
                        computer.pc = new_pc as usize;
                    }
                } else {
                    computer.pc += 1;
                }
            }

            Tgl { x } => {
                let index = computer.pc as i32 + computer.get(x);

                if index >= 0 && index < computer.instructions.len() as i32 {
                    let index = index as usize;

                    let new_instruction = match computer.instructions[index] {
                        Cpy { x, y } => Jnz { x, y: Register(y) },
                        Inc { x } => Dec { x },
                        Dec { x } => Inc { x },
                        Jnz { x, y: Register(y) } => Cpy { x, y },
                        Jnz { x: _, y: Value(_) } => Skip,
                        Tgl { x: Register(x) } => Inc { x },
                        Tgl { x: Value(x) } => Skip,
                        Skip => Skip,
                        Out { x: Register(x) } => Inc { x },
                        Out { x: Value(_) } => Skip,
                    };

                    computer.instructions[index] = new_instruction;
                }

                computer.pc += 1;
            }

            Skip => computer.pc += 1,

            Out { x } => {
                computer.output.push(computer.get(x));
                computer.pc += 1;
            }
        };

        computer.pc < computer.instructions.len()
    }

    fn toggle(&self) -> Instruction {
        unimplemented!()
    }
}

impl FromStr for Instruction {
    type Err = ParseError;

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        let parts: Vec<&str> = s.split(" ").collect();

        match parts[0] {
            "cpy" => Ok(Cpy { x: parts[1].parse().unwrap(), y: parts[2].parse().unwrap() }),
            "inc" => Ok(Inc { x: parts[1].parse().unwrap() }),
            "dec" => Ok(Dec { x: parts[1].parse().unwrap() }),
            "jnz" => Ok(Jnz { x: parts[1].parse().unwrap(), y: parts[2].parse().unwrap() }),
            "tgl" => Ok(Tgl { x: parts[1].parse().unwrap() }),
            "out" => Ok(Out { x: parts[1].parse().unwrap() }),

            _ => Err(ParseError{}),
        }
    }
}

#[cfg(test)]
mod instruction_tests {
    use super::*;

    #[test]
    fn parse() {
        assert_eq!("cpy a b".parse::<Instruction>().unwrap(),
                   Cpy {x: Register(A), y: B});
        assert_eq!("inc d".parse::<Instruction>().unwrap(), Inc {x: D});
        assert_eq!("dec c".parse::<Instruction>().unwrap(), Dec {x: C});
        assert_eq!("jnz -1 b".parse::<Instruction>().unwrap(),
                   Jnz {x: Value(-1), y: Register(B)});
        assert_eq!("tgl 2".parse::<Instruction>().unwrap(), Tgl { x: Value(2) });
        assert_eq!("out a".parse::<Instruction>().unwrap(), Out { x: Register(A) });
    }

    #[test]
    fn run() {
        let mut comp = Computer::new(vec![
            "cpy 10 a".parse::<Instruction>().unwrap(),
            "inc a".parse::<Instruction>().unwrap(),
            "dec a".parse::<Instruction>().unwrap(),
            "jnz a b".parse::<Instruction>().unwrap(),
            "jnz 0 -1".parse::<Instruction>().unwrap(),
            "tgl -5".parse::<Instruction>().unwrap(),
            "tgl -5".parse::<Instruction>().unwrap(),
            "tgl -5".parse::<Instruction>().unwrap(),
            "tgl -5".parse::<Instruction>().unwrap(),
            "out a".parse::<Instruction>().unwrap(),
            "tgl -1".parse::<Instruction>().unwrap(),
        ]);

        // cpy 10 a
        comp.instructions[0].clone().run(&mut comp);
        assert_eq!([10, 0, 0, 0], comp.registers);
        assert_eq!(1, comp.pc);

        // inc a
        comp.instructions[1].clone().run(&mut comp);
        assert_eq!([11, 0, 0, 0], comp.registers);
        assert_eq!(2, comp.pc);

        // dec a
        comp.instructions[2].clone().run(&mut comp);
        assert_eq!([10, 0, 0, 0], comp.registers);
        assert_eq!(3, comp.pc);

        // jnz a b - a is non-zero and b is 0, so the pc doesn't move.
        comp.instructions[3].clone().run(&mut comp);
        assert_eq!([10, 0, 0, 0], comp.registers);
        assert_eq!(3, comp.pc);

        // jnz 0 -1 - false
        comp.pc = 4;
        comp.instructions[4].clone().run(&mut comp);
        assert_eq!([10, 0, 0, 0], comp.registers);
        assert_eq!(5, comp.pc);

        // tgl -5 (cpy 10 a -> jnz 10 a)
        comp.instructions[5].clone().run(&mut comp);
        assert_eq!([10, 0, 0, 0], comp.registers);
        assert_eq!(6, comp.pc);
        assert_eq!("jnz 10 a".parse::<Instruction>().unwrap(), comp.instructions[0]);

        // tgl -5 (inc a -> dec a)
        comp.instructions[6].clone().run(&mut comp);
        assert_eq!([10, 0, 0, 0], comp.registers);
        assert_eq!(7, comp.pc);
        assert_eq!("dec a".parse::<Instruction>().unwrap(), comp.instructions[1]);

        // tgl -5 (dec a -> inc a)
        comp.instructions[7].clone().run(&mut comp);
        assert_eq!([10, 0, 0, 0], comp.registers);
        assert_eq!(8, comp.pc);
        assert_eq!("inc a".parse::<Instruction>().unwrap(), comp.instructions[2]);

        // tgl -5 (jnz a 2 -> cpy a 2)
        comp.instructions[8].clone().run(&mut comp);
        assert_eq!([10, 0, 0, 0], comp.registers);
        assert_eq!(9, comp.pc);
        assert_eq!("cpy a b".parse::<Instruction>().unwrap(), comp.instructions[3]);

        // out a
        comp.instructions[9].clone().run(&mut comp);
        assert_eq!([10, 0, 0, 0], comp.registers);
        assert_eq!(vec![10], comp.output);
        assert_eq!(10, comp.pc);

        // tgl -1 (inc a)
        comp.instructions[10].clone().run(&mut comp);
        assert_eq!([10, 0, 0, 0], comp.registers);
        assert_eq!(11, comp.pc);
        assert_eq!("inc a".parse::<Instruction>().unwrap(), comp.instructions[9]);
    }
}

#[derive(Eq, PartialEq, Debug, Clone)]
pub struct Computer {
    pc: usize,
    registers: [i32; 4],
    instructions: Vec<Instruction>,
    pub output: Vec<i32>,
}

impl fmt::Display for Computer {
    fn fmt(&self, f: &mut Formatter<'_>) -> fmt::Result {
        write!(f, "pc: {}, [a: {}, b: {}, c: {}, d: {}], output: {:?}",
               self.pc, self.registers[0], self.registers[1], self.registers[2], self.registers[3],
               self.output)
    }
}

impl Computer {
    fn new(instructions: Vec<Instruction>) -> Computer {
        Computer {
            pc: 0,
            registers: [0; 4],
            instructions,
            output: Vec::new(),
        }
    }

    pub fn load(filename: &str) -> Computer {
        let f = File::open(filename).unwrap();
        let f = BufReader::new(f);

        let instructions = f.lines()
            .map(|line| line.unwrap().as_str().parse().unwrap())
            .collect();

        Computer::new(instructions)
    }

    /// Returns the value of the register or value.
    pub fn get(&self, r_or_v: &RegisterOrValue) -> i32 {
        match r_or_v {
            Register(r) => self.registers[r.ordinal()],
            Value(value) => *value,
        }
    }

    /// Sets the value of a register.
    pub fn set(&mut self, r: &Reg, value: i32) {
        self.registers[r.ordinal()] = value;
    }

    /// Runs the instruction at the current program counter, returning whether the computer
    /// should keep running.
    fn step(&mut self) -> bool {
        let instruction = self.instructions[self.pc].clone();
        instruction.run(self)
    }

    /// Runs the program on this computer until it completes.
    pub fn run(&mut self) {
        while self.step() {}
    }

    /// Runs the computer up to the given number of steps, returning true if the computer
    /// can still run or false if it halted.
    pub fn run_steps(&mut self, steps: usize) -> bool {
        let mut step = 0;
        let mut running = true;

        while running && step < steps {
            running = self.step();
            step += 1;
        }

        running
    }

    /// Runs the computer until just before the given instruction would be executed.
    pub fn run_until(&mut self, instruction_num: usize) -> bool {
        let mut running = true;

        while running && self.pc != instruction_num {
            running = self.step();
        }

        running
    }

    /// Runs the computer until just before the given instruction would be executed for
    /// the nth time.
    pub fn run_until_times(&mut self, instruction_num: usize, n: usize) -> bool {
        let mut running = true;
        let mut count = 0;

        while running && (self.pc != instruction_num || count < n) {
            if self.pc == instruction_num {
                count += 1;
            }

            running = self.step();
        }

        running
    }
}

#[cfg(test)]
mod computer_tests {
    use super::*;

    #[test]
    fn load() {
        let comp = Computer::load("sample_no_tgl.txt");

        let expected_comp = Computer {
            pc: 0,
            registers: [0; 4],
            instructions: vec![
                Cpy {x: Value(41), y: A},
                Inc {x: A},
                Inc {x: A},
                Dec {x: A},
                Jnz {x: Register(A), y: Value(2)},
                Dec {x: A},
            ],
            output: Vec::new(),
        };

        assert_eq!(comp, expected_comp);
    }

    #[test]
    fn get() {
        let mut comp = Computer::new(vec![]);

        comp.registers = [1, 2, 3, 4];

        assert_eq!(comp.get(&Register(A)), 1);
        assert_eq!(comp.get(&Register(B)), 2);
        assert_eq!(comp.get(&Register(C)), 3);
        assert_eq!(comp.get(&Register(D)), 4);

        assert_eq!(comp.get(&Value(42)), 42);
    }

    #[test]
    fn set() {
        let mut comp = Computer::new(vec![]);

        comp.set(&A, 1);
        comp.set(&B, 2);
        comp.set(&C, 3);
        comp.set(&D, 4);

        assert_eq!(comp.registers, [1, 2, 3, 4]);
    }

    #[test]
    fn run_no_tgl() {
        let mut comp = Computer::load("sample_no_tgl.txt");
        comp.run();

        assert_eq!(comp.registers[0], 42);
    }

    #[test]
    fn run_tgl() {
        let mut comp = Computer::load("sample.txt");
        comp.run();

        assert_eq!(comp.registers[0], 3);
    }
}
