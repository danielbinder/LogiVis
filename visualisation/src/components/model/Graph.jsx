import React, {useEffect, useState} from 'react';
import Graphviz from 'graphviz-react';
import {useRecoilState, useSetRecoilState} from 'recoil';
import {modelState, modelStatusMessageState} from '../atoms';
import {modelPlaceholder, compactModelPlaceholder} from '../constants';

export default function Graph() {
    const setModelStatusMessage = useSetRecoilState(modelStatusMessageState)
    const [model, setModel] = useRecoilState(modelState)
    const [graph, setGraph] = useState('')

    // whenever 'model' changes, the graph attempts to update
    useEffect(() => {
        setModelStatusMessage('')

        if(model === 'this') {
            setModel(modelPlaceholder)
        } else if(model === 'compact') {
            setModel(compactModelPlaceholder)
        }

        try {
            setGraph(model2Graph(model))
        } catch (e) {}
    }, [model])

    return (
        <div>
            {graph && <Graphviz className='graph' dot={graph}/>}
        </div>)
}

const model2Graph = (model) => {
    const result = 'digraph {\n' +
        'ratio="0.5";\n' +
        'rankdir=LR;\n' +
        'bgcolor="#1c1c1c";\n'

    model = removeComments(model).replaceAll('\n', ' ')

    return result + ((/S\s*?=\s*?\{.*?}/g).test(model) ? traditionalModel2Graph(model) : compactModel2Graph(model)) + '}'
}

