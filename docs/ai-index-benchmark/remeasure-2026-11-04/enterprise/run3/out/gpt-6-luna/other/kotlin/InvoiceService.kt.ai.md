### InvoiceService.kt
- H: 1.0
- C: 4ED52ECD
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:20:26Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
- N: 579A2910
---
**Facts (exact, whole file):** type declarations: 6; companion objects: 1; data classes: 3; sealed types: 1; functions (approx): 4; extension functions: 1; suspend functions: 1; secondary constructors: 0; constructor properties (approx): 9; const vals: 2; lateinit properties: 1; imports: 4

> Completed orders are turned into numbered invoices with payment deadlines and category-based tax rates.

#### Purpose
- `InvoiceService` issues invoices for non-empty completed orders, saves them, and returns an issuance or rejection result.
- `InvoiceLine` calculates line amounts; `List<InvoiceLine>.total()` sums them.

#### Type
- `PAYMENT_TERM_DAYS` — top-level `const val` of type `Int`, value `14`.
- `InvoiceNumber` — `value class`, annotated `@JvmInline`; wraps a `String`.
- `InvoiceLine` — `data class`; constructor properties `sku: String`, `quantity: Int`, `unitPrice: BigDecimal`; computed `amount: BigDecimal`.
- `InvoiceResult` — `sealed interface`; nested `Issued` data class (`number: InvoiceNumber`, `dueDate: LocalDate`) and `Rejected` data class (`reason: String`).
- `InvoiceService` — class; constructor-injected `InvoiceRepository` and clock function, defaulting to `LocalDate::now`.
- `InvoiceService.Companion` — companion object; exposes reduced-tax categories.
- Top-level extension function: `List<InvoiceLine>.total()`.

#### Input
- `InvoiceService` constructor: `repository: InvoiceRepository`, `clock: () -> LocalDate`.
- `issue(order: CompletedOrder)` reads the order’s `lines`, `id`, and `customerId`.
- `taxRate(category: String)` receives a category name.
- `total()` consumes a list of `InvoiceLine` values.

#### Output
- `issue()` returns `InvoiceResult`; successful issuance saves an `Invoice` and returns its number and due date.
- `taxRate()` returns `BigDecimal`: `0.07` for reduced-tax categories, otherwise `0.19`.
- `InvoiceLine.amount` and `total()` return `BigDecimal`.
- `defaultCurrency: String` is publicly readable and writable; it is declared `lateinit`.

#### Core logic
- `issue()` runs in `Dispatchers.IO`.
- Rejects orders with no lines, including the order ID in the reason; this path does not generate a number or save an invoice.
- For a non-empty order, increments the service sequence to generate an `INV-N` number.
- Computes the due date as `clock() + PAYMENT_TERM_DAYS`, builds an `Invoice` from the number, customer ID, and order lines, then calls `repository.save()`.
- Returns `InvoiceResult.Issued` with the invoice number and due date.
- `taxRate()` checks membership in `REDUCED_TAX_CATEGORIES`, which contains `"books"` and `"food"`.
- `InvoiceLine.amount` multiplies unit price by quantity; `total()` folds line amounts from `BigDecimal.ZERO`.

#### Public API
- `PAYMENT_TERM_DAYS: Int` — payment term in days.
- `InvoiceNumber.value: String` — wrapped invoice number.
- `InvoiceLine(sku: String, quantity: Int, unitPrice: BigDecimal)` — invoice line data.
- `InvoiceLine.amount: BigDecimal` — calculated line amount.
- `InvoiceResult.Issued(number: InvoiceNumber, dueDate: LocalDate)` — successful issuance result.
- `InvoiceResult.Rejected(reason: String)` — rejection result.
- `InvoiceService(repository: InvoiceRepository, clock: () -> LocalDate)` — create invoice service.
- `InvoiceService.REDUCED_TAX_CATEGORIES: Set<String>` — reduced-rate categories.
- `defaultCurrency: String` — get/set default currency.
- `issue(order: CompletedOrder) -> InvoiceResult` — issue and save invoice.
- `taxRate(category: String) -> BigDecimal` — select category tax rate.
- `List<InvoiceLine>.total() -> BigDecimal` — sum line amounts.

#### Dependencies
- `BigDecimal`, `LocalDate`
- `Dispatchers`, `withContext`
- `InvoiceRepository`, `CompletedOrder`, `Invoice`

#### Exceptions / Errors
- Empty orders return `InvoiceResult.Rejected`; no exception is explicitly thrown or caught.
- Reading `defaultCurrency` before initialization can raise `UninitializedPropertyAccessException`.

#### Concurrency
- `issue()` uses the `Dispatchers.IO` coroutine dispatcher.
- The mutable `sequence` is unsynchronized; concurrent calls have no explicit thread-safety protection.
