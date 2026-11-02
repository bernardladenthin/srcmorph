### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:41:09Z
- G: 
- A: 0.0.0
- X: file
---
> Renders a monthly account statement as an HTML page with header, rows per booking, and a total.

#### Purpose
- Transforms a statement XML into an HTML report.
- Highlights bookings exceeding a threshold.

#### Type
- XSLT stylesheet (XML) – `xsl:stylesheet`.

#### Input
- XML document matching `st:statement` root.
- Parameters: `currencySymbol` (default `'EUR'`), `highlightAbove` (default `1000`).

#### Output
- HTML document with:
  - `<h1>` header containing account number.
  - `<p>` period.
  - `<table>` rows for each `st:booking`.
  - `<p>` total amount.

#### Core logic
- `match="/st:statement"`: builds `<html>` skeleton, calls header, table, and total templates.
- `statementHeader`: outputs account number and period.
- `match="st:booking"`: creates `<tr>`; if `st:amount` > `$highlightAbove`, adds `class="large"`. Outputs date, purpose, formatted amount with currency symbol.
- `statementTotal`: sums all `st:booking/st:amount` and formats the total.

#### Public API
- Parameters:
  - `currencySymbol` → selects currency symbol for amounts.
  - `highlightAbove` → threshold for highlighting rows.
- Templates:
  - `statementHeader()` → renders header section.
  - `statementTotal()` → renders total paragraph.
  - `match="st:booking"` → renders each booking row.

#### Constants and configuration
- `$currencySymbol` default `'EUR'`.
- `$highlightAbove` default `1000`.
- `xsl:key` `bookingsByDay` (unused in current logic).

#### Dependencies
- XSLT 1.0 processor.
- Namespace `st` bound to `urn:example:statement`.

#### Exceptions / Errors
- None explicitly handled; relies on XSLT processor defaults.
