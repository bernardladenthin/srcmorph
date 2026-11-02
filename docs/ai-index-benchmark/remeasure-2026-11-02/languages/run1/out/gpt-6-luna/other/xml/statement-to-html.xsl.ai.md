### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:24:24Z
- G: 
- A: 0.0.0
- X: file
- P: 144CA22B
---
> Transforms a monthly account statement into an HTML page with account details, booking rows, and a formatted total.

#### Purpose
- Render statement period and account number, one table row per booking, and the sum of booking amounts.
- Mark bookings above a configurable amount threshold with the `large` CSS class.

#### Type
- XSLT 1.0 stylesheet; root element `xsl:stylesheet`.
- Namespaces: `xsl` = `http://www.w3.org/1999/XSL/Transform`; `st` = `urn:example:statement`.

#### Definitions
- Output method: `html`.
- Parameters: `currencySymbol` (default `EUR`); `highlightAbove` (default `1000`).
- Key: `bookingsByDay`, matching `st:booking`, indexed by `st:valueDate`.
- Templates:
  - Match `/st:statement`: emits HTML title, header, booking table, and total.
  - Named `statementHeader`: emits account number and period.
  - Match `st:booking`: emits a row with value date, purpose, and formatted amount plus currency symbol; conditionally sets class `large`.
  - Named `statementTotal`: emits the formatted sum of `st:booking/st:amount`.

#### Configuration
- `xsl:output`: `method="html"`, `encoding="UTF-8"`, `indent="yes"`.
- `currencySymbol`: default `EUR`.
- `highlightAbove`: default `1000`; booking row gets class `large` when `number(st:amount) > $highlightAbove`.
- Amount and total number format: `#,##0.00`.

#### Dependencies
- Input vocabulary namespace: `urn:example:statement`; reads `st:statement`, `st:accountNumber`, `st:booking`, `st:valueDate`, `st:purpose`, and `st:amount`.
- Uses XSLT functions `format-number()`, `sum()`, and `number()`.
