import React from 'react';
import {Has} from '../Has';
import {useRecoilState} from 'recoil';
import {solverStrategyState} from "../atoms";

export default function SolverStrategyChooser() {
    const [solverStrategy, setSolverStrategy] = useRecoilState(solverStrategyState)

    return (
        <div className='rows'>
            <fieldset className='smallFieldset'>
                <legend>&nbsp;Select a solving strategy&nbsp;</legend>
                <div>
                    <div>
                        <input
                            type='radio'
                            id='brute'
                            name='solverStrategy'
                            value='brute'
                            checked={solverStrategy === 'brute'}
                            onChange={() => setSolverStrategy('brute')}
                        />
                        <label htmlFor='brute'>Brute Force</label>
                    </div>
                    <div>
                        <input
                            type='radio'
                            id='DPLLrec'
                            name='solverStrategy'
                            value='DPLLrec'
                            checked={solverStrategy === 'DPLLrec'}
                            onChange={() => setSolverStrategy('DPLLrec')}
                        />
                        <label htmlFor='DPLLrec'>Recursive DPLL</label>
                    </div>
                    <div>
                        <input
                            type='radio'
                            id='DPLLnonrec'
                            name='solverStrategy'
                            value='DPLLnonrec'
                            checked={solverStrategy === 'DPLLnonrec'}
                            onChange={() => setSolverStrategy('DPLLnonrec')}
                        />
                        <label htmlFor='DPLLnonrec'>Non-Recursive DPLL</label>
                    </div>
                    <div>
                        <input
                            type='radio'
                            id='CDCL'
                            name='solverStrategy'
                            value='CDCL'
                            checked={solverStrategy === 'CDCL'}
                            onChange={() => setSolverStrategy('CDCL')}
                        />
                        <label htmlFor='CDCL'>CDCL</label>
                    </div>
                    {(solverStrategy === 'DPLLnonrec') &&
                        <Has>Literal watching</Has>
                    }
                </div>
            </fieldset>
        </div>
    )
}