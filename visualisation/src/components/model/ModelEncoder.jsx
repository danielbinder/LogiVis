import React, {useState} from 'react';
import {useRecoilValue, useSetRecoilState} from 'recoil';
import {evalStatusMessageState, formulaTypeState} from '../atoms';
import {modelSelector} from '../selectors';
import {cleanResultData, serverURL} from '../constants';
import {Requires} from '../Requires';

export default function ModelEncoder({setFormulaTab, setSolutionTab}) {
    const getModel = useRecoilValue(modelSelector)
    const setFormulaType = useSetRecoilState(formulaTypeState)
    const setEvalStatusMessage = useSetRecoilState(evalStatusMessageState)

    const [loadingEncode, setLoadingEncode] = useState(false)
    const [generationParameters, setGenerationParameters] = useState({
        steps: 2,
        encodingType: 'compact'
    })

    const urls = new Map([
        ['naive', serverURL + '/kripke2formula/'],
        ['compact', serverURL + '/kripke2compactFormula/'],
        ['compactTrace', serverURL + '/encodeAndSolveWithTrace/'],
        ['compactQBF', serverURL + '/kripke2compactQBFFormula/']
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
            getModel + '/' + generationParameters.steps)
            .then(response => response.json())
            .then((data) => {
                if(generationParameters.encodingType === 'naive' || generationParameters.encodingType === 'compact') {
                    setFormulaTab(data)
                    setFormulaType('boolean')
                } else {
                    setSolutionTab(data)
                    setEvalStatusMessage(generateLimbooleLink(data['result']))
                }
            })
            .finally(() => setLoadingEncode(false))
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
                {(getModel.length === 0 || !/[a-z0-9]+\s*\[/g.test(getModel)) &&
                    <Requires>Kripke Structure</Requires>}
                {(getModel.indexOf('>') === -1 || getModel.indexOf('<') === -1) &&
                    <Requires>Trace start/end {"'>' & '<'"}</Requires>}
                <div className='centerContainer'>
                    <button className='button' onClick={handleEncodeClick}>
                        {loadingEncode && <div className='loading'></div>}
                        Encode model
                        <a className='modelEncoderInfo' href='https://youtu.be/7grzBILj4rM' rel="noreferrer" target='_blank'>
                            &#9432;
                        </a>
                    </button>
                </div>
            </fieldset>
        </div>
    )
}

const generateLimbooleLink = (data) =>
    <a href={'https://maximaximal.github.io/limboole/#2' + cleanResultData(data)}
       rel="noreferrer" target='_blank'>
        Check in Limboole
    </a>