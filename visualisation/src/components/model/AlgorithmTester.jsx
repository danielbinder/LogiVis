import React, {useState} from 'react';
import {algorithmURL} from '../constants';
import {useRecoilValue} from 'recoil';
import {modelSelector, secondModelSelector} from '../selectors';

export default function AlgorithmTester({setSolutionTab, setModelTab}) {
    const [applyLoading, setApplyLoading] = useState(false)
    const [testLoading, setTestLoading] = useState(false)
    const [algorithm, setAlgorithm] = useState('')
    const getModel = useRecoilValue(modelSelector)
    const getSecondModel = useRecoilValue(secondModelSelector)

    function handleApplyClick() {
        setApplyLoading(true)
        fetch(algorithmURL + `/${algorithm}/${getModel}${requiresSecondModel() ? '/' + getSecondModel : ''}`)
            .then(response => response.json())
            .then((data) => algorithm === 'isDeterministic' || algorithm === 'isComplete'
                ? setSolutionTab(data)
                : setModelTab(data))
            .finally(() => setApplyLoading(false))
    }

    function handleTestClick() {
        setTestLoading(true)
        fetch(algorithmURL + `/test${algorithm.charAt(0).toUpperCase() + algorithm.slice(1)}/${getModel}
        ${requiresSecondModel() ? '/' + getSecondModel : ''}`)
            .then(response => response.json())
            .then((data) => algorithm === 'isDeterministic' || algorithm === 'isComplete'
                ? setSolutionTab(data)
                : setModelTab(data))
            .finally(() => setTestLoading(false))
    }

    function requiresSecondModel() {
        return algorithm === 'toProductAutomaton'
    }

    return (
        <div className='rows'>
            <fieldset className='smallFieldset'>
                <legend>&nbsp;Test your algorithm&nbsp;</legend>
                <div className='centerContainer'>
                    <select
                        className='select'
                        id='algorithm'
                        name='algorithm'
                        value={algorithm}
                        onChange={(event) => setAlgorithm(event.target.value)}
                    >
                        <option className='center' value=''>Choose</option>
                        <option value='isDeterministic'>isDeterministic</option>
                        <option value='isComplete'>isComplete</option>
                        <option value='toProductAutomaton'>toProductAutomaton</option>
                        <option value='toPowerAutomaton'>toPowerAutomaton</option>
                        <option value='toComplementAutomaton'>toComplementAutomaton</option>
                        <option value='toSinkAutomaton'>toSinkAutomaton</option>
                        <option value='toOracleAutomaton'>toOracleAutomaton</option>
                        <option value='toOptimisedOracleAutomaton'>toOptimisedOracleAutomaton</option>
                    </select>
                </div>
                <div className='centerContainer'>
                    <button className='button' onClick={handleApplyClick}>
                        {applyLoading && <div className='loading'></div>}
                        Apply algorithm
                    </button>
                </div>
                <div className='centerContainer'>
                    <button className='button' onClick={handleTestClick}>
                        {testLoading && <div className='loading'></div>}
                        Test your algorithm
                    </button>
                </div>
            </fieldset>
        </div>
    )
}