import React from "react";

export default function FormulaTypeSelection({ formulaType, setFormulaType }) {
    return (
        <fieldset className="fieldset">
            <legend>&nbsp;Choose a formula type&nbsp;</legend>
            <input
                type="radio"
                id="boolean"
                name="formulaTypeSelection"
                value="boolean"
                checked={formulaType === "boolean"}
                onChange={() => setFormulaType("boolean")}
            />
            <label htmlFor="boolean">Boolean Algebra</label>
            <input
                type="radio"
                id="ctl"
                name="formulaTypeSelection"
                value="ctl"
                checked={formulaType === "ctl"}
                onChange={() => setFormulaType("ctl")}
            />
            <label htmlFor="ctl">CTL Expression</label>
            <input
                type="radio"
                id="regex"
                name="formulaTypeSelection"
                value="regex"
                checked={formulaType === "regex"}
                onChange={() => setFormulaType("regex")}
            />
            <label htmlFor="regex">Regular Expression</label>
            <input
                type="radio"
                id="process"
                name="formulaTypeSelection"
                value="process"
                checked={formulaType === "process"}
                onChange={() => setFormulaType("process")}
            />
            <label htmlFor="process">Process Algebra</label>
            <input 
                type="radio"
                id="qbf"
                name="formulaTypeSelection"
                value="qbf"
                checked={formulaType === "qbf"}
                onChange={() => setFormulaType("qbf")}
            />
            <label htmlFor="qbf">QBF</label>
        </fieldset>
    )
}