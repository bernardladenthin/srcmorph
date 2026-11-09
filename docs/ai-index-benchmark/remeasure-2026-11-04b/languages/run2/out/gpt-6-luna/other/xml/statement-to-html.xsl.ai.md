### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:17:07Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Transforms a monthly account statement into an HTML page with account details, booking rows, and a formatted total.

#### Purpose
- Renders statement period and account number, one row per booking, and the sum of booking amounts.
- Marks booking rows as `large` when their amount exceeds a configurable threshold.

#### Type
- XSLT 1.0 stylesheet; root element `xsl:stylesheet`.
- Namespace: `xsl` = `http://www.w3.org/1999/XSL/Transform`; `st` = `urn:example:statement`.

#### Definitions
- Templates:
  - Match `/st:statement`: renders the HTML page, invokes `statementHeader` and `statementTotal`, and applies templates to `st:booking`.
  - Named `statementHeader`: renders `st:accountNumber` and the statement `@period`.
  - Match `st:booking`: renders value date, purpose, and formatted amount; assigns class `large` when the amount exceeds `$highlightAbove`.
  - Named `statementTotal`: renders the formatted sum of `st:booking/st:amount`.
- Parameters: `currencySymbol` (default `EUR`); `highlightAbove` (default `1000`).
- Key: `bookingsByDay`, matching `st:booking`, indexed by `st:valueDate`.
- Output method: `html`, encoding `UTF-8`, `indent="yes"`.

#### Configuration
- `currencySymbol` = `EUR`; appended to each displayed booking amount.
- `highlightAbove` = `1000`; booking amounts greater than this value receive class `large`.
- Amount format: `#,##0.00`.

#### Dependencies
- Input statement elements in namespace `urn:example:statement`: `st:statement`, `st:accountNumber`, `st:booking`, `st:valueDate`, `st:purpose`, and `st:amount`.
