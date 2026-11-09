### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:04:46Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
---
> Transforms a namespaced monthly account statement into an HTML page with account details, booking rows, and a formatted total.

#### Purpose
- Renders statement period and account number, one row per booking, and the sum of booking amounts.
- Marks bookings as `large` when their amount exceeds the configured threshold.

#### Type
- XSLT 1.0 stylesheet; root `xsl:stylesheet`.
- Namespace: `xsl` — `http://www.w3.org/1999/XSL/Transform`; `st` — `urn:example:statement`.

#### Definitions
- Templates:
  - Match `/st:statement`: outputs the HTML page, invokes `statementHeader` and `statementTotal`, and applies templates to `st:booking`.
  - Named `statementHeader`: outputs `st:accountNumber` and `@period`.
  - Match `st:booking`: outputs `st:valueDate`, `st:purpose`, and formatted `st:amount`; conditionally sets row class `large`.
  - Named `statementTotal`: outputs the formatted sum of `st:booking/st:amount`.
- Parameters: `currencySymbol` defaults to `'EUR'`; `highlightAbove` defaults to `1000`.
- Key: `bookingsByDay` matches `st:booking`, keyed by `st:valueDate`.
- Output method: `html`; encoding `UTF-8`; indentation enabled.

#### Configuration
- `currencySymbol`: `'EUR'`.
- `highlightAbove`: `1000`; booking row receives class `large` when `number(st:amount) > $highlightAbove`.
- Amount format: `#,##0.00`.

#### Dependencies
- Input vocabulary in namespace `urn:example:statement`: `st:statement`, `st:accountNumber`, `st:booking`, `st:valueDate`, `st:purpose`, `st:amount`; statement attribute `period`.
- Uses XSLT functions `format-number()` and `sum()`.
