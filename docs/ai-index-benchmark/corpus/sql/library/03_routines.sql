-- SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
--
-- SPDX-License-Identifier: Apache-2.0
--
-- Lending library: checkout, return, late fee and audit trail (PostgreSQL, PL/pgSQL).

CREATE TABLE loan_audit (
    audit_id   BIGSERIAL PRIMARY KEY,
    loan_id    BIGINT NOT NULL,
    action     VARCHAR(10) NOT NULL CHECK (action IN ('CHECKOUT', 'RETURN')),
    changed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Lends one free copy of a book to a member for p_days days and returns the new loan id.
CREATE FUNCTION fn_checkout_book(p_member_id BIGINT, p_book_id BIGINT, p_days INTEGER DEFAULT 21)
RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
    v_loan_id BIGINT;
BEGIN
    IF EXISTS (SELECT 1 FROM member WHERE member_id = p_member_id AND is_blocked) THEN
        RAISE EXCEPTION 'member % is blocked', p_member_id;
    END IF;

    UPDATE book
       SET copies_free = copies_free - 1
     WHERE book_id = p_book_id
       AND copies_free > 0;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'no free copy of book %', p_book_id;
    END IF;

    INSERT INTO loan (book_id, member_id, due_on)
    VALUES (p_book_id, p_member_id, CURRENT_DATE + p_days)
    RETURNING loan_id INTO v_loan_id;

    RETURN v_loan_id;
END;
$$;

-- Marks a loan returned and puts the copy back.
CREATE PROCEDURE sp_return_book(p_loan_id BIGINT)
LANGUAGE plpgsql
AS $$
DECLARE
    v_book_id BIGINT;
BEGIN
    UPDATE loan
       SET returned_on = CURRENT_DATE
     WHERE loan_id = p_loan_id
       AND returned_on IS NULL
    RETURNING book_id INTO v_book_id;
    IF v_book_id IS NULL THEN
        RAISE EXCEPTION 'loan % is unknown or already returned', p_loan_id;
    END IF;

    UPDATE book SET copies_free = copies_free + 1 WHERE book_id = v_book_id;
END;
$$;

-- Late fee in cents: 50 per day overdue, capped at 2000.
CREATE FUNCTION fn_late_fee(p_due_on DATE, p_returned_on DATE)
RETURNS INTEGER
LANGUAGE sql
IMMUTABLE
AS $$
    SELECT LEAST(GREATEST(COALESCE(p_returned_on, CURRENT_DATE) - p_due_on, 0) * 50, 2000);
$$;

CREATE FUNCTION trg_loan_audit_fn()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO loan_audit (loan_id, action) VALUES (NEW.loan_id, 'CHECKOUT');
    ELSIF NEW.returned_on IS NOT NULL AND OLD.returned_on IS NULL THEN
        INSERT INTO loan_audit (loan_id, action) VALUES (NEW.loan_id, 'RETURN');
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_loan_audit
AFTER INSERT OR UPDATE OF returned_on ON loan
FOR EACH ROW EXECUTE FUNCTION trg_loan_audit_fn();
