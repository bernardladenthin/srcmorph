### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:34:43Z
- G: 
- A: 0.0.0
- X: file
---
> Renders a monthly account statement as an HTML page with account details, booking rows, and a formatted total.

#### Purpose
- Transforms a statement in the `urn:example:statement` namespace into an HTML account statement.
- Highlights bookings whose amounts exceed a configurable threshold.

#### Type
- XSLT 1.0 stylesheet; root element `xsl:stylesheet`.
- Namespaces: `xsl` — `http://www.w3.org/1999/XSL/Transform`; `st` — `urn:example:statement`.

#### Definitions
- Output: method `html`, encoding `UTF-8`, `indent="yes"`.
- Parameters: `currencySymbol` defaults to `'EUR'`; `highlightAbove` defaults to `1000`.
- Key: `bookingsByDay` matches `st:booking`, indexed by `st:valueDate`.
- Templates:
  - Match `/st:statement`: emits an HTML page with title from `@period`, calls `statementHeader`, applies templates to `st:booking`, then calls `statementTotal`.
  - Name `statementHeader`: emits account heading from `st:accountNumber` and period from `@period`.
  - Match `st:booking`: emits a table row with `st:valueDate`, `st:purpose`, and formatted `st:amount` plus `currencySymbol`; assigns class `large` when the amount exceeds `highlightAbove`.
  - Name `statementTotal`: emits the formatted sum of `st:booking/st:amount`.

#### Configuration
- `version`: `1.0`.
- `xsl:output`: `method="html"`, `encoding="UTF-8"`, `indent="yes"`.
- `currencySymbol`: `'EUR'`.
- `highlightAbove`: `1000`.
- Amount format: `#,##0.00`.

#### Dependencies
- Input elements and attributes: `st:statement`, `st:accountNumber`, `st:booking`, `st:valueDate`, `st:purpose`, `st:amount`, `@period`.
- XSLT functions: `number()`, `format-number()`, `sum()`.
