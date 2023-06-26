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
        setFormula(data['result'].replaceAll('$', '\n'))
        setSolutionInfo(data['info'].replaceAll('$', '\n'))
        setEvalStatusMessage('')
        setEvalWarningMessage(data['warning'].replaceAll('$', '\n'))
        setEvalErrorMessage(data['error'].replaceAll('$', '\n'))
    }

    const setSolutionTab = (data) => {
        setSolution(data['result'].replaceAll('$', '\n'))
        setSolutionInfo(data['info'].replaceAll('$', '\n'))
        setEvalStatusMessage('')
        setEvalWarningMessage(data['warning'].replaceAll('$', '\n'))
        setEvalErrorMessage(data['error'].replaceAll('$', '\n'))
        return data
    }

    const setModelTab = (data) => {
        setModel(data['result'].replaceAll('$', '\n')
            .replaceAll(/_/g, ';')
            .replaceAll(/[+]/g, '\n'))
        setSolutionInfo(data['info'].replaceAll('$', '\n'))
        setModelStatusMessage('')
        setModelWarningmessage(data['warning'].replaceAll('$', '\n'))
        setModelErrorMessage(data['error'].replaceAll('$', '\n'))
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
                setFormula={setFormula}
                setEvalStatusMessage={setEvalStatusMessage}
                setSolution={setSolution}
                setSolutionInfo={setSolutionInfo}
                setEvalTab={setSolutionTab}
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