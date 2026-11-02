### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:39:25Z
- G: 
- A: 0.0.0
- X: file
---
> Renders a monthly account statement as an HTML page with booking rows and a formatted total.

#### Purpose
- Transforms a statement into an HTML page showing its account number, period, bookings, and total.
- Marks bookings above a configurable amount as large.

#### Type
- XSLT 1.0 stylesheet; root element `xsl:stylesheet`.
- Namespaces: `xsl` = `http://www.w3.org/1999/XSL/Transform`; `st` = `urn:example:statement`.

#### Definitions
- Output method: `html`; encoding `UTF-8`; indentation enabled.
- Parameters: `currencySymbol` defaults to `EUR`; `highlightAbove` defaults to `1000`.
- Key: `bookingsByDay` matches `st:booking`, indexed by `st:valueDate`.
- Templates:
  - Match `/st:statement`: renders an HTML document, calls `statementHeader`, applies templates to `st:booking`, then calls `statementTotal`.
  - Named `statementHeader`: renders `st:accountNumber` and `@period`.
  - Match `st:booking`: renders a table row with `st:valueDate`, `st:purpose`, and formatted `st:amount` plus `currencySymbol`; sets class `large` when the amount exceeds `highlightAbove`.
  - Named `statementTotal`: renders the sum of booking amounts formatted as `#,##0.00`.

#### Configuration
- `currencySymbol`: `EUR`.
- `highlightAbove`: `1000`.
- `xsl:output`: `method="html"`, `encoding="UTF-8"`, `indent="yes"`.
- Amount and total number format: `#,##0.00`.

#### Dependencies
- Input elements in namespace `urn:example:statement`: `st:statement`, `st:accountNumber`, `st:booking`, `st:valueDate`, `st:purpose`, `st:amount`.
- Input attribute: `@period`.
