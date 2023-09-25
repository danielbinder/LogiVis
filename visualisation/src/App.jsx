import React from 'react'
import Solver from './components/solver/Solver'
import Model from './components/model/Model'
import {useRecoilState, useSetRecoilState} from 'recoil';
import {
    evalErrorMessageState, evalStatusMessageState, evalWarningMessageState,
    formulaState, modelErrorMessageState, modelState, modelStatusMessageState, modelWarningMessageState,
    solutionInfoState, solutionState
} from './components/atoms';
import {cleanResultData, solutionInfoWarning} from './components/constants';

export default function App() {
    const setFormula = useSetRecoilState(formulaState)
    const setSolution = useSetRecoilState(solutionState)
    const [solutionInfo, setSolutionInfo] = useRecoilState(solutionInfoState)
    const setModel = useSetRecoilState(modelState)
    // status messages
    const setEvalStatusMessage = useSetRecoilState(evalStatusMessageState)
    const setEvalWarningMessage = useSetRecoilState(evalWarningMessageState)
    const setEvalErrorMessage = useSetRecoilState(evalErrorMessageState)
    const setModelStatusMessage = useSetRecoilState(modelStatusMessageState)
    const setModelWarningMessage = useSetRecoilState(modelWarningMessageState)
    const setModelErrorMessage = useSetRecoilState(modelErrorMessageState)

    const setFormulaTab = (data) => {
        data['result'] && setFormula(cleanResultData(data['result']))
        data['info'] && setSolutionInfo(cleanResultData(data['info']))
        setEvalStatusMessage(cleanResultData(data['info']).split('\n').length > 500 ? solutionInfoWarning : '')
        setEvalWarningMessage(cleanResultData(data['warning']))
        setEvalErrorMessage(cleanResultData(data['error']))

        return data
    }

    const setSolutionTab = (data) => {
        data['result'] && setSolution(cleanResultData(data['result']))
        data['info'] && setSolutionInfo(cleanResultData(data['info']))
        setEvalStatusMessage(cleanResultData(data['info']).split('\n').length > 500 ? solutionInfoWarning : '')
        setEvalWarningMessage(cleanResultData(data['warning']))
        setEvalErrorMessage(cleanResultData(data['error']))

        return data
    }

    const setModelTab = (data) => {
        data['result'] && setModel(cleanResultData(data['result']))
        data['info'] && setSolutionInfo(cleanResultData(data['info']))
        setModelStatusMessage(cleanResultData(data['info']).split('\n').length > 500 ? solutionInfoWarning : '')
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