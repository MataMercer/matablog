import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { Button, Modal, Delete, Field, Tag, Control, Input } from 'rbx';

type TagFieldProps = {
    name: string;
    suggestions: string[];
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    value: string[];
};

type TagFieldState = {
    newTag: string;
};

class TagField extends Component<TagFieldProps, TagFieldState> {
    constructor(props: TagFieldProps) {
        super(props);

        this.handleChange = this.handleChange.bind(this);
        this.handleRemoveTag = this.handleRemoveTag.bind(this);
        this.handleKeyDown = this.handleKeyDown.bind(this);
    }

    handleChange(e: React.ChangeEvent<HTMLInputElement>) {
        this.setState({ newTag: e.target.value });
    }

    handleKeyDown(e: any) {
        
        if (e.keyCode === 13 && e.target.value !== '') {
            e.preventDefault();
            let newTag = this.state.newTag.trim();

            if (this.props.value.indexOf(newTag) === -1) {
                this.props.value.push(newTag);
                this.setState({ newTag: '' });
            }
            e.target.value = '';
        }
    }

    handleRemoveTag(e: React.ChangeEvent<HTMLInputElement>) {
        const tag = e.target.parentNode?.textContent?.trim();
        let index = this.props.value.indexOf(tag as string);
        this.props.value.splice(index, 1);
        this.setState({ newTag: '' });
    }

    render() {
        return (
            <div className="tags-input">
                    <Field multiline kind="group">
                        {this.props.value.map((name) => (
                            <Control key={name}>
                                <Tag.Group gapless>
                                    <Tag color="dark" size="medium">{name}</Tag>
                                    <Tag
                                        delete
                                        onClick={this.handleRemoveTag}
                                    />
                                </Tag.Group>
                            </Control>
                        ))}
                    </Field>
                    <Input
                        type="text"
                        onChange={this.handleChange}
                        onKeyDown={this.handleKeyDown}
                    ></Input>
            </div>
        );
    }
}

export default TagField;
