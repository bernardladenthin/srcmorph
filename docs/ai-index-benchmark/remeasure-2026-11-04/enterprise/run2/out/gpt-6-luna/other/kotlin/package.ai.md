### other/kotlin
- H: 1.0
- C: 284EE5DF
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:15:49Z
- G: 
- A: 0.0.0
- X: package
- F: [InvoiceService.kt](InvoiceService.kt.ai.md)
---
> Converts completed orders into persisted invoices with payment deadlines, line totals, and category-based tax rates.

#### Purpose
- Issues invoices for non-empty completed orders, returning either an issued invoice number and due date or a rejection.
- Provides invoice-line amount and total calculations, plus reduced-rate category lookup.

#### Responsibilities
- Invoice issuance: reject empty orders; number, date, persist, and report valid invoices.
- Invoice calculations and tax rules: multiply line price by quantity, sum line amounts, and select tax rates by category.

#### Key units
- `InvoiceService` — issues invoices through `InvoiceRepository`, using an injectable date clock.
- `InvoiceNumber` — value class wrapping an invoice number.
- `InvoiceResult` — sealed result with `Issued` and `Rejected` variants.
- `InvoiceLine` — holds SKU, quantity, and unit price; exposes the calculated `amount`.
- `List<InvoiceLine>.total()` — sums line amounts.
- `PAYMENT_TERM_DAYS`, `REDUCED_TAX_CATEGORIES`, `defaultCurrency` — payment-term constant, reduced-rate categories (`books`, `food`), and mutable currency state.
- `taxRate(category)` — returns `0.07` for reduced-rate categories and `0.19` otherwise.

#### Data flow
- `issue(order)` reads the completed order’s lines, ID, and customer ID; empty lines yield `Rejected` before numbering or persistence.
- For non-empty orders, increments the sequence to form `INV-<sequence>`, sets the due date from `clock()` plus `PAYMENT_TERM_DAYS`, saves an `Invoice`, and returns `Issued`.
- Invoice-line amounts multiply `unitPrice` by `quantity`; list totals fold amounts from `BigDecimal.ZERO`.

#### Dependencies
- Domain collaborators: `CompletedOrder`, `Invoice`, `InvoiceRepository`.
- Standard types: `BigDecimal`, `LocalDate`.
- Coroutine execution: `withContext`, `Dispatchers.IO`.

#### Cross-cutting
- `issue` runs on `Dispatchers.IO`; mutable sequence increments are unsynchronized and may race under concurrent issuance.
- Empty-order handling uses `InvoiceResult.Rejected`; repository and clock failures are not caught.
- `defaultCurrency` is `lateinit`; accessing it before initialization can fail.
