import React, {useState} from "react"
import Solver from './components/solver/Solver'
import Model from "./components/model/Model";

export default function App() {
    const [formulaType, setFormulaType] = useState("boolean")
    const [formula, setFormula] = useState("")
    const [solution, setSolution] = useState("")
    const [solutionInfo, setSolutionInfo] = useState("")
    const [model, setModel] = useState("")

    return (
        <div>
            <Solver
                formula={formula}
                setFormula={setFormula}
                solution={solution}
                setSolution={setSolution}
                solutionInfo={solutionInfo}
                setSolutionInfo={setSolutionInfo}
                model={model}
            />
            <Model
                setFormulaType={setFormulaType}
                setFormula={setFormula}
                setSolution={setSolution}
                setSolutionInfo={setSolutionInfo}
                model={model}
                setModel={setModel}
            />
        </div>
    )
}