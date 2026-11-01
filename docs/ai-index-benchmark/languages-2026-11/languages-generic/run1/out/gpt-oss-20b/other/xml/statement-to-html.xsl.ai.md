### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:31:22Z
- G: 
- A: 0.0.0
- X: file
---
> Renders a monthly account statement as an HTML page with header, rows per booking, and a total.

#### Purpose
- Convert `<st:statement>` XML into an HTML page.
- Highlight bookings exceeding a threshold.
- Display total amount.

#### Type
- XSLT stylesheet (XML) – `xsl:stylesheet` version 1.0.

#### Input
- XML document matching `/st:statement` with:
  - `@period` attribute.
  - `st:accountNumber` element.
  - Multiple `st:booking` elements containing `st:valueDate`, `st:purpose`, `st:amount`.
- Parameters:
  - `currencySymbol` (default `'EUR'`).
  - `highlightAbove` (default `1000`).

#### Output
- HTML document:
  - `<h1>` with account number.
  - `<p>` with period.
  - `<table>` rows for each booking.
  - `<p>` with total amount.
- Bookings with `st:amount` > `highlightAbove` receive `class="large"`.

#### Core logic
- `match="/st:statement"`: build `<html>` skeleton, call header, table rows, and total.
- `statementHeader`: output account number and period.
- `match="st:booking"`: create `<tr>`, conditionally add `class="large"`, output date, purpose, formatted amount with currency.
- `statementTotal`: compute sum of all `st:booking/st:amount`, format, and display.

#### Public API
- Parameters:
  - `currencySymbol` → sets displayed currency symbol.
  - `highlightAbove` → threshold for highlighting.
- Templates:
  - `statementHeader()` → renders header section.
  - `statementTotal()` → renders total paragraph.
  - `match="st:booking"` → renders each booking row.

#### Constants and configuration
- `currencySymbol` default `'EUR'`.
- `highlightAbove` default `1000`.

#### Dependencies
- XSLT 1.0 functions: `format-number`, `sum`, `number`, `xsl:apply-templates`, `xsl:call-template`, `xsl:attribute`, `xsl:if`, `xsl:value-of`.
- Namespace `st` bound to `urn:example:statement`.

#### Exceptions / Errors
- None explicitly handled; relies on XSLT runtime for type conversion errors.
