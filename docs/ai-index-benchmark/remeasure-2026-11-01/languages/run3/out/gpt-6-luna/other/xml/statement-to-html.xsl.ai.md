### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:56:32Z
- G: 
- A: 0.0.0
- X: file
---
> Renders a monthly account statement as an HTML page with booking rows and a formatted total.

#### Purpose
- Transforms a namespaced statement into HTML containing account and period details, booking rows, and a total.
- Marks bookings above a configurable amount threshold with the `large` class.

#### Type
- XSLT 1.0 stylesheet; root element `xsl:stylesheet`.
- Namespace: `xsl` — `http://www.w3.org/1999/XSL/Transform`; `st` — `urn:example:statement`.

#### Definitions
- Output method: `html`, encoding `UTF-8`, indentation enabled.
- Parameters: `currencySymbol` (default `EUR`); `highlightAbove` (default `1000`).
- Key: `bookingsByDay`, matching `st:booking`, indexed by `st:valueDate`.
- Templates:
  - Match `/st:statement`: creates an HTML page, calls `statementHeader` and `statementTotal`, and applies templates to `st:booking`.
  - Named `statementHeader`: outputs the account number and statement period.
  - Match `st:booking`: outputs a table row with value date, purpose, and formatted amount; adds class `large` when the amount exceeds `$highlightAbove`.
  - Named `statementTotal`: outputs the formatted sum of booking amounts.

#### Configuration
- `currencySymbol`: `EUR`.
- `highlightAbove`: `1000`.
- Amount format: `#,##0.00`.
- Booking row highlight class: `large`.
- Output encoding: `UTF-8`; indentation: `yes`.

#### Dependencies
- Input elements in namespace `urn:example:statement`: `st:statement`, `st:accountNumber`, `st:booking`, `st:valueDate`, `st:purpose`, `st:amount`.
- Input attribute: `period` on `st:statement`.
- XSLT functions: `number()`, `format-number()`, `sum()`.
