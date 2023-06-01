import React, {ChangeEvent, useState} from "react";
import FormulaTypeSelection from "./FormulaTypeSelection";
import FormulaButtonArray from "./FormulaButtonArray";

export default function Solver() {
    const [formula, setFormula] = useState("")
    const [solution, setSolution] = useState("")
    const [solutionHints, setSolutionHints] = useState("")
    const [errorMessage, setErrorMessage] = useState("")

    function handleChange(event: { target: { value: React.SetStateAction<string>; }; }) {
        setFormula(event.target.value)
    }

    return (
        <div className="column">
            <h3 className="center">Evaluate a formula</h3>
            <FormulaTypeSelection />
            <textarea
                className="textArea"
                value={formula}
                placeholder="Enter a formula"
                onChange={handleChange}
                name="formula"
            />
            <FormulaButtonArray />
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
                value={solutionHints}
                placeholder="Solution information"
                name="solutionInfo"
            />
        </div>
    )
}