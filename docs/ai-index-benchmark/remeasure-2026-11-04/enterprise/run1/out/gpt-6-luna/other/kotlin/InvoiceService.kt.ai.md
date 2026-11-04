### InvoiceService.kt
- H: 1.0
- C: 4ED52ECD
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:07:44Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
- N: 579A2910
---
**Facts (exact, whole file):** type declarations: 6; companion objects: 1; data classes: 3; sealed types: 1; functions (approx): 4; extension functions: 1; suspend functions: 1; secondary constructors: 0; constructor properties (approx): 9; const vals: 2; lateinit properties: 1; imports: 4

> Issues invoices for completed orders, assigns payment deadlines, applies category-based tax rates, and stores invoice records.

#### Purpose
- `InvoiceService` converts non-empty `CompletedOrder` values into stored invoices with numbered identifiers and due dates.
- `InvoiceLine` calculates line amounts; `List<InvoiceLine>.total()` sums them.

#### Type
- `InvoiceNumber` — value class (`@JvmInline`), wraps `String`.
- `InvoiceLine` — data class; constructor properties `sku: String`, `quantity: Int`, `unitPrice: BigDecimal`.
- `InvoiceResult` — sealed interface.
- `InvoiceResult.Issued` — data class implementing `InvoiceResult`; properties `number: InvoiceNumber`, `dueDate: LocalDate`.
- `InvoiceResult.Rejected` — data class implementing `InvoiceResult`; property `reason: String`.
- `InvoiceService` — class; primary constructor takes `InvoiceRepository` and a `() -> LocalDate` clock defaulting to `LocalDate::now`.
- `InvoiceService.Companion` — companion object; contains private constant `PREFIX` and public tax-category set.
- Top-level constant `PAYMENT_TERM_DAYS: Int`.
- Top-level extension function on `List<InvoiceLine>`.

#### Input
- `InvoiceService` receives an `InvoiceRepository` and a date-producing clock.
- `issue` consumes a `CompletedOrder` with `id`, `customerId`, and `lines`.
- `taxRate` consumes a category string.
- `InvoiceLine` receives SKU, quantity, and unit price; `InvoiceNumber` receives a string.

#### Output
- `issue` returns `InvoiceResult`; successful issuance saves an `Invoice` and returns its number and due date.
- Empty orders produce `InvoiceResult.Rejected` with an order-specific reason.
- `taxRate` returns a `BigDecimal`.
- `InvoiceLine.amount` multiplies unit price by quantity; `total()` sums line amounts.
- `InvoiceService` mutates its private invoice sequence; `defaultCurrency` is publicly readable and writable.

#### Core logic
- `issue` runs its work in `Dispatchers.IO`.
- Rejects orders with no lines before allocating an invoice number.
- Increments the sequence and formats the number as `INV-N`.
- Sets the due date to the clock date plus `PAYMENT_TERM_DAYS` (14).
- Constructs and saves the invoice, then returns `Issued`.
- `taxRate` returns `0.07` for categories in `REDUCED_TAX_CATEGORIES` (`books`, `food`), otherwise `0.19`.
- `InvoiceLine.total()` folds from `BigDecimal.ZERO`, adding each line’s `amount`.

#### Public API
- `PAYMENT_TERM_DAYS: Int` — payment term in days.
- `InvoiceNumber(value: String)` — wraps an invoice number.
- `InvoiceLine(sku: String, quantity: Int, unitPrice: BigDecimal)` — creates a priced line.
- `amount: BigDecimal` — calculated line amount.
- `InvoiceResult.Issued(number: InvoiceNumber, dueDate: LocalDate)` — represents issuance.
- `InvoiceResult.Rejected(reason: String)` — represents rejection.
- `InvoiceService(repository: InvoiceRepository, clock: () -> LocalDate = LocalDate::now)` — creates the issuer.
- `REDUCED_TAX_CATEGORIES: Set<String>` — categories receiving reduced tax.
- `defaultCurrency: String` — default currency property.
- `issue(order: CompletedOrder): InvoiceResult` — issues and stores an invoice.
- `taxRate(category: String): BigDecimal` — returns the category tax rate.
- `List<InvoiceLine>.total(): BigDecimal` — sums line amounts.

#### Dependencies
- `BigDecimal`, `LocalDate`
- `Dispatchers`, `withContext`
- `InvoiceRepository`, `CompletedOrder`, `Invoice`

#### Exceptions / Errors
- Empty order lines yield `Rejected`; no exception is thrown for this case.
- Repository or clock exceptions are not caught and propagate to the caller.
- `defaultCurrency` is `lateinit`; reading it before initialization fails.

#### Concurrency
- `issue` is suspending and switches execution to `Dispatchers.IO`.
- Invoice numbering uses a mutable, unsynchronized sequence; concurrent calls are not protected.
