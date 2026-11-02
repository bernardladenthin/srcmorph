### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:35:21Z
- G: 
- A: 0.0.0
- X: file
---
> Renders a monthly account statement as an HTML page with header, rows per booking, and total.

#### Purpose
- Transforms a statement XML into an HTML page.
- Highlights bookings exceeding a threshold.

#### Type
- XSLT stylesheet (XML) version 1.0.

#### Input
- `<st:statement>` root element with attributes `period`, child `<st:booking>` elements.
- Parameters: `currencySymbol` (default `'EUR'`), `highlightAbove` (default `1000`).

#### Output
- HTML document with `<h1>` header, `<table>` rows for each booking, and a total paragraph.

#### Core logic
- `match="/st:statement"`: builds `<html>` skeleton, calls `statementHeader`, applies templates to bookings, calls `statementTotal`.
- `statementHeader`: outputs account number and period.
- `match="st:booking"`: creates `<tr>`, adds class `large` if amount > `highlightAbove`, outputs date, purpose, formatted amount with currency.
- `statementTotal`: sums all booking amounts, formats, outputs in a paragraph.

#### Public API
- `statementHeader()` → outputs header section.
- `statementTotal()` → outputs total paragraph.
- `match="st:booking"` template → renders each booking row.

#### Constants and configuration
- `currencySymbol` param: `'EUR'`.
- `highlightAbove` param: `1000`.

#### Dependencies
- XSLT 1.0 functions: `format-number`, `sum`, `number`, `xsl:output`, `xsl:key`.
- Namespace `st` bound to `urn:example:statement`.

#### Exceptions / Errors
- None explicitly handled; relies on XSLT error handling for malformed input.
