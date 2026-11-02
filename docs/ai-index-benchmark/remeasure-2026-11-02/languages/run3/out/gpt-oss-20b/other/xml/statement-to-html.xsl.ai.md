### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:56:31Z
- G: 
- A: 0.0.0
- X: file
- P: 144CA22B
---
> Renders a monthly account statement as an HTML page with header, rows per booking, and a total.

#### Purpose
- Convert a `<st:statement>` XML document into an HTML page.
- Highlight bookings exceeding a threshold.

#### Type
- XSLT stylesheet (`xsl:stylesheet` root).
- Namespaces: `xmlns:xsl="http://www.w3.org/1999/XSL/Transform"`, `xmlns:st="urn:example:statement"`.

#### Definitions
- **Parameters**: `currencySymbol` (`'EUR'`), `highlightAbove` (`1000`).
- **Key**: `bookingsByDay` (matches `st:booking`, uses `st:valueDate`).
- **Templates**:
  - `match="/st:statement"`: main entry, builds `<html>`.
  - `name="statementHeader"`: outputs account number and period.
  - `match="st:booking"`: renders a table row, applies `class="large"` if amount > `$highlightAbove`.
  - `name="statementTotal"`: outputs total amount.

#### Configuration
- `xsl:output`: method `html`, encoding `UTF-8`, indent `yes`.
- `currencySymbol`: `'EUR'`.
- `highlightAbove`: `1000`.

#### Dependencies
- Input XML must use namespace `urn:example:statement` with elements: `statement`, `accountNumber`, `period`, `booking`, `valueDate`, `purpose`, `amount`.
