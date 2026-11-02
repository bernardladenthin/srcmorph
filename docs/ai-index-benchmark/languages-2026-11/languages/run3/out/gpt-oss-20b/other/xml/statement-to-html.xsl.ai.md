### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T01:56:11Z
- G: 
- A: 0.0.0
- X: file
---
> **Transforms a monthly account statement XML into an HTML page with header, rows per booking, and total.**

#### Purpose
- Render a monthly account statement as an HTML page.
- Include header, booking rows, and total amount.

#### Type
- XSLT stylesheet (`<xsl:stylesheet>` root).
- Namespace declarations:
  - `xsl` → `http://www.w3.org/1999/XSL/Transform`
  - `st` → `urn:example:statement`

#### Definitions
- **Templates**
  - `match="/st:statement"` – main entry point.
  - `name="statementHeader"` – renders account header.
  - `match="st:booking"` – renders each booking row.
  - `name="statementTotal"` – renders total paragraph.
- **Key**
  - `name="bookingsByDay"` – matches `st:booking` elements, indexed by `st:valueDate`.
- **Parameters**
  - `$currencySymbol` – default `'EUR'`.
  - `$highlightAbove` – default `1000`.
- **Output**
  - Method: `html`, encoding: `UTF-8`, indent: `yes`.

#### Configuration
- `<xsl:param name="currencySymbol" select="'EUR'"/>`
- `<xsl:param name="highlightAbove" select="1000"/>`

#### Dependencies
- Input XML must use namespace `urn:example:statement` with elements:
  - `st:statement` (attributes: `@period`, child `st:accountNumber`).
  - `st:booking` (children: `st:valueDate`, `st:purpose`, `st:amount`).
- XSLT processor supporting XSLT 1.0.
