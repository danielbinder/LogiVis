import React, {useState} from "react"
import Solver from './components/solver/Solver'
import Model from "./components/model/Model";

export default function App() {
    const [solution, setSolution] = useState("")
    const [solutionInfo, setSolutionInfo] = useState("")
    const [model, setModel] = useState("")

    return (
        <div>
            <Solver
                solution={solution}
                setSolution={setSolution}
                solutionInfo={solutionInfo}
                setSolutionInfo={setSolutionInfo}
                model={model}
            />
            <Model
                setSolution={setSolution}
                setSolutionInfo={setSolutionInfo}
                model={model}
                setModel={setModel}
            />
        </div>
    )
}