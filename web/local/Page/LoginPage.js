import React, {Component} from 'react';
import {Button, Form, Grid, Header, Message, Segment} from 'semantic-ui-react';
import {Redirect} from 'react-router-dom';

const fakeAuth = {
    isAuthenticated: false,
    authenticate(cb) {
        this.isAuthenticated = true;
        setTimeout(cb, 100); // fake async
    },
    signout(cb) {
        this.isAuthenticated = false;
        setTimeout(cb, 100);
    },
};
class LoginPage extends Component {
    state = {redirectToReferrer: false};
    login = () => {
        fakeAuth.authenticate(() => {
            this.setState({redirectToReferrer: true});
        });
    };
    render() {
        let {from} = this.props.location.state || {from: {pathname: '/home'}};
        let {redirectToReferrer} = this.state;
        if (redirectToReferrer) return <Redirect to={from} />;

        return (
            <Segment raised>
                <Grid
                    textAlign="center"
                    style={{height: '50vh'}}
                    verticalAlign="middle">
                    <Grid.Column style={{maxWidth: 450}}>
                        <Header as="h2" textAlign="center">
                            Sign in your account
                        </Header>
                        <Form size="large">
                            <Form.Input fluid placeholder="E-mail address" />
                            <Form.Input
                                fluid
                                placeholder="Password"
                                type="password"
                            />
                            <Button
                                primary
                                fluid
                                size="large"
                                onClick={this.login}>
                                Login
                            </Button>
                        </Form>
                        <Message>
                            New to us? <a href="#">Sign Up</a>
                        </Message>
                    </Grid.Column>
                </Grid>
            </Segment>
        );
    }
}
export {LoginPage, fakeAuth};
