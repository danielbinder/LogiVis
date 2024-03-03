import React from 'react';
import {useRecoilState} from 'recoil';
import {formulaTypeState} from '../atoms';

export default function FormulaTypeSelection() {
        const [formulaType, setFormulaType] = useRecoilState(formulaTypeState)

    return (
        <fieldset className='fieldset'>
            <legend>&nbsp;Choose a formula type&nbsp;</legend>
            <input
                type='radio'
                id='boolean'
                name='formulaTypeSelection'
                value='boolean'
                checked={formulaType === 'boolean'}
                onChange={() => setFormulaType('boolean')}
            />
            <label htmlFor='boolean'>Boolean Algebra</label>
            <input
                type='radio'
                id='main.java.ctl'
                name='formulaTypeSelection'
                value='main.java.ctl'
                checked={formulaType === 'main.java.ctl'}
                onChange={() => setFormulaType('main.java.ctl')}
            />
            <label htmlFor='main.java.ctl'>CTL</label>
            <input
                type='radio'
                id='regex'
                name='formulaTypeSelection'
                value='regex'
                checked={formulaType === 'regex'}
                onChange={() => setFormulaType('regex')}
            />
            <label htmlFor='regex' className='notImplemented'>Regular Expression</label>
            <input
                type='radio'
                id='process'
                name='formulaTypeSelection'
                value='process'
                checked={formulaType === 'process'}
                onChange={() => setFormulaType('process')}
            />
            <label htmlFor='process' className='notImplemented'>Process Algebra</label>
        </fieldset>
    )
}