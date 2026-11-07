### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:13:38Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Transforms a monthly account statement into an HTML page with account details, booking rows, and a total.

#### Purpose
- Renders statement period and account number, one row per booking, and the summed booking amount.
- Marks booking rows as `large` when the amount exceeds a configurable threshold.

#### Type
- XSLT 1.0 stylesheet; root element `xsl:stylesheet`.
- Namespaces: `xsl` — `http://www.w3.org/1999/XSL/Transform`; `st` — `urn:example:statement`.

#### Definitions
- Output method: `html`, encoding `UTF-8`, `indent="yes"`.
- Parameters: `currencySymbol` defaults to `EUR`; `highlightAbove` defaults to `1000`.
- Key `bookingsByDay`: matches `st:booking`, indexed by `st:valueDate`.
- Templates:
  - Match `/st:statement`: emits the HTML page, calls `statementHeader` and `statementTotal`, and applies templates to `st:booking`.
  - Named `statementHeader`: emits the account number and period.
  - Match `st:booking`: emits date, purpose, and formatted amount with currency; assigns class `large` if `number(st:amount) > $highlightAbove`.
  - Named `statementTotal`: emits the formatted sum of `st:booking/st:amount`.

#### Configuration
- `currencySymbol` — `EUR`.
- `highlightAbove` — `1000`.
- Amount format — `#,##0.00`.

#### Dependencies
- Input statement data in namespace `urn:example:statement`, using `st:statement`, `st:accountNumber`, `st:booking`, `st:valueDate`, `st:purpose`, and `st:amount`; period is read from `@period`.
