### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:20:28Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Transforms a monthly account statement into an HTML page showing account and period details, booking rows, and a total.

#### Purpose
- Render statement data as an HTML account statement.
- Mark booking rows as `large` when their amount exceeds a configurable threshold.

#### Type
- XSLT 1.0 stylesheet; root element `xsl:stylesheet`.
- Namespace: `xsl` — `http://www.w3.org/1999/XSL/Transform`; `st` — `urn:example:statement`.

#### Definitions
- Templates:
  - Match `/st:statement`: emits the HTML page, calls `statementHeader` and `statementTotal`, and applies templates to `st:booking`.
  - Named `statementHeader`: outputs account number and period.
  - Match `st:booking`: outputs value date, purpose, and formatted amount with currency; adds class `large` when the amount exceeds `$highlightAbove`.
  - Named `statementTotal`: outputs the formatted sum of booking amounts.
- Parameters: `currencySymbol` (default `EUR`); `highlightAbove` (default `1000`).
- Key: `bookingsByDay`, matching `st:booking`, indexed by `st:valueDate`.
- Output method: `html`; encoding `UTF-8`; indentation enabled.

#### Configuration
- `currencySymbol` — default `EUR`.
- `highlightAbove` — default `1000`.

#### Dependencies
- Input statement data in namespace `urn:example:statement`, using `st:statement`, `st:accountNumber`, `st:booking`, `st:valueDate`, `st:purpose`, and `st:amount`.
- Uses XSLT functions `format-number()` and `sum()`.
