### other/kotlin
- H: 1.0
- C: 284EE5DF
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:22:00Z
- G: 
- A: 0.0.0
- X: package
- F: [InvoiceService.kt](InvoiceService.kt.ai.md)
---
> Converts completed orders into numbered invoices, calculates line totals and category-based tax rates, and records invoices with payment deadlines.

#### Purpose
- Issue invoices for non-empty completed orders and report issuance or rejection.
- Provide invoice-line amount and total calculations plus category tax-rate selection.

#### Responsibilities
- Invoice issuance: validate order lines, allocate an invoice number, set a payment deadline, save the invoice, and return a result.
- Invoice calculations and policy: calculate line and order totals, and select reduced or standard tax rates.

#### Key units
- `InvoiceService` — coordinates invoice issuance through `InvoiceRepository`, with an injectable date clock.
- `InvoiceNumber` — value-class wrapper for invoice identifiers.
- `InvoiceResult` — sealed result with `Issued` (number and due date) and `Rejected` (reason) variants.
- `InvoiceLine` — holds SKU, quantity, and unit price; computes its amount.
- `List<InvoiceLine>.total()` — sums line amounts.
- `PAYMENT_TERM_DAYS` — defines the 14-day payment term.
- `InvoiceService.REDUCED_TAX_CATEGORIES` — identifies `"books"` and `"food"` for the reduced rate.

#### Data flow
- `issue(order)` rejects an order with no lines; otherwise it generates an `INV-N` number, calculates the due date from the clock and payment term, builds an `Invoice`, saves it, and returns `InvoiceResult.Issued`.
- `InvoiceLine.amount` multiplies unit price by quantity; `total()` folds amounts from `BigDecimal.ZERO`.
- `taxRate(category)` returns `0.07` for reduced-tax categories and `0.19` otherwise.

#### Dependencies
- Internal domain and persistence types: `CompletedOrder`, `Invoice`, and `InvoiceRepository`.
- Kotlin coroutine APIs: `Dispatchers.IO` and `withContext`.
- Java numeric and date types: `BigDecimal` and `LocalDate`.

#### Cross-cutting
- `issue()` runs on `Dispatchers.IO`; the mutable invoice-number sequence has no explicit concurrency protection.
- `defaultCurrency` is a writable `lateinit` property; reading it before initialization can raise `UninitializedPropertyAccessException`.
