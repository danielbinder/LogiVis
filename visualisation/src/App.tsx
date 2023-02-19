import React from 'react';
import './App.css';
import {Buffer} from "buffer";

// nodes;initialNodes;variables;minSuccessors;maxSuccessors;allStatesReachable

function App() {
  return (
    <div className="App">
      <header className="App-header">
          <div className={"row"}>
              <Solver/>
              <div className={"column"}>
                  <br/><br/><br/><br/><br/><br/><br/><br/>
                  <button>→</button>
                  <br/><br/>
                  <InputGenerator text={"Steps"} type_str={"text"} id={"steps"} placeholder={"steps"} defaultVal={"3"}/>
                  <button onClick={handleKripke2Formula}>←</button>
              </div>
              <Generator/>
          </div>
      </header>
    </div>
  );
}

function Solver() {
    return (
        <div className={"column"}>
        <h3> Evaluate a given formula</h3>
            <textarea rows={1} cols={80} id="formula" placeholder="Formula"/>
            <br/><br/>
            <div>
                <button onClick={handleCheckFormula}>Check formula</button>
                <button className="button_margin_left" onClick={handleAllAssignments}>All satisfiable assignments</button>
            </div>
            <br/><br/>
            <textarea rows={10} cols={80} id="formula_eval_result" placeholder="result" readOnly/>
        </div>
    );
}

function Generator () {
    return (
        <div className={"column"}>
        <h3> Generate a formula/Kripke structure</h3>
            <div className={"left"}>
        <InputGenerator text={"Nodes"} type_str={"text"} id={"node_cnt"} placeholder={"node count"} defaultVal={"4"}/>
        <InputGenerator text={"Variables"} type_str={"text"} id={"variables"} placeholder={"variables"} defaultVal={"3"}/>
        <InputGenerator text={"Successors at least"} type_str={"text"} id={"min_succ"} placeholder={"min. successors"} defaultVal={"1"}/>
        <InputGenerator text={"Successors at most"} type_str={"text"} id={"max_succ"} placeholder={"max. successors"} defaultVal={"3"}/>
        <InputGenerator text={"Initial Nodes"} type_str={"text"} id={"initial_nodes"} placeholder={"initial nodes"} defaultVal={"2"}/>
        <div>
            <input type="checkbox" id="states_reachable" defaultChecked />
            <span>   All states reachable</span>
        </div>
            </div>
            <br/><br/>
            <div>
                <button onClick={handleGenKripke}>Generate Kripke structure</button>
            </div>
            <br/><br/>
            <textarea rows={10} cols={80} id="generation_result" placeholder="result" readOnly/>
        </div>);
}

const isNonEmptyString = (val: string) => !!val;

function handleCheckFormula() {
  let formula = extractValueFromTextInput("formula");
  if(isNonEmptyString(formula)) {
	  console.log(formula);
	  return fetch('http://localhost:4000/solve/' + formula)
		  .then(response => response.json())
		  .then(data => {
			  console.log(data);
			  (document.getElementById("formula_eval_result") as HTMLInputElement).value = JSON.stringify(data);
		  });
  }
}

function handleKripke2Formula() {
    //TODO:
    let kripke = ''
    let steps = extractValueFromTextInput("steps")
    if(isNonEmptyString(kripke)) {
        console.log(kripke)
        return fetch('http://localhost:4000/kripke2formula/' + kripke + '/' + steps)
            .then(response => response.json())
            .then(data => {
                console.log(data);
                (document.getElementById("formula_eval_result") as HTMLInputElement).value = JSON.stringify(data);
            })
    }
}

function handleAllAssignments() {
  let formula = extractValueFromTextInput("formula");
  if(isNonEmptyString(formula)) {
    console.log(formula);
    return fetch('http://localhost:4000/solveAll/' + formula)
      .then(response => response.json())
      .then(data => {
        console.log(data);
        (document.getElementById("formula_eval_result") as HTMLInputElement).value = JSON.stringify(data);
      });
  }
}

function handleGenKripke() {
  let nodeCnt = extractValueFromTextInput("node_cnt");
	let varCnt = extractValueFromTextInput("variables");
	let minSucc = extractValueFromTextInput("min_succ");
	let maxSucc = extractValueFromTextInput("max_succ");
	let initialNodes = extractValueFromTextInput("initial_nodes");
	let allStatesReachable = (document.getElementById("states_reachable") as HTMLInputElement).checked;
	const dataStr = nodeCnt + "_" + initialNodes + "_" + varCnt + "_" + minSucc + "_" + maxSucc + "_" + allStatesReachable;
	const url = "http://localhost:4000/generate/" + dataStr;
	console.log(url);
	return fetch(url)
		.then(response => response.json())
		.then(data => {
			console.log(data);
			(document.getElementById("generation_result") as HTMLInputElement).value = JSON.stringify(data);
		});
}

function extractValueFromTextInput(component_name: string) {
	return (document.getElementById(component_name) as HTMLInputElement).value;
}

function InputGenerator(props: { text: string; type_str: string; id: string; placeholder: string; defaultVal: string; }) {
  return <div>
      <input size={1} type={props.type_str} id={props.id} placeholder={props.placeholder} defaultValue={props.defaultVal}/>
      <span>
          &nbsp;&nbsp;
        {props.text}
      </span>
  </div>;
}

export default App;
