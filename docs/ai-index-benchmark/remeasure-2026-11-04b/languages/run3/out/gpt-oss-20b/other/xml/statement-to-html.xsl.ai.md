### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:21:40Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Transforms a custom `<st:statement>` XML into an HTML page with header, table rows per booking, and a total.

#### Purpose
- Render monthly account statements as HTML.
- Highlight bookings exceeding a threshold.

#### Type
- XSLT stylesheet (`xsl:stylesheet` root).
- Namespace: `xmlns:st="urn:example:statement"`.

#### Definitions
- **Parameters**: `currencySymbol` (default `'EUR'`), `highlightAbove` (default `1000`).
- **Key**: `bookingsByDay` – matches `st:booking` elements, indexed by `st:valueDate`.
- **Templates**:
  - `match="/st:statement"` – main entry, outputs `<html>`, calls header, table, total.
  - `name="statementHeader"` – outputs `<h1>` with account number and `<p>` with period.
  - `match="st:booking"` – outputs `<tr>`; adds `class="large"` if `st:amount` > `$highlightAbove`; columns: date, purpose, amount with currency symbol.
  - `name="statementTotal"` – outputs `<p>` with sum of all `st:booking/st:amount`.

#### Configuration
- `xsl:output` method: `html`, encoding: `UTF-8`, indent: `yes`.
- `currencySymbol` parameter: `'EUR'`.
- `highlightAbove` parameter: `1000`.

#### Dependencies
- Input XML must use namespace `urn:example:statement` with elements: `statement`, `accountNumber`, `period`, `booking`, `valueDate`, `purpose`, `amount`.
