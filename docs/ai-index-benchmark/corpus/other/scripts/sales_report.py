# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: Apache-2.0
"""Monthly sales report: reads order CSV exports and prints revenue per month and region."""

import argparse
import csv
import sys
from collections import defaultdict
from dataclasses import dataclass
from datetime import date
from decimal import Decimal, InvalidOperation

REQUIRED_COLUMNS = ("order_date", "region", "amount", "currency")


@dataclass(frozen=True)
class Order:
    order_date: date
    region: str
    amount: Decimal


def parse_orders(path, currency):
    """Yields the orders of one CSV file in the given currency; other currencies are skipped."""
    with open(path, newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        missing = [c for c in REQUIRED_COLUMNS if c not in (reader.fieldnames or [])]
        if missing:
            raise ValueError(f"{path}: missing columns {', '.join(missing)}")
        for line_no, row in enumerate(reader, start=2):
            if row["currency"] != currency:
                continue
            try:
                yield Order(date.fromisoformat(row["order_date"]), row["region"].strip(), Decimal(row["amount"]))
            except (ValueError, InvalidOperation) as e:
                raise ValueError(f"{path}:{line_no}: {e}") from e


def revenue_by_month_and_region(orders):
    totals = defaultdict(Decimal)
    for order in orders:
        totals[(order.order_date.strftime("%Y-%m"), order.region)] += order.amount
    return dict(sorted(totals.items()))


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("csv_files", nargs="+")
    parser.add_argument("--currency", default="EUR")
    parser.add_argument("--min-revenue", type=Decimal, default=Decimal("0"))
    args = parser.parse_args(argv)

    orders = [o for path in args.csv_files for o in parse_orders(path, args.currency)]
    if not orders:
        print("no orders found", file=sys.stderr)
        return 1
    for (month, region), revenue in revenue_by_month_and_region(orders).items():
        if revenue >= args.min_revenue:
            print(f"{month}\t{region}\t{revenue:.2f} {args.currency}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
