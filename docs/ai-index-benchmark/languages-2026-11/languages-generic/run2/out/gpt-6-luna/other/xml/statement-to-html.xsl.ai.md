### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-10-31T21:33:19Z
- G: 
- A: 0.0.0
- X: file
---
> Renders a monthly account statement as an HTML page with booking rows, large-amount highlighting, and a total.

#### Purpose
- Transforms an `st:statement` XML document into an HTML account statement.
- Displays account and period details, booking amounts, and the sum of bookings.

#### Type
- XSLT 1.0 stylesheet; XML transformation using the `st` namespace (`urn:example:statement`).

#### Input
- Source `st:statement` with `@period`, `st:accountNumber`, and `st:booking` elements.
- Each `st:booking` may contain `st:valueDate`, `st:purpose`, and `st:amount`.
- Parameters: `currencySymbol` and `highlightAbove`.

#### Output
- HTML document with a title, account header, period, booking table, and total.
- Booking rows above the threshold receive `class="large"`.

#### Core logic
- Matches `/st:statement`; renders the title from `@period`, then calls `statementHeader`, applies templates to bookings, and calls `statementTotal`.
- `statementHeader` renders the account number and statement period.
- Each `st:booking` becomes a table row containing value date, purpose, and formatted amount plus currency symbol.
- Adds the `large` class when the numeric booking amount exceeds `$highlightAbove`.
- `statementTotal` sums `st:booking/st:amount` and formats the result.

#### Public API
- `statementHeader()` → HTML fragment — renders account and period.
- `statementTotal()` → HTML fragment — renders summed booking amounts.
- Template `match="/st:statement"` — renders the statement page.
- Template `match="st:booking"` — renders a booking row.

#### Constants and configuration
- `currencySymbol`: stylesheet parameter; default `'EUR'`.
- `highlightAbove`: stylesheet parameter; default `1000`.
- `bookingsByDay`: key matching `st:booking`, indexed by `st:valueDate`; defined but not used in the shown templates.
- Amount format pattern: `#,##0.00`.
- Highlight class: `large`.

#### Dependencies
- XSLT 1.0; `xsl:output`, `xsl:param`, `xsl:key`, `xsl:template`, `xsl:call-template`, `xsl:apply-templates`, `xsl:if`, `xsl:attribute`, `xsl:value-of`.
- XPath functions: `number()`, `format-number()`, `sum()`.

#### Exceptions / Errors
- No explicit error handling or error conditions are defined.
