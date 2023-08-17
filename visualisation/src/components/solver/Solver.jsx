import React from 'react';
import FormulaTypeSelection from './FormulaTypeSelection';
import FormulaButtonArray from './FormulaButtonArray';

export default function Solver({formulaType, setFormulaType,
                                   formula,     // do NOT send over REST - use getFormula() instead!
                                   getFormula, setFormula,
                                   evalStatusMessage, setEvalStatusMessage,
                                   evalWarningMessage,
                                   evalErrorMessage,
                                   solution, setSolution,
                                   solutionInfo, setSolutionInfo,
                                   setFormulaTab,
                                   setSolutionTab,
                                   getModel}) {

    function handleChange({target}) {
        setFormula(target.value)
    }

    return (
        <div className='column'>
            <h3 className='center'>Evaluate a formula</h3>
            <FormulaTypeSelection formulaType={formulaType} setFormulaType={setFormulaType}/>
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
                formulaType={formulaType}
                getFormula={getFormula}
                setEvalStatusMessage={setEvalStatusMessage}
                setSolution={setSolution}
                setSolutionInfo={setSolutionInfo}
                setFormulaTab={setFormulaTab}
                setSolutionTab={setSolutionTab}
                getModel={getModel}
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