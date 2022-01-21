ALTER TABLE posts
    ADD published BOOLEAN;

ALTER TABLE posts
    ALTER COLUMN published SET DEFAULT FALSE,
    ALTER COLUMN published SET NOT NULL;

ALTER TABLE posts
    DROP COLUMN is_published;