import React, {Component} from 'react';
import {Form, Grid, Header, Message, Segment} from 'semantic-ui-react';
import {Redirect} from 'react-router-dom';

class RegisterPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            email: '',
            password: '',
            passwordComfirm: '',
            error: null,
            success: false,
        };
    }

    handleChange = (e, {name, value}) => this.setState({[name]: value});

    submitRegister = () => {
        const {username, email, password} = this.state;
        if (username && email && password) {
            const endpoint =
                'https://lilchengs.herokuapp.com/';
            const request = new Request(endpoint, {
                method: 'POST',
                body: JSON.stringify({
                    username: username,
                    email: email,
                    pass: password,
                }),
            });
            fetch(request)
                .then(res => res.json())
                .then(
                    result => {
                        if (result.mStatus === 'ok') {
                            alert('Successfully registered!');
                            this.setState({success: true});
                        } else {
                            this.setState({error: result.mMessage});
                        }
                    },
                    error => {
                        this.setState({error: error});
                    },
                );
        } else {
            this.setState({
                error: 'You have to fill in all fields marked required',
            });
        }
    };

    handleDismiss = () => {
        this.setState({error: null});
    };

    render() {
        const {
            username,
            email,
            password,
            passwordComfirm,
            error,
            success,
        } = this.state;
        if (success) {
            return <Redirect to="/login" />;
        }
        return (
            <Segment padded="very" raised>
                <Grid textAlign="center" verticalAlign="middle">
                    <Grid.Column style={{maxWidth: 350}}>
                        {!error || (
                            <Message
                                negative
                                size="large"
                                onDismiss={this.handleDismiss}
                                header="Uh oh... something went wrong"
                                content={`${error}`}
                            />
                        )}
                        <Header as="h2" textAlign="center">
                            Sign Up
                        </Header>
                        <Form size="large" onSubmit={this.submitRegister}>
                            <Form.Input
                                fluid
                                onChange={this.handleChange}
                                placeholder="Username"
                                name="username"
                                value={username}
                            />
                            <Form.Input
                                fluid
                                onChange={this.handleChange}
                                placeholder="E-mail address"
                                name="email"
                                type="email"
                                value={email}
                            />
                            <Form.Input
                                onChange={this.handleChange}
                                fluid
                                placeholder="Password"
                                type="password"
                                name="password"
                                value={password}
                            />
                            <Form.Input
                                fluid
                                onChange={this.handleChange}
                                placeholder="Comfirm Password"
                                type="password"
                                name="passwordComfirm"
                                value={passwordComfirm}
                            />
                            <Form.Button
                                primary
                                fluid
                                size="large"
                                content="Submit"
                            />
                        </Form>
                    </Grid.Column>
                </Grid>
            </Segment>
        );
    }
}
export default RegisterPage;
