import React, {useState} from "react";

export default function FormulaButtonArray({formulaType,
                                               formula, setFormula,
                                               setEvalStatusMessage,
                                               setSolution,
                                               setSolutionInfo,
                                               model}) {
    const [simplifyFormulaLoading, setSimplifyFormulaLoading] = useState(false)
    const [checkFormulaLoading, setCheckFormulaLoading] = useState(false)
    const [allModelsLoading, setAllModelsLoading] = useState(false)
    const [checkModelLoading, setCheckModelLoading] = useState(false)

    function handleSimplify() {
        setSimplifyFormulaLoading(true)
        fetch('http://localhost:4000/simplify/' + formula)
            .then(response => {
                if(!response.ok) {
                    setSimplifyFormulaLoading(false)
                }

                return response
            })
            .then(response => response.json())
            .then(data => setFormula(getResultFromJSON(data)))
            .then(() => setSimplifyFormulaLoading(false))
    }

    function handleCheckFormula() {
        setCheckFormulaLoading(true)
        fetch('http://localhost:4000/solve/' +  formula)
            .then(response => {
                if(!response.ok) {
                    setCheckFormulaLoading(false)
                }

                return response
            })
            .then(response => response.json())
            .then(data => JSON.stringify(data))
            .then(data => setSolution(data))
            .then(() => {
                setEvalStatusMessage('')
                setCheckFormulaLoading(false)
            })
    }

    function handleAllModels() {
        setAllModelsLoading(true)
        fetch('http://localhost:4000/solveAll/' + formula)
            .then(response => {
                if(!response.ok) {
                    setAllModelsLoading(false)
                }

                return response
            })
            .then(response => response.json())
            .then(data => setSolution(JSON.stringify(data)))
            .then(() => {
                setEvalStatusMessage('')
                setAllModelsLoading(false)
            })
    }

    function handleCheckModel() {
        setCheckModelLoading(true)
        fetch('http://localhost:4000/solveCTL/' + formula + '/' + formatModel(model))
            .then(response => {
                if(!response.ok) {
                    setCheckModelLoading(false)
                }

                return response
            })
            .then(response => response.json())
            .then(data => {
                setSolutionInfo(data['steps'].replaceAll(/_/g, "\n"))
                delete data['steps'];
                setSolution(JSON.stringify(data))
                setEvalStatusMessage('')
            })
            .then(() => setCheckModelLoading(false))
    }

    return (
        <div className="centerContainer">
            {formulaType === "boolean"  &&
                <button className="button" onClick={handleSimplify}>
                    {simplifyFormulaLoading && <div className="loading"></div>}
                    Simplify formula
                </button>}
            {formulaType === "boolean" &&
                <button className="button" onClick={handleCheckFormula}>
                    {checkFormulaLoading && <div className="loading"></div>}
                    Check formula
                </button>}
            {formulaType === "boolean" &&
                <button className="button" onClick={handleAllModels}>
                    {allModelsLoading && <div className="loading"></div>}
                    All models
                </button>}
            {formulaType === "ctl" &&
                <button className="button" onClick={handleCheckModel}>
                    {checkModelLoading && <div className="loading"></div>}
                    Check model
                </button>}
        </div>
    )
}

const getResultFromJSON = (data) => `${JSON.parse(JSON.stringify(data))['result']}`

const formatModel = (raw_model) => raw_model.replace(/\n/g, "").replace(/;/g, "_")
