import React from 'react'
import Solver from './components/solver/Solver'
import Model from './components/model/Model'
import {useSetRecoilState} from 'recoil';
import {
    evalErrorMessageState, evalStatusMessageState, evalWarningMessageState,
    formulaState, modelErrorMessageState, modelState, modelStatusMessageState, modelWarningMessageState,
    solutionInfoState, solutionState
} from './components/atoms';
import {cleanResultData} from './components/constants';

export default function App() {
    const setFormula = useSetRecoilState(formulaState)
    const setSolution = useSetRecoilState(solutionState)
    const setSolutionInfo = useSetRecoilState(solutionInfoState)
    const setModel = useSetRecoilState(modelState)
    // status messages
    const setEvalStatusMessage = useSetRecoilState(evalStatusMessageState)
    const setEvalWarningMessage = useSetRecoilState(evalWarningMessageState)
    const setEvalErrorMessage = useSetRecoilState(evalErrorMessageState)
    const setModelStatusMessage = useSetRecoilState(modelStatusMessageState)
    const setModelWarningMessage = useSetRecoilState(modelWarningMessageState)
    const setModelErrorMessage = useSetRecoilState(modelErrorMessageState)

    const setFormulaTab = (data) => {
        setFormula(cleanResultData(data['result']))
        setSolutionInfo(cleanResultData(data['info']))
        setEvalStatusMessage('')
        setEvalWarningMessage(cleanResultData(data['warning']))
        setEvalErrorMessage(cleanResultData(data['error']))

        return data
    }

    const setSolutionTab = (data) => {
        setSolution(cleanResultData(data['result']))
        setSolutionInfo(cleanResultData(data['info']))
        setEvalStatusMessage('')
        setEvalWarningMessage(cleanResultData(data['warning']))
        setEvalErrorMessage(cleanResultData(data['error']))

        return data
    }

    const setModelTab = (data) => {
        setModel(cleanResultData(data['result']))
        setSolutionInfo(cleanResultData(data['info']))
        setModelStatusMessage('')
        setModelWarningMessage(cleanResultData(data['warning']))
        setModelErrorMessage(cleanResultData(data['error']))

        return data
    }

    return (
        <div>
            <Solver
                setFormulaTab={setFormulaTab}
                setSolutionTab={setSolutionTab}
            />
            <Model
                setFormulaTab={setFormulaTab}
                setSolutionTab={setSolutionTab}
                setModelTab={setModelTab}
            />
        </div>
    )
}