### other/kotlin
- H: 1.0
- C: 284EE5DF
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:09:15Z
- G: 
- A: 0.0.0
- X: package
- F: [InvoiceService.kt](InvoiceService.kt.ai.md)
---
> Converts completed orders into numbered invoices, applies payment deadlines and category-based tax rates, and records issued invoices.

#### Purpose
- Issue and persist invoices for non-empty `CompletedOrder` values, or return a rejection for empty orders.
- Provide invoice-line amount and total calculations plus category tax-rate lookup.

#### Responsibilities
- Invoice creation and persistence: validate order lines, generate invoice numbers and due dates, construct an `Invoice`, and save it through `InvoiceRepository`.
- Invoice data and calculations: represent invoice numbers, lines, and issuance outcomes; calculate line amounts and list totals.
- Tax and payment terms: apply reduced rates to configured categories and set a 14-day payment deadline.

#### Key units
- `InvoiceService` — issues invoices, looks up tax rates, and exposes `defaultCurrency`.
- `InvoiceNumber` — value class wrapping an invoice-number string.
- `InvoiceLine` — stores SKU, quantity, and unit price; calculates its amount.
- `InvoiceResult` — sealed outcome type with `Issued` and `Rejected` variants.
- `PAYMENT_TERM_DAYS` — payment deadline interval.
- `REDUCED_TAX_CATEGORIES` — categories receiving the reduced tax rate.
- `List<InvoiceLine>.total()` — sums line amounts.

#### Data flow
- `issue` receives a `CompletedOrder`; empty `lines` produce an order-specific `Rejected` result before numbering.
- For a non-empty order, the service increments its sequence, formats an `INV-N` number, calculates a due date from the injected clock, constructs and saves an `Invoice`, then returns `Issued`.
- `taxRate` returns `0.07` for `books` and `food`, and `0.19` otherwise; line amounts and totals use `BigDecimal`.

#### Dependencies
- Domain types: `CompletedOrder`, `Invoice`, `InvoiceRepository`.
- Kotlin/JVM and date, numeric, and coroutine APIs: `BigDecimal`, `LocalDate`, `Dispatchers`, `withContext`.

#### Cross-cutting
- `issue` is suspending and performs its work in `Dispatchers.IO`.
- The invoice sequence is mutable and unsynchronized; concurrent calls are not protected.
- The clock is injectable and defaults to `LocalDate::now`; repository and clock exceptions propagate.
- `defaultCurrency` is `lateinit`; reading it before initialization fails.
