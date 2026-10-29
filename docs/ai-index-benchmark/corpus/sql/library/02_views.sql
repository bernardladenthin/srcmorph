-- SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
--
-- SPDX-License-Identifier: Apache-2.0
--
-- Lending library: reporting views (PostgreSQL).

CREATE VIEW overdue_loans AS
SELECT l.loan_id,
       m.member_id,
       m.email,
       b.title,
       l.due_on,
       CURRENT_DATE - l.due_on AS days_overdue
FROM loan l
JOIN member m ON m.member_id = l.member_id
JOIN book b ON b.book_id = l.book_id
WHERE l.returned_on IS NULL
  AND l.due_on < CURRENT_DATE;

CREATE VIEW member_loan_stats AS
SELECT m.member_id,
       m.full_name,
       COUNT(l.loan_id) AS loans_total,
       COUNT(l.loan_id) FILTER (WHERE l.returned_on IS NULL) AS loans_open,
       MAX(l.loaned_on) AS last_loan_on
FROM member m
LEFT JOIN loan l ON l.member_id = m.member_id
GROUP BY m.member_id, m.full_name;

CREATE MATERIALIZED VIEW popular_books AS
SELECT b.book_id,
       b.title,
       a.full_name AS author_name,
       COUNT(*) AS times_loaned
FROM loan l
JOIN book b ON b.book_id = l.book_id
JOIN author a ON a.author_id = b.author_id
WHERE l.loaned_on >= CURRENT_DATE - INTERVAL '365 days'
GROUP BY b.book_id, b.title, a.full_name
HAVING COUNT(*) >= 5;

CREATE UNIQUE INDEX idx_popular_books_id ON popular_books (book_id);
