alter table files add post_id BIGINT;

alter table files add CONSTRAINT FK_FILES_ON_POST FOREIGN KEY (post_id) REFERENCES posts (id);

alter table files drop constraint fk_files_on_file;

alter table files drop COLUMN file_id;