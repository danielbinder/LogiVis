import {selector} from 'recoil';
import {formulaState, modelState} from './atoms';

export const formulaSelector = selector({
    key: 'formulaSelector',
    get: ({get}) => get(formulaState).replaceAll('\n', '')
})

export const modelSelector = selector({
    key: 'modelSelector',
    get: ({get}) => get(modelState).replaceAll(/(#.*?(\n|$))|\n/g, ' ')
})