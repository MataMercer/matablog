import React, { useEffect, useRef, useState } from 'react';
import ReactTags from 'react-tag-autocomplete';
import { Tag } from 'react-tag-autocomplete';

type TagsInputProps = {
    tags: Tag[];
    setTags: (tags: Tag[]) => void;
};

export default ({ tags, setTags }: TagsInputProps) => {
    const [suggestions, setSuggestions] = useState<Tag[]>([]);

    useEffect(() => {
        //TODO: API call
        const convertStringListToTagList = (stringList: string[]) => {
            return stringList.map((elem, i) => ({ id: i, name: elem }));
        };
        setSuggestions(convertStringListToTagList(['tag1', 'tag2', 'tag3']));
    }, []);

    const onDelete = (i: number) => {
        const tempTags = tags.slice(0);
        tempTags.splice(i, 1);
        setTags(tempTags);
    };

    const onAddition = (tag: Tag) => {
        setTags([...tags, tag]);
    };

    return (
        <ReactTags
            tags={tags}
            suggestions={suggestions}
            onAddition={onAddition}
            onDelete={onDelete}
            allowNew={true}
        />
    );
};
