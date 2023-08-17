import {atom} from 'recoil'

export const formulaTypeState = atom({
    key: 'formulaType',
    default: 'boolean'
})

export const formulaState = atom({
    key: 'formula',
    default: ''
})

// when using setSolution(), also set evalStatusMessage
export const solutionState = atom({
    key: 'solution',
    default: ''
})

export const solutionInfoState = atom({
    key: 'solutionInfo',
    default: ''
})

export const modelTypeState = atom({
    key: 'modelType',
    default: 'kripke'
})

export const modelState = atom({
    key: 'model',
    default: ''
})

// status messages

export const evalStatusMessageState = atom({
    key: 'evalStatusMessage',
    default: ''
})

export const evalWarningMessageState = atom({
    key: 'evalWarningMessage',
    default: ''
})

export const evalErrorMessageState = atom({
    key: 'evalErrorMessage',
    default: ''
})

export const modelStatusMessageState = atom({
    key: 'modelStatusMessage',
    default: ''
})

export const modelWarningMessageState = atom({
    key: 'modelWarningMessage',
    default: ''
})

export const modelErrorMessageState = atom({
    key: 'modelErrorMessage',
    default: ''
})