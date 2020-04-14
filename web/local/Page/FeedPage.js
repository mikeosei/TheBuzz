import React, {Component} from 'react';
import {
    Button,
    Comment,
    Icon,
    Form,
    Segment,
    Header,
    Message,
    Embed,
} from 'semantic-ui-react';
import {Link, useParams} from 'react-router-dom';
import auth from '../auth';
import { AST_PropAccess } from 'terser';

// gets the URL for the parameter 
function FeedPage() {
    const {feedId} = useParams();
    return <FeedPageComponent feedId={feedId} />;
}

class FeedPageComponent extends Component {
    constructor(props) {
        super(props);
        this.fileInput = React.createRef();  //for uploading files
        this.state = {
            cred: auth.getCred(),
            messageMap: null,
            currentMsg: null,
            files: null,     
            upLoadedFiles: '',
            fileName:'',
            base64file: [],         
            isLoaded: false,
            error: null,
        };
    }

    handleSubmitComment = () => {
        const {msg, cred} = this.state;
        const endpoint = 'https://lilchengs.herokuapp.com/';
        const request = new Request(endpoint, {
            method: 'POST',
            body: JSON.stringify({
                uid: cred.uid,
                token: cred.token,
                title: 'comment',
                message: msg.content,
            }),
        });
        fetch(request)
            .then(res => res.json())
            .then(
                result => {
                    alert('You have successfully posted a message!');
                    this.setState({success: true});
                },
                error => {
                    this.setState({error: error});
                },
            );
    };

    // get all messages 
    //also get all the files that were uploaded 
    componentDidMount() {
        const {cred, msg} = this.state;
        const endpoint = 'https://lilchengs.herokuapp.com/messages ' ; //idk what this url is supposed to be and its killing me
        const request = new Request(endpoint, {
            method: 'GET',
        });
        fetch(request)
            .then(res => res.json())
            .then(
                result => {
                    const commentMap = new Map();
                    const msgId = this.props.feedId;
                    const objArray = result.mData;
                    let currentMsg;
                    let pdfLink;
                    for (const obj of objArray) {
                        if (obj.pid === msgId) {
                            commentMap.set(obj.id, obj);
                             //let pdfLink = obj.link;
                        }
                        if (obj.id == msgId) {
                            currentMsg = obj;
                           
                        }
                    }
                    this.setState({isLoaded: true, currentMsg: currentMsg, commentMap: commentMap, upLoadedFiles: pdfLink});
                },
                error => {
                    alert("failed to retrieve comments");
                },
            );
    }

    handleDismiss = () => {
        this.setState({error: null});
    };

    handleFile = (e) => { // e = event
        let files = e.target.files;
        //convert to base64string
        var allFiles = [];
        for (var i = 0; i < files.length; i++) {
            let file = files[i];
            // Make new FileReader
            let reader = new FileReader();
        
            // Convert the file to base64 text
            reader.readAsDataURL(file);
        
            // on reader load somthing...
            reader.onload = () => {
                        
                //Make a fileInfo Object
                let fileInfo = {
                    name: file.name,
                    base64file: reader.result,
                };
            
                // Push it to the state
                allFiles.push(fileInfo);
                // this.setState({base64file: fileInfo});
            } // reader.onload
        } // for        
    }

    //TODO: implement the handle submit comment
    render() {
        const {currentMsg, commentMap, isLoaded, error} = this.state;
        if (error) {
            return (
                <Message
                    negative
                    size="large"
                    onDismiss={this.handleDismiss}
                    header="Oops... something went wrong"
                    content={error}
                />
            );
        } else if (!isLoaded) {
            return (
                <Message icon>
                    <Icon name="circle notched" loading />
                    <Message.Content>
                        <Message.Header>Just one second</Message.Header>
                        We are fetching that content for you.
                    </Message.Content>
                </Message>
            );
        } else {
            return (
                <Segment raised>
                    <Comment.Group style={{'max-width': 'none'}}>
                        <Article
                            title={currentMsg.title}
                            content={currentMsg.content}
                            uid={currentMsg.uid}
                        />
                        <CommentList commentMap={commentMap} />
                    </Comment.Group>
                </Segment>
            );
        }
    }
}

const Article = props => (
    <Header as="h3" dividing>
        <Header.Content>
            {props.title}
            <Header.Subheader as={Link} to={`/feed/${props.uid}`}>
                By user {props.uid}
            </Header.Subheader>
        </Header.Content>
    </Header>
);

// commentList takes in a hashmap and return a react component
// have to find a way to limit by Lehigh accounts
const CommentList = props => (
    <>
        <Header as="h4" dividing>
            Uploaded File: 
        </Header>
        <Header as="h4" dividing>
            Upload More Files:
        </Header>
        <Form.Input
            type="file" 
            accept="application/pdf" 
        />
        <br/>
        <Header as="h3" dividing>
            Comments
        </Header>
        {Object.keys(props.commentMap).length === 0 ? Array.from(props.commentMap.values()).map(item => (
            <CommentItem
                author={item.uid}
                timestamp={item.timestamp}
                content={item.message}
            />
        )) : <CommentItem
    />}
        <Form reply>

            <Form.TextArea />
            <Button
                content="Add Comment"
                labelPosition="left"
                icon="edit"
                primary
            />
        </Form>
    </>
);
const CommentItem = props => (
    <Comment>
        <Comment.Content>
            <Comment.Author as={Link} to="/profile">
                {props.author}
            </Comment.Author>
            <Comment.Metadata>
                <div>{props.timestamp}</div>
            </Comment.Metadata>
            <Comment.Text>{props.content}</Comment.Text>
        </Comment.Content>
    </Comment>
);

export default FeedPage;
