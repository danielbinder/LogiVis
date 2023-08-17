import React, {useState} from 'react';
import ModelGenerator from './ModelGenerator';
import ModelEncoder from './ModelEncoder';
import FormulaGenerator from './FormulaGenerator';
import ModelTypeSelector from './ModelTypeSelector';
import AlgorithmTester from './AlgorithmTester';
import Graph from "./Graph";
import {ErrorBoundary} from "../ErrorBoundary";
import ModelTracer from "./ModelTracer";

export default function Model({setFormulaType,
                                  setFormulaTab,
                                  setSolutionTab,
                                  setEvalStatusMessage,
                                  modelStatusMessage, setModelStatusMessage,
                                  modelWarningMessage,
                                  modelErrorMessage,
                                  model,     // do NOT send over REST - use getModel() instead!
                                  getModel, setModel,
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
                            getModel={getModel}
                        />
                    </div>
                    <div className='smallColumn'>
                        <ModelGenerator
                            setModelTab={setModelTab}
                        />
                        <ModelTracer
                            setSolutionTab={setSolutionTab}
                            getModel={getModel}
                        />
                    </div>
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
                            modelPlaceholder={modelPlaceholder}
                            compactModelPlaceholder={compactModelPlaceholder}
                            model={model}
                            setModel={setModel}
                        />
                    </ErrorBoundary>
                </div>
            </div>
        </div>)
}

const modelPlaceholder =
    '# Model = (S, I, T, F) # Type \'this\' to use this model or \'compact\' for compact\n' +
    'S = {s1> [!p !q], s2 [!p q],\n' +
    '     s3 [p !q], s4< [p q \'deadlock\']}            # Set of states\n' +
    'I = {s1 [\'starting here\']}                       # Set of initial states\n' +
    'T = {(s1, s2), (s2, s1), (s1, s3), (s3, s1),\n' +
    '     (s3, s4) [\'unsafe transition\'], (s4, s1)}   # Set of transitions (s, s\')\n' +
    'F = {}                         # Set of final states (you can omit empty sets)\n' +
    '# For boolean encoding use \'>\' as suffix for start-, and \'<\' for goal states'

const compactModelPlaceholder =
    '# Type \'compact\' to use this model\n' +
    '# Initial states are denoted by \'_\' as suffix, final states by \'*\'\n' +
    '# For boolean encoding use \'>\' as suffix for start-, and \'<\' for goal states\n' +
    '# Both states and transitions can be labeled with \'[\'Text: \' var1 var2]\'\n' +
    '# Transitions are denoted by either \'->\' for unidirectional transitions\n' +
    '# or \'-\' for bidirectional transitions\n' +
    's1_> [!p !q] - [\'bidirectional\'] s2 [!p q], s1 - s3 [p !q],\n' +
    's3 -> [\'unsafe transition\'] s4< [p q \'deadlock\'], s4 -> s1'