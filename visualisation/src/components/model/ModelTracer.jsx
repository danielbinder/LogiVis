import React, {useState} from 'react'
import {useRecoilValue} from 'recoil';
import {modelSelector} from '../selectors';
import {serverURL} from '../constants';

export default function ModelTracer({setSolutionTab}) {
    const getModel = useRecoilValue(modelSelector)

    const [traceLoading, setTraceLoading] = useState(false)
    const [shortestLoading, setShortestLoading] = useState(false)

    function handleTraceClick() {
        setTraceLoading(true)
        fetch(serverURL + '/trace/' + getModel)
            .then(response => response.json())
            .then(setSolutionTab)
            .finally(() => setTraceLoading(false))
    }

    function handleShortestClick() {
        setShortestLoading(true)
        fetch(serverURL + '/shortestTrace/' + getModel)
            .then(response => response.json())
            .then(setSolutionTab)
            .finally(() => setShortestLoading(false))
    }

    return (
        <div className='rows'>
            <fieldset className='smallFieldset'>
                <legend>&nbsp;Trace a model&nbsp;</legend>
                <div className='centerContainer'>
                    <button className='button' onClick={handleTraceClick}>
                        {traceLoading && <div className='loading'></div>}
                        Find trace
                    </button>
                </div>
                <div className='centerContainer'>
                    <button className='button' onClick={handleShortestClick}>
                        {shortestLoading && <div className='loading'></div>}
                        Find shortest trace
                    </button>
                </div>
            </fieldset>
        </div>)
}