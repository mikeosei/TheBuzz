import React, {Component} from 'react';
import {
    Button,
    Message,
    Segment,
    Form,
    Input,
    TextArea,
    Label,
} from 'semantic-ui-react';
import auth from '../auth';
import {Redirect} from 'react-router-dom';
import { connectableObservableDescriptor } from 'rxjs/internal/observable/ConnectableObservable';
//import { Document } from 'react-pdf';

class AddNewPage extends Component {
    constructor(props) {
        super(props);
        this.fileInput = React.createRef();  //for uploading files
        this.state = {
            title: '',
            files: null,
            content: '',
            base64file: [],
            error: null,
            success: false,
            cred: auth.getCred(),
        };
    }

    handleChange = (e, {name, value}) => this.setState({[name]: value});

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
        
                // If all files have been proceed
                // if (allFiles.length == files.length){
                //     // Apply Callback function
                //     if (this.props.multiple) {
                //         this.props.onDone(allFiles);
                //     } else {
                //         this.props.onDone(allFiles[0]);
                //     }
                // }
        
                //use the following to prove that conversion works and the file 
                //information is stored in the fileInfo
                // console.log("file name is " + fileInfo.name);
                // console.log("thebase64string is " + fileInfo.base64file);
                this.setState({base64file: fileInfo});
            } // reader.onload
        
        } // for

        
    }
    handleDismiss = () => {
        this.setState({error: null});
    };

    handleSubmit = () => {
        const {title, content, files, base64file, cred} = this.state;
        const endpoint = 'https://lilchengs.herokuapp.com/' ; //URL throwing error

        const request = new Request(endpoint, {
            method: 'POST',
            body: JSON.stringify({
                title: title,
                message: content,
                files: FileList,    //newly added for sending base64string file to the backend. 
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

    render() {
        const {title, content, files, error, success} = this.state;
        if (success) {
            return <Redirect to="/home" />;
        }
        return (
            <Segment raised>
                {!error || (
                    <Message
                        negative
                        size="large"
                        onDismiss={this.handleDismiss}
                        header="Oops... something went wrong"
                        content={error}
                    />
                )}
                <Form>
                    <Form.Group widths="equal">
                        <Form.Input
                            control={Input}
                            name="title"
                            label="Title"
                            placeholder="Title"
                            value={title}
                            onChange={this.handleChange}
                        />
                    </Form.Group>
                    <Form.Input
                        control={TextArea}
                        label="Content"
                        name="content"
                        placeholder="Write down what you would like to share..."
                        value={content}
                        onChange={this.handleChange}
                    />
                    <Form.Input
                        type="file" 
                        accept="application/pdf" 
                        onChange={this.handleFile}
                    />
                    <Form.Field>
                        <Button control={Button} onClick={this.handleSubmit} content='Add New Post' primary />
                    </Form.Field>
                </Form>
            </Segment>
        );
    }
}
export default AddNewPage;
