import React, {useState} from "react";

export default function ModelEncoder({setFormulaType, setFormula, setSolutionInfo, kripke}) {
    const [generationParameters, setGenerationParameters] = useState({
        steps: 3,
        compact: true
    })

    function handleChange({target: {name, value, type, checked}}) {
        setGenerationParameters(
            prevGenerationParameters => ({
                ...prevGenerationParameters,
                [name]: type === "checkbox" ? checked : value
            })
        )
    }

    function handleButtonClick() {
        fetch('http://localhost:4000/kripke2' + (generationParameters.compact ? 'CompactF' : 'f') + 'ormula/' +
            kripke().replaceAll(';', ',') + '/' + generationParameters.steps)
            .then(response => response.json())
            .then(data => {
                setFormulaType('boolean')
                setFormula(getResultFromJSON(data))
                if(generationParameters.compact) {
                    delete data['result']
                    setSolutionInfo(data['truth-table'].replace(/[+]/g, "\n"))
                }
            })
    }

    return (
        <div className="rows">
            <fieldset className="smallFieldset">
                <legend>&nbsp;Encode a model&nbsp;</legend>
                <div>
                    <div>
                        <input
                            className="input"
                            type="text"
                            id="steps"
                            name="steps"
                            placeholder="Steps"
                            value={generationParameters.steps}
                            onChange={handleChange}
                        />
                        <label htmlFor="steps">Steps</label>
                    </div>
                    <input
                        className="input"
                        type="checkbox"
                        id="compact"
                        name="compact"
                        checked={generationParameters.compact}
                        onChange={handleChange}
                    />
                    <label htmlFor="compact">Compact</label>
                </div>
                <div className="centerContainer">
                    <button className="button" onClick={handleButtonClick}>Encode model</button>
                </div>
            </fieldset>
        </div>
    )
}

const getResultFromJSON = (data) => `${JSON.parse(JSON.stringify(data))['result']}`