# propositional-logic-resolver
Implemented a resolution theorem prover for propositional logic in Java, constructing expression trees to parse and manipulate well-formed formulas and applying linear resolution to verify proofs.

## Overview
This repository contains the implementation of a resolution theorem prover for propositional logic as part of the COSC-3450: Artificial Intelligence course. The project involves reading, parsing, and internally storing propositional well-formed formulas (wffs) as expression trees and then applying resolution to prove logical entailment.

## Features
- **Expression Tree Parsing:** Converts propositional logic statements into a tree structure for easier manipulation.
- **Resolution Proving:** Implements the resolution method to determine the validity of logical propositions.

## Project Structure
- `Formula.java`: Handles the parsing and storage of propositional formulas.
- `Tokenizer.java`: Utility class for tokenizing strings into logical components.
- `Clause.java`, `Clauses.java`: Classes for handling the logical clauses used in the resolution process.
- `Main.java`: Entry point of the application that manages command line inputs and outputs the results of the resolution process.
