import React, {useState} from "react"
import Solver from './components/solver/Solver'
import Model from "./components/model/Model";

export default function App() {
    const [formula, setFormula] = useState("")
    const [solutionInfo, setSolutionInfo] = useState("")
    const [model, setModel] = useState("")

    return (
        <div>
            <Solver
                formula={formula}
                setFormula={setFormula}
                solutionInfo={solutionInfo}
                setSolutionInfo={setSolutionInfo}
                model={model}
            />
            <Model
                setFormula={setFormula}
                setSolutionInfo={setSolutionInfo}
                model={model}
                setModel={setModel}
            />
        </div>
    )
}