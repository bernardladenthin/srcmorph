### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:16:26Z
- G: 
- A: 0.0.0
- X: file
---
> Renders a monthly account statement as an HTML page with booking rows and a formatted total.

#### Purpose
- Transforms a statement into an HTML page containing account and period details, one row per booking, and a total.
- Marks booking rows as large when their amount exceeds a configurable threshold.

#### Type
- XSLT 1.0 stylesheet; root element `xsl:stylesheet`.
- Namespaces: `xsl` → `http://www.w3.org/1999/XSL/Transform`; `st` → `urn:example:statement`.

#### Definitions
- Output: HTML, UTF-8, indented.
- Parameters: `currencySymbol` (default `EUR`); `highlightAbove` (default `1000`).
- Key: `bookingsByDay`, matching `st:booking`, indexed by `st:valueDate`.
- Templates:
  - Match `/st:statement`: emits the HTML page, calls `statementHeader` and `statementTotal`, and applies templates to `st:booking`.
  - Named `statementHeader`: emits the account number from `st:accountNumber` and period from `@period`.
  - Match `st:booking`: emits a table row with `st:valueDate`, `st:purpose`, and formatted `st:amount` plus `currencySymbol`; assigns class `large` above the threshold.
  - Named `statementTotal`: emits the sum of booking amounts formatted to two decimal places.

#### Configuration
- `xsl:output`: `method="html"`, `encoding="UTF-8"`, `indent="yes"`.
- `currencySymbol`: default `EUR`.
- `highlightAbove`: default `1000`; booking rows are highlighted when `number(st:amount) > $highlightAbove`.
- Number format for booking amounts and total: `#,##0.00`.
- Highlight class: `large`.

#### Dependencies
- Input elements in namespace `urn:example:statement`: `st:statement`, `st:accountNumber`, `st:booking`, `st:valueDate`, `st:purpose`, `st:amount`.
- Input attribute: `@period`.
- XSLT functions: `number()`, `format-number()`, `sum()`.
