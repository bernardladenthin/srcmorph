### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T06:36:15Z
- G: 
- A: 0.0.0
- X: file
---
> Transforms a monthly account statement XML into an HTML page with header, booking rows, and total.

#### Purpose
- Renders a monthly account statement as an HTML page.
- Highlights bookings exceeding a threshold.

#### Type
- XSLT stylesheet (`xsl:stylesheet` root).
- Namespaces: `xmlns:xsl="http://www.w3.org/1999/XSL/Transform"`, `xmlns:st="urn:example:statement"`.

#### Definitions
- Parameters: `currencySymbol`, `highlightAbove`.
- Key: `bookingsByDay` (match `st:booking`, use `st:valueDate`).
- Templates:
  - `match="/st:statement"`: main entry, outputs `<html>`.
  - `name="statementHeader"`: outputs account header.
  - `match="st:booking"`: outputs a table row for each booking, adds `class="large"` if amount > `$highlightAbove`.
  - `name="statementTotal"`: outputs total amount.

#### Configuration
- `xsl:param` values:
  - `currencySymbol` default `'EUR'`.
  - `highlightAbove` default `1000`.
- `xsl:output` method `html`, encoding `UTF-8`, indent `yes`.

#### Dependencies
- Input XML must use namespace `urn:example:statement` with elements:
  - `st:statement` (attributes: `period`).
  - `st:accountNumber`.
  - `st:booking` (children: `st:valueDate`, `st:purpose`, `st:amount`).
- External formatting via `format-number` function.
