import React, {useState} from 'react';

export default function ModelEncoder({setFormulaType,
                                         setFormulaTab,
                                         setSolutionTab,
                                         setFormulaAndSolutionTab,
                                         setEvalStatusMessage,
                                         model}) {
    const [loadingEncode, setLoadingEncode] = useState(false)
    const [loadingTrace, setLoadingTrace] = useState(false)
    const [generationParameters, setGenerationParameters] = useState({
        steps: 3,
        encodingType: 'compact'
    })

    const urls = new Map([
        ['naive', 'http://localhost:4000/kripke2formula/'],
        ['compact', 'http://localhost:4000/kripke2compactFormula/'],
        ['compactTrace', 'http://localhost:4000/encodeAndSolveWithTrace/'],
        ['compactQBF', 'http://localhost:4000/kripke2compactQBFFormula/']
    ])

    function handleChange({target: {name, value, type, checked}}) {
        setGenerationParameters(
            prevGenerationParameters => ({
                ...prevGenerationParameters,
                [name]: type === 'checkbox' ? checked : value
            })
        )
    }

    function handleEncodeClick() {
        setLoadingEncode(true)
        fetch(urls.get(generationParameters.encodingType) +
            removeComments(model).replaceAll('\n', ' ') + '/' + generationParameters.steps)
            .then(response => response.json())
            .then(generationParameters.encodingType === 'naive' || generationParameters.encodingType === 'compact'
                ? setFormulaTab
                : setSolutionTab)
            .then(data => generationParameters.encodingType === 'naive' || generationParameters.encodingType === 'compact'
                ? setFormulaType('boolean')
                : setEvalStatusMessage(generateLimbooleLink(data['result'])))
            .finally(() => setLoadingEncode(false))
    }

    function handleTraceClick() {
        setLoadingTrace(true);
        fetch(urls.get('compactTrace') +
            removeComments(model).replaceAll('\n', ' ') + '/' + generationParameters.steps)
            .then(response => response.json())
            .then(setFormulaAndSolutionTab)
            .then(() => setFormulaType('boolean'))
            .finally(() => setLoadingTrace(false))
    }

    return (
        <div className='rows'>
            <fieldset className='smallFieldset'>
                <legend>&nbsp;Encode a model&nbsp;</legend>
                <div>
                    <div className='bottomSpace'>
                        <input
                            className='input'
                            type='number'
                            min='1'
                            id='steps'
                            name='steps'
                            placeholder='Steps'
                            value={generationParameters.steps}
                            onChange={handleChange}
                        />
                        <label htmlFor='steps'>Steps</label>
                    </div>
                    <div>
                        <input
                            type='radio'
                            id='naive'
                            name='encodingType'
                            value='naive'
                            checked={generationParameters.encodingType === 'naive'}
                            onChange={handleChange}
                        />
                        <label htmlFor='naive'>Naive</label>
                    </div>
                    <div>
                        <input
                            type='radio'
                            id='compact'
                            name='encodingType'
                            value='compact'
                            checked={generationParameters.encodingType === 'compact'}
                            onChange={handleChange}
                        />
                        <label htmlFor='compact'>Compact</label>
                    </div>
                    <div>
                        <input
                            type='radio'
                            id='compactQBF'
                            name='encodingType'
                            value='compactQBF'
                            checked={generationParameters.encodingType === 'compactQBF'}
                            onChange={handleChange}
                        />
                        <label htmlFor='compactQBF'>Limboole QBF</label>
                    </div>
                </div>
                <div className='centerContainer'>
                    <button className='button' onClick={handleEncodeClick}>
                        {loadingEncode && <div className='loading'></div>}
                        Encode model
                    </button>
                </div>
                <div className='centerContainer'>
                    {generationParameters.encodingType === 'compact' &&
                        <button className='button' onClick={handleTraceClick}>
                            {loadingTrace && <div className='loading'></div>}
                            Encode + trace
                        </button>
                    }
                </div>
            </fieldset>
        </div>
    )
}

const removeComments = (s) => s.replaceAll(/#.*?(\n|$)/g, '\n')

const generateLimbooleLink = (data) =>
    <a href={'https://maximaximal.github.io/limboole/#2' + data.replaceAll(/[$]/g, '\n')}
       target='_blank'>
        Check in Limboole
    </a>