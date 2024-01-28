### Accumulated Featureset
##### Boolean:
* Get one or all possible solutions for a formula
* Simplify a formula (limited functionality)
* (De-) parenthesise a formula
##### CTL:
* Evaluate a CTL formula on a given model
##### Generate:
* Generate a boolean formula
* Generate a CTL formula
* Generate a Kripke Structure
* Generate a Finite Automaton
##### Encode:
* Encode a Kripke Structure into a boolean formula
##### Trace:
* Get a trace from a start state to a goal state
* Get the shortest trace
##### Finite Automata Algorithms:
* Select an algorithm to apply on a Finite Automata
* Code your own implementation and test it
---
### 1.6.2 Added 'always endable' option to
* It encodes end states at all steps
### 1.6.1 Added areReachable() to finite Automaton algorithms
* Checks if all 'isEncodingEnd' states are reachable
### 1.6.0 Restructured and Refactored the whole Project
* Classes are grouped by concept and not by function
* Adapted CTL Lexer, Parser, and Interpreter to other concepts
### 1.5.7 CTL Formula Generator
* Added CTL formula generator
### 1.5.6 Created Logger
* Contains static methods that log to `System.out` even when called in `Result`
* Added video on how to create and test your own algorithm to frontend
### 1.5.5 Created Algorithm validation and Report
* Report is saved in `/resources`
### 1.5.4 Created light/dark mode button for graph
* Clicking the button inverts the graph colors for all graphs
### 1.5.3 Added hints to buttons that refer you to tutorials
* Also uploaded corresponding video tutorials to youtube.com/@LogiVis
### 1.5.2 Initial publication improvements
* Made `.jar` buildable with one single command
* Replaced application entry points with a single one
  * What is run is now defined by program arguments
* Slightly adapted READMEs
### 1.5.1 Initial public release
##### Boolean:
* Get one or all possible solutions for a formula 
* Simplify a formula (limited functionality)
* (De-) parenthesise a formula
##### CTL:
* Evaluate a CTL formula on a given model
##### Generate:
* Generate a boolean formula
* Generate a Kripke Structure
* Generate a Finite Automaton
##### Encode:
* Encode a Kripke Structure into a boolean formula
##### Trace:
* Get a trace from a start state to a goal state
* Get the shortest trace
##### Finite Automata Algorithms:
* Select an algorithm to apply on a Finite Automata
* Code your own implementation and test it