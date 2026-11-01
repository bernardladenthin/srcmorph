### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:33:38Z
- G: 
- A: 0.0.0
- X: file
---
> Renders a monthly account statement as an HTML page with booking rows and a formatted total.

#### Purpose
- Transforms a statement into an HTML page containing its account number, period, bookings, and total.
- Highlights booking rows whose amount exceeds a configurable threshold.

#### Type
- XSLT 1.0 stylesheet; root element `xsl:stylesheet`.
- Namespaces: `xsl` — `http://www.w3.org/1999/XSL/Transform`; `st` — `urn:example:statement`.

#### Definitions
- Output method: `html`; encoding `UTF-8`; indentation enabled.
- Parameters: `currencySymbol`, default `EUR`; `highlightAbove`, default `1000`.
- Key: `bookingsByDay`, matching `st:booking`, indexed by `st:valueDate`.
- Templates:
  - Match `/st:statement`: creates the HTML page, calls `statementHeader` and `statementTotal`, and applies templates to `st:booking`.
  - Name `statementHeader`: outputs `st:accountNumber` and `@period`.
  - Match `st:booking`: outputs `st:valueDate`, `st:purpose`, and formatted `st:amount`; sets row class to `large` when the amount exceeds `$highlightAbove`.
  - Name `statementTotal`: outputs the formatted sum of `st:booking/st:amount`.

#### Configuration
- `currencySymbol` — `EUR`.
- `highlightAbove` — `1000`.
- Output: method `html`, encoding `UTF-8`, `indent="yes"`.
- Amount format: `#,##0.00`.

#### Dependencies
- Input elements: `st:statement`, `st:accountNumber`, `st:booking`, `st:valueDate`, `st:purpose`, `st:amount`; statement attribute `period`.
- Uses XSLT functions `number()`, `format-number()`, and `sum()`.
