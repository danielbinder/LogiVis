import React, {useState} from 'react';

export default function FormulaButtonArray({formulaType,
                                               getFormula,
                                               setEvalStatusMessage,
                                               setSolution,
                                               setSolutionInfo,
                                               setFormulaTab,
                                               setSolutionTab,
                                               getModel}) {
    const [simplifyFormulaLoading, setSimplifyFormulaLoading] = useState(false)
    const [checkFormulaLoading, setCheckFormulaLoading] = useState(false)
    const [allModelsLoading, setAllModelsLoading] = useState(false)
    const [checkModelLoading, setCheckModelLoading] = useState(false)

    function handleSimplify() {
        setSimplifyFormulaLoading(true)
        fetch('http://localhost:4000/simplify/' + getFormula())
            .then(response => response.json())
            .then(setFormulaTab)
            .finally(() => setSimplifyFormulaLoading(false))
    }

    function handleCheckFormula() {
        setCheckFormulaLoading(true)
        fetch('http://localhost:4000/solve/' +  getFormula())
            .then(response => response.json())
            .then(setSolutionTab)
            .finally(() => setCheckFormulaLoading(false))
    }

    function handleAllModels() {
        setAllModelsLoading(true)
        fetch('http://localhost:4000/solveAll/' + getFormula())
            .then(response => response.json())
            .then(setSolutionTab)
            .finally(() => setAllModelsLoading(false))
    }

    function handleCheckModel() {
        setCheckModelLoading(true)
        fetch('http://localhost:4000/solveCTL/' + getFormula() + '/' + getModel())
            .then(response => response.json())
            .then(setSolutionTab)
            .finally(() => setCheckModelLoading(false))
    }

    return (
        <div className='centerContainer'>
            {formulaType === 'boolean'  &&
                <button className='button' onClick={handleSimplify}>
                    {simplifyFormulaLoading && <div className='loading'></div>}
                    Simplify formula
                </button>}
            {formulaType === 'boolean' &&
                <button className='button' onClick={handleCheckFormula}>
                    {checkFormulaLoading && <div className='loading'></div>}
                    Check formula
                </button>}
            {formulaType === 'boolean' &&
                <button className='button' onClick={handleAllModels}>
                    {allModelsLoading && <div className='loading'></div>}
                    All models
                </button>}
            {formulaType === 'ctl' &&
                <button className='button' onClick={handleCheckModel}>
                    {checkModelLoading && <div className='loading'></div>}
                    Check model
                </button>}
        </div>
    )
}
