### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T14:18:42Z
- G: 
- A: 0.0.0
- X: file
---
> Renders a monthly account statement as an HTML page: a header, one row per booking, a total.

#### Purpose
- Transforms a `st:statement` XML document into an HTML page.
- Highlights bookings exceeding a threshold.
- Calculates and displays the total amount.

#### Type
- XSLT stylesheet (`xsl:stylesheet` root).
- Namespaces: `xsl` (`http://www.w3.org/1999/XSL/Transform`), `st` (`urn:example:statement`).

#### Definitions
- **Parameters**
  - `currencySymbol` (`'EUR'`)
  - `highlightAbove` (`1000`)
- **Key**
  - `bookingsByDay` (`match="st:booking" use="st:valueDate"`)
- **Templates**
  - `match="/st:statement"` – main entry, outputs `<html>`.
  - `name="statementHeader"` – outputs account header.
  - `match="st:booking"` – outputs a table row; adds `class="large"` if `st:amount > $highlightAbove`.
  - `name="statementTotal"` – outputs total amount.

#### Configuration
- `xsl:output` method: `html`, encoding: `UTF-8`, indent: `yes`.
- `currencySymbol` default: `'EUR'`.
- `highlightAbove` default: `1000`.

#### Dependencies
- Input XML must contain elements in namespace `urn:example:statement`: `statement`, `booking`, `accountNumber`, `valueDate`, `purpose`, `amount`.
- Uses XSLT 1.0 functions: `format-number`, `sum`, `number`, `xsl:apply-templates`, `xsl:call-template`, `xsl:if`, `xsl:attribute`.
