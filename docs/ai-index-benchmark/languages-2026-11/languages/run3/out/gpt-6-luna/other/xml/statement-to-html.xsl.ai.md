### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:54:34Z
- G: 
- A: 0.0.0
- X: file
---
> Transforms a monthly account statement into an HTML page with account details, booking rows, and a formatted total.

#### Purpose
- Renders statement period and account number, one row per booking, and the sum of booking amounts.
- Marks booking rows as `large` when their amount exceeds the configured threshold.

#### Type
- XSLT 1.0 stylesheet; root element `xsl:stylesheet`.
- Namespaces: `xsl` = `http://www.w3.org/1999/XSL/Transform`; `st` = `urn:example:statement`.

#### Definitions
- Output method: `html`; encoding `UTF-8`; indentation enabled.
- Parameters:
  - `currencySymbol` — default `EUR`.
  - `highlightAbove` — default `1000`.
- Key: `bookingsByDay` — matches `st:booking`, indexed by `st:valueDate`.
- Templates:
  - Match `/st:statement` — outputs HTML with a title using `@period`, calls `statementHeader`, applies templates to `st:booking`, and calls `statementTotal`.
  - Named `statementHeader` — outputs `st:accountNumber` and `@period`.
  - Match `st:booking` — outputs a row containing `st:valueDate`, `st:purpose`, and formatted `st:amount` with `currencySymbol`; adds class `large` above the threshold.
  - Named `statementTotal` — outputs the formatted sum of `st:booking/st:amount`.
- Input structure referenced: `st:statement` contains `st:accountNumber` and repeated `st:booking` elements; bookings contain `st:valueDate`, `st:purpose`, and `st:amount`.

#### Configuration
- `currencySymbol` default: `EUR`.
- `highlightAbove` default: `1000`.
- Amount format pattern: `#,##0.00`.
- Large-row class: `large`.

#### Dependencies
- Input namespace: `urn:example:statement`.
- Input elements and attributes: `st:statement`, `st:accountNumber`, `st:booking`, `st:valueDate`, `st:purpose`, `st:amount`, `@period`.
- XSLT functions: `format-number()`, `sum()`, `number()`.
