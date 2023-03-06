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
    const [hidden, setHidden] = useState(true);

    return (
        <div className="column">
            <h3>Evaluate a given formula</h3>
            <div>
                Chose formula type:  
                <select className="button_margin_left" id="dropdown" onChange={() => {
                    var dropdown = getElementById("dropdown") as unknown as HTMLSelectElement;
                    if(dropdown.options[dropdown.selectedIndex].value === "ctl") {
                        setHidden(false);
                    } else {
                        setHidden(true);
                    }
                }}>
                    <option value="bool">Boolean algebra</option>
                    <option value="ctl">CTL expression</option>
                </select>
            </div>
            <textarea rows={1} cols={80} id="formula" placeholder="formula"/>
            {!hidden ? <textarea rows={10} cols={80} id="model" placeholder={
                "Example model:\n"
                + "s1, s2, s3; # model has three states S={s1, s2, s3}\n"
                + "initial: s1; # model has one initial state I = {s1}\n"
                + "t1: s1 - s2,\n"
                + "t2: s1 - s3,\n"
                + "t3: s2 - s1,\n"
                + "t4: s3 - s2; # model has four transitions T={t1, t2, t3, t4}\n"
                + "s1: , # state s1 has no properties (i.e., {}) \n"
                + "s2: p, # state s2 has property p\n"
                + "s3: p q; # state s3 has properties p and q\n"
            }/> : null}
            <br/><br/>
            <div>
                {hidden ? <button onClick={handleSimplify}>Simplify formula</button> : null}
                <button className="button_margin_left" onClick={handleCheckFormula}>Check formula</button>
                {hidden ? <button className="button_margin_left" onClick={handleAllAssignments}>All satisfiable assignments</button> : null}
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
            const kripke = getElementById("generation_result").value;
            if(!isEmptyString(kripke)) {
                setGraph(kripkeString2Graph(kripke));
            }
        } catch (ex) {
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
            <div>
                <button onClick={handleGenKripke}>Generate Kripke structure</button>
            </div>
            <br/>
            <input type="hidden" id="generation_result" style={{display: 'none'}} placeholder='result' onInput={handleGenGraph} readOnly/>
            {graph !== "" && <Graphviz dot={graph} />}
        </div>);
}

function LeftRightButtons() {
    return (
        <div className={"column"}>
            <br/><br/><br/><br/><br/><br/><br/><br/>
            <button onClick={handleModel2Kripke}>→</button>
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
            const raw_name = s.trim();
            if(!isEmptyString(raw_name)) {
                const sName = raw_name.replace(/\+/g, "_")
                    .replace(/-/g, "_");
                result += `  ${nodeName} -> ${sName};\n`;
            }
        });

        result += `  ${nodeName} [label="${assignments}"];\n`;
        result += `  ${nodeName} [shape=circle];\n`;
    }

    return result + "}";
}

/** BUTTON HANDLERS */

const handleModel2Kripke = () => {
    const model_input = getElementById("model");
    if(model_input === null || isEmptyString(model_input.value)) return;

    const model_parts = model_input.value.split(";");
    let unique_atoms: Set<string> = new Set();
    let states_and_atoms: Map<string, string[]> = new Map();
    const properties = model_parts[3].split(",");
    for(let i = 0; i < properties.length; i++) {
        const state_and_atoms = properties[i].split(":");
        const state_atoms = state_and_atoms[1].split(" ");
        let clean_atoms: string[] = [];
        state_atoms.forEach((s) => {
            if(!isEmptyString(s)) {
                unique_atoms.add(s);
                clean_atoms.push(s);
            }
        });
        states_and_atoms.set(state_and_atoms[0].trim(), clean_atoms);
    }

    const transitions = model_parts[2].split(",");
    let states_and_transitions: Map<string, string[]> = new Map();
    for(let i = 0; i < transitions.length; i++) {
        const state_pair = transitions[i].split(":")[1].split("-");
        const from_state = state_pair[0].trim();
        const to_state = state_pair[1].trim();
        if(!states_and_transitions.has(from_state)) states_and_transitions.set(from_state, [] as string[]);
        if(!states_and_transitions.get(from_state)?.includes(to_state)) {
            states_and_transitions.get(from_state)?.push(to_state);
        }
    }

    const initial_states_parts = model_parts[1].split(":");
    let initial_states: Set<string> = new Set();
    if(!isEmptyString(initial_states_parts[1].trim())) {
        let init_states = initial_states_parts[1].trim().split(",");
        init_states.forEach((s) => {
            const state_name = s.trim();
            if(states_and_atoms.has(state_name)) initial_states.add(state_name);
        });
    }

    let result = "";
    let state_cnt = states_and_atoms.size;
    for(let state of Array.from(states_and_atoms.keys())) {
        result += state + ";";
        const state_properties = states_and_atoms.get(state);
        let elem_cnt = unique_atoms.size;
        for(let property of Array.from(unique_atoms)) {
            result += property + ":";
            if(state_properties?.includes(property)) result += "true";
            else result += "false";
            elem_cnt--;
            if(elem_cnt > 0) result += "+";
        }
        result += ";";
        if(initial_states.has(state)) result += "true";
        else result += "false";
        result += ";";
        if(states_and_transitions.has(state)) {
            const successors = states_and_transitions.get(state) as string[];
            let succ_cnt = successors?.length;
            for(let successor of successors) {
                result += successor;
                succ_cnt--;
                if(succ_cnt > 0) result += "+";
            }
        }
        state_cnt--;
        if(state_cnt > 0) result += "_";
    }

    getElementById("generation_result").value = result;
    dispatchEventForElement("generation_result", "input");
}

const handleCheckFormula = () => {
    let formula = getElementById("formula").value;
    if(isEmptyString(formula)) return;

    let url = 'http://localhost:4000/solve';
    var dropdown = getElementById("dropdown") as unknown as HTMLSelectElement;
    if(dropdown.options[dropdown.selectedIndex].value === "ctl") {
        url += 'CTL/' + formula + '/';
        const raw_model = getElementById("model").value;
        const model = formatModel(raw_model);
        if(isEmptyString(model)) return;
        else url += model;
    } else {
        url += '/' + formula;
    }

    fetch(url)
        .then(response => response.json())
        .then(data => getElementById("formula_eval_result").value = JSON.stringify(data));
}

const formatModel = (raw_model: string) => {
    let model = raw_model.replace(/\n/g, "").replace(/;/g, "_");
    return model;
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
            getElementById("generation_result").value = kripke;
            dispatchEventForElement("generation_result", "input");
            return kripke;
        });
}

/** HELPERS */

const dispatchEventForElement = (element: string, event_type: string) => getElementById(element).dispatchEvent(new Event(event_type, {bubbles: true}))

const isEmptyString = (val: string) => !val;

const getResultFromJSON = (data: JSON) => `${JSON.parse(JSON.stringify(data))['result']}`;

const getElementById = (component_name: string) => (document.getElementById(component_name) as HTMLInputElement);

export default App;