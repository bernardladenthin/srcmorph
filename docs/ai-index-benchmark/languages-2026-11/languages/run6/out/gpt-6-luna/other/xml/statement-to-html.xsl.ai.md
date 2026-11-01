### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:38:17Z
- G: 
- A: 0.0.0
- X: file
---
> Renders a monthly account statement as an HTML page with booking rows and a formatted total.

#### Purpose
- Transforms a statement into an HTML report containing account, period, bookings, and total.
- Marks booking rows whose amount exceeds a configurable threshold.

#### Type
- XSLT 1.0 stylesheet; root element `xsl:stylesheet`.
- Namespaces: `xsl` = `http://www.w3.org/1999/XSL/Transform`; `st` = `urn:example:statement`.

#### Definitions
- Output method: `html`, encoding `UTF-8`, `indent="yes"`.
- Parameters: `currencySymbol` (default `EUR`); `highlightAbove` (default `1000`).
- Key: `bookingsByDay` matches `st:booking`, indexed by `st:valueDate`.
- Templates:
  - Match `/st:statement`: emits the page title, header, booking table, and total.
  - Named `statementHeader`: emits account number and period.
  - Match `st:booking`: emits a row with value date, purpose, and formatted amount; assigns class `large` when the amount exceeds `highlightAbove`.
  - Named `statementTotal`: emits the sum of booking amounts.
- Input structure used: `st:statement` with `@period`, `st:accountNumber`, and repeated `st:booking` elements containing `st:valueDate`, `st:purpose`, and `st:amount`.

#### Configuration
- `currencySymbol` defaults to `EUR`; displayed after each booking amount.
- `highlightAbove` defaults to `1000`; booking rows above it receive class `large`.
- Booking and total amounts use format pattern `#,##0.00`.

#### Dependencies
- Input elements in namespace `urn:example:statement`: `st:statement`, `st:accountNumber`, `st:booking`, `st:valueDate`, `st:purpose`, `st:amount`.
