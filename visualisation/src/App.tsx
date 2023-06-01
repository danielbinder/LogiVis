import React, {useState} from "react"
import Solver from './components/Solver'
import Model from "./components/Model";

export default function App() {
    return (
        <div>
            <Solver />
            <Model />
        </div>
    )
}
