import React from "react";

export default function FormulaButtonArray({formulaType, formula, model, setFormula, setSolution, setSolutionInfo}) {
    function handleSimplify() {
        fetch('http://localhost:4000/simplify/' + formula)
            .then(response => response.json())
            .then(data => setFormula(getResultFromJSON(data)));
    }

    function handleCheckFormula() {
        fetch('http://localhost:4000/solve/' +  formula)
            .then(response => response.json())
            .then(data => JSON.stringify(data))
            .then(data => setSolution(data))
    }

    function handleAllModels() {
        fetch('http://localhost:4000/solveAll/' + formula)
            .then(response => response.json())
            .then(data => setSolution(JSON.stringify(data)))
    }
    function handleCheckModel() {
        fetch('http://localhost:4000/solveCTL/' + formula + '/' + formatModel(model))
            .then(response => response.json())
            .then(data => {
                setSolutionInfo(data['steps'].replaceAll(/_/g, "\n"))
                delete data['steps'];
                setSolution(JSON.stringify(data))
            })
    }

    return (
        <div className="centerContainer">
            {formulaType === "boolean"  &&
                <button className="button" onClick={handleSimplify}>Simplify formula</button>}
            {formulaType === "boolean" &&
                <button className="button" onClick={handleCheckFormula}>Check formula</button>}
            {formulaType === "boolean" &&
                <button className="button" onClick={handleAllModels}>All models</button>}

            {formulaType === "ctl" &&
                <button className="button" onClick={handleCheckModel}>Check model</button>}
        </div>
    )
}

const getResultFromJSON = (data) => `${JSON.parse(JSON.stringify(data))['result']}`

const formatModel = (raw_model) => raw_model.replace(/\n/g, "").replace(/;/g, "_")
