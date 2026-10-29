-- SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
--
-- SPDX-License-Identifier: Apache-2.0
--
-- Lending library: core schema (PostgreSQL).

CREATE TABLE author (
    author_id    BIGSERIAL PRIMARY KEY,
    full_name    VARCHAR(200) NOT NULL,
    birth_year   SMALLINT CHECK (birth_year BETWEEN 1000 AND 2100),
    country_code CHAR(2)
);

CREATE TABLE book (
    book_id        BIGSERIAL PRIMARY KEY,
    isbn           VARCHAR(17) NOT NULL UNIQUE,
    title          VARCHAR(300) NOT NULL,
    author_id      BIGINT NOT NULL REFERENCES author (author_id),
    published_year SMALLINT,
    copies_total   INTEGER NOT NULL DEFAULT 1 CHECK (copies_total >= 0),
    copies_free    INTEGER NOT NULL DEFAULT 1 CHECK (copies_free >= 0),
    CONSTRAINT book_free_le_total CHECK (copies_free <= copies_total)
);

CREATE TABLE member (
    member_id  BIGSERIAL PRIMARY KEY,
    email      VARCHAR(254) NOT NULL UNIQUE,
    full_name  VARCHAR(200) NOT NULL,
    joined_on  DATE NOT NULL DEFAULT CURRENT_DATE,
    is_blocked BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE loan (
    loan_id     BIGSERIAL PRIMARY KEY,
    book_id     BIGINT NOT NULL REFERENCES book (book_id),
    member_id   BIGINT NOT NULL REFERENCES member (member_id) ON DELETE CASCADE,
    loaned_on   DATE NOT NULL DEFAULT CURRENT_DATE,
    due_on      DATE NOT NULL,
    returned_on DATE,
    CONSTRAINT loan_due_after_loan CHECK (due_on > loaned_on)
);

CREATE INDEX idx_book_author ON book (author_id);
CREATE INDEX idx_loan_member ON loan (member_id);
CREATE INDEX idx_loan_open ON loan (due_on) WHERE returned_on IS NULL;
