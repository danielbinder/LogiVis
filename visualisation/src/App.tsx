import React from 'react';
import logo from './logo.svg';
import './App.css';

        // nodes;variables;minSuccessors;maxSuccessors;allStatesReachable

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <p> Evaluate a given formula</p>
        <div>
          <span>
            <text>Formula: </text>
          </span>
          <input type="text" id="formula" placeholder="formula"/>
        </div>
        <button onClick={handleCheckFormula}>Check formula</button>
        <p> Parameters for generating formulas:</p>
        <div>
            <span>
                <text>Node count: </text>
            </span>
            <input type="text" id="node_cnt" defaultValue="4" placeholder="formula"/>
        </div>
        <div>
            <span>
                <text>Variable count: </text>
            </span>
            <input type="text" id="variables" placeholder="variables" defaultValue="3"/>
        </div>
        <div>
            <span>
                <text>Min. successors: </text>
            </span>
            <input type="text" id="min_succ" placeholder="min. successors" defaultValue="1"/>
        </div>
        <div>
            <span>
                <text>Max. successors: </text>
            </span>
            <input type="text" id="max_succ" placeholder="max. successors" defaultValue="3"/>
        </div>
        <div>
            <span>
                <text>All states reachable: </text>
            </span>
            <input type="checkbox" id="states_reachable" checked />
        </div>
        <button onClick={handleGenKripke}>Generate Kripke structure</button>
      </header>
    </div>
  );
}

function handleCheckFormula() {
  return fetch('http://localhost:4000/solve/var1')
      .then(response => response.json())
      .then(data => console.log(data))
}

function handleGenKripke() {

}

type props = {
        type_str: string;
        id: string;
        placeholder: string;
        defaultVal: string;
        text: string;
};

function inputGenerator(p: props) {
  return <div>
    <span>
      <text>{p.text}</text>
    </span>
    <input type={p.type_str} id={p.id} placeholder={p.placeholder} defaultValue={p.defaultVal}/>
  </div>;
}

export default App;
