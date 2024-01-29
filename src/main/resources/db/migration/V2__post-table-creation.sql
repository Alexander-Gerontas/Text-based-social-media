CREATE TABLE posts (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    content VARCHAR(3000) NOT NULL,
    author_id SERIAL NOT NULL,
    create_date DATE NOT NULL,
    CONSTRAINT post_fk FOREIGN KEY (author_id) REFERENCES accounts(id)
);