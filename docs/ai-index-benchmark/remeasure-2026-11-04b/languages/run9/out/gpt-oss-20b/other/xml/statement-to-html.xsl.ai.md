### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:48:40Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Transforms a monthly account statement XML into an HTML page with header, rows per booking, and a total.

#### Purpose
- Render a monthly account statement as an HTML page.
- Highlight bookings exceeding a threshold.

#### Type
- XSLT stylesheet (`xsl:stylesheet` root).
- Namespace: `xmlns:st="urn:example:statement"`.
- Output method: `html`.

#### Definitions
- **Parameters**
  - `currencySymbol` (`'EUR'`).
  - `highlightAbove` (`1000`).
- **Key**
  - `bookingsByDay`: matches `st:booking` elements, indexed by `st:valueDate`.
- **Templates**
  - `match="/st:statement"`: builds the overall HTML structure.
  - `name="statementHeader"`: outputs account number and period.
  - `match="st:booking"`: renders a table row; adds `class="large"` if `st:amount` > `$highlightAbove`.
  - `name="statementTotal"`: outputs the sum of all `st:booking/st:amount`.

#### Configuration
- `xsl:param` values: `currencySymbol='EUR'`, `highlightAbove=1000`.
- `xsl:output` method: `html`, encoding: `UTF-8`, indent: `yes`.

#### Dependencies
- Input XML must use namespace `urn:example:statement` with elements:
  - `st:statement` (attributes: `period`, child: `st:accountNumber`).
  - `st:booking` (children: `st:valueDate`, `st:purpose`, `st:amount`).
- No external schemas or files referenced.
