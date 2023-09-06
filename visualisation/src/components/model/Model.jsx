import React from 'react';
import ModelGenerator from './ModelGenerator';
import ModelEncoder from './ModelEncoder';
import FormulaGenerator from './FormulaGenerator';
import ModelTypeSelector from './ModelTypeSelector';
import AlgorithmTester from './AlgorithmTester';
import Graph from './Graph';
import {ErrorBoundary} from '../ErrorBoundary';
import ModelTracer from './ModelTracer';
import {modelStatusMessageState, modelWarningMessageState, modelErrorMessageState, modelState} from '../atoms';
import {useRecoilState, useRecoilValue} from 'recoil';
import {modelPlaceholder} from '../constants';

export default function Model({setFormulaTab, setSolutionTab, setModelTab}) {
    const [modelStatusMessage, setModelStatusMessage] = useRecoilState(modelStatusMessageState)
    const modelWarningMessage = useRecoilValue(modelWarningMessageState)
    const modelErrorMessage = useRecoilValue(modelErrorMessageState)
    // do NOT send over REST - use getModel instead!
    const [model, setModel] = useRecoilState(modelState)

    function handleChange({target: {value}}) {
        setModel(value)
    }

    return (
        <div>
            <div className='column'>
                <h3 className='center'>Tune and apply parameters</h3>
                <div className='parameters'>
                    <div className='smallColumn'>
                        <ModelTypeSelector/>
                        <AlgorithmTester/>
                    </div>
                    <div className='smallColumn'>
                        <FormulaGenerator
                            setFormulaTab={setFormulaTab}
                        />
                        <ModelEncoder
                            setFormulaTab={setFormulaTab}
                            setSolutionTab={setSolutionTab}
                        />
                    </div>
                    <div className='smallColumn'>
                        <ModelGenerator
                            setModelTab={setModelTab}
                        />
                        <ModelTracer
                            setSolutionTab={setSolutionTab}
                        />
                    </div>
                </div>
        </div>
            <div className='column'>
                <p className='red'>{modelErrorMessage}</p>
                <p className='orange'>{modelWarningMessage}</p>
                <p className='green'>{modelStatusMessage}</p>
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
                        <Graph/>
                    </ErrorBoundary>
                </div>
            </div>
        </div>)
}