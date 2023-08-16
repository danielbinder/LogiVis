import React, {useState} from 'react'

export default function ModelTracer({setSolutionTab,
                                        model}) {
    const [traceLoading, setTraceLoading] = useState(false)
    const [shortestLoading, setShortestLoading] = useState(false)

    function handleTraceClick() {
        setTraceLoading(true)
        fetch('http://localhost:4000/trace/' +
            removeComments(model).replaceAll('\n', ' '))
            .then(response => response.json())
            .then(setSolutionTab)
            .finally(() => setTraceLoading(false))
    }

    function handleShortestClick() {
        setShortestLoading(true)
        fetch('http://localhost:4000/shortestTrace/' +
            removeComments(model).replaceAll('\n', ' '))
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

const removeComments = (s) => s.replaceAll(/#.*?(\n|$)/g, '\n')