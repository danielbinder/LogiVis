import React from 'react'
import ReactDOM from 'react-dom/client'
import './index.css'
import App from './App'
import {RecoilRoot} from 'recoil'
import {RecoilURLSyncJSON} from 'recoil-sync'

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);
root.render(
    <RecoilRoot>
        <RecoilURLSyncJSON location={{part: 'queryParams'}}>
            <App />
        </RecoilURLSyncJSON>
    </RecoilRoot>
);
