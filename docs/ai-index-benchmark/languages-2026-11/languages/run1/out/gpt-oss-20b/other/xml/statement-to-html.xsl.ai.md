### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:13:01Z
- G: 
- A: 0.0.0
- X: file
---
> Transforms a monthly account statement XML into an HTML page with header, booking rows, and total.

#### Purpose
- Render a monthly account statement as an HTML page.
- Highlight bookings exceeding a threshold.

#### Type
- XSLT stylesheet (`<xsl:stylesheet>`)  
- Namespaces: `xsl` (`http://www.w3.org/1999/XSL/Transform`), `st` (`urn:example:statement`)

#### Definitions
- **Parameters**  
  - `$currencySymbol` (default `'EUR'`)  
  - `$highlightAbove` (default `1000`)
- **Key**  
  - `bookingsByDay` (`match="st:booking"`, `use="st:valueDate"`)
- **Templates**  
  - `match="/st:statement"` – main entry, outputs `<html>` structure.  
  - `name="statementHeader"` – outputs account number and period.  
  - `match="st:booking"` – outputs a `<tr>` for each booking, adds `class="large"` if amount > `$highlightAbove`.  
  - `name="statementTotal"` – outputs total amount of all bookings.

#### Configuration
- `<xsl:output method="html" encoding="UTF-8" indent="yes"/>`
- `$currencySymbol` value `'EUR'`
- `$highlightAbove` value `1000`
- `class="large"` applied when `number(st:amount) > $highlightAbove`

#### Dependencies
- Input XML must have root `<st:statement>` with attributes `period`, child `<st:accountNumber>`, and multiple `<st:booking>` elements containing `<st:valueDate>`, `<st:purpose>`, `<st:amount>`.  
- Uses XSLT 1.0 functions: `format-number`, `sum`, `number`.
