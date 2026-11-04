### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:44:52Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Transforms a monthly account statement XML into an HTML page with header, rows per booking, and total.

#### Purpose
- Render a monthly account statement as an HTML page.

#### Type
- XSLT stylesheet (`xsl:stylesheet` root).  
- Namespaces: `xsl` → `http://www.w3.org/1999/XSL/Transform`; `st` → `urn:example:statement`.

#### Definitions
- **Parameters**  
  - `currencySymbol` (default `'EUR'`)  
  - `highlightAbove` (default `1000`)
- **Key**  
  - `bookingsByDay` matches `st:booking` using `st:valueDate`
- **Templates**  
  - `match="/st:statement"`: outputs `<html>` with `<head>` title and `<body>` containing header, table of bookings, and total.  
  - `name="statementHeader"`: outputs `<h1>` with account number and `<p>` with period.  
  - `match="st:booking"`: outputs `<tr>`; adds `class="large"` if `st:amount` > `$highlightAbove`; contains `<td>` cells for `st:valueDate`, `st:purpose`, and formatted `st:amount` with `$currencySymbol`.  
  - `name="statementTotal"`: outputs `<p>` with total amount formatted.

#### Configuration
- `xsl:output` method `html`, encoding `UTF-8`, indent `yes`.
- Parameter values: `currencySymbol='EUR'`, `highlightAbove=1000`.

#### Dependencies
- Input XML must use namespace `urn:example:statement` with elements: `statement`, `accountNumber`, `period`, `booking`, `valueDate`, `purpose`, `amount`.
