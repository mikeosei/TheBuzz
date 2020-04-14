import React, {Component} from 'react';
import {
    Feed,
    Icon,
    Modal,
    Button,
    Form,
    TextArea,
    Message,
    Segment,
} from 'semantic-ui-react';
import {Link, Redirect} from 'react-router-dom';
import auth from '../auth';

class HomePage extends Component {
    render() {
        return (
            <Segment raised>
                <FeedList />
            </Segment>
        );
    }
}

class FeedList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            cred: auth.getCred(),
            error: null,
            isLoaded: false,
            feedMap: null,
        };
    }

    componentDidMount() {
        const {cred} = this.state;
        const endpoint = 'https://lilchengs.herokuapp.com/messages'; //doesnt seem to be working wiht this URI

        const request = new Request(endpoint, {method: 'GET'});
        fetch(request)
            .then(res => res.json())
            .then(
                result => {
                    if (result.mStatus == "error") {
                        this.setState({
                            error: "No messages available",
                        });
                    } else {
                        const feedMap = new Map();
                        const objArray = result.mData;
                        for (const obj of objArray) {
                            if (obj.pid === 0) {
                                feedMap.set(obj.id, obj);
                            }
                        }
                        this.setState({
                            isLoaded: true,
                            feedMap: feedMap,
                        });
                    }
                },
                error => {
                    this.setState({
                        isLoaded: true,
                        error: error,
                    });
                },
            );
    }

    handleDismiss = () => {
        this.setState({error: null});
    };

    render() {
        const {error, isLoaded, feedMap, cred} = this.state;
        if (error) {
            return (
                <Message
                    negative
                    size="large"
                    onDismiss={this.handleDismiss}
                    header="Oops... something went wrong"
                    content={`${error}`}
                />
            );
        } else if (!isLoaded) {
            return (
                <Message icon>
                    <Icon name="circle notched" loading />
                    <Message.Content>
                        <Message.Header>Please Wait</Message.Header>
                        We are fetching the content for you.
                    </Message.Content>
                </Message>
            );
        } else {
            return (
                <Feed size="large">
                    {Array.from(feedMap.values()).map(item => (
                        <FeedItem cred={cred} key={item.id} item={item} />
                    ))}
                </Feed>
            );
        }
    }
}

class FeedItem extends Component {
    constructor(props) {
        super(props);
        this.state = {
            vote: 0,
            success: false,
        };
    }

    submitVote = flag => {
        /*
         * if flag = 1 then upvote button pressed
         * flag = -1 then downvote button pressed
         *  users can only upvote, downvote, or unvote.
         *       unvoted or opposite vote state listens to the flag
         * */
        const vote =
            this.state.vote === 0 || flag !== this.state.vote ? flag : 0;

        const {item, cred} = this.props;
        const endpoint = `https://lilchengs.herokuapp.com/messages/${item.id}/vote/${vote}?uid=${cred.uid}&token=${cred.token}`;

        const request = new Request(endpoint, {
            method: 'PUT',
            body: JSON.stringify({
                uid: cred.userid,
                token: cred.token,
            }),
        });

        fetch(request)
            .then(res => res.json())
            .then(
                result => {
                    this.setState({
                        vote: vote,
                        success: true,
                    });
                },
                error => {
                    //TODO temporary used for debugging
                    alert(error);
                },
            );
    };

    render() {
        const {item} = this.props;
        const {success, vote} = this.state;
        if (success) {
            return <Redirect to="/home" />;
        }
        
        return (
            <Feed.Event key={item.id}>
                <Feed.Content>
                    <Feed.Summary>
                        <Feed.User as={Link} to={`/feed/${item.id}`}>
                            {item.title}
                        </Feed.User>
                        <Feed.Date>
                            posted on {item.timestamp.substring(0, 10)}
                            {/*
                            {' by '}
                            <Feed.User as={Link} to={`/profile/${item.uid}`}>
                                {item.uid}
                            </Feed.User> */}
                        </Feed.Date>
                    </Feed.Summary>
                    {/*<Feed.Extra text>{item.message}</Feed.Extra>*/}
                    <Feed.Meta>
                        <Feed.Like onClick={() => this.submitVote(1)}>
                            <Icon name="thumbs up outline" />
                            {item.upvotes} Upvotes
                        </Feed.Like>
                        <Feed.Like onClick={() => this.submitVote(-1)}>
                            <Icon name="thumbs down outline" />
                            {item.downvotes} Downvotes
                        </Feed.Like>
                        <EditModal item={item} />
                    </Feed.Meta>
                </Feed.Content>
            </Feed.Event>
        );
    }
}

class EditModal extends Component {
    constructor(props) {
        super(props);
        this.state = {
            title: this.props.item.title,
            content: this.props.item.message,
            showModal: false,
            error: null,
        };
    }
    // update value as input comes in
    handleChange = (e, {name, value}) => this.setState({[name]: value});

    submitEdit = () => {
        const {item} = this.props;
        const endpoint = `https://salty-everglades-74589.herokuapp.com/messages/${item.id}`;
        const request = new Request(endpoint, {
            method: 'PUT',
            body: JSON.stringify({
                title: this.state.title,
                message: this.state.content,
            }),
        });
        fetch(request)
            .then(res => res.json())
            .then(
                result => {
                    this.closeModal();
                    window.location.reload();
                },
                error => {
                    this.setState({error: error});
                },
            );
    };

    closeModal = () => {
        this.setState({showModal: false});
    };

    handleDismiss = () => {
        this.setState({error: null});
    };

    render() {
        const {title, content, error, showModal} = this.state;
        return (
            <Modal
                open={showModal}
                centered={false}
                trigger={
                    <a onClick={() => this.setState({showModal: true})}>Edit</a>
                }>
                <Modal.Header>{title}</Modal.Header>
                <Modal.Content image>
                    <Modal.Description>
                        <Form>
                            <Form.Input
                                control={TextArea}
                                label="Content"
                                name="content"
                                placeholder="Write down what you would like to share..."
                                value={content}
                                onChange={this.handleChange}
                            />
                        </Form>
                        {!error || (
                            <Message
                                negative
                                size="large"
                                onDismiss={this.handleDismiss}
                                header="Oops... something went wrong"
                                content={`${error}`}
                            />
                        )}
                    </Modal.Description>
                </Modal.Content>
                <Modal.Actions>
                    <Button onClick={this.closeModal}>Cancel</Button>
                    <Button primary onClick={this.submitEdit}>
                        Submit Changes
                    </Button>
                </Modal.Actions>
            </Modal>
        );
    }
}

export default HomePage;
