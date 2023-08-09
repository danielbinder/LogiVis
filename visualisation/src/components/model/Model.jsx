import React, {useState} from 'react';
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
                            model={model}
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
                        placeholder={modelPlaceholder}
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