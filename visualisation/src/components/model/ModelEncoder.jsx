import React, {useState} from "react";

export default function ModelEncoder({setSolution, setSolutionInfo, kripke}) {
    const [generationParameters, setGenerationParameters] = useState({
        steps: 3,
        encodingType: 'compact'
    })

    const urls = new Map([
        ['naive', 'http://localhost:4000/kripke2formula/'],
        ['compact', 'http://localhost:4000/kripke2compactFormula/'],
        ['compactQBF', 'http://localhost:4000/kripke2compactQBFFormula/']
    ])

    function handleChange({target: {name, value, type, checked}}) {
        setGenerationParameters(
            prevGenerationParameters => ({
                ...prevGenerationParameters,
                [name]: type === "checkbox" ? checked : value
            })
        )
    }

    function handleButtonClick() {
        fetch(urls.get(generationParameters.encodingType) +
            kripke().replaceAll(';', ',') + '/' + generationParameters.steps)
            .then(response => response.json())
            .then(data => {
                setSolution(getResultFromJSON(data))
                if(generationParameters.encodingType === "compact" ||
                    generationParameters.encodingType === "compactQBF") {
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
                    <div className="bottomSpace">
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
                    <div>
                        <input
                            type="radio"
                            id="naive"
                            name="encodingType"
                            value="naive"
                            checked={generationParameters.encodingType === "naive"}
                            onChange={handleChange}
                        />
                        <label htmlFor="naive">Naive</label>
                    </div>
                    <div>
                        <input
                            type="radio"
                            id="compact"
                            name="encodingType"
                            value="compact"
                            checked={generationParameters.encodingType === "compact"}
                            onChange={handleChange}
                        />
                        <label htmlFor="compact">Compact</label>
                    </div>
                    <div>
                        <input
                            type="radio"
                            id="compactQBF"
                            name="encodingType"
                            value="compactQBF"
                            checked={generationParameters.encodingType === "compactQBF"}
                            onChange={handleChange}
                        />
                        <label htmlFor="compactQBF">Compact QBF</label>
                    </div>
                </div>
                <div className="centerContainer">
                    <button className="button" onClick={handleButtonClick}>Encode model</button>
                </div>
            </fieldset>
        </div>
    )
}

const getResultFromJSON = (data) => `${JSON.parse(JSON.stringify(data))['result']}`