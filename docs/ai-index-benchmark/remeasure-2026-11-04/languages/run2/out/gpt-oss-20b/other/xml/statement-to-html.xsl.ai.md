### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:03:06Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
---
> Transforms a monthly account statement XML into an HTML page with header, rows per booking, and a total.

#### Purpose
- Render a monthly account statement as an HTML page.
- Highlight bookings exceeding a threshold.

#### Type
- XSLT stylesheet (`xsl:stylesheet` root).
- Namespace: `xmlns:xsl="http://www.w3.org/1999/XSL/Transform"`, `xmlns:st="urn:example:statement"`.

#### Definitions
- **Parameters**
  - `currencySymbol` (default `'EUR'`).
  - `highlightAbove` (default `1000`).
- **Key**
  - `bookingsByDay`: matches `st:booking`, indexed by `st:valueDate`.
- **Templates**
  - `match="/st:statement"`: builds the overall HTML structure.
  - `name="statementHeader"`: outputs account number and period.
  - `match="st:booking"`: outputs a table row; adds `class="large"` if `st:amount` > `$highlightAbove`.
  - `name="statementTotal"`: outputs the sum of all `st:booking/st:amount`.

#### Configuration
- `xsl:output` method `html`, encoding `UTF-8`, indent `yes`.
- `currencySymbol` parameter value `'EUR'`.
- `highlightAbove` parameter value `1000`.

#### Dependencies
- Input XML must use namespace `urn:example:statement` with elements:
  - `st:statement` (root, attributes `period`, child `st:accountNumber`).
  - `st:booking` (children `st:valueDate`, `st:purpose`, `st:amount`).
- No external schemas or stylesheets referenced.
