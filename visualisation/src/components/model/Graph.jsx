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
    const states = model.match(/S\s*=\s*[{].*?[}]/g)[0]
        .replace(/(S\s*=\s*[{])|}/g, '')
        .split(',')
    const initial = (/I\s*=\s*[{]\s*[}]/g).test(model) ? [] : model.match(/I\s*=\s*[{].*?[}]/g)?.[0]
        .replace(/(I\s*=\s*[{])|}/g, '')
        .split(',')
        ?? []
    const transitions = (/T\s*=\s*[{]\s*[}]/g).test(model) ? [] : model.match(/T\s*=\s*[{].*?[}]/g)?.[0]
        .replace(/(T\s*=\s*[{])|}/g, '')
        .match(/\(.+?\)(\s*\[.*?])?/g)
        ?? []
    const final = (/F=[{]\s*[}]/g).test(removeWhiteSpaces(model)) ? [] : removeWhiteSpaces(model).match(/F=[{].*?[}]/g)?.[0]
        .replace('F={', '')
        .replace('}', '')
        .split(',')
        ?? []

    return states
        .map(s => {
            const withoutLbl = withoutLabel(s)
            const label = getLabel(s)

            return `${removeStateDescriptors(withoutLbl)} ` +
            `[label="${label ? label : removeStateDescriptors(s)}" fontcolor="#c7c7c7" ` +
            (withoutLbl.includes('>') && withoutLbl.includes('<')
                ? `style=filled fillcolor="#1a7a5a" `
                : withoutLbl.includes('>')
                    ? `style=filled fillcolor="#1a4a7a" `
                    : withoutLbl.includes('<')
                        ? `style=filled fillcolor="#1a7a2a" `
                        : ``) +
            `${final.includes(removeStateDescriptors(withoutLabel(removeWhiteSpaces(s)))) ? 'shape=doublecircle' : 'shape=circle'} ` +
            `color="#c7c7c7"];\n`
        }).join('') +
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
        if(t.includes('->')) return t.split('->').map(createGraphNodeFromCState).join('')
        else if(t.includes('-')) return t.split('-').map(createGraphNodeFromCState).join('')
        else return createGraphNodeFromCState(t)
    }).join('') +
        transitions.map(t => {
            const index = transitions.indexOf(t)

            if(t.includes('->'))
                return createInitialAndFinalNode(index, t.split('->')[0], 'l') +
                createInitialAndFinalNode(index, t.split('->')[1], 'r')
            else if(t.includes('-'))
                return createInitialAndFinalNode(index, t.split('-')[0], 'l') +
                createInitialAndFinalNode(index, t.split('-')[1], 'r')
            else return createInitialAndFinalNode(index, t, 'c')
        }).join('') +
        transitions.map(t => {
            if(t.includes('->'))
                return `${getStateName(t.split('->')[0])} -> ${getStateName(t.split('->')[1])} ` +
                    `[color="#c7c7c7" fontcolor="#c7c7c7" label="${getCTransitionLabel(t)}"];\n`
            else if(t.includes('-'))
                return `${getStateName(t.split('-')[0])} -> ${getStateName(t.split('-')[1])} ` +
                    `[color="#c7c7c7" fontcolor="#c7c7c7" label="${getCTransitionLabel(t)}" dir="both"];\n`
            else return ''
        }).join('')
}

const getStateName = (s) => removeStateDescriptors(withoutLabel(s))

const removeWhiteSpaces = (s) => s.replaceAll(/\s+/g,'')

const removeComments = (s) => {
    const lines = s.split('\n')
    let result = ''

    for(const line of lines) {
        const commentIndex = line.indexOf('#')
        result += commentIndex === -1 ? line : line.substring(0, commentIndex) + '\n'
    }

    return result
}

const getLabel = (s) => {
    const startIndex = s.indexOf('[');
    if(startIndex !== -1) return s.substring(startIndex + 1, s.indexOf(']', startIndex))

    return ''
}

const getCStateLabel = (s) => {
    const match = s.match(/[a-z][a-z0-9]*[_*><\s]*\[.*?]/)
    return match ? match[0].replace(/[a-z][a-z0-9]*[_*><\s]*\[/g, '').replace(']', '') : ''
}

const getCTransitionLabel = (s) => {
    const match = s.match(/->?\s*\[.*?]/)
    return match ? match[0].replace(/->?\s*\[|]/g, '') : ''
}

const withoutLabel = (s) => s.includes('[')
    ? removeWhiteSpaces(s.replace(/\[.*?]/g, ''))
    : removeWhiteSpaces(s)

const removeStateDescriptors = (s) => s.replace(/[_*><]/g, '')

const createGraphNodeFromCState = (s) => {
    const cStateLabel = getCStateLabel(s)
    const withoutLbl = withoutLabel(s)

    return `${getStateName(s)} ` +
    `[${cStateLabel ? `label="${cStateLabel}"` : ''} fontcolor="#c7c7c7" ` +
    (withoutLbl.includes('>') && withoutLbl.includes('<')
        ? `style=filled fillcolor="#1a7a5a" `
        : withoutLbl.includes('>')
            ? `style=filled fillcolor="#1a4a7a" `
            : withoutLbl.includes('<')
                ? `style=filled fillcolor="#1a7a2a" `
                : ``) +
    `${withoutLbl.includes('*') ? 'shape=doublecircle' : 'shape=circle'} ` +
    `color="#c7c7c7"];\n`
}

const createInitialAndFinalNode = (index, s, uniqueAddition) => {
    const stateName = getStateName(s)
    const withoutLbl = withoutLabel(s)

    return (withoutLbl.includes('_')
        ? `none${index + uniqueAddition} [shape=none label=""];\n` +
            `none${index + uniqueAddition} -> ${stateName} ` +
            `[color="#c7c7c7"];\n`
        : '') +
        (withoutLbl.includes('*')
            ? `${stateName} [shape=doublecircle];\n`
            : '')
}