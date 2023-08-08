import React, {Component} from 'react'

export class ErrorBoundary extends Component {
    constructor(props) {
        super(props);
        this.state = {hasError: false}
    }

    static getDeliveredStateFromError(error) {
        return {hasError: true}
    }

    componentDidCatch(error, errorInfo) {}

    render() {
        return this.state.hasError ? <div></div> : this.props.children
    }
}