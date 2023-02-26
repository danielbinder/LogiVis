import React, {useState} from 'react';
import './App.css';
import { Graphviz } from 'graphviz-react';

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

function Generator() {
    const [graph, setGraph] = useState("");

    const handleGenGraph = () => {
        try {
            const graphStr = kripkeString2Graph(
                extractValueFromTextInput("generation_result")
            );
            console.log(graphStr);
            setGraph(graphStr);
        } catch (e) {
            setGraph("");
        }
    };

    return (
        <div className="column">
            <h3>Generate a Kripke structure</h3>
            <div className="left">
                <InputGenerator text="Nodes" type_str="text" id="node_cnt" placeholder="node count" defaultVal="4"/>
                <InputGenerator text="Variables" type_str="text" id="variables" placeholder="variables" defaultVal="3"/>
                <InputGenerator text="Successors at least" type_str="text" id="min_succ" placeholder="min. successors" defaultVal="1"/>
                <InputGenerator text="Successors at most" type_str="text" id="max_succ" placeholder="max. successors" defaultVal="3"/>
                <InputGenerator text="Initial Nodes" type_str="text" id="initial_nodes" placeholder="initial nodes" defaultVal="2"
                />
                <div>
                    <input type="checkbox" id="states_reachable" defaultChecked />
                    <span> All states reachable</span>
                </div>
            </div>
            <br />
            <div>
                <button onClick={handleGenKripke}>Generate Kripke structure</button>
                <button className="button_margin_left" onClick={handleGenGraph}>Generate Graph</button>
            </div>
            <br />
            <textarea rows={10} cols={80} id="generation_result" placeholder="result" readOnly/>
            {graph !== "" && <Graphviz dot={graph} />}
        </div>
    );
}

function kripkeString2Graph(nodes: string) {
    let result = 'digraph {\n';
    result += 'ratio="0.5";\n';
    result += 'rankdir=LR;\n';

    const nodeList = nodes.split('_');
    const initialNodes = new Set<string>();

    for (let i = 0; i < nodeList.length; i++) {
        const parts = nodeList[i].split(';');
        const name = parts[0];
        const assignments = parts[1]
            .split('+')
            .map((a) => (a.split(':')[1] === 'true' ? '' : '!') + a.split(':')[0])
            .join(" ");
        const isInitialNode = parts[2] === 'true';
        const successors = parts[3].split('+');
        const nodeName = name;

        if (isInitialNode) {
            result += `  none${i} -> ${nodeName};\n`; // use backticks for string interpolation
            result += `  none${i} [shape=none];\n`;
            result += `  none${i} [label=""];\n`;
            initialNodes.add(nodeName);
        }
        successors.forEach((s) => {
            const sName = s.replace(/\+/g, "_")
                .replace(/-/g, "_");
            result += `  ${nodeName} -> ${sName};\n`;
        });

        result += `  ${nodeName} [label="${assignments}"];\n`;
        result += `  ${nodeName} [shape=circle];\n`;
    }

    result += "}";
    return result;
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
    let kripke = extractValueFromTextInput("generation_result")
    let steps = extractValueFromTextInput("steps")
    if(isNonEmptyString(kripke)) {
        console.log(kripke)
        let url = 'http://localhost:4000/kripke2formula/' + kripke + '/' + steps;
        url = url.replaceAll(';', ',');
        return fetch(url)
            .then(response => response.json())
            .then(data => {
                console.log(data);
                let rawFormula = JSON.stringify(data);
                rawFormula = rawFormula.substring(rawFormula.indexOf(':') + 2, rawFormula.length - 2);
                (document.getElementById("formula") as HTMLInputElement).value = rawFormula;
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