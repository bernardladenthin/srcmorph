### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:06:02Z
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
  - `currencySymbol` (default `'EUR'`)  
  - `highlightAbove` (default `1000`)
- **Key**  
  - `bookingsByDay` matches `st:booking` using `st:valueDate`.
- **Templates**  
  - `match="/st:statement"`: outputs `<html>` with header, table of bookings, and total.  
  - `name="statementHeader"`: outputs `<h1>` with account number and `<p>` with period.  
  - `match="st:booking"`: outputs a `<tr>`; adds `class="large"` if `st:amount` > `$highlightAbove`; contains `<td>` cells for date, purpose, and formatted amount with currency symbol.  
  - `name="statementTotal"`: outputs `<p>` with total amount formatted.

#### Configuration
- `xsl:output` method `html`, encoding `UTF-8`, indent `yes`.
- Parameter values: `currencySymbol='EUR'`, `highlightAbove=1000`.

#### Dependencies
- Input XML must use namespace `urn:example:statement` with elements: `statement`, `accountNumber`, `period`, `booking`, `valueDate`, `purpose`, `amount`.
