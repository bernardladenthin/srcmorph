### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:39:25Z
- G: 
- A: 0.0.0
- X: file
---
> Renders a monthly account statement as an HTML page with booking rows, large-amount highlights, and a total.

#### Purpose
- Transforms an `st:statement` XML document into an HTML account statement.
- Displays account and period details, booking dates, purposes, amounts, and the summed amount.

#### Type
- XSLT 1.0 stylesheet; HTML output.
- Uses the `st` namespace `urn:example:statement`.

#### Input
- `st:statement` root with `@period`, `st:accountNumber`, and `st:booking` elements.
- Each `st:booking` may contain `st:valueDate`, `st:purpose`, and `st:amount`.
- Parameters: `currencySymbol` (default `EUR`), `highlightAbove` (default `1000`).

#### Output
- HTML document with a title, account heading, period, booking table, and formatted total.
- Booking rows above `highlightAbove` receive `class="large"`.

#### Core logic
- Matches `/st:statement` and builds the HTML page.
- Calls `statementHeader` to render account number and period.
- Applies templates to each `st:booking`, rendering date, purpose, and formatted amount with the currency symbol.
- Adds the `large` class when the numeric booking amount exceeds `highlightAbove`.
- Calls `statementTotal`, which sums booking amounts and formats the result.
- Defines `bookingsByDay` keyed by booking value date; the key is not used elsewhere in this file.

#### Public API
- `statementHeader()` → HTML nodes — renders account and period.
- `statementTotal()` → HTML nodes — renders summed booking amount.
- Template `match="/st:statement"` → HTML document — renders statement page.
- Template `match="st:booking"` → HTML table row — renders booking details.

#### Constants and configuration
- `currencySymbol`: XSLT parameter; default `EUR`.
- `highlightAbove`: XSLT parameter; default `1000`.
- `bookingsByDay`: key matching `st:booking`, indexed by `st:valueDate`.
- Number format: `#,##0.00`.
- Highlight class: `large`.

#### Dependencies
- XSLT namespace `http://www.w3.org/1999/XSL/Transform`.
- XSLT functions: `xsl:value-of`, `xsl:call-template`, `xsl:apply-templates`, `xsl:if`, `xsl:attribute`, `format-number()`, `sum()`, `number()`.

#### Exceptions / Errors
- No explicit error handling or exception behavior is defined.
