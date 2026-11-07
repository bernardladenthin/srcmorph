# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Writes the large-file corpus: one SQL dump, one XML catalog and one Java enum, each far beyond a
small model's window, with the facts a faithful summary states (<corpus dir>-facts.json next to the
corpus directory, so the scorer does not take it for a source file).

Each file also carries names only its middle holds: a table and an index between the dump's
INSERT batches, an element of the catalog's middle categories, and an area of error codes in the
middle of the enum. A summary that read the head of a file, or sampled past its middle, misses
them; one that read every chunk names them.

The files are generated, not stored: the same seed writes the same bytes, so a run is reproducible
and the repository carries no megabytes of fixture. run.py takes the directory as --corpus and
score.py the facts file as --facts.

Usage: python large.py --out <corpus dir> [--scale 1.0]
"""
import argparse
import json
import random
import sys
from pathlib import Path

SEED = 20261031


def sql_dump(rng, scale):
    """A bank data dump: three tables, two indexes and a view, then multi-row INSERT batches, with a fourth
    table and a third index between them in the middle (ledger_hold, idx_hold_account)."""
    customers, accounts, transfers = int(4000 * scale), int(6000 * scale), int(20000 * scale)
    lines = [
        "-- Data dump of the payments ledger.",
        "CREATE TABLE ledger_customer (",
        "    customer_id INTEGER PRIMARY KEY,",
        "    display_name VARCHAR(120) NOT NULL,",
        "    country_code CHAR(2) NOT NULL",
        ");",
        "CREATE TABLE ledger_account (",
        "    account_id INTEGER PRIMARY KEY,",
        "    customer_id INTEGER NOT NULL REFERENCES ledger_customer (customer_id),",
        "    iban VARCHAR(34) NOT NULL UNIQUE,",
        "    opened_on DATE NOT NULL",
        ");",
        "CREATE TABLE ledger_transfer (",
        "    transfer_id INTEGER PRIMARY KEY,",
        "    from_account INTEGER NOT NULL REFERENCES ledger_account (account_id),",
        "    to_account INTEGER NOT NULL REFERENCES ledger_account (account_id),",
        "    amount_cents BIGINT NOT NULL CHECK (amount_cents > 0),",
        "    booked_at TIMESTAMP NOT NULL",
        ");",
        "CREATE INDEX idx_transfer_from ON ledger_transfer (from_account);",
        "CREATE INDEX idx_transfer_booked ON ledger_transfer (booked_at);",
        "CREATE VIEW ledger_daily_volume AS",
        "    SELECT CAST(booked_at AS DATE) AS day, SUM(amount_cents) AS volume_cents",
        "    FROM ledger_transfer GROUP BY CAST(booked_at AS DATE);",
    ]
    countries = ["DE", "AT", "CH", "NL", "FR", "IT", "ES", "PL"]

    def batches(table, columns, count, row):
        for start in range(1, count + 1, 500):
            end = min(start + 499, count)
            lines.append(f"INSERT INTO {table} ({columns}) VALUES")
            lines.extend(row(i) + ("," if i < end else ";") for i in range(start, end + 1))

    holds = max(1, int(50 * scale))
    transfer_row = (lambda i: f"    ({i}, {rng.randint(1, accounts)}, {rng.randint(1, accounts)}, "
                              f"{rng.randint(100, 10**7)}, '2026-{rng.randint(1, 10):02d}-{rng.randint(1, 28):02d} "
                              f"{rng.randint(0, 23):02d}:00:00')")
    batches("ledger_customer", "customer_id, display_name, country_code", customers,
            lambda i: f"    ({i}, 'Customer {i:05d}', '{rng.choice(countries)}')")
    batches("ledger_account", "account_id, customer_id, iban, opened_on", accounts,
            lambda i: f"    ({i}, {rng.randint(1, customers)}, 'DE{rng.randint(10**19, 10**20 - 1)}', "
                      f"'20{rng.randint(10, 26)}-{rng.randint(1, 12):02d}-{rng.randint(1, 28):02d}')")
    # The first transfers, the planted middle (a table, an index, its rows), the rest: the customers and
    # accounts before them weigh about as much as 8 600 transfer rows, so the middle of the file's bytes
    # comes after 5 500 of the 20 000 transfers.
    half = int(transfers * 0.275) // 500 * 500
    batches("ledger_transfer", "transfer_id, from_account, to_account, amount_cents, booked_at", half,
            transfer_row)
    lines.extend([
        "-- Holds placed by the fraud review on an account.",
        "CREATE TABLE ledger_hold (",
        "    hold_id INTEGER PRIMARY KEY,",
        "    account_id INTEGER NOT NULL REFERENCES ledger_account (account_id),",
        "    hold_reason VARCHAR(80) NOT NULL",
        ");",
        "CREATE INDEX idx_hold_account ON ledger_hold (account_id);",
    ])
    batches("ledger_hold", "hold_id, account_id, hold_reason", holds,
            lambda i: f"    ({i}, {rng.randint(1, accounts)}, 'review {i}')")
    batches("ledger_transfer", "transfer_id, from_account, to_account, amount_cents, booked_at", transfers - half,
            lambda i: transfer_row(i + half))
    facts = {
        "ids": ["ledger_customer", "ledger_account", "ledger_transfer", "idx_transfer_from",
                "idx_transfer_booked", "ledger_daily_volume", "iban", "amount_cents",
                "ledger_hold", "idx_hold_account", "hold_reason"],
        "numbers": [customers, accounts, transfers, holds],
        "middle": ["ledger_hold", "idx_hold_account", "hold_reason"],
    }
    return "\n".join(lines) + "\n", facts


def xml_catalog(rng, scale):
    """A product catalog: categories holding products, each with a price and attributes."""
    categories, per_category = 40, int(125 * scale)
    out = ['<?xml version="1.0" encoding="UTF-8"?>',
           '<catalog xmlns="urn:example:catalog" xmlns:inv="urn:example:inventory" version="3">']
    # The two middle categories' products carry a recall notice, an element nothing else has.
    middle = (categories // 2, categories // 2 + 1)
    for c in range(1, categories + 1):
        out.append(f'  <category id="cat-{c:03d}" name="Category {c}">')
        for p in range(1, per_category + 1):
            sku = f"SKU-{c:03d}-{p:04d}"
            out.append(f'    <product sku="{sku}">')
            out.append(f"      <title>Product {c}.{p}</title>")
            out.append(f'      <price currency="EUR">{rng.randint(100, 99999) / 100:.2f}</price>')
            out.append(f"      <inv:stock warehouse=\"W{rng.randint(1, 9)}\">{rng.randint(0, 500)}</inv:stock>")
            if c in middle:
                out.append(f'      <inv:recall batch="R-{c:03d}-{p:04d}">supplier recall</inv:recall>')
            out.append("    </product>")
        out.append("  </category>")
    out.append("</catalog>")
    facts = {"ids": ["catalog", "category", "product", "price", "sku", "urn:example:catalog", "inv:stock",
                     "inv:recall"],
             "words": ["catalog", "category", "product", "price"],
             "numbers": [categories, categories * per_category],
             "middle": ["inv:recall"]}
    return "\n".join(out) + "\n", facts


def java_enum(rng, scale):
    """An error-code enum: many constants, a constructor, two accessors and a lookup."""
    count = int(6000 * scale)
    areas = ["AUTH", "BILLING", "STORAGE", "NETWORK", "REPORT", "IMPORT"]
    out = ["// Generated error catalogue.", "package example.errors;", "",
           "import java.util.HashMap;", "import java.util.Map;", "",
           "public enum PlatformErrorCode {"]
    # The middle tenth of the constants belongs to an area nothing else has.
    middle = range(count * 9 // 20 + 1, count * 11 // 20 + 1)
    for i in range(1, count + 1):
        area = "LEDGER_LOCK" if i in middle else areas[i % len(areas)]
        out.append(f'    {area}_{i:05d}({i}, "{area.lower()} failure {i}"){"," if i < count else ";"}')
    out += ["",
            "    private static final Map<Integer, PlatformErrorCode> BY_NUMBER = new HashMap<>();", "",
            "    static {",
            "        for (PlatformErrorCode code : values()) {",
            "            BY_NUMBER.put(code.number, code);",
            "        }",
            "    }", "",
            "    private final int number;",
            "    private final String message;", "",
            "    PlatformErrorCode(int number, String message) {",
            "        this.number = number;",
            "        this.message = message;",
            "    }", "",
            "    public int getNumber() {", "        return number;", "    }", "",
            "    public String getMessage() {", "        return message;", "    }", "",
            "    public static PlatformErrorCode byNumber(int number) {",
            "        return BY_NUMBER.get(number);",
            "    }",
            "}"]
    facts = {"ids": ["PlatformErrorCode", "getNumber", "getMessage", "byNumber", "BY_NUMBER", "number", "message",
                     "LEDGER_LOCK"],
             "words": ["number", "message"],
             "numbers": [count],
             "middle": ["LEDGER_LOCK"]}
    return "\n".join(out) + "\n", facts


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--out", required=True)
    parser.add_argument("--scale", type=float, default=1.0)
    args = parser.parse_args()
    if args.scale <= 0:
        sys.exit("--scale must be above 0")
    out = Path(args.out)
    rng = random.Random(SEED)
    facts = {"_comment": "Generated by large.py --scale %s; ids, words and numbers as in facts.json; 'middle' "
                         "the ids only the middle of a file holds." % args.scale}
    for rel, (text, fact) in (("sql/ledger/ledger_dump.sql", sql_dump(rng, args.scale)),
                              ("other/catalog/catalog.xml", xml_catalog(rng, args.scale)),
                              ("java/errors/PlatformErrorCode.java", java_enum(rng, args.scale))):
        path = out / rel
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_bytes(text.encode("utf-8"))
        facts[rel] = fact
        print(f"{rel}: {len(text.encode('utf-8')) / 1000:.0f} KB")
    facts_path = out.parent / (out.name + "-facts.json")
    facts_path.write_text(json.dumps(facts, indent=1) + "\n", encoding="utf-8", newline="\n")
    print(f"facts: {facts_path}")


if __name__ == "__main__":
    main()
