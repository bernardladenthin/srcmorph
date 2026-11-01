### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:11:24Z
- G: 
- A: 0.0.0
- X: file
---
> Renders an account statement as an HTML page with booking rows, large-amount highlighting, and a total.

#### Purpose
- Transforms a statement into an HTML page containing its account number, period, bookings, and total.
- Marks booking rows as `large` when their amount exceeds the configured threshold.

#### Type
- XSLT 1.0 stylesheet; root element `xsl:stylesheet`.
- Namespaces: `xsl` — `http://www.w3.org/1999/XSL/Transform`; `st` — `urn:example:statement`.

#### Definitions
- Output method: `html`.
- Parameters: `currencySymbol` (default `EUR`); `highlightAbove` (default `1000`).
- Key: `bookingsByDay`, matching `st:booking` and indexing by `st:valueDate`.
- Templates:
  - Match `/st:statement`: outputs the HTML document, calls `statementHeader` and `statementTotal`, and applies templates to `st:booking`.
  - Named `statementHeader`: outputs the account number and statement period.
  - Match `st:booking`: outputs a table row with value date, purpose, and formatted amount; adds class `large` when the amount exceeds `$highlightAbove`.
  - Named `statementTotal`: outputs the sum of booking amounts.

#### Configuration
- `xsl:output`: `method="html"`, `encoding="UTF-8"`, `indent="yes"`.
- `currencySymbol`: default `EUR`.
- `highlightAbove`: default `1000`.
- Amount format: `#,##0.00`.

#### Dependencies
- Input elements in namespace `urn:example:statement`: `st:statement`, `st:accountNumber`, `st:booking`, `st:valueDate`, `st:purpose`, `st:amount`.
- Uses XSLT functions `number()`, `format-number()`, and `sum()`.
