import {selector} from 'recoil';
import {formulaState, modelState} from './atoms';

export const formulaSelector = selector({
    key: 'formulaSelector',
    get: ({get}) => get(formulaState).replaceAll('\n', '$')
})

export const modelSelector = selector({
    key: 'modelSelector',
    get: ({get}) => get(modelState).split(';')[0].replaceAll(/(#.*?(\n|$))|\n/g, '$')
})

export const secondModelSelector = selector({
    key: 'secondModelSelector',
    get: ({get}) => get(modelState).split(';')[1]?.replaceAll(/(#.*?(\n|$))|\n/g, '$')
})