const traditionalModel2Graph = (model) => {
    const states = model.match(/S\s*?=\s*?[{].*?[}]/g)[0]
        .replaceAll(/S\s*?=\s*?[{]/g, '')
        .replace('}', '')
        .split(',')
    const initial = model.match(/I\s*?=\s*?[{].*?[}]/g)?.[0]
        .replaceAll(/I\s*?=\s*?[{]/g, '')
        .replace('}', '')
        .split(',')
        ?? []
    const transitions = model.match(/T\s*?=\s*?[{].*?[}]/g)?.[0]
        .replaceAll(/T\s*?=\s*?[{]/g, '')
        .replace('}', '')
        .match(/\(.+?\)(\s*?\[.*?])?/g)
        ?? []
    const final = removeWhiteSpaces(model).match(/F=[{].*?[}]/g)?.[0]
        .replace('F={', '')
        .replace('}', '')
        .split(',')
        ?? []

    return states
        .map(s =>
            `${removeStateDescriptors(withoutLabel(s))} ` +
            `[label="${getLabel(s)}" fontcolor="#c7c7c7" ` +
            (withoutLabel(s).includes('>') && withoutLabel(s).includes('<')
                ? `style=filled fillcolor="#1a7a5a" `
                : withoutLabel(s).includes('>')
                    ? `style=filled fillcolor="#1a4a7a" `
                    : withoutLabel(s).includes('<')
                        ? `style=filled fillcolor="#1a7a2a" `
                        : ``) +
            `${final.includes(removeStateDescriptors(withoutLabel(removeWhiteSpaces(s)))) ? 'shape=doublecircle' : 'shape=circle'} ` +
            `color="#c7c7c7"];\n`).join('') +
        initial.map(s => `none${initial.indexOf(s)} [shape=none label=""];\n`).join('') +
        initial.map(s => `none${initial.indexOf(s)} -> ${withoutLabel(s)} ` +
            `[color="#c7c7c7" fontcolor="#c7c7c7" label="${getLabel(s)}"];\n`).join('') +
        transitions.map(t =>
            `${withoutLabel(t).replace('(', '')
                .replace(')', '')
                .replace(',', ' -> ')} ` +
            `[color="#c7c7c7" fontcolor="#c7c7c7" label="${getLabel(t)}"];\n`).join('')
}

const compactModel2Graph = (model) => {
    const transitions = model.split(',')

    return transitions.map(t => {
        if(t.includes('->')) {
            return t.split('->').map(createGraphNodeFromCState).join('')
        } else if(t.includes('-')) {
            return t.split('-').map(createGraphNodeFromCState).join('')
        } else {
            return createGraphNodeFromCState(t)
        }
    }).join('') +
        transitions.map(t => {
            if(t.includes('->')) {
                return createInitialAndFinalNode(transitions.indexOf(t), t.split('->')[0], 'l') +
                createInitialAndFinalNode(transitions.indexOf(t), t.split('->')[1], 'r')
            } else if(t.includes('-')) {
                return createInitialAndFinalNode(transitions.indexOf(t), t.split('-')[0], 'l') +
                createInitialAndFinalNode(transitions.indexOf(t), t.split('-')[1], 'r')
            } else {
                return createInitialAndFinalNode(transitions.indexOf(t), t, 'c')
            }
        }).join('') +
        transitions.map(t => {
            if(t.includes('->')) {
                return `${getStateName(t.split('->')[0])} -> ${getStateName(t.split('->')[1])} ` +
                    `[color="#c7c7c7" fontcolor="#c7c7c7" label="${getCTransitionLabel(t)}"];\n`
            } else if(t.includes('-')) {
                return `${getStateName(t.split('-')[0])} -> ${getStateName(t.split('-')[1])} ` +
                    `[color="#c7c7c7" fontcolor="#c7c7c7" label="${getCTransitionLabel(t)}" dir="both"];\n`
            }

            return ''
        }).join('')
}

const getStateName = (s) => removeStateDescriptors(withoutLabel(s))

const removeWhiteSpaces = (s) => s.replaceAll(/\s/g,'')

const removeComments = (s) => s.replaceAll(/#.*?(\n|$)/g, '\n')

const getLabel = (s) => s.includes('[')
    ? s.match(/\[.*?]/g)[0].replace('[', '').replace(']', '')
    : ''

const getCStateLabel = (s) => (/[a-z]+([a-z]*[0-9]*)*[_*><\s]*?\[/g).test(s)
    ? s.match(/[a-z]+([a-z]*[0-9]*)*[_*><\s]*?\[.*?]/g)[0].replaceAll(/[a-z]+([a-z]*[0-9]*)*[_*><\s]*?\[/g, '').replace(']', '')
    : ''

const getCTransitionLabel = (s) => (/->?\s*?\[/g).test(s)
    ? s.match(/->?\s*?\[.*?]/g)[0].replaceAll(/->?\s*?\[/g, '').replace(']', '')
    : ''

const withoutLabel = (s) => s.includes('[')
    ? removeWhiteSpaces(s.replaceAll(/\[.*?]/g, ''))
    : removeWhiteSpaces(s)

const removeStateDescriptors = (s) => s.replace('_', '').replace('*', '').replace('>', '').replace('<', '')

const createGraphNodeFromCState = (s) =>
    `${getStateName(s)} ` +
    `[${getCStateLabel(s) ? `label="${getCStateLabel(s)}"` : ''} fontcolor="#c7c7c7" ` +
    (withoutLabel(s).includes('>') && withoutLabel(s).includes('<')
        ? `style=filled fillcolor="#1a7a5a" `
        : withoutLabel(s).includes('>')
            ? `style=filled fillcolor="#1a4a7a" `
            : withoutLabel(s).includes('<')
                ? `style=filled fillcolor="#1a7a2a" `
                : ``) +
    `${withoutLabel(s).includes('*') ? 'shape=doublecircle' : 'shape=circle'} ` +
    `color="#c7c7c7"];\n`

const createInitialAndFinalNode = (index, s, uniqueAddition) => {
    return (withoutLabel(s).includes('_')
        ? `none${index + uniqueAddition} [shape=none label=""];\n` +
            `none${index + uniqueAddition} -> ${getStateName(s)} ` +
            `[color="#c7c7c7"];\n`
        : '') +
        (withoutLabel(s).includes('*')
            ? `${getStateName(s)} [shape=doublecircle];\n`
            : '')
}