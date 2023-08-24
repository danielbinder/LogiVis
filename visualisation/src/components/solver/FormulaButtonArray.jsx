import React, {useState} from 'react';
import {useRecoilValue, useSetRecoilState} from 'recoil';
import {formulaSelector, modelSelector} from '../selectors';
import {evalStatusMessageState, formulaTypeState} from '../atoms';
import {serverURL, solutionInfoWarning} from '../constants';

export default function FormulaButtonArray({setFormulaTab, setSolutionTab}) {
    const formulaType = useRecoilValue(formulaTypeState)
    const getFormula = useRecoilValue(formulaSelector)
    const getModel = useRecoilValue(modelSelector)
    const setEvalStatusMessage = useSetRecoilState(evalStatusMessageState)

    const [simplifyFormulaLoading, setSimplifyFormulaLoading] = useState(false)
    const [checkFormulaLoading, setCheckFormulaLoading] = useState(false)
    const [allModelsLoading, setAllModelsLoading] = useState(false)
    const [checkModelLoading, setCheckModelLoading] = useState(false)
    const [parenthesiseLoading, setParenthesiseLoading] = useState(false)
    const [parenthesiseAllLoading, setParenthesiseAllLoading] = useState(false)

    function handleSimplify() {
        setSimplifyFormulaLoading(true)
        fetch(serverURL + '/simplify/' + getFormula)
            .then(response => response.json())
            .then(setFormulaTab)
            .finally(() => setSimplifyFormulaLoading(false))
    }

    function handleCheckFormula() {
        setCheckFormulaLoading(true)
        fetch(serverURL + '/solve/' +  getFormula)
            .then(response => response.json())
            .then(setSolutionTab)
            .finally(() => setCheckFormulaLoading(false))
    }

    function handleAllModels() {
        setAllModelsLoading(true)
        fetch(serverURL + '/solveAll/' + getFormula)
            .then(response => response.json())
            .then(setSolutionTab)
            .then(() => setEvalStatusMessage(solutionInfoWarning))
            .finally(() => setAllModelsLoading(false))
    }

    function handleParenthesise() {
        setParenthesiseLoading(true)
        fetch(serverURL + '/parenthesise/' + getFormula)
            .then(response => response.json())
            .then(setFormulaTab)
            .finally(() => setParenthesiseLoading(false))
    }

    function handleParenthesiseAll() {
        setParenthesiseAllLoading(true)
        fetch(serverURL + '/parenthesiseAll/' + getFormula)
            .then(response => response.json())
            .then(setFormulaTab)
            .finally(() => setParenthesiseAllLoading(false))
    }

    function handleCheckModel() {
        setCheckModelLoading(true)
        fetch(serverURL + '/solveCTL/' + getFormula + '/' + getModel)
            .then(response => response.json())
            .then(setSolutionTab)
            .finally(() => setCheckModelLoading(false))
    }

    return (
        <div className='centerContainer'>
            {formulaType === 'boolean'  &&
                <button className='button' onClick={handleSimplify}>
                    {simplifyFormulaLoading && <div className='loading'></div>}
                    Simplify
                </button>}
            {formulaType === 'boolean' &&
                <button className='doubleButtonLeft' onClick={handleCheckFormula}>
                    {checkFormulaLoading && <div className='loading'></div>}
                    Check
                </button>}
            {formulaType === 'boolean' &&
                <button className='doubleButtonRight' onClick={handleAllModels}>
                    {allModelsLoading && <div className='loading'></div>}
                    all
                </button>}
            {formulaType === 'boolean' &&
                <button className='doubleButtonLeft' onClick={handleParenthesise}>
                    {parenthesiseLoading && <div className='loading'></div>}
                    Parenthesise
                </button>}
            {formulaType === 'boolean' &&
                <button className='doubleButtonRight' onClick={handleParenthesiseAll}>
                    {parenthesiseAllLoading && <div className='loading'></div>}
                    all
                </button>}
            {formulaType === 'ctl' &&
                <button className='button' onClick={handleCheckModel}>
                    {checkModelLoading && <div className='loading'></div>}
                    Check model
                </button>}
        </div>
    )
}
