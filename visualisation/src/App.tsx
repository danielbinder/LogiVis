import React from 'react';
import logo from './logo.svg';
import './App.css';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.tsx</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
        <button onClick={handleButtonClick}>Fetch rest API</button>
      </header>
    </div>
  );
}

function handleButtonClick() {
  return fetch('http://localhost:4000/solve/var1')
      .then(response => response.json())
      .then(data => console.log(data))
}

export default App;
