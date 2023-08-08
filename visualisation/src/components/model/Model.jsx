import React, {useEffect, useState} from 'react';
import ModelGenerator from './ModelGenerator';
import ModelEncoder from './ModelEncoder';
import FormulaGenerator from './FormulaGenerator';
import ModelTypeSelector from './ModelTypeSelector';
import AlgorithmTester from './AlgorithmTester';
import Graph from "./Graph";
import {ErrorBoundary} from "../ErrorBoundary";

export default function Model({setFormulaType,
                                  setFormulaTab,
                                  setSolutionTab,
                                  setEvalStatusMessage,
                                  modelStatusMessage, setModelStatusMessage,
                                  modelWarningMessage,
                                  modelErrorMessage,
                                  model, setModel,
                                  setModelTab}) {
    const [modelType, setModelType] = useState('kripke')


    function handleChange({target: {value}}) {
        setModel(value)
    }

    return (
        <div>
            <div className='column'>
                <h3 className='center'>Tune and apply parameters</h3>
                <div className='parameters'>
                    <div className='smallColumn'>
                        <ModelTypeSelector
                            modelType={modelType}
                            setModelType={setModelType}
                        />
                        <AlgorithmTester/>
                    </div>
                    <div className='smallColumn'>
                        <FormulaGenerator/>
                        <ModelEncoder
                            setFormulaType={setFormulaType}
                            setFormulaTab={setFormulaTab}
                            setSolutionTab={setSolutionTab}
                            setEvalStatusMessage={setEvalStatusMessage}
                            kripke={() => model2Kripke(model)}
                        />
                    </div>
                    <ModelGenerator
                        setModelTab={setModelTab}
                    />
                </div>
        </div>
            <div className='column'>
                <p className='green'>{modelStatusMessage}</p>
                <p className='orange'>{modelWarningMessage}</p>
                <p className='red'>{modelErrorMessage}</p>
                <div className='model'>
                    <textarea
                        className='textArea'
                        value={model}
                        placeholder={generatorPlaceholder}
                        onChange={handleChange}
                        name='model'
                        onDoubleClick={() =>
                            navigator.clipboard.writeText(model)
                                .then(() => setModelStatusMessage('Copied Model to Clipboard'))}
                    />
                    <ErrorBoundary>
                        <Graph
                            setModelStatusMessage={setModelStatusMessage}
                            model={model}
                            setModel={setModel}
                        />
                    </ErrorBoundary>
                </div>
            </div>
        </div>)
}

const model2Kripke = (model) => {
    if(!model) return '';

    const model_parts = model.split(';');
    let unique_atoms = new Set();
    let states_and_atoms = new Map();
    const properties = model_parts[3].split(',');
    for(let i = 0; i < properties.length; i++) {
        const state_and_atoms = properties[i].split(':');
        const state_atoms = state_and_atoms[1].split(' ');
        let clean_atoms = [];
        state_atoms.forEach((s) => {
            if(s) {
                unique_atoms.add(s);
                clean_atoms.push(s);
            }
        });
        states_and_atoms.set(state_and_atoms[0].trim(), clean_atoms);
    }

    const transitions = model_parts[2].split(',');
    let states_and_transitions = new Map();
    for(let i = 0; i < transitions.length; i++) {
        const state_pair = transitions[i].split(':')[1].split('-');
        const from_state = state_pair[0].trim();
        const to_state = state_pair[1].trim();
        if(!states_and_transitions.has(from_state)) states_and_transitions.set(from_state, []);
        if(!states_and_transitions.get(from_state)?.includes(to_state)) {
            states_and_transitions.get(from_state)?.push(to_state);
        }
    }

    const initial_states_parts = model_parts[1].split(':');
    let initial_states = new Set();
    if(initial_states_parts[1].trim()) {
        let init_states = initial_states_parts[1].trim().split(',');
        init_states.forEach((s) => {
            const state_name = s.trim();
            if(states_and_atoms.has(state_name)) initial_states.add(state_name);
        });
    }

    let result = '';
    let state_cnt = states_and_atoms.size;
    for(let state of Array.from(states_and_atoms.keys())) {
        result += state + ';';
        const state_properties = states_and_atoms.get(state);
        let elem_cnt = unique_atoms.size;
        const unique_atoms_array = Array.from(unique_atoms);
        unique_atoms_array.sort();
        for(let property of unique_atoms_array) {
            result += property + ':';
            if(state_properties?.includes(property)) result += 'true';
            else result += 'false';
            elem_cnt--;
            if(elem_cnt > 0) result += '+';
        }
        result += ';';
        if(initial_states.has(state)) result += 'true';
        else result += 'false';
        result += ';';
        if(states_and_transitions.has(state)) {
            const successors = states_and_transitions.get(state);
            let succ_cnt = successors?.length;
            for(let successor of successors) {
                result += successor;
                succ_cnt--;
                if(succ_cnt > 0) result += '+';
            }
        }
        state_cnt--;
        if(state_cnt > 0) result += '_';
    }

    return result;
}

const modelPlaceholder =
    '# Model = (S, I, T, F)           Type \'this\' to use this model\n' +
    'S = {s1 [p q], s2}             # Set of states\n' +
    'I = {s1}                       # Set of initial states\n' +
    'T = {(s1, s1), (s1, s2)}       # Set of transitions (s, s\')\n' +
    'F = {}                         # Set of final states (you can omit empty sets)\n' +
    '# For encoding this into a boolean formula,\n' +
    '# use \' as state suffix to denote start states (e.g. s1\')\n' +
    '# and \'\' as state suffix to denote goal states (e.g. s1\'\')'

const compactModelPlaceholder =
    '# Type \'this\' to use this model\n' +
    '# Initial states are denoted by \'_\' as suffix, final states by \'*\'\n' +
    '# For boolean formula encoding use \'>\' as suffix for start-, and \'<\' for goal states\n' +
    '# Both states and transitions can be labeled with \'[\'Text: \' var1 var2]\'\n' +
    '# Transition labels are denoted by either \'->\' for unidirectional transitions\n' +
    '# or \'-\' for bidirectional transitions\n' +
    's1_ [p q] -> s1, s2_* [p] - s3 [q], [\'unsafe transition\'], s4*\n' +
    's1 -> s2, s3 -> s4 # you could also list your transitions afterwards'

const generatorPlaceholder =
    'Example model (type \'this\' to use this model):\n'
    + 's1, s2;          # model has two states S={s1, s2}\n'
    + 'initial: s1;     # model has one initial state I = {s1}\n'
    + 't1: s1 - s2,\n'
    + 't2: s1 - s1;     # model has two transitions T={t1, t2}\n'
    + 's1: ,            # state s1 has no properties (i.e., {}) \n'
    + 's2: p q;         # state s2 has property p and q\n'