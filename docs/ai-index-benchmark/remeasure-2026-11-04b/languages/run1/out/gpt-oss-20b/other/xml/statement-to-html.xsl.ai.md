### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:15:20Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Transforms a monthly account statement XML into an HTML page with header, booking rows, and total.

#### Purpose
- Render statement as HTML.
- Highlight bookings above a threshold.

#### Type
- XSLT stylesheet (`xsl:stylesheet` root).
- Namespace: `xmlns:xsl="http://www.w3.org/1999/XSL/Transform"`, `xmlns:st="urn:example:statement"`.

#### Definitions
- **Parameters**  
  - `$currencySymbol` (`'EUR'`)  
  - `$highlightAbove` (`1000`)
- **Key**  
  - `bookingsByDay`: matches `st:booking`, uses `st:valueDate`.
- **Templates**  
  - `match="/st:statement"`: builds `<html>` with header, table, total.  
  - `name="statementHeader"`: outputs `<h1>` with `st:accountNumber` and period.  
  - `match="st:booking"`: outputs `<tr>`; adds `class="large"` if `st:amount > $highlightAbove`; columns: `st:valueDate`, `st:purpose`, formatted `st:amount` + `$currencySymbol`.  
  - `name="statementTotal"`: outputs total of all `st:booking/st:amount`.

#### Configuration
- Output method: `html`, encoding `UTF-8`, indent `yes`.
- `$currencySymbol` default `'EUR'`.
- `$highlightAbove` default `1000`.

#### Dependencies
- Input XML must use namespace `urn:example:statement` with elements: `statement`, `accountNumber`, `period`, `booking`, `valueDate`, `purpose`, `amount`.
