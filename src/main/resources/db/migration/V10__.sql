alter table files add file_id BIGINT;

alter table files add CONSTRAINT FK_FILES_ON_FILE FOREIGN KEY (file_id) REFERENCES posts (id);

alter table files drop constraint fk_files_on_post;

alter table files drop COLUMN post_id;