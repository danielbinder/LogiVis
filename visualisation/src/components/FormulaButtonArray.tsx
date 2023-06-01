import React, {useState} from "react";

export default function FormulaButtonArray() {
    function handleButtonClick() {

    }

    return (
        <div className="centerContainer">
            <button className="button" onClick={handleButtonClick}>Simplify formula</button>
            <button className="button" onClick={handleButtonClick}>Check formula</button>
            <button className="button" onClick={handleButtonClick}>All models</button>
        </div>
    )
}