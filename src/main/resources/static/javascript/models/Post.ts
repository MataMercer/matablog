import { Blog } from './Blog';

export type Post = {
    id: string;
    blog: Blog;
    title: string;
    content: string;
    isSensitive: boolean;
    createdAt: Date
}