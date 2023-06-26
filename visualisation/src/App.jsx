import React, {useState} from 'react'
import Solver from './components/solver/Solver'
import Model from './components/model/Model';

export default function App() {
    const [formulaType, setFormulaType] = useState('boolean')
    const [formula, setFormula] = useState('')
    const [solution, setSolution] = useState('')    // when using setSolution(), also set evalStatusMessage
    const [solutionInfo, setSolutionInfo] = useState('')
    const [model, setModel] = useState('')
    // status messages
    const [evalStatusMessage, setEvalStatusMessage] = useState('')
    const [evalWarningMessage, setEvalWarningMessage] = useState('')
    const [evalErrorMessage, setEvalErrorMessage] = useState('')
    const [modelStatusMessage, setModelStatusMessage] = useState('')
    const [modelWarningMessage, setModelWarningmessage] = useState('')
    const [modelErrorMessage, setModelErrorMessage] = useState('');

    const setFormulaTab = (data) => {
        setFormula(data['result'].replaceAll(/[$]/g, '\n'))
        setSolutionInfo(data['info'].replaceAll(/[$]/g, '\n'))
        setEvalStatusMessage('')
        setEvalWarningMessage(data['warning'].replaceAll(/[$]/g, '\n'))
        setEvalErrorMessage(data['error'].replaceAll(/[$]/g, '\n'))
    }

    const setSolutionTab = (data) => {
        setSolution(data['result'].replaceAll(/[$]/g, '\n'))
        setSolutionInfo(data['info'].replaceAll(/[$]/g, '\n'))
        setEvalStatusMessage('')
        setEvalWarningMessage(data['warning'].replaceAll(/[$]/g, '\n'))
        setEvalErrorMessage(data['error'].replaceAll(/[$]/g, '\n'))
        return data
    }

    const setModelTab = (data) => {
        console.log(data)
        setModel(data['result']
            .replaceAll(/[$]/g, '\n')
            .replaceAll(/_/g, ';')
            .replaceAll(/[+]/g, '\n'))
        setSolutionInfo(data['info'].replaceAll(/[$]/g, '\n'))
        setModelStatusMessage('')
        setModelWarningmessage(data['warning'].replaceAll(/[$]/g, '\n'))
        setModelErrorMessage(data['error'].replaceAll(/[$]/g, '\n'))
    }

    const getFormula = () => formula.replaceAll('\n', '')

    return (
        <div>
            <Solver
                formulaType={formulaType}
                formula={formula}
                getFormula={getFormula}
                setFormulaType={setFormulaType}
                setFormula={setFormula}
                evalStatusMessage={evalStatusMessage}
                setEvalStatusMessage={setEvalStatusMessage}
                evalWarningMessage={evalWarningMessage}
                evalErrorMessage={evalErrorMessage}
                solution={solution}
                setSolution={setSolution}
                solutionInfo={solutionInfo}
                setSolutionInfo={setSolutionInfo}
                setFormulaTab={setFormulaTab}
                setSolutionTab={setSolutionTab}
                model={model}
            />
            <Model
                setFormulaType={setFormulaType}
                setFormulaTab={setFormulaTab}
                setSolutionTab={setSolutionTab}
                setEvalStatusMessage={setEvalStatusMessage}
                modelStatusMessage={modelStatusMessage}
                modelWarningMessage={modelWarningMessage}
                modelErrorMessage={modelErrorMessage}
                model={model}
                setModel={setModel}
                setModelTab={setModelTab}
            />
        </div>
    )
}