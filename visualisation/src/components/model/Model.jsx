import React, {useEffect, useState} from "react";
import ModelGenerator from "./ModelGenerator";
import Graphviz from "graphviz-react";
import ModelEncoder from "./ModelEncoder";
import FormulaGenerator from "./FormulaGenerator";
import ModelTypeSelector from "./ModelTypeSelector";
import AlgorithmTester from "./AlgorithmTester";

export default function Model({setSolution, setSolutionInfo, model, setModel}) {
    const [modelType, setModelType] = useState("kripke")
    const [graph, setGraph] = useState("")
    const [errorMessage, setErrorMessage] = useState("")

    function handleChange({target: {value}}) {
        setModel(value)
    }

    // whenever 'model' changes, the graph attempts to update
    useEffect(() => {
        try {
            setGraph(kripkeString2Graph(model2Kripke(model)))
        } catch (e) {}
    }, [model])

    return (
        <div className="column">
            <h3 className="center">Tune and apply parameters</h3>
            <div className="parameters">
                <div className="smallColumn">
                    <ModelTypeSelector
                        modelType={modelType}
                        setModelType={setModelType}
                    />
                    <AlgorithmTester/>
                </div>
                <div className="smallColumn">
                    <FormulaGenerator/>
                    <ModelEncoder
                        setSolution={setSolution}
                        setSolutionInfo={setSolutionInfo}
                        kripke={() => model2Kripke(model)}
                    />
                </div>
                <ModelGenerator
                    setModel={setModel}
                />
            </div>
            <p className="red">
                {errorMessage}
            </p>
            <div className="model">
                <textarea
                    className="textArea"
                    value={model}
                    placeholder={generatorPlaceholder}
                    onChange={handleChange}
                    name="model"
                />
                {graph && <Graphviz className="graph" dot={graph}/>}
            </div>
        </div>
    )
}

const kripkeString2Graph = (kripke) => {
    let result = 'digraph {\n';
    result += 'ratio="0.5";\n';
    result += 'rankdir=LR;\n';
    result += 'bgcolor="#1c1c1c";\n';

    const nodeList = kripke.split('_');
    for (let i = 0; i < nodeList.length; i++) {
        const parts = nodeList[i].split(';');
        const name = parts[0];
        const assignments = parts[1]
            .split('+')
            .map(a => (a.split(':')[1] === 'true' ? ' ' : '!') + a.split(':')[0])
            .join(" ");
        const isInitialNode = parts[2] === 'true';
        const successors = parts[3].split('+');
        const nodeName = name;

        if (isInitialNode) {
            result += `  none${i} -> ${nodeName} [color="#c7c7c7"];\n`; // use backticks for string interpolation
            result += `  none${i} [shape=none];\n`;
            result += `  none${i} [label=""];\n`;
        }

        successors.forEach((s) => {
            const raw_name = s.trim();
            if(raw_name) {
                const sName = raw_name.replace(/\+/g, "_")
                    .replace(/-/g, "_");
                result += `  ${nodeName} -> ${sName} [color="#c7c7c7"];\n`;
            }
        });

        result += `  ${nodeName} [label="${assignments}" fontcolor="#c7c7c7"];\n`;
        // shape=doublecircle for final nodes
        result += `  ${nodeName} [shape=circle];\n`;
        result += `  ${nodeName} [color="#c7c7c7"];\n`;
    }

    return result + "}";
}

const model2Kripke = (model) => {
    if(!model) return "";

    const model_parts = model.split(";");
    let unique_atoms = new Set();
    let states_and_atoms = new Map();
    const properties = model_parts[3].split(",");
    for(let i = 0; i < properties.length; i++) {
        const state_and_atoms = properties[i].split(":");
        const state_atoms = state_and_atoms[1].split(" ");
        let clean_atoms = [];
        state_atoms.forEach((s) => {
            if(s) {
                unique_atoms.add(s);
                clean_atoms.push(s);
            }
        });
        states_and_atoms.set(state_and_atoms[0].trim(), clean_atoms);
    }

    const transitions = model_parts[2].split(",");
    let states_and_transitions = new Map();
    for(let i = 0; i < transitions.length; i++) {
        const state_pair = transitions[i].split(":")[1].split("-");
        const from_state = state_pair[0].trim();
        const to_state = state_pair[1].trim();
        if(!states_and_transitions.has(from_state)) states_and_transitions.set(from_state, []);
        if(!states_and_transitions.get(from_state)?.includes(to_state)) {
            states_and_transitions.get(from_state)?.push(to_state);
        }
    }

    const initial_states_parts = model_parts[1].split(":");
    let initial_states = new Set();
    if(initial_states_parts[1].trim()) {
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
        const unique_atoms_array = Array.from(unique_atoms);
        unique_atoms_array.sort();
        for(let property of unique_atoms_array) {
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
            const successors = states_and_transitions.get(state);
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

    return result;
}

const generatorPlaceholder =
    "Example model:\n"
    + "s1, s2;          # model has 3 states S={s1, s2, s3}\n"
    + "initial: s1;     # model has 1 initial state I = {s1}\n"
    + "t1: s1 - s2,\n"
    + "t2: s1 - s1;     # model has 3 transitions T={t1, t2}\n"
    + "s1: ,            # state s1 has no properties (i.e., {}) \n"
    + "s2: p q;        # state s2 has property p and q\n"