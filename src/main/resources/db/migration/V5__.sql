ALTER TABLE follows
    ADD fk_liker BIGINT;

ALTER TABLE follows
    ADD fk_post BIGINT;

ALTER TABLE follows
    ADD CONSTRAINT FK_FOLLOWS_ON_FK_LIKER FOREIGN KEY (fk_liker) REFERENCES blogs (id);

ALTER TABLE follows
    ADD CONSTRAINT FK_FOLLOWS_ON_FK_POST FOREIGN KEY (fk_post) REFERENCES posts (id);

ALTER TABLE follows
DROP
CONSTRAINT fk_follows_on_fk_followee_blog;

ALTER TABLE follows
DROP
CONSTRAINT fk_follows_on_fk_follower_blog;

ALTER TABLE follows
DROP
COLUMN fk_followee_blog;

ALTER TABLE follows
DROP
COLUMN fk_follower_blog;

ALTER TABLE follows
DROP
COLUMN muted;

ALTER TABLE follows
DROP
COLUMN notifications_enabled;