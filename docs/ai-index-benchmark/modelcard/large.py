# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: MIT OR Apache-2.0
"""Writes the large-file corpus: one SQL dump, one XML catalog and one Java enum, each far beyond a
small model's window, with the facts a faithful summary states (facts-large.json next to them).

The files are generated, not stored: the same seed writes the same bytes, so a run is reproducible
and the repository carries no megabytes of fixture. run.py takes the directory as --corpus and
score.py the facts file as --facts.

Usage: python large.py --out <corpus dir> [--scale 1.0]
"""
import argparse
import json
import random
from pathlib import Path

SEED = 20261031


def sql_dump(rng, scale):
    """A bank data dump: three tables, two indexes, a view, then multi-row INSERT batches."""
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

    batches("ledger_customer", "customer_id, display_name, country_code", customers,
            lambda i: f"    ({i}, 'Customer {i:05d}', '{rng.choice(countries)}')")
    batches("ledger_account", "account_id, customer_id, iban, opened_on", accounts,
            lambda i: f"    ({i}, {rng.randint(1, customers)}, 'DE{rng.randint(10**19, 10**20 - 1)}', "
                      f"'20{rng.randint(10, 26)}-{rng.randint(1, 12):02d}-{rng.randint(1, 28):02d}')")
    batches("ledger_transfer", "transfer_id, from_account, to_account, amount_cents, booked_at", transfers,
            lambda i: f"    ({i}, {rng.randint(1, accounts)}, {rng.randint(1, accounts)}, {rng.randint(100, 10**7)}, "
                      f"'2026-{rng.randint(1, 10):02d}-{rng.randint(1, 28):02d} {rng.randint(0, 23):02d}:00:00')")
    facts = {
        "ids": ["ledger_customer", "ledger_account", "ledger_transfer", "idx_transfer_from",
                "idx_transfer_booked", "ledger_daily_volume", "iban", "amount_cents"],
        "numbers": [customers, accounts, transfers],
    }
    return "\n".join(lines) + "\n", facts


def xml_catalog(rng, scale):
    """A product catalog: categories holding products, each with a price and attributes."""
    categories, per_category = 40, int(125 * scale)
    out = ['<?xml version="1.0" encoding="UTF-8"?>',
           '<catalog xmlns="urn:example:catalog" xmlns:inv="urn:example:inventory" version="3">']
    for c in range(1, categories + 1):
        out.append(f'  <category id="cat-{c:03d}" name="Category {c}">')
        for p in range(1, per_category + 1):
            sku = f"SKU-{c:03d}-{p:04d}"
            out.append(f'    <product sku="{sku}">')
            out.append(f"      <title>Product {c}.{p}</title>")
            out.append(f'      <price currency="EUR">{rng.randint(100, 99999) / 100:.2f}</price>')
            out.append(f"      <inv:stock warehouse=\"W{rng.randint(1, 9)}\">{rng.randint(0, 500)}</inv:stock>")
            out.append("    </product>")
        out.append("  </category>")
    out.append("</catalog>")
    facts = {"ids": ["catalog", "category", "product", "price", "sku", "urn:example:catalog", "inv:stock"],
             "numbers": [categories, categories * per_category]}
    return "\n".join(out) + "\n", facts


def java_enum(rng, scale):
    """An error-code enum: many constants, a constructor, two accessors and a lookup."""
    count = int(6000 * scale)
    areas = ["AUTH", "BILLING", "STORAGE", "NETWORK", "REPORT", "IMPORT"]
    out = ["// Generated error catalogue.", "package example.errors;", "",
           "import java.util.HashMap;", "import java.util.Map;", "",
           "public enum PlatformErrorCode {"]
    for i in range(1, count + 1):
        area = areas[i % len(areas)]
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
    facts = {"ids": ["PlatformErrorCode", "getNumber", "getMessage", "byNumber", "BY_NUMBER", "number", "message"],
             "numbers": [count]}
    return "\n".join(out) + "\n", facts


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--out", required=True)
    parser.add_argument("--scale", type=float, default=1.0)
    args = parser.parse_args()
    out = Path(args.out)
    rng = random.Random(SEED)
    facts = {"_comment": "Generated by large.py --scale %s; ids and numbers as in facts.json." % args.scale}
    for rel, (text, fact) in (("sql/ledger/ledger_dump.sql", sql_dump(rng, args.scale)),
                              ("other/catalog/catalog.xml", xml_catalog(rng, args.scale)),
                              ("java/errors/PlatformErrorCode.java", java_enum(rng, args.scale))):
        path = out / rel
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_bytes(text.encode("utf-8"))
        facts[rel] = fact
        print(f"{rel}: {len(text.encode('utf-8')) / 1000:.0f} KB")
    (out / "facts-large.json").write_text(json.dumps(facts, indent=1) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
