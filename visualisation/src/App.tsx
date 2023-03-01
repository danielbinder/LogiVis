import React, {useState} from 'react';
import './App.css';
import { Graphviz } from 'graphviz-react';

function App() {
    return (
        <div className="App">
            <header className="App-header">
                <div className="row">
                    <Solver/>
                    <LeftRightButtons/>
                    <Generator/>
                </div>
            </header>
        </div>
    );
}

function Solver() {
    return (
        <div className="column">
            <h3>Evaluate a given formula</h3>
            <textarea rows={1} cols={80} id="formula" placeholder="Formula"/>
            <br/><br/>
            <div>
                <button onClick={handleSimplify}>Simplify formula</button>
                <button className="button_margin_left" onClick={handleCheckFormula}>Check formula</button>
                <button className="button_margin_left" onClick={handleAllAssignments}>All satisfiable assignments</button>
            </div>
            <br/><br/>
            <textarea rows={10} cols={80} id="formula_eval_result" placeholder="result" readOnly/>
        </div>
    );
}

function Generator() {
    const [graph, setGraph] = useState("");

    const handleGenGraph = () => {
        try {
          handleGenKripke().then(kripke => setGraph(kripkeString2Graph(kripke)));
        } catch (e) {
          setGraph("");
        }
    }

    return (
        <div className="column">
            <h3>Generate a Kripke structure</h3>
            <div className="left">
                <InputGenerator text="Nodes" id="node_cnt" placeholder="node count" defaultVal="4"/>
                <InputGenerator text="Variables" id="variables" placeholder="variables" defaultVal="3"/>
                <InputGenerator text="Successors at least" id="min_succ" placeholder="min. successors" defaultVal="1"/>
                <InputGenerator text="Successors at most" id="max_succ" placeholder="max. successors" defaultVal="3"/>
                <InputGenerator text="Initial Nodes" id="initial_nodes" placeholder="initial nodes" defaultVal="2"/>
                <div>
                    <input type="checkbox" id="states_reachable" defaultChecked />
                    <span>All states reachable</span>
                </div>
            </div>
            <br/>
            <button onClick={handleGenGraph}>Generate Kripke structure</button>
            <br/>
            <input type="hidden" id="generation_result" style={{display: 'none'}} placeholder='result' readOnly/>
            {graph !== "" && <Graphviz dot={graph} />}
        </div>);
}

function LeftRightButtons() {
    return (
        <div className={"column"}>
            <br/><br/><br/><br/><br/><br/><br/><br/>
            <button>→</button>
            <br/><br/>
            <InputGenerator text="Steps" id="steps" placeholder="steps" defaultVal="3"/>
            <button onClick={handleKripke2Formula}>←</button>
        </div>);
}

function InputGenerator(props: { text: string, id: string, placeholder: string, defaultVal: string }) {
    return (
        <div>
            <input size={1} type="text" id={props.id} placeholder={props.placeholder} defaultValue={props.defaultVal}/>
            <span>
              &nbsp;&nbsp;{props.text}
            </span>
        </div>);
}

const kripkeString2Graph = (kripke: string) => {
    let result = 'digraph {\n';
    result += 'ratio="0.5";\n';
    result += 'rankdir=LR;\n';

    const nodeList = kripke.split('_');
    for (let i = 0; i < nodeList.length; i++) {
        const parts = nodeList[i].split(';');
        const name = parts[0];
        const assignments = parts[1]
            .split('+')
            .map(a => (a.split(':')[1] === 'true' ? '' : '!') + a.split(':')[0])
            .join(" ");
        const isInitialNode = parts[2] === 'true';
        const successors = parts[3].split('+');
        const nodeName = name;

        if (isInitialNode) {
            result += `  none${i} -> ${nodeName};\n`; // use backticks for string interpolation
            result += `  none${i} [shape=none];\n`;
            result += `  none${i} [label=""];\n`;
        }
        successors.forEach((s) => {
            const sName = s.replace(/\+/g, "_")
                .replace(/-/g, "_");
            result += `  ${nodeName} -> ${sName};\n`;
        });

        result += `  ${nodeName} [label="${assignments}"];\n`;
        result += `  ${nodeName} [shape=circle];\n`;
    }

    return result + "}";
}

/** BUTTON HANDLERS */

const handleCheckFormula = () => {
    let formula = getElementById("formula").value;
    if(isEmptyString(formula)) return;

    fetch('http://localhost:4000/solve/' + formula)
        .then(response => response.json())
        .then(data => getElementById("formula_eval_result").value = JSON.stringify(data));
}

const handleSimplify = () => {
    let formula = getElementById("formula").value;
    if(isEmptyString(formula)) return;

    fetch('http://localhost:4000/simplify/' + formula)
        .then(response => response.json())
        .then(data => getElementById("formula").value = getResultFromJSON(data));
}

const handleKripke2Formula = () => {
    const kripke = getElementById('generation_result').value
        // REST API does not seem to work with ';' in url
        .replaceAll(';', ',');
    if(isEmptyString(kripke)) return;
    const steps = getElementById("steps").value

    fetch('http://localhost:4000/kripke2formula/' + kripke + '/' + steps)
        .then(response => response.json())
        .then(data => getElementById("formula").value = getResultFromJSON(data))
}

const handleAllAssignments = () => {
    const formula = getElementById("formula").value;
    if(isEmptyString(formula)) return;

    fetch('http://localhost:4000/solveAll/' + formula)
        .then(response => response.json())
        .then(data => getElementById("formula_eval_result").value = JSON.stringify(data));
}

const handleGenKripke = () => {
    const dataStr = getElementById("node_cnt").value + '_' +
        getElementById("initial_nodes").value + '_' +
        getElementById("variables").value + '_' +
        getElementById("min_succ").value + '_' +
        getElementById("max_succ").value + '_' +
        getElementById("states_reachable").checked;

    return fetch("http://localhost:4000/generate/" + dataStr)
        .then(response => response.json())
        .then(data => getResultFromJSON(data))
        .then(kripke => {
            getElementById('generation_result').value = kripke
            return kripke;
        });
}

/** HELPERS */

const isEmptyString = (val: string) => !val;

const getResultFromJSON = (data: JSON) => `${JSON.parse(JSON.stringify(data))['result']}`;

const getElementById = (component_name: string) => (document.getElementById(component_name) as HTMLInputElement);

export default App;