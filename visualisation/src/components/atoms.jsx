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
    default: '',
    effects: [syncEffect({refine: string()})]
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

// status messages

export const evalStatusMessageState = atom({
    key: 'evalStatusMessage',
    default: '',
    effects: [syncEffect({refine: string()})]
})

export const evalWarningMessageState = atom({
    key: 'evalWarningMessage',
    default: '',
    effects: [syncEffect({refine: string()})]
})

export const evalErrorMessageState = atom({
    key: 'evalErrorMessage',
    default: '',
    effects: [syncEffect({refine: string()})]
})

export const modelStatusMessageState = atom({
    key: 'modelStatusMessage',
    default: '',
    effects: [syncEffect({refine: string()})]
})

export const modelWarningMessageState = atom({
    key: 'modelWarningMessage',
    default: '',
    effects: [syncEffect({refine: string()})]
})

export const modelErrorMessageState = atom({
    key: 'modelErrorMessage',
    default: '',
    effects: [syncEffect({refine: string()})]
})