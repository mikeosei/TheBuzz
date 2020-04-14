import React, {Component} from 'react';
import {Header, Segment, Icon, Message} from 'semantic-ui-react';
import {useParams} from 'react-router-dom';
import auth from '../auth';

// gets the url for the parameter
function ProfilePage() {
    const {userId} = useParams();
    return <ProfilePageComponent userId={userId} />;
}
//must pass using heirarchy
class ProfilePageComponent extends Component {
    constructor(props) {
        super(props);
        this.state = {
            cred: auth.getCred(),
            user: null,
            error: null,
        };
    }

    handleDismiss = () => {
        this.setState({error: null});
    };

    componentDidMount() {
        const {cred} = this.state;
        const userId = this.props.userId;
        //unsure which URL to use below that might be the issue
        const endpoint = `postgres://ecxfhpxnovpejh:0a0657538f39c41357b1de598e83f75940333ef42441c9eb41268f0a5d08dc2a@ec2-34-235-108-68.compute-1.amazonaws.com:5432/d70uqvt93jbpoq${userId}`;
        const request = new Request(endpoint, {
            method: 'GET',
        });
        fetch(request)
            .then(res => res.json())
            .then(
                result => {
                    this.setState({
                        isLoaded: true,
                        user: result.mData,
                    });
                },
                error => {
                    this.setState({
                        isLoaded: true,
                        error: error,
                    });
                },
            );
    }

    render() {
        const {user, isLoaded, error} = this.state;
        if (error) {
            return (
                <Message
                    negative
                    size="large"
                    onDismiss={this.handleDismiss}
                    header="Uh oh... something went wrong"
                    content={error}
                />
            );
        } else if (!isLoaded) {
            return (
                <Message icon>
                    <Icon name="circle notched" loading />
                    <Message.Content>
                        <Message.Header>Please wait</Message.Header>
                        We are fetching the content for you.
                    </Message.Content>
                </Message>
            );
        } else { 
            return <Profile username={user.username} about={user.about} />;
        }
    }
}

const Profile = props => (
    <Segment raised>
        <Header as="h2" icon textAlign="center">
            <Icon name="user" circular />
            {props.username}
            <Header.Subheader>{props.about}</Header.Subheader>
        </Header>
    </Segment>
);

export default ProfilePage;