import React, {Component} from 'react';
import {Header, Form, Message, Segment, Input, Button} from 'semantic-ui-react';
import auth from '../auth';

class AccountPage extends Component {
    constructor(props) {
        super(props);
        this.state = {
            cred: auth.getCred(),
            user: null,
            disabled: true,
            about: '',
            password: '',
            error: null,
        };
    }

    handleToggle = () => this.setState({disabled: !this.state.disabled});

    handleChange = (e, {name, value}) => this.setState({[name]: value});

    componentDidMount() {
        this.setState({user: this.getUser()});
    }

    getUser = () => {
        const {cred} = this.state;
        const endpoint = `https://lilchengs.herokuapp.com/}?uid=${cred.uid}&token=${cred.token}`;
        const request = new Request(endpoint, {
            method: 'GET',
        });
        fetch(request)
            .then(res => res.json())
            .then(
                result => {
                    return result.mData;
                },
                error => {
                    return null;
                },
            );
    };

    handleSubmit = () => {
        const {password, cred} = this.state;
        const endpoint = `https://lilchengs.herokuapp.com/}`;
        const request = new Request(endpoint, {
            method: 'PUT',
            body: JSON.stringify({}),
        });
        fetch(request)
            .then(res => res.json())
            .then(
                result => {
                    this.setState({disabled: !this.state.disabled});
                },
                error => {
                    this.setState({
                        disabled: !this.state.disabled,
                        error: error,
                    });
                },
            );
    };

    handleDismiss = () => {
        this.setState({error: null});
    };

    render() {
        const {about, password, disabled, error} = this.state;
        return (
            <Segment raised>
                {!error || (
                    <Message
                        negative
                        size="large"
                        onDismiss={this.handleDismiss}
                        header="Uh oh... something went wrong"
                        content={`${error}`}
                    />
                )}
                <Header
                    as="h2"
                    content="Account Setting"
                    subheader="Manage your account settings and set e-mail preferences"
                />
                <Header.Content>
                    <Form>
                        <Form.Field>
                            <label>About</label>
                            <Input
                                onChange={this.handleChange}
                                disabled={disabled}
                                value={about}
                                name="about"
                            />
                        </Form.Field>
                        <Form.Field>
                            <label>Username</label>
                            <Input disabled={true} value="<username>" />
                        </Form.Field>
                        <Form.Field>
                            <label>Email(must be lehigh.edu)</label>
                            <Input disabled={true} value="<email>" />
                        </Form.Field>
                        <Form.Field>
                            <label>Password</label>
                            <Input
                                onChange={this.handleChange}
                                disabled={disabled}
                                value={password}
                                name="password"
                            />
                        </Form.Field>
                        <Button
                            primary={!disabled}
                            onClick={
                                disabled ? this.handleToggle : this.handleSubmit
                            }>
                            {disabled ? 'Edit' : 'Save'}
                        </Button>
                    </Form>
                </Header.Content>
            </Segment>
        );
    }
}
export default AccountPage;
