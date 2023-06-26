import React, {useState} from 'react';

export default function FormulaGenerator() {
    const [loading, setLoading] = useState(false)
    const [generationParameters, setGenerationParameters] = useState({
        variables: 3,
        operators: 5,
    })

    function handleChange({target: {name, value, type, checked}}) {
        setGenerationParameters(
            prevGenerationParameters => ({
                ...prevGenerationParameters,
                [name]: type === 'checkbox' ? checked : value
            })
        )
    }

    function handleButtonClick() {
        // setLoading(true)
        // fetch('someURL')
        //     .then(response => {
        //         if(!response.ok) {
        //             setLoading(false)
        //         }
        //
        //         return response
        //     })
        //     .then()     // your code here
        //     .then(() => setLoading(false))
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
                    </button>
                </div>
            </fieldset>
        </div>
    )
}