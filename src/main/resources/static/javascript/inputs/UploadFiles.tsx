/* eslint-disable react/no-array-index-key */
import React, { useState, useCallback, useEffect } from 'react';
import { Notification, Progress, Button } from 'rbx';
import { useDropzone } from 'react-dropzone';

type UploadFilesProps = {
    id: string;
    pictures: File[];
    existingPictureUrls: string[];
    setPictures: (pictures: File[]) => void;
    setExistingPictureUrls: (existingPictureUrls: string[]) => void;
};

const UploadFiles = ({
    id,
    pictures,
    setPictures,
    existingPictureUrls,
    setExistingPictureUrls,
}: UploadFilesProps) => {
    const [pictureSrcs, setPictureSrcs] = useState<string[]>([]);
    const processFile = (file: File) => {
        const reader = new FileReader();

        return new Promise((resolve, reject) => {
            reader.readAsDataURL(file);
            reader.onabort = () => {
                reject(new Error('File reading was aborted'));
            };
            reader.onerror = () => {
                reject(new Error('File reading has failed'));
            };
            reader.onload = () => {
                resolve(reader.result);
            };
        });
    };

    const onDrop = useCallback(
        async (acceptedFiles) => {
            const processedFiles = await Promise.all(
                acceptedFiles.map((acceptedFile: File) =>
                    processFile(acceptedFile)
                )
            );
            setPictureSrcs([...pictureSrcs, ...(processedFiles as string[])]);
            setPictures([...pictures, ...acceptedFiles]);
        },
        [setPictures, pictures, setPictureSrcs, pictureSrcs]
    );
    const { getRootProps, getInputProps } = useDropzone({ onDrop });

    const handleDeleteClick = (e: React.MouseEvent<HTMLInputElement>) => {
        const indexToDelete = (e.target as HTMLInputElement).value.toString();
        if (parseInt(indexToDelete, 10) >= existingPictureUrls.length) {
            setPictures(
                pictures.filter(
                    (picture: File, index: number) =>
                        index.toString() !== indexToDelete
                )
            );
        } else {
            setExistingPictureUrls(
                existingPictureUrls.filter(
                    (pictureUrl: string, index: number) =>
                        index.toString() !== indexToDelete
                )
            );
        }

        setPictureSrcs(
            pictureSrcs.filter(
                (pictureSrc, index) => index.toString() !== indexToDelete
            )
        );
    };

    return (
        <>
            <div {...getRootProps()}>
                <input id={id} {...getInputProps()} />
                <p>[Drag and drop some files here, or click to select files]</p>
            </div>
            <ul>
                {pictureSrcs.map((pictureSrc, index) => (
                    <li key={index}>
                        <img width="50px" src={pictureSrc} alt="uploaded" />
                        <Button
                            size="small"
                            color="danger"
                            onClick={handleDeleteClick}
                            value={index}
                        >
                            X
                        </Button>
                    </li>
                ))}
            </ul>
        </>
    );
};

export default UploadFiles;
