import React, {useState} from "react";

export default function ModelEncoder({setFormulaType, setFormula,
                                         setEvalStatusMessage,
                                         setSolution, setSolutionInfo,
                                         kripke}) {
    const [loading, setLoading] = useState(false)
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
        setLoading(true)
        fetch(urls.get(generationParameters.encodingType) +
            kripke().replaceAll(';', ',') + '/' + generationParameters.steps)
            .then(response => {
                if(!response.ok) {
                    setLoading(false)
                }

                return response
            })
            .then(response => response.json())
            .then(data => {
                if(generationParameters.encodingType === 'naive' || generationParameters.encodingType === 'compact') {
                    setFormulaType('boolean')
                    setFormula(getResultFromJSON(data))
                } else {    // encodingType === 'compactQBF'
                    setSolution(getResultFromJSON(data).replace(/[+]/g, "\n"))
                    setEvalStatusMessage(generateLimbooleLink(data))
                }

                // Add truth table for compact and compactQBF
                if( generationParameters.encodingType === 'compact' || generationParameters.encodingType === 'compactQBF') {
                    delete data['result']
                    setSolutionInfo(data['truth-table'].replace(/[+]/g, "\n"))
                }
            })
            .then(() => setLoading(false))
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
                        <label htmlFor="compactQBF">Limboole QBF</label>
                    </div>
                </div>
                <div className="centerContainer">
                    <button className="button" onClick={handleButtonClick}>
                        {loading && <div className="loading"></div>}
                        Encode model
                    </button>
                </div>
            </fieldset>
        </div>
    )
}

const getResultFromJSON = (data) => `${JSON.parse(JSON.stringify(data))['result']}`

const generateLimbooleLink = (data) =>
    <a href={"https://maximaximal.github.io/limboole/#2" + getResultFromJSON(data).replace(/[+]/g, "\n")}>Check in Limboole</a>