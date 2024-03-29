create TABLE blogs
(
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at          TIMESTAMP with time zone                NOT NULL,
    updated_at          TIMESTAMP with time zone                NOT NULL,
    blog_name           VARCHAR(255)                            NOT NULL,
    preferred_blog_name VARCHAR(255)                            NOT NULL,
    is_sensitive        BOOLEAN                                 NOT NULL,
    CONSTRAINT pk_blogs PRIMARY KEY (id)
);

create TABLE files
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at TIMESTAMP with time zone                NOT NULL,
    updated_at TIMESTAMP with time zone                NOT NULL,
    name       VARCHAR(255)                            NOT NULL,
    blog_id    BIGINT,
    file_id    BIGINT,
    CONSTRAINT pk_files PRIMARY KEY (id)
);

create TABLE follows
(
    id                    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at            TIMESTAMP with time zone                NOT NULL,
    updated_at            TIMESTAMP with time zone                NOT NULL,
    fk_follower_blog      BIGINT,
    fk_followee_blog      BIGINT,
    notifications_enabled BOOLEAN,
    muted                 BOOLEAN,
    CONSTRAINT pk_follows PRIMARY KEY (id)
);

create TABLE post_posttag
(
    post_id    BIGINT NOT NULL,
    posttag_id BIGINT NOT NULL,
    CONSTRAINT pk_post_posttag PRIMARY KEY (post_id, posttag_id)
);

create TABLE post_tags
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at TIMESTAMP with time zone                NOT NULL,
    updated_at TIMESTAMP with time zone                NOT NULL,
    name       VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_post_tags PRIMARY KEY (id)
);

create TABLE posts
(
    id                           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at                   TIMESTAMP with time zone                NOT NULL,
    updated_at                   TIMESTAMP with time zone                NOT NULL,
    blog_id                      BIGINT                                  NOT NULL,
    post_id                      BIGINT,
    title                        VARCHAR(255),
    content                      VARCHAR(255)                            NOT NULL,
    is_community_tagging_enabled BOOLEAN                                 NOT NULL,
    is_sensitive                 BOOLEAN                                 NOT NULL,
    is_published                 BOOLEAN                                 NOT NULL,
    CONSTRAINT pk_posts PRIMARY KEY (id)
);

create TABLE refresh_tokens
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at TIMESTAMP with time zone                NOT NULL,
    updated_at TIMESTAMP with time zone                NOT NULL,
    user_id    BIGINT                                  NOT NULL,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id)
);

create TABLE user_blog
(
    blog_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT pk_user_blog PRIMARY KEY (blog_id, user_id)
);

create TABLE user_key_pairs
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at     TIMESTAMP with time zone                NOT NULL,
    updated_at     TIMESTAMP with time zone                NOT NULL,
    user_key_pairs BIGINT,
    public_key     VARCHAR(2048)                           NOT NULL,
    private_key    VARCHAR(2048)                           NOT NULL,
    CONSTRAINT pk_user_key_pairs PRIMARY KEY (id)
);

create TABLE users
(
    id                         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at                 TIMESTAMP with time zone                NOT NULL,
    updated_at                 TIMESTAMP with time zone                NOT NULL,
    username                   VARCHAR(255),
    email                      VARCHAR(255),
    password                   VARCHAR(255),
    is_account_non_expired     BOOLEAN                                 NOT NULL,
    is_account_non_locked      BOOLEAN                                 NOT NULL,
    is_credentials_non_expired BOOLEAN                                 NOT NULL,
    is_enabled                 BOOLEAN                                 NOT NULL,
    blog_id                    BIGINT,
    authentication_provider    VARCHAR(255),
    role                       VARCHAR(255),
    o_auth2id                  VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

create TABLE verification_token
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created_at  TIMESTAMP with time zone                NOT NULL,
    updated_at  TIMESTAMP with time zone                NOT NULL,
    token       VARCHAR(255),
    user_id     BIGINT                                  NOT NULL,
    expiry_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_verification_token PRIMARY KEY (id)
);

alter table post_tags
    add CONSTRAINT uc_post_tags_name UNIQUE (name);

alter table users
    add CONSTRAINT uc_users_email UNIQUE (email);

alter table users
    add CONSTRAINT uc_users_username UNIQUE (username);

alter table files
    add CONSTRAINT FK_FILES_ON_BLOG FOREIGN KEY (blog_id) REFERENCES blogs (id);

alter table files
    add CONSTRAINT FK_FILES_ON_FILE FOREIGN KEY (file_id) REFERENCES posts (id);

alter table follows
    add CONSTRAINT FK_FOLLOWS_ON_FK_FOLLOWEE_BLOG FOREIGN KEY (fk_followee_blog) REFERENCES blogs (id);

alter table follows
    add CONSTRAINT FK_FOLLOWS_ON_FK_FOLLOWER_BLOG FOREIGN KEY (fk_follower_blog) REFERENCES blogs (id);

alter table posts
    add CONSTRAINT FK_POSTS_ON_BLOG FOREIGN KEY (blog_id) REFERENCES blogs (id);

alter table posts
    add CONSTRAINT FK_POSTS_ON_POST FOREIGN KEY (post_id) REFERENCES posts (id);

alter table refresh_tokens
    add CONSTRAINT FK_REFRESH_TOKENS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

alter table users
    add CONSTRAINT FK_USERS_ON_BLOG FOREIGN KEY (blog_id) REFERENCES blogs (id);

alter table user_key_pairs
    add CONSTRAINT FK_USER_KEY_PAIRS_ON_USERKEYPAIRS FOREIGN KEY (user_key_pairs) REFERENCES users (id);

alter table verification_token
    add CONSTRAINT FK_VERIFICATION_TOKEN_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

alter table post_posttag
    add CONSTRAINT fk_pospos_on_post FOREIGN KEY (post_id) REFERENCES posts (id);

alter table post_posttag
    add CONSTRAINT fk_pospos_on_post_tag FOREIGN KEY (posttag_id) REFERENCES post_tags (id);

alter table user_blog
    add CONSTRAINT fk_user_blog_on_blog FOREIGN KEY (blog_id) REFERENCES blogs (id);

alter table user_blog
    add CONSTRAINT fk_user_blog_on_user FOREIGN KEY (user_id) REFERENCES users (id);