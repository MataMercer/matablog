import React, { useEffect, useRef, useState } from 'react';
import axios from 'axios';
import CreateReplyForm from '../forms/CreateReplyForm';
import { Post } from '../models/Post';
import Reply from './Reply';

type RepliesSectionProps = {
    postId: string;
};

export default ({ postId }: RepliesSectionProps) => {
    const [index, setIndex] = useState<number>(0);
    const [status, setStatus] = useState<'idle' | 'loading' | 'errored'>(
        'loading',
    );
    const [apiError, setApiError] = useState<string>('');
    const [replies, setReplies] = useState<Post[]>([]);

    useEffect(() => {
        axios({
            url: `/api/post/${postId}/replies`,
            method: 'GET',
        })
            .then((response) => {
                setStatus('idle');
                console.log(response.data);
                setReplies(response.data);
            })
            .catch((err) => {
                setStatus('errored');
            });
    }, [apiError, status]);

    const reload = () => {
        setStatus('loading');
    }

    return (
        <div>
            <CreateReplyForm parentPostId={postId} reloadReplies={reload}/>
            {
                replies.map(reply => <Reply key={reply.id} post={reply}/>)
            }
        </div>
    );
};
