import React from 'react';
import ModelGenerator from './ModelGenerator';
import ModelEncoder from './ModelEncoder';
import FormulaGenerator from './FormulaGenerator';
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
            <a className='topRight' href='https://youtu.be/PyPAU3YHYFU' rel="noreferrer" target='_blank'>
                &#9432;
            </a>
            <div className='column'>
                <h3 className='center'>Tune and apply parameters</h3>
                <div className='parameters'>
                    <div className='smallColumn'>
                        <AlgorithmTester
                            setSolutionTab={setSolutionTab}
                            setModelTab={setModelTab}
                        />
                    </div>
                    <div className='smallColumn'>
                        <ModelTracer
                            setSolutionTab={setSolutionTab}
                        />
                        <ModelEncoder
                            setFormulaTab={setFormulaTab}
                            setSolutionTab={setSolutionTab}
                        />
                    </div>
                    <div className='smallColumn'>
                        <FormulaGenerator
                            setFormulaTab={setFormulaTab}
                        />
                        <ModelGenerator
                            setModelTab={setModelTab}
                        />
                    </div>
                </div>
        </div>
            <div className='column'>
                <p className='red'>{modelErrorMessage}</p>
                <p className='orange'>{modelWarningMessage}</p>
                <p className='green'>{modelStatusMessage}</p>
                <div className='model'>
                    <a className='modelNotationInfo' href='https://youtu.be/JDQRmMbmgfM' rel="noreferrer" target='_blank'>
                        &#9432;
                    </a>
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
                    <p>
                        Code
                        <a className='inTextInfo' href='https://youtu.be/CLPqkk8B1pM' rel="noreferrer" target='_blank'>
                            &nbsp;&#9432;&nbsp;
                        </a>
                        & Download
                        <a className='inTextInfo' href='https://youtu.be/lzKHhATYbmM' rel="noreferrer" target='_blank'>
                            &nbsp;&#9432;&nbsp;
                        </a>
                        : <a href='https://github.com/danielbinder/LogiVis' rel="noreferrer" target='_blank'>github.com/danielbinder/LogiVis</a>
                        <br></br>
                        Contributors:&nbsp;
                        <a href='https://github.com/danielbinder' rel="noreferrer" target='_blank'>Daniel Binder</a>,&nbsp;
                        <a href='https://github.com/csteidl' rel="noreferrer" target='_blank'>Christoph Steidl</a>
                    </p>
                </div>
            </div>
        </div>)
}