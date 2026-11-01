### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:28:40Z
- G: 
- A: 0.0.0
- X: file
---
> Renders an account statement as an HTML page with booking rows and a formatted total.

#### Purpose
- Transforms an XML statement into a readable HTML statement.
- Highlights bookings whose amount exceeds a configurable threshold.

#### Type
- XSLT 1.0 stylesheet using the `st` namespace `urn:example:statement`.

#### Input
- XML rooted at `st:statement`; reads `@period`, `st:accountNumber`, and each `st:booking`’s `st:valueDate`, `st:purpose`, and `st:amount`.
- Parameters: `currencySymbol` and `highlightAbove`.

#### Output
- HTML document containing a statement title and header, a table of bookings, and a formatted total.

#### Core logic
- Matches `/st:statement`, builds the HTML page, and applies templates to `st:booking` elements.
- `statementHeader` displays the account number and period.
- Each booking becomes a table row; rows receive class `large` when the numeric amount exceeds `$highlightAbove`.
- Booking amounts and the sum of all booking amounts use `#,##0.00` formatting; booking rows append `$currencySymbol`.

#### Public API
- `match="/st:statement"()` → HTML document — renders statement page.
- `match="st:booking"()` → HTML row — renders one booking.
- `statementHeader()` → HTML fragments — renders account and period.
- `statementTotal()` → HTML fragment — renders summed booking amounts.

#### Constants and configuration
- `currencySymbol` — XSLT parameter; default `'EUR'`.
- `highlightAbove` — XSLT parameter; default `1000`.
- `bookingsByDay` — key matching `st:booking`, indexed by `st:valueDate`; declared but not used in the shown templates.
- Output settings: HTML method, UTF-8 encoding, indentation enabled.

#### Dependencies
- XSLT namespace: `http://www.w3.org/1999/XSL/Transform`.
- XPath/XSLT functions: `number()`, `format-number()`, `sum()`, `xsl:value-of`, `xsl:apply-templates`, `xsl:call-template`, `xsl:attribute`.

#### Exceptions / Errors
- No explicit error handling or custom exceptions.
