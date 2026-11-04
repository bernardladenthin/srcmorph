### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-11-02T16:51:10Z
- T: 2026-11-04T08:01:46Z
- G: 
- A: 0.0.0
- X: file
- P: 326D0B29
---
> Renders a monthly account statement as an HTML page with booking rows, conditional highlighting, and a total.

#### Purpose
- Transforms a statement document into an HTML account summary.
- Highlights bookings whose amount exceeds a configurable threshold.

#### Type
- XSLT 1.0 stylesheet; root element `xsl:stylesheet`.
- Namespaces: `xsl` — `http://www.w3.org/1999/XSL/Transform`; `st` — `urn:example:statement`.

#### Definitions
- Output method: `html`; encoding `UTF-8`; `indent="yes"`.
- Parameters: `currencySymbol` defaults to `EUR`; `highlightAbove` defaults to `1000`.
- Key `bookingsByDay`: matches `st:booking`, indexed by `st:valueDate`.
- Templates:
  - Match `/st:statement`: renders statement title and header, applies booking templates, and renders total.
  - Named `statementHeader`: renders account number and period.
  - Match `st:booking`: renders a row with value date, purpose, and formatted amount; adds class `large` when the amount exceeds `$highlightAbove`.
  - Named `statementTotal`: renders the sum of booking amounts.

#### Configuration
- `currencySymbol`: `EUR`.
- `highlightAbove`: `1000`.
- Amount format: `#,##0.00`.

#### Dependencies
- Input statement namespace: `urn:example:statement`; expected data includes `st:accountNumber`, `st:booking`, `st:valueDate`, `st:purpose`, `st:amount`, and statement attribute `period`.
