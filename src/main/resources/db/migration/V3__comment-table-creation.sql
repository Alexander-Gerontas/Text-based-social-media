CREATE TABLE comments (
    id SERIAL PRIMARY KEY,
    content VARCHAR(1000) NOT NULL,
    author_id SERIAL NOT NULL,
    post_id SERIAL NOT NULL,
    create_date DATE NOT NULL,
    CONSTRAINT account_fk FOREIGN KEY (author_id) REFERENCES accounts(id),
    CONSTRAINT post_fk FOREIGN KEY (post_id) REFERENCES posts(id)
);