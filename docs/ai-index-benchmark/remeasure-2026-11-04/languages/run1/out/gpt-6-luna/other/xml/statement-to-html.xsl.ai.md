### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T07:58:40Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
---
> Transforms a monthly account statement into an HTML page with account details, booking rows, and a total.

#### Purpose
- Render statement data as an HTML account statement.
- Mark bookings above a configurable amount threshold with the `large` class.

#### Type
- XSLT 1.0 stylesheet; root element `xsl:stylesheet`.
- Namespaces: `xsl` — `http://www.w3.org/1999/XSL/Transform`; `st` — `urn:example:statement`.

#### Definitions
- Output method: `html`, encoding `UTF-8`, indentation enabled.
- Templates:
  - Match `/st:statement`: outputs page title using `@period`, calls `statementHeader`, renders `st:booking` rows in a table, and calls `statementTotal`.
  - Named `statementHeader`: outputs `st:accountNumber` and `@period`.
  - Match `st:booking`: outputs value date, purpose, and formatted amount with currency symbol; adds class `large` when `number(st:amount) > $highlightAbove`.
  - Named `statementTotal`: outputs the formatted sum of booking amounts.
- Parameters: `currencySymbol` defaults to `EUR`; `highlightAbove` defaults to `1000`.
- Key `bookingsByDay`: matches `st:booking`, indexed by `st:valueDate`.

#### Configuration
- `currencySymbol`: `EUR`.
- `highlightAbove`: `1000`.
- Amount format: `#,##0.00`.

#### Dependencies
- Input statement vocabulary in namespace `urn:example:statement`: `statement`, `accountNumber`, `booking`, `valueDate`, `purpose`, and `amount`.
- XSLT functions: `format-number()`, `sum()`, `number()`.
