### other/kotlin
- H: 1.0
- C: 284EE5DF
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:40:07Z
- G: 
- A: 0.0.0
- X: package
- F: [InvoiceService.kt](InvoiceService.kt.ai.md)
---
> Completed customer orders are converted into numbered invoices with payment deadlines, persisted for billing, and assigned category-based tax rates.

#### Purpose
- Issue invoices for nonempty completed orders and return either an issued result or a rejection.
- Calculate invoice line totals and apply standard or reduced tax rates.

#### Responsibilities
- **Invoice issuance:** Validate order lines, assign an invoice number and due date, and save the invoice through `InvoiceRepository`.
- **Invoice calculations:** Compute line amounts and sum invoice lines; select reduced rates for books and food.
- **Invoice result modeling:** Represent successful issuance or rejection with a sealed result type.

#### Key units
- `InvoiceService` — issues invoices, determines tax rates, and generates sequential `INV-` numbers.
- `InvoiceNumber` — value class wrapping an invoice identifier.
- `InvoiceLine` — stores SKU, quantity, and unit price; exposes the calculated `amount`.
- `InvoiceResult` — sealed interface with `Issued(number, dueDate)` and `Rejected(reason)` outcomes.
- `PAYMENT_TERM_DAYS` — payment deadline interval used when calculating invoice due dates.
- `REDUCED_TAX_CATEGORIES` — set of reduced-rate categories: `"books"` and `"food"`.
- `List<InvoiceLine>.total()` — sums line amounts from `BigDecimal.ZERO`.

#### Data flow
- `issue(order)` rejects orders with no lines without numbering or saving.
- For nonempty orders, it generates an invoice number, builds an `Invoice` from the order’s customer ID and lines, and sets the due date from the injected clock plus `PAYMENT_TERM_DAYS`.
- The service saves the invoice through `InvoiceRepository` and returns `InvoiceResult.Issued` with its number and due date.
- `taxRate(category)` returns `0.07` for reduced-tax categories and `0.19` otherwise; line and list calculations produce `BigDecimal` amounts and totals.

#### Dependencies
- `InvoiceRepository` for invoice persistence; `CompletedOrder` for order ID, customer ID, and lines; `Invoice` as the persisted invoice model.
- Kotlin coroutines `withContext` and `Dispatchers.IO` for invoice issuance.
- Java `BigDecimal` and `LocalDate` for monetary values and dates.

#### Cross-cutting
- `InvoiceService` receives its repository and clock as constructor dependencies; the clock defaults to `LocalDate::now`.
- `issue` executes on `Dispatchers.IO`.
- The mutable invoice sequence has no explicit synchronization or concurrency-safety mechanism.
- `defaultCurrency` is a public mutable `lateinit` property.
