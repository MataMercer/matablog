import { Post } from '../models/Post';
import React from 'react';
import { Content, Card, Media, Title } from 'rbx';

type ReplyProps = {
    post: Post;
}

export default ({ post }: ReplyProps) => {


    return (
        <Card>
            <Card.Content>
                <Media>
                    <Media.Item as='figure' align='left'>
                        //some image for profile pic
                    </Media.Item>
                    <Media.Item>
                        <Title as='p' size={4}>
                            {post.blog.preferredBlogName}
                        </Title>
                        <Title as='p' subtitle size={6}>
                            {`@${post.blog.blogName}`}
                        </Title>
                    </Media.Item>
                </Media>
                <Content>
                    {post.content}
                    <br />
                    <time>{post.createdAt}</time>
                </Content>
            </Card.Content>

        </Card>
    );

}