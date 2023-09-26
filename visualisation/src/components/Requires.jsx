import React, {Component} from 'react'

export class Requires extends Component {
    render() {
        return <div className='requires'>❌{this.props.children} </div>
    }
}