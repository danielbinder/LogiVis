import React, {useState} from "react";

export default function FormulaGenerator() {
    const [generationParameters, setGenerationParameters] = useState({
        variables: 3,
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
                <legend>&nbsp;Generate a formula&nbsp;</legend>
                <div>
                    <div>
                        <input
                            className="input"
                            type="text"
                            id="variables"
                            name="variables"
                            placeholder="Variables"
                            value={generationParameters.variables}
                            onChange={handleChange}
                        />
                        <label htmlFor="variables">Variables</label>
                    </div>
                </div>
                <div className="centerContainer">
                    <button className="button" onClick={handleButtonClick}>Generate formula</button>
                </div>
            </fieldset>
        </div>
    )
}