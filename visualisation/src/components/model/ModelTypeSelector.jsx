import React from "react";

export default function ModelTypeSelector({modelType, setModelType}) {
    return (
        <div className="rows">
            <fieldset className="smallFieldset">
                <legend>&nbsp;Choose a model type&nbsp;</legend>
                <div>
                    <input
                        type="radio"
                        id="kripke"
                        name="modelTypeSelection"
                        value="kripke"
                        checked={modelType === "kripke"}
                        onChange={() => setModelType("kripke")}
                    />
                    <label htmlFor="kripke">Kripke Structure</label>
                </div>
                <div>
                    <input
                        type="radio"
                        id="mealy"
                        name="modelTypeSelection"
                        value="mealy"
                        checked={modelType === "mealy"}
                        onChange={() => setModelType("mealy")}
                    />
                    <label htmlFor="mealy">Mealy Automaton</label>
                </div>
                <div>
                    <input
                        type="radio"
                        id="moore"
                        name="modelTypeSelection"
                        value="moore"
                        checked={modelType === "moore"}
                        onChange={() => setModelType("moore")}
                    />
                    <label htmlFor="moore">Moore Automaton</label>
                </div>
            </fieldset>
        </div>
    )
}