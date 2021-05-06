import React, { useEffect, useRef, useState } from 'react';
import {
    Notification,
    Progress,
    Help,
    Card,
    Label,
    Input,
    Field,
    Button,
    Title,
    Textarea,
} from 'rbx';
import {
    faCheck,
    faFontAwesomeLogoFull,
} from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useForm, Controller } from 'react-hook-form';
import axios from 'axios';
import ReactMarkdown from 'react-markdown';
import ReactMde from 'react-mde';
import UploadFiles from '../inputs/UploadFiles';
import { Tag } from 'react-tag-autocomplete';
import TagsInput from '../inputs/TagsInput';

type FormData = {
    title: string;
    content: string;
    tags: Tag[];
    communityTaggingEnabled: boolean;
    sensitive: boolean;
    files: File[];
};

type CreateReplyFormProps = {
    parentPostId: string;
};

export default ({ parentPostId }: CreateReplyFormProps) => {
    const [loading, setLoading] = useState<boolean>(false);
    const [apiError, setApiError] = useState<string>('');
    const {
        control,
        register,
        handleSubmit,
        errors,
        setValue,
        watch,
    } = useForm<FormData>({
        criteriaMode: 'all',
    });

    const onSubmit = handleSubmit((data) => {
        setLoading(true);
        const url = '/api/post/create';

        const formData = new FormData();
        formData.append('title', data.title);
        formData.append(
            'postTags',
            convertJsonListToFormDataList(convertTagListToStringList(data.tags))
        );
        formData.append('content', data.content);
        formData.append(
            'communityTaggingEnabled',
            JSON.stringify(data.communityTaggingEnabled)
        );
        formData.append('sensitive', JSON.stringify(data.sensitive));
        data.files.forEach((file) => {
            formData.append('files', file);
        });
        formData.append('parentPostId', parentPostId);

        axios({
            url,
            method: 'post',
            headers: {
                'Content-Type': 'multipart/form-data',
            },
            data: formData,
        })
            .then((response) => {
                setLoading(false);
                setApiError('');
                console.log(response.data);
            })
            .catch((err) => {
                setLoading(false);
                setApiError(err.message);
                console.log(err);
            });
    });

    const convertTagListToStringList = (tags: Tag[]) => {
        return tags.map((elem) => elem.name);
    };

    const convertJsonListToFormDataList = (input: string[]) => {
        return JSON.stringify(input)
            .replaceAll('"', '')
            .replaceAll('[', '')
            .replaceAll(']', '');
    };

    return (
        <>
            <Title size={3}>Reply</Title>
            <form onSubmit={onSubmit}>
                {loading && <Progress />}
                {apiError && (
                    <Notification color="danger">{apiError}</Notification>
                )}
                <Field>
                    <Label>Content</Label>
                    <Field.Body>
                        <Controller
                            name="content"
                            control={control}
                            defaultValue={''}
                            rules={{ required: true }}
                            render={(props) => {
                                const [selectedTab, setSelectedTab] = useState<
                                    'write' | 'preview' | undefined
                                >('write');
                                return (
                                    <ReactMde
                                        value={props.value}
                                        onChange={props.onChange}
                                        selectedTab={selectedTab}
                                        onTabChange={setSelectedTab}
                                        generateMarkdownPreview={(markdown) =>
                                            Promise.resolve(
                                                <ReactMarkdown
                                                    source={markdown}
                                                />
                                            )
                                        }
                                    />
                                );
                            }} // props contains: onChange, onBlur and value
                        />
                    </Field.Body>
                </Field>
                <Field>
                    <Label>Tags</Label>
                    <Field.Body>
                        <Controller
                            name="tags"
                            control={control}
                            defaultValue={[]}
                            rules={{ required: false }}
                            render={(props) => {
                                return (
                                    <TagsInput
                                        tags={props.value}
                                        setTags={(tags) => {
                                            setValue('tags', tags);
                                        }}
                                    />
                                );
                            }}
                        />
                    </Field.Body>
                </Field>
                <Field>
                    <Label>Image Attachments</Label>
                    <Controller
                        name="files"
                        control={control}
                        defaultValue={[]}
                        rules={{ required: false }}
                        render={(props) => {
                            return (
                                //TODO: fix existing
                                <UploadFiles
                                    id="files"
                                    pictures={props.value}
                                    setPictures={(files) => {
                                        setValue('files', files);
                                    }}
                                    existingPictureUrls={[]}
                                    setExistingPictureUrls={() => {}}
                                />
                            );
                        }}
                    />
                </Field>
                <Field>
                    <label className="checkbox">
                        <input
                            type="checkbox"
                            name="communityTaggingEnabled"
                            ref={register({
                                required: false,
                            })}
                        />
                        Allow Community Tagging
                    </label>
                </Field>
                <Field>
                    <label className="checkbox">
                        <input
                            type="checkbox"
                            name="sensitive"
                            ref={register({
                                required: false,
                            })}
                        />
                        Mark as sensitive
                    </label>
                </Field>

                <Button color="primary" type="submit">
                    Submit
                </Button>
            </form>
        </>
    );
};
