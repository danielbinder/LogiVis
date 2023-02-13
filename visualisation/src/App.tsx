import React from 'react';
import logo from './logo.svg';
import './App.css';

// nodes;variables;minSuccessors;maxSuccessors;allStatesReachable

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <p> Evaluate a given formula</p>
        <InputGenerator text={"Formula: "} type_str={"text"} id={"formula"} placeholder={"formula"} defaultVal={""}/>
        <button onClick={handleCheckFormula}>Check formula</button>
        <p> Parameters for generating formulas:</p>
        <InputGenerator text={"Node count: "} type_str={"text"} id={"node_cnt"} placeholder={"node count"} defaultVal={"4"}/>
        <InputGenerator text={"Variable count: "} type_str={"text"} id={"variables"} placeholder={"variables"} defaultVal={"3"}/>
        <InputGenerator text={"Min. successors: "} type_str={"text"} id={"min_succ"} placeholder={"min. successors"} defaultVal={"1"}/>
        <InputGenerator text={"Max. successors: "} type_str={"text"} id={"max_succ"} placeholder={"max. successors"} defaultVal={"3"}/>
        <div>
            <span>
                All states reachable:
            </span>
            <input type="checkbox" id="states_reachable" defaultChecked />
        </div>
        <button onClick={handleGenKripke}>Generate Kripke structure</button>
      </header>
    </div>
  );
}

function handleCheckFormula() {
  let formula = (document.getElementById("formula") as HTMLInputElement).value;
  console.log(formula);
  return fetch('http://localhost:4000/solve/' + formula)
      .then(response => response.json())
      .then(data => console.log(data));
}

function handleGenKripke() {
    // todo
}

function InputGenerator(props: { text: string; type_str: string; id: string; placeholder: string; defaultVal: string; }) {
  return <div>
    <span>
      {props.text}
    </span>
    <input type={props.type_str} id={props.id} placeholder={props.placeholder} defaultValue={props.defaultVal}/>
  </div>;
}

export default App;
