import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import {
    Button,
    Modal,
    Delete,
    Field,
    Label,
    Control,
    Input,
    Checkbox,
} from 'rbx';
import TagField from './Tags/TagField';
import axios from 'axios';
import qs, { stringify } from 'qs';

type CreatePostFormState = {
    title: string;
    content: string;
    tags: string[];
    isCommunityTaggingEnabled: boolean;
    isSensitive: boolean;
    suggestions: string[];
};

type CreatePostFormProps = {
    csrfParameterName: string | null;
    csrfToken: string | null;
};

class CreatePostForm extends Component<
    CreatePostFormProps,
    CreatePostFormState
> {
    constructor(props: CreatePostFormProps) {
        super(props);

        this.state = {
            title: '',
            content: '',
            tags: ['apples', 'oranges'],
            suggestions: [],
            isSensitive: false,
            isCommunityTaggingEnabled: false,
        };

        this.handleOnChange = this.handleOnChange.bind(this);
        this.handleOnSubmit = this.handleOnSubmit.bind(this);
    }

    handleOnChange(e: React.ChangeEvent<HTMLInputElement>) {
        switch (e.target.name) {
            case 'title':
                this.setState({ title: e.target.value });
                break;
            case 'content':
                this.setState({ content: e.target.value });
                break;
            case 'tags':
                this.setState((prev) => ({
                    tags: prev.tags.concat(e.target.value),
                }));
                break;
            case 'isSensitive':
                this.setState({ isSensitive: e.target.checked });
                break;
            case 'isCommunityTaggingEnabled':
                this.setState({ isCommunityTaggingEnabled: e.target.checked });
                break;
        }
    }

    handleOnSubmit = async (e: React.SyntheticEvent) => {
        interface FormData {
            [data: string]: string | Array<string> | boolean;//indexer
        }

        let formData : FormData = {
            'title': this.state.title,
            'content': this.state.content,
            'postTags': this.state.tags,
            'sensitive': this.state.isSensitive,
            'communityTaggingEnabled': this.state.isCommunityTaggingEnabled,
        };

        if(this.props.csrfParameterName && this.props.csrfToken){
            formData[this.props.csrfParameterName] = this.props.csrfToken;
        }
        
        axios({
            method: 'post',
            url: '/newpost',
            data: qs.stringify(formData),
        })
            .then(function (response) {
                //handle success
                console.log(response);
            })
            .catch(function (response) {
                //handle error
                
                console.log(response);
            });

    };

    public render() {
        return (
            <form method="post" action="/newpost">
                <Field>
                    <Label>Title</Label>
                    <Control>
                        <Input
                            name="title"
                            type="text"
                            placeholder="Title"
                            onChange={this.handleOnChange}
                        />
                    </Control>
                </Field>

                <Field>
                    <Label>Content</Label>
                    <Control>
                        <Input
                            name="content"
                            type="text"
                            placeholder="Content of the post..."
                            onChange={this.handleOnChange}
                        />
                    </Control>
                </Field>
                <Field>
                    <Label>Tags</Label>
                    <TagField
                        name="tags"
                        value={this.state.tags}
                        suggestions={this.state.suggestions}
                        onChange={this.handleOnChange}
                    />
                </Field>
                <Field>
                    <Label>
                        <Checkbox
                            name="isSensitive"
                            onChange={this.handleOnChange}
                        />{' '}
                        is this sensitive?
                    </Label>
                </Field>

                <Field>
                    <Label>
                        <Checkbox
                            name="isCommunityTaggingEnabled"
                            onChange={this.handleOnChange}
                        />{' '}
                        allow tagging by community?
                    </Label>
                </Field>
                <Button color="primary">Submit</Button>
            </form>
        );
    }
}

export default CreatePostForm;
