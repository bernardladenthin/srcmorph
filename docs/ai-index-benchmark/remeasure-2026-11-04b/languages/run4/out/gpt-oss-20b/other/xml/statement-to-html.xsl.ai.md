### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T12:43:55Z
- G: 
- A: 0.0.0
- X: file
- P: D9E787D0
---
> Transforms a monthly account statement XML into an HTML page with header, table rows for each booking, and a total.

#### Purpose
- Render monthly account statements as HTML pages.

#### Type
- XSLT stylesheet (`xsl:stylesheet` root).  
- Namespace: `xmlns:st="urn:example:statement"`.

#### Definitions
- **Parameters**:  
  - `currencySymbol` (`'EUR'`)  
  - `highlightAbove` (`1000`)  
- **Key**: `bookingsByDay` (matches `st:booking`, indexed by `st:valueDate`).  
- **Templates**:  
  - `match="/st:statement"` – main entry, outputs `<html>` with header, table, and total.  
  - `name="statementHeader"` – outputs account number and period.  
  - `match="st:booking"` – outputs a `<tr>` for each booking; adds `class="large"` if `st:amount` > `$highlightAbove`.  
  - `name="statementTotal"` – outputs total amount of all bookings.

#### Configuration
- `xsl:output` method: `html`, encoding: `UTF-8`, indent: `yes`.  
- `currencySymbol` default: `'EUR'`.  
- `highlightAbove` default: `1000`.

#### Dependencies
- Input XML must use namespace `urn:example:statement` with elements: `statement`, `booking`, `valueDate`, `purpose`, `amount`, `accountNumber`.  
- No external files or schemas referenced.
