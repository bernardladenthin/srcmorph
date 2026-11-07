### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:00:00Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
---
> Transforms a monthly account statement XML into an HTML page with header, table rows for each booking, and a total.

#### Purpose
- Renders a monthly account statement as an HTML page.
- Highlights bookings exceeding a threshold.

#### Type
- XSLT stylesheet (`xsl:stylesheet` root).
- Namespaces: `xmlns:xsl="http://www.w3.org/1999/XSL/Transform"`, `xmlns:st="urn:example:statement"`.

#### Definitions
- **Parameters**
  - `currencySymbol` (default `'EUR'`).
  - `highlightAbove` (default `1000`).
- **Key**
  - `bookingsByDay`: matches `st:booking`, indexed by `st:valueDate`.
- **Templates**
  - `match="/st:statement"`: outputs `<html>` with header, table, and total.
  - `name="statementHeader"`: outputs `<h1>` with account number and `<p>` with period.
  - `match="st:booking"`: outputs `<tr>` with optional `class="large"` if `st:amount > $highlightAbove`; cells for date, purpose, amount with currency symbol.
  - `name="statementTotal"`: outputs `<p>` with total amount of all bookings.

#### Configuration
- `xsl:output` method: `html`, encoding: `UTF-8`, indent: `yes`.
- `currencySymbol` parameter: `'EUR'`.
- `highlightAbove` parameter: `1000`.

#### Dependencies
- Input XML must use namespace `urn:example:statement` with elements:
  - `st:statement` (attributes: `period`, child `st:accountNumber`).
  - `st:booking` (children: `st:valueDate`, `st:purpose`, `st:amount`).
- No external schemas or files referenced.
