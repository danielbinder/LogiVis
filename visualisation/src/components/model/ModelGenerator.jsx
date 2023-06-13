import React, {useState} from "react";

export default function ModelGenerator({setModel}) {
    const [loading, setLoading] = useState(false)
    const [generationParameters, setGenerationParameters] = useState({
            nodes: 4,
            variables: 3,
            minSuccessors: 1,
            maxSuccessors: 3,
            initialNodes: 2,
            allReachable: true
        }
    )

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
        fetch('http://localhost:4000/generate/' +
            generationParameters.nodes + '_' +
            generationParameters.initialNodes + '_' +
            generationParameters.variables + '_' +
            generationParameters.minSuccessors + '_' +
            generationParameters.maxSuccessors + '_' +
            generationParameters.allReachable)
            .then(response => {
                if(!response.ok) {
                    setLoading(false)
                }

                return response
            })
            .then(response => response.json())
            .then(data => getResultFromJSON(data).replaceAll(';', ','))
            .then(data => {
                fetch('http://localhost:4000/kripkeString2ModelString/' + data)
                    .then(response => {
                        if(!response.ok) {
                            setLoading(false)
                        }

                        return response
                    })
                    .then(response => response.json())
                    .then(data => setModel(getResultFromJSON(data)
                        .replace(/_/g, ";")
                        .replace(/[+]/g, "\n")))
                    .then(() => setLoading(false))
            })
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
                        <button className="button" onClick={handleButtonClick}>
                            {loading && <div className="loading"></div>}
                            Generate Model
                        </button>
                    </div>
                </fieldset>
            </div>
        </div>
    )
}

const getResultFromJSON = (data) => `${JSON.parse(JSON.stringify(data))['result']}`