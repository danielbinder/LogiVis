import React, {useState} from 'react';
import {serverURL} from '../constants';

export default function ModelGenerator({setModelTab}) {
    const [loading, setLoading] = useState(false)
    const [generationParameters, setGenerationParameters] = useState({
            modelType: 'ks',
            nodes: 4,
            variables: 3,
            minSuccessors: 1,
            maxSuccessors: 3,
            initialNodes: 2,
            finalNodes: 2,
            allReachable: true
        }
    )

    function handleChange({target: {name, value, type, checked}}) {
        setGenerationParameters(
            prevGenerationParameters => ({
                    ...prevGenerationParameters,
                    [name]: type === 'checkbox' ? checked : value
                })
        )
    }

    function handleButtonClick() {
        setLoading(true)
        fetch(serverURL +
            (generationParameters.modelType === 'ks'
                ? '/generateKripke/'
                : '/generateFiniteAutomaton/') +
            generationParameters.nodes + '/' +
            generationParameters.initialNodes + '/' +
            (generationParameters.modelType === 'fa'
                ? generationParameters.finalNodes + '/'
                : '') +
            generationParameters.variables + '/' +
            generationParameters.minSuccessors + '/' +
            generationParameters.maxSuccessors + '/' +
            generationParameters.allReachable)
            .then(response => response.json())
            .then(data => {setModelTab(data); return data})
            .finally(() => setLoading(false))
    }

    return (
        <div className='rows'>
            <fieldset className='smallFieldset'>
                <legend>&nbsp;Generate a model&nbsp;</legend>
                <div>
                    <div>
                        <input
                            type='radio'
                            id='ks'
                            name='modelType'
                            value='ks'
                            checked={generationParameters.modelType === 'ks'}
                            onChange={handleChange}
                        />
                        <label htmlFor='ks'>Kripke Structure</label>
                    </div>
                    <div className='bottomSpace'>
                        <input
                            type='radio'
                            id='fa'
                            name='modelType'
                            value='fa'
                            checked={generationParameters.modelType === 'fa'}
                            onChange={handleChange}
                        />
                        <label htmlFor='fa'>Finite Automaton</label>
                    </div>
                    <input
                        className='input'
                        type='number'
                        min='1'
                        id='nodes'
                        name='nodes'
                        placeholder='Nodes'
                        value={generationParameters.nodes}
                        onChange={handleChange}
                    />
                    <label htmlFor='nodes'>Nodes</label>
                </div>
                <div>
                    <input
                        className='input'
                        type='number'
                        min='1'
                        id='variables'
                        name='variables'
                        placeholder='Variables'
                        value={generationParameters.variables}
                        onChange={handleChange}
                    />
                    <label htmlFor='variables'>
                        {generationParameters.modelType === 'ks'
                            ? 'Variables'
                            : 'Alphabet size'}
                    </label>
                </div>
                <div>
                    <input
                        className='input'
                        type='number'
                        min='0'
                        id='minSuccessors'
                        name='minSuccessors'
                        placeholder='Min. Successors'
                        value={generationParameters.minSuccessors}
                        onChange={handleChange}
                    />
                    <label htmlFor='minSuccessors'>Min. Successors</label>
                </div>
                <div>
                    <input
                        className='input'
                        type='number'
                        min='0'
                        id='maxSuccessors'
                        name='maxSuccessors'
                        placeholder='Max. Successors'
                        value={generationParameters.maxSuccessors}
                        onChange={handleChange}
                    />
                    <label htmlFor='maxSuccessors'>Max. Successors</label>
                </div>
                <div>
                    <input
                        className='input'
                        type='number'
                        min='0'
                        id='initialNodes'
                        name='initialNodes'
                        placeholder='Initial Nodes'
                        value={generationParameters.initialNodes}
                        onChange={handleChange}
                    />
                    <label htmlFor='initialNodes'>Initial Nodes</label>
                </div>
                {generationParameters.modelType === 'fa' && <div>
                    <input
                        className='input'
                        type='number'
                        min='0'
                        id='finalNodes'
                        name='finalNodes'
                        placeholder='Final Nodes'
                        value={generationParameters.finalNodes}
                        onChange={handleChange}
                    />
                    <label htmlFor='finalNodes'>Final Nodes</label>
                </div>}
                <div>
                    <input
                        className='input'
                        type='checkbox'
                        id='allReachable'
                        name='allReachable'
                        checked={generationParameters.allReachable}
                        onChange={handleChange}
                    />
                    <label htmlFor='allReachable'>All reachable</label>
                </div>
                <div className='centerContainer'>
                    <button className='button' onClick={handleButtonClick}>
                        {loading && <div className='loading'></div>}
                        Generate Model
                        <a className='modelGeneratorInfo' href='https://youtu.be/OTenhwKF0JM' rel="noreferrer" target='_blank'>
                            &#9432;
                        </a>
                    </button>
                </div>
            </fieldset>
        </div>
    )
}