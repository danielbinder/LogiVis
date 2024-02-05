import React, {useState} from 'react';
import {algorithmURL} from '../constants';
import {useRecoilValue} from 'recoil';
import {modelSelector, secondModelSelector} from '../selectors';

export default function AlgorithmTester({setSolutionTab, setModelTab}) {
    const [applyLoading, setApplyLoading] = useState(false)
    const [testLoading, setTestLoading] = useState(false)
    const [validateLoading, setValidateLoading] = useState(false)
    const [validateAllLoading, setValidateAllLoading] = useState(false)
    const [algorithm, setAlgorithm] = useState('')
    const [compactReport, setCompactReport] = useState(true)
    const [reportAuthor, setReportAuthor] = useState('')
    const getModel = useRecoilValue(modelSelector)
    const getSecondModel = useRecoilValue(secondModelSelector)

    function handleApplyClick() {
        setApplyLoading(true)
        fetch(algorithmURL + `/${algorithm}/${getModel}${requiresSecondModel() ? '/' + getSecondModel : ''}`)
            .then(response => response.json())
            .then((data) => requiresSetSolutionTab()
                ? setSolutionTab(data)
                : setModelTab(data))
            .finally(() => setApplyLoading(false))
    }

    function handleTestClick() {
        setTestLoading(true)
        fetch(algorithmURL + `/test${algorithm.charAt(0).toUpperCase() + algorithm.slice(1)}/${getModel}
        ${requiresSecondModel() ? '/' + getSecondModel : ''}`)
            .then(response => response.json())
            .then((data) => requiresSetSolutionTab()
                ? setSolutionTab(data)
                : setModelTab(data))
            .finally(() => setTestLoading(false))
    }

    function handleValidateClick() {
        setValidateLoading(true)
        fetch(algorithmURL + `/validate/${algorithm}/${reportAuthor === '' ? ' ' : reportAuthor}/${compactReport}`)
            .then(response => response.json())
            .then(setSolutionTab)
            .finally(() => setValidateLoading(false))
    }

    function handleValidateAllClick() {
        setValidateAllLoading(true)
        fetch(algorithmURL + `/validateAll/${reportAuthor === '' ? ' ' : reportAuthor}/${compactReport}`)
            .then(response => response.json())
            .then(setSolutionTab)
            .finally(() => setValidateAllLoading(false))
    }

    function requiresSecondModel() {
        return  ['toProductAutomaton', 'isEquivalent', 'isSimulatedBy'].includes(algorithm)
    }

    function requiresSetSolutionTab() {
        return ['isDeterministic', 'isComplete', 'isEquivalent', 'isSimulatedBy', 'areReachable',
            'getStronglyConnectedComponents'].includes(algorithm)
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
                        <option value='isEquivalent'>isEquivalent</option>
                        <option value='areReachable'>areReachable</option>
                        <option value='toProductAutomaton'>toProductAutomaton</option>
                        <option value='toPowerAutomaton'>toPowerAutomaton</option>
                        <option value='toComplementAutomaton'>toComplementAutomaton</option>
                        <option value='toSinkAutomaton'>toSinkAutomaton</option>
                        <option value='toOracleAutomaton'>toOracleAutomaton</option>
                        <option value='toOptimisedOracleAutomaton'>toOptimisedOracleAutomaton</option>
                        <option value='isSimulatedBy'>isSimulatedBy</option>
                        <option value='getStronglyConnectedComponents'>getStronglyConnectedComponents</option>
                    </select>
                </div>
                <div className='centerContainer'>
                    <button className='button' onClick={handleApplyClick}>
                        {applyLoading && <div className='loading'></div>}      Set.of(Set.of("s5", "s4", "s3", "s2", "s1", "s0"))
                        Apply algorithm
                        <a className='algorithmTesterInfo' href='https://youtu.be/hA5PsrhkGxs' rel="noreferrer" target='_blank'>
                            &#9432;
                        </a>
                    </button>
                </div>
                <div className='centerContainer'>
                    <button className='button' onClick={handleTestClick}>
                        {testLoading && <div className='loading'></div>}
                        Apply your own
                        <a className='algorithmImplementationInfo' href='https://youtu.be/UXu0TBOh53U' rel="noreferrer" target='_blank'>
                            &#9432;
                        </a>
                    </button>
                </div>
                <div>
                    <input
                        className='textInput'
                        type='text'
                        id='reportAuthor'
                        name='reportAuthor'
                        placeholder='Report Author'
                        value={reportAuthor}
                        onChange={({target: {value}}) => setReportAuthor(value)}
                    />
                </div>
                <div>
                    <input
                        className='checkbox'
                        type='checkbox'
                        id='compactReport'
                        name='compactReport'
                        checked={compactReport}
                        onChange={() => setCompactReport(!compactReport)}
                    />
                    <label htmlFor='compactReport'>Compact Report</label>
                </div>
                <div className='centerContainer'>
                    <button className='button' onClick={handleValidateClick}>
                        {validateLoading && <div className='loading'></div>}
                        Validate algorithm
                    </button>
                </div>
                <div className='centerContainer'>
                    <button className='button' onClick={handleValidateAllClick}>
                        {validateAllLoading && <div className='loading'></div>}
                        Validate all
                    </button>
                </div>
            </fieldset>
        </div>
    )
}