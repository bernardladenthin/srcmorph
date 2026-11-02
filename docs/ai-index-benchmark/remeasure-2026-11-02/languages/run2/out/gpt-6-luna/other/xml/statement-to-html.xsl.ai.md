### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:40:50Z
- G: 
- A: 0.0.0
- X: file
- P: 144CA22B
---
> Renders a monthly account statement as an HTML page with booking rows, conditional highlighting, and a total.

#### Purpose
- Transforms statement data into an HTML account statement.
- Displays account and period details, booking dates and purposes, formatted amounts, and the summed total.

#### Type
- XSLT 1.0 stylesheet; root element `xsl:stylesheet`.
- Namespaces: `xsl` = `http://www.w3.org/1999/XSL/Transform`; `st` = `urn:example:statement`.

#### Definitions
- Output method: `html`; encoding `UTF-8`; indentation enabled.
- Parameters: `currencySymbol` defaults to `EUR`; `highlightAbove` defaults to `1000`.
- Key: `bookingsByDay` matches `st:booking`, indexed by `st:valueDate`.
- Templates:
  - Match `/st:statement`: creates the HTML page, invokes `statementHeader` and `statementTotal`, and applies templates to `st:booking`.
  - Named `statementHeader`: outputs the account number and period.
  - Match `st:booking`: outputs a table row with value date, purpose, and formatted amount; adds class `large` when the amount exceeds `highlightAbove`.
  - Named `statementTotal`: outputs the formatted sum of booking amounts.

#### Configuration
- `currencySymbol`: default `EUR`; appended to each booking amount.
- `highlightAbove`: default `1000`; amounts greater than this value receive class `large`.
- Amount format: `#,##0.00` for booking amounts and the total.

#### Dependencies
- Input vocabulary in namespace `urn:example:statement`: `st:statement`, `st:accountNumber`, `st:booking`, `st:valueDate`, `st:purpose`, and `st:amount`; statement attribute `period`.
