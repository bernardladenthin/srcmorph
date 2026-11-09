### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:47:42Z
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
- Namespaces: `xsl` (`http://www.w3.org/1999/XSL/Transform`), `st` (`urn:example:statement`).

#### Definitions
- **Parameters**  
  - `currencySymbol` (default `'EUR'`)  
  - `highlightAbove` (default `1000`)
- **Key**  
  - `bookingsByDay` – matches `st:booking`, indexed by `st:valueDate`.
- **Templates**  
  - `match="/st:statement"` – main entry, outputs `<html>`.  
  - `name="statementHeader"` – outputs account number and period.  
  - `match="st:booking"` – outputs a `<tr>` for each booking, adds `class="large"` if `st:amount > $highlightAbove`.  
  - `name="statementTotal"` – outputs total amount of all bookings.

#### Configuration
- `xsl:output` method `html`, encoding `UTF-8`, indent `yes`.
- `currencySymbol` and `highlightAbove` parameters are configurable.

#### Dependencies
- Input XML must use namespace `urn:example:statement` with elements: `statement`, `accountNumber`, `period`, `booking`, `valueDate`, `purpose`, `amount`.
