import React from 'react';
import './App.css';

// nodes;initialNodes;variables;minSuccessors;maxSuccessors;allStatesReachable

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <h3> Evaluate a given formula</h3>
        <InputGenerator text={"Formula: "} type_str={"text"} id={"formula"} placeholder={"formula"} defaultVal={""}/>
		    <textarea rows={5} cols={60} id="formula_eval_result" placeholder="result" readOnly/>
        <div>
          <button onClick={handleCheckFormula}>Check formula</button>
          <button className="button_margin_left" onClick={handleAllAssignments}>All satisfiable assignments</button>
        </div>
        <h3> Generate a formula/Kripke structure</h3>
        <InputGenerator text={"Node count: "} type_str={"text"} id={"node_cnt"} placeholder={"node count"} defaultVal={"4"}/>
        <InputGenerator text={"Variable count: "} type_str={"text"} id={"variables"} placeholder={"variables"} defaultVal={"3"}/>
        <InputGenerator text={"Min. successors: "} type_str={"text"} id={"min_succ"} placeholder={"min. successors"} defaultVal={"1"}/>
        <InputGenerator text={"Max. successors: "} type_str={"text"} id={"max_succ"} placeholder={"max. successors"} defaultVal={"3"}/>
		    <InputGenerator text={"Initial nodes: "} type_str={"text"} id={"initial_nodes"} placeholder={"initial nodes"} defaultVal={"2"}/>
        <div>
            <span>
                All states reachable:
            </span>
            <input type="checkbox" id="states_reachable" defaultChecked />
        </div>
		    <textarea rows={5} cols={60} id="generation_result" placeholder="result" readOnly/>
        <button onClick={handleGenKripke}>Generate Kripke structure</button>
      </header>
    </div>
  );
}

const isNonEmptyString = (val: string) => typeof val === 'string' && !!val;

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
    <span>
      {props.text}
    </span>
    <input type={props.type_str} id={props.id} placeholder={props.placeholder} defaultValue={props.defaultVal}/>
  </div>;
}

export default App;
