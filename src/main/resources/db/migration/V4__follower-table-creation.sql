-- V4__follower-table-creation.sql
CREATE TABLE followers (
    id SERIAL PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    CONSTRAINT follower_acc_fk FOREIGN KEY (follower_id) REFERENCES accounts(id),
    CONSTRAINT following_acc_fk FOREIGN KEY (following_id) REFERENCES accounts(id)
);