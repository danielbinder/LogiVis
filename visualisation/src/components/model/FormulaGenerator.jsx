import React, {useState} from 'react';
import {serverURL} from '../constants';
import {useRecoilValue} from 'recoil';
import {formulaTypeState} from '../atoms';

export default function FormulaGenerator({setFormulaTab}) {
    const [loading, setLoading] = useState(false)
    const [generationParameters, setGenerationParameters] = useState({
        variables: 3,
        operators: 5,
    })
    const formulaType = useRecoilValue(formulaTypeState)

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
        fetch(serverURL + (formulaType === 'boolean' ? '/generateBooleanFormula/' : '/generateCTLFormula/') +
            generationParameters.variables + "/"
            + generationParameters.operators)
            .then(response => response.json())
            .then(data => {setFormulaTab(data); return data})
            .finally(() => setLoading(false))
    }

    return (
        <div className='rows'>
            <fieldset className='smallFieldset'>
                <legend>&nbsp;Generate a formula&nbsp;</legend>
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
                    <label htmlFor='variables'>Variables</label>
                </div>
                <div>
                    <input
                        className='input'
                        type='number'
                        min='1'
                        id='operators'
                        name='operators'
                        placeholder='Operators'
                        value={generationParameters.operators}
                        onChange={handleChange}
                    />
                    <label htmlFor='operators'>Operators</label>
                </div>
                <div className='centerContainer'>
                    <button className='button' onClick={handleButtonClick}>
                        {loading && <div className='loading'></div>}
                        Generate formula
                        <a className='formulaGeneratorInfo' href='https://youtu.be/BJx75bndTwE' rel="noreferrer" target='_blank'>
                            &#9432;
                        </a>
                    </button>
                </div>
            </fieldset>
        </div>
    )
}