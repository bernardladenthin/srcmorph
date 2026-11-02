### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:27:55Z
- G: 
- A: 0.0.0
- X: file
---
> Renders an account statement as an HTML page with booking rows, large-amount highlighting, and a total.

#### Purpose
- Transforms a statement in the `urn:example:statement` namespace into an HTML account statement.
- Displays the account, period, booking details, and summed amount.

#### Type
- XSLT 1.0 stylesheet; root element `xsl:stylesheet`.
- Namespaces: `xsl` = `http://www.w3.org/1999/XSL/Transform`; `st` = `urn:example:statement`.

#### Definitions
- Output: method `html`, encoding `UTF-8`, indentation enabled.
- Parameters: `currencySymbol` (default `EUR`); `highlightAbove` (default `1000`).
- Key: `bookingsByDay`, matching `st:booking`, indexed by `st:valueDate`.
- Templates:
  - Match `/st:statement`: emits an HTML page, calls `statementHeader` and `statementTotal`, and applies templates to `st:booking`.
  - Named `statementHeader`: outputs `st:accountNumber` and the statement’s `@period`.
  - Match `st:booking`: outputs value date, purpose, and formatted amount with currency symbol; adds class `large` when the amount exceeds `$highlightAbove`.
  - Named `statementTotal`: outputs the formatted sum of booking amounts.

#### Configuration
- `currencySymbol`: default `EUR`.
- `highlightAbove`: default `1000`.
- Amount display format: `#,##0.00`.
- Highlight class: `large`.

#### Dependencies
- Input elements in namespace `urn:example:statement`: `st:statement`, `st:accountNumber`, `st:booking`, `st:valueDate`, `st:purpose`, `st:amount`.
- XSLT functions: `number()`, `format-number()`, `sum()`.
