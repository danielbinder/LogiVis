import {atom} from 'recoil'
import {syncEffect} from 'recoil-sync'
import {string} from '@recoiljs/refine';

export const formulaTypeState = atom({
    key: 'formulaType',
    default: 'boolean',
    effects: [syncEffect({refine: string()})]
})

export const formulaState = atom({
    key: 'formula',
    default: '',
    effects: [syncEffect({refine: string()})]
})

// when using setSolution(), also set evalStatusMessage
export const solutionState = atom({
    key: 'solution',
    default: '',
    effects: [syncEffect({refine: string()})]
})

export const solutionInfoState = atom({
    key: 'solutionInfo',
    default: ''
})

export const modelTypeState = atom({
    key: 'modelType',
    default: 'kripke',
    effects: [syncEffect({refine: string()})]
})

export const modelState = atom({
    key: 'model',
    default: '',
    effects: [syncEffect({refine: string()})]
})

export const darkModeState = atom({
    key: 'darkMode',
    default: true
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