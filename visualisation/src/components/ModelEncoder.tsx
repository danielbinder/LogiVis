import React, {useState} from "react";

export default function ModelEncoder() {
    const [generationParameters, setGenerationParameters] = useState({
        steps: 3,
        compact: true
    })

    function handleChange(event: { target: { name: string; value: string; type: string; checked: boolean; }; }) {
        const {name, value, type, checked} = event.target

        setGenerationParameters(
            prevGenerationParameters => ({
                ...prevGenerationParameters,
                [name]: type === "checkbox" ? checked : value
            })
        )
    }

    function handleButtonClick() {

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