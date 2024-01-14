import React, {useState} from 'react';
import {useRecoilValue} from 'recoil';
import {formulaSelector, modelSelector} from '../selectors';
import {formulaTypeState} from '../atoms';
import {serverURL} from '../constants';

export default function FormulaButtonArray({setFormulaTab, setSolutionTab}) {
    const formulaType = useRecoilValue(formulaTypeState)
    const getFormula = useRecoilValue(formulaSelector)
    const getModel = useRecoilValue(modelSelector)

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
                    <a className='simplifyInfo' href='https://youtu.be/K1o87uwvaQU' rel="noreferrer" target='_blank'>
                        &#9432;
                    </a>
                </button>}
            {formulaType === 'boolean' &&
                <button className='leftBigDoubleButtonLeft' onClick={handleCheckFormula}>
                    {checkFormulaLoading && <div className='loading'></div>}
                    Check
                </button>}
            {formulaType === 'boolean' &&
                <button className='leftBigDoubleButtonRight' onClick={handleAllModels}>
                    {allModelsLoading && <div className='loading'></div>}
                    all
                    <a className='checkInfo' href='https://youtu.be/QnphQwwmdHo' rel="noreferrer" target='_blank'>
                        &#9432;
                    </a>
                </button>}
            {formulaType === 'boolean' &&
                <button className='rightBigDoubleButtonLeft' onClick={handleParenthesise}>
                    {parenthesiseLoading && <div className='loading'></div>}
                    De
                </button>}
            {formulaType === 'boolean' &&
                <button className='rightBigDoubleButtonRight' onClick={handleParenthesiseAll}>
                    {parenthesiseAllLoading && <div className='loading'></div>}
                    parenthesise
                    <a className='parenthesiseInfo' href='https://youtu.be/n4lEA4JjBB8' rel="noreferrer" target='_blank'>
                        &#9432;
                    </a>
                </button>}
            {formulaType === 'ctl' &&
                <button className='button' onClick={handleCheckModel}>
                    {checkModelLoading && <div className='loading'></div>}
                    Check model
                    <a className='checkModelInfo' href='https://youtu.be/68CpPHSrQ9o' rel="noreferrer" target='_blank'>
                        &#9432;
                    </a>
                </button>}
        </div>
    )
}
