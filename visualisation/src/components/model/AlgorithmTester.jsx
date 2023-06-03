import React, {useState} from "react";

export default function AlgorithmTester() {
    const [algorithm, setAlgorithm] = useState("")

    function handleButtonClick() {

    }

    return (
        <div className="rows">
            <fieldset className="smallFieldset">
                <legend>&nbsp;Test your algorithm&nbsp;</legend>
                <div className="centerContainer">
                    <select
                        className="select"
                        id="algorithm"
                        name="algorithm"
                        value={algorithm}
                        onChange={(event) => setAlgorithm(event.target.value)}
                    >
                        <option className="center" value="">Choose</option>
                        <option value="isDeterministic">isDeterministic</option>
                        <option value="isComplete">isComplete</option>
                        <option value="toProductAutomaton">toProductAutomaton</option>
                        <option value="toPowerAutomaton">toPowerAutomaton</option>
                        <option value="toComplementAutomaton">toComplementAutomaton</option>
                        <option value="toOracleAutomaton">toOracleAutomaton</option>
                        <option value="toOptimizedOracleAutomaton">toOptimizedOracleAutomaton</option>
                        <option value="toIOAutomaton">toIOAutomaton</option>
                    </select>
                </div>
                <div className="centerContainer">
                    <button className="button" onClick={handleButtonClick}>Test algorithm</button>
                </div>
            </fieldset>
        </div>
    )
}