ALTER TABLE refresh_tokens
    ADD fk_user BIGINT;

ALTER TABLE likes
    ADD CONSTRAINT uc_896cc0ba557eb7b2c96f7ca13 UNIQUE (fk_liker, fk_post);

ALTER TABLE refresh_tokens
    ADD CONSTRAINT FK_REFRESH_TOKENS_ON_FK_USER FOREIGN KEY (fk_user) REFERENCES users (id);

ALTER TABLE refresh_tokens
    DROP CONSTRAINT fk_refresh_tokens_on_user;

ALTER TABLE refresh_tokens
    DROP COLUMN user_id;