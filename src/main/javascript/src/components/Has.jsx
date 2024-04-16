import React, {Component} from 'react'

export class Has extends Component {
    render() {
        return <div className='has'>✔️{this.props.children} </div>
    }
}