import React from 'react';
import FormulaTypeSelection from './FormulaTypeSelection';
import FormulaButtonArray from './FormulaButtonArray';
import {useRecoilState, useRecoilValue} from 'recoil';
import {
    evalErrorMessageState,
    evalStatusMessageState,
    evalWarningMessageState,
    formulaState, solutionInfoState,
    solutionState
} from '../atoms';

export default function Solver({setFormulaTab, setSolutionTab}) {
    const [evalStatusMessage, setEvalStatusMessage] = useRecoilState(evalStatusMessageState)
    const evalWarningMessage = useRecoilValue(evalWarningMessageState)
    const evalErrorMessage = useRecoilValue(evalErrorMessageState)
    const [formula, setFormula] = useRecoilState(formulaState)
    const solution = useRecoilValue(solutionState)
    const solutionInfo = useRecoilValue(solutionInfoState)

    function handleChange({target}) {
        setFormula(target.value)
    }

    return (
        <div className='column'>
            <h3 className='center'>Evaluate a formula</h3>
            <FormulaTypeSelection/>
            <textarea
                className='textArea'
                value={formula}
                placeholder='Enter a formula'
                onChange={handleChange}
                name='formula'
                onDoubleClick={() =>
                    navigator.clipboard.writeText(formula)
                        .then(() => setEvalStatusMessage('Copied Formula to Clipboard'))}
            />
            <FormulaButtonArray
                setFormulaTab={setFormulaTab}
                setSolutionTab={setSolutionTab}
            />
            <p className='green'>{evalStatusMessage}</p>
            <p className='orange'>{evalWarningMessage}</p>
            <p className='red'>{evalErrorMessage}</p>
            <textarea
                readOnly={true}
                className='textArea'
                value={solution}
                placeholder='Solution'
                name='solution'
                onDoubleClick={() =>
                    navigator.clipboard.writeText(solution)
                        .then(() => setEvalStatusMessage('Copied Solution to Clipboard'))}
            />
            <textarea
                readOnly={true}
                className='textArea'
                value={solutionInfo}
                placeholder='Solution information'
                name='solutionInfo'
                onDoubleClick={() =>
                    navigator.clipboard.writeText(solutionInfo)
                        .then(() => setEvalStatusMessage('Copied Solution Information to Clipboard'))}
            />
        </div>
    )
}