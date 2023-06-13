import React, {useState} from "react";
import FormulaTypeSelection from "./FormulaTypeSelection";
import FormulaButtonArray from "./FormulaButtonArray";

export default function Solver({formulaType, setFormulaType,
                                   formula, setFormula,
                                   solution, setSolution,
                                   solutionInfo, setSolutionInfo,
                                   model}) {
    const [errorMessage, setErrorMessage] = useState("")

    function handleChange({target}) {
        setFormula(target.value)
    }

    return (
        <div className="column">
            <h3 className="center">Evaluate a formula</h3>
            <FormulaTypeSelection formulaType={formulaType} setFormulaType={setFormulaType}/>
            <textarea
                className="textArea"
                value={formula}
                placeholder="Enter a formula"
                onChange={handleChange}
                name="formula"
            />
            <FormulaButtonArray
                formulaType={formulaType}
                formula={formula}
                model={model}
                setFormula={setFormula}
                setSolution={setSolution}
                setSolutionInfo={setSolutionInfo}
            />
            <p className="red">
                {errorMessage}
            </p>
            <textarea
                readOnly={true}
                className="textArea"
                value={solution}
                placeholder="Solution"
                name="solution"
            />
            <textarea
                readOnly={true}
                className="textArea"
                value={solutionInfo}
                placeholder="Solution information"
                name="solutionInfo"
            />
        </div>
    )
}