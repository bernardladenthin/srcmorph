### statement-to-html.xsl
- H: 1.0
- C: E1851E80
- D: 2026-10-31T21:26:46Z
- T: 2026-11-01T23:55:23Z
- G: 
- A: 0.0.0
- X: file
- P: 144CA22B
---
> Transforms a namespaced monthly account statement into an HTML page with booking rows, large-amount highlighting, and a total.

#### Type
- XSLT 1.0 stylesheet; root element `xsl:stylesheet`.
- Namespace `xsl`: `http://www.w3.org/1999/XSL/Transform`; namespace `st`: `urn:example:statement`.

#### Definitions
- Output method: `html`.
- Templates: match `/st:statement`; match `st:booking`; named `statementHeader`; named `statementTotal`.
- Parameters: `currencySymbol`, `highlightAbove`.
- Key: `bookingsByDay`, matching `st:booking`, indexed by `st:valueDate`.
- The statement template emits a title and account header, applies booking templates, and calls the total template. Booking rows contain value date, purpose, and formatted amount; rows exceeding the threshold get class `large`.

#### Configuration
- `currencySymbol`: default `EUR`.
- `highlightAbove`: default `1000`.
- Amount and total format: `#,##0.00`.
- Output encoding: `UTF-8`; indentation enabled.

#### Dependencies
- Input elements in namespace `urn:example:statement`: `st:statement`, `st:accountNumber`, `st:booking`, `st:valueDate`, `st:purpose`, `st:amount`.
- Statement attribute: `period`.
