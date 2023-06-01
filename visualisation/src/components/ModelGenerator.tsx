import React, {useState} from "react";

export default function ModelGenerator() {
    const [generationParameters, setGenerationParameters] = useState({
            nodes: 4,
            variables: 3,
            minSuccessors: 1,
            maxSuccessors: 3,
            initialNodes: 2,
            allReachable: true
        }
    )

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
        <div className="smallColumn">
            <div className="rows">
                <fieldset className="smallFieldset">
                    <legend>&nbsp;Generate a model&nbsp;</legend>
                    <div>
                        <input
                            className="input"
                            type="text"
                            id="nodes"
                            name="nodes"
                            placeholder="Nodes"
                            value={generationParameters.nodes}
                            onChange={handleChange}
                        />
                        <label htmlFor="nodes">Nodes</label>
                    </div>
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
                    <div>
                        <input
                            className="input"
                            type="text"
                            id="minSuccessors"
                            name="minSuccessors"
                            placeholder="Min. Successors"
                            value={generationParameters.minSuccessors}
                            onChange={handleChange}
                        />
                        <label htmlFor="minSuccessors">Min. Successors</label>
                    </div>
                    <div>
                        <input
                            className="input"
                            type="text"
                            id="maxSuccessors"
                            name="maxSuccessors"
                            placeholder="Max. Successors"
                            value={generationParameters.maxSuccessors}
                            onChange={handleChange}
                        />
                        <label htmlFor="maxSuccessors">Max. Successors</label>
                    </div>
                    <div>
                        <input
                            className="input"
                            type="text"
                            id="initialNodes"
                            name="initialNodes"
                            placeholder="Initial Nodes"
                            value={generationParameters.initialNodes}
                            onChange={handleChange}
                        />
                        <label htmlFor="initialNodes">Initial Nodes</label>
                    </div>
                    <div>
                        <input
                            className="input"
                            type="checkbox"
                            id="allReachable"
                            name="allReachable"
                            checked={generationParameters.allReachable}
                            onChange={handleChange}
                        />
                        <label htmlFor="allReachable">All reachable</label>
                    </div>
                    <div className="centerContainer">
                        <button className="button" onClick={handleButtonClick}>Generate model</button>
                    </div>
                </fieldset>
            </div>
        </div>
    )
}