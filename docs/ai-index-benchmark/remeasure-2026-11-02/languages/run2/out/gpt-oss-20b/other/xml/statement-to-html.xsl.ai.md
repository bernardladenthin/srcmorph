### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:42:03Z
- G: 
- A: 0.0.0
- X: file
- P: 144CA22B
---
> Renders a monthly account statement as an HTML page: a header, one row per booking, a total.

#### Purpose
- Transforms a `<st:statement>` XML document into an HTML page.
- Highlights bookings exceeding a threshold.
- Calculates and displays the total amount.

#### Type
- XSLT stylesheet (`<xsl:stylesheet>` root).
- Namespaces: `xsl` (`http://www.w3.org/1999/XSL/Transform`), `st` (`urn:example:statement`).

#### Definitions
- **Parameters**  
  - `currencySymbol` (`'EUR'`)  
  - `highlightAbove` (`1000`)
- **Key**  
  - `bookingsByDay`: matches `st:booking`, indexed by `st:valueDate`
- **Templates**  
  - `match="/st:statement"`: main entry, outputs `<html>`.  
  - `name="statementHeader"`: outputs account number and period.  
  - `match="st:booking"`: outputs a `<tr>` per booking, with conditional `class="large"`.  
  - `name="statementTotal"`: outputs total amount.

#### Configuration
- `xsl:output`: `method="html"`, `encoding="UTF-8"`, `indent="yes"`.
- `currencySymbol`: `'EUR'`.
- `highlightAbove`: `1000`.
- `class` attribute added to `<tr>` when `number(st:amount) > $highlightAbove`.

#### Dependencies
- Input XML must contain elements in namespace `urn:example:statement`:  
  - `<st:statement>` with attributes `period`, child `<st:accountNumber>`.  
  - `<st:booking>` elements with children `st:valueDate`, `st:purpose`, `st:amount`.  
- Uses XSLT 1.0 functions: `format-number`, `sum`, `number`, `xsl:key`.
