### InvoiceService.kt
- H: 1.0
- C: 4ED52ECD
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:29:52Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
- N: 579A2910
---
**Facts (exact, whole file):** type declarations: 6; companion objects: 1; data classes: 3; sealed types: 1; functions (approx): 4; extension functions: 1; suspend functions: 1; secondary constructors: 0; constructor properties (approx): 9; const vals: 2; lateinit properties: 1; imports: 4

> Completed orders with invoice lines receive a numbered invoice, a 14-day payment deadline, and persistent storage.

#### Purpose
- `InvoiceService` issues invoices for completed orders and selects reduced tax rates for configured categories.
- `InvoiceLine.total()` sums line amounts.

#### Type
- `InvoiceNumber` — value class; wraps `String`; annotated `@JvmInline`.
- `InvoiceLine` — data class; constructor properties `sku: String`, `quantity: Int`, `unitPrice: BigDecimal`.
- `InvoiceResult` — sealed interface.
- `InvoiceResult.Issued` — data class implementing `InvoiceResult`; `number: InvoiceNumber`, `dueDate: LocalDate`.
- `InvoiceResult.Rejected` — data class implementing `InvoiceResult`; `reason: String`.
- `InvoiceService` — class; constructor dependencies `InvoiceRepository` and `() -> LocalDate`.
- `InvoiceService.Companion` — companion object of `InvoiceService`.
- No declared supertype for `InvoiceService`; no annotations beyond those noted above.

#### Input
- `InvoiceService` constructor: `repository: InvoiceRepository`; `clock: () -> LocalDate`, defaulting to `LocalDate::now`.
- `InvoiceService.issue(order: CompletedOrder)` consumes the order’s `lines`, `id`, and `customerId`.
- `InvoiceService.taxRate(category: String)` consumes a category name.
- `InvoiceLine` constructor consumes SKU, quantity, and unit price.
- `InvoiceNumber` constructor consumes a string value.
- Top-level `PAYMENT_TERM_DAYS` and companion `REDUCED_TAX_CATEGORIES` provide payment-term and reduced-tax configuration.

#### Output
- `issue` returns `InvoiceResult.Issued` with invoice number and due date, or `InvoiceResult.Rejected` for orders without lines.
- `taxRate` returns `BigDecimal`.
- `InvoiceLine.amount` and `List<InvoiceLine>.total()` return `BigDecimal`.
- `issue` saves the constructed `Invoice` through `repository.save(invoice)`.
- `defaultCurrency` is a mutable, uninitialized `lateinit` property; no use is shown.

#### Core logic
- `issue` switches to `Dispatchers.IO` with `withContext`.
- If `order.lines.isEmpty()`, returns `InvoiceResult.Rejected("order ${order.id} has no lines")` without numbering or saving.
- Otherwise, calls private `nextNumber()`, builds `Invoice` from the number, `order.customerId`, `order.lines`, and `clock().plusDays(PAYMENT_TERM_DAYS.toLong())`, then calls `repository.save(invoice)` and returns `InvoiceResult.Issued(number, invoice.dueDate)`.
- `nextNumber()` increments `sequence` and formats an `InvoiceNumber` as `"$PREFIX-${++sequence}"`; `PREFIX` is `"INV"`.
- `taxRate` returns `BigDecimal("0.07")` when `category` is in `REDUCED_TAX_CATEGORIES` (`"books"`, `"food"`); otherwise returns `BigDecimal("0.19")`.
- `InvoiceLine.amount` multiplies `unitPrice` by `BigDecimal(quantity)`.
- `List<InvoiceLine>.total()` folds from `BigDecimal.ZERO`, adding each `line.amount`.

#### Public API
- `PAYMENT_TERM_DAYS: Int` — payment term in days.
- `InvoiceNumber(value: String)` — invoice-number value type.
- `InvoiceLine(sku: String, quantity: Int, unitPrice: BigDecimal)` — invoice line data.
- `amount: BigDecimal` — calculated line amount.
- `InvoiceResult.Issued(number: InvoiceNumber, dueDate: LocalDate)` — issued result.
- `InvoiceResult.Rejected(reason: String)` — rejection result.
- `InvoiceService(repository: InvoiceRepository, clock: () -> LocalDate)` — create invoice service.
- `REDUCED_TAX_CATEGORIES: Set<String>` — reduced-rate categories.
- `defaultCurrency: String` — get/set default currency.
- `issue(order: CompletedOrder): InvoiceResult` — issue and store invoice.
- `taxRate(category: String): BigDecimal` — select category tax rate.
- `List<InvoiceLine>.total(): BigDecimal` — sum line amounts.

#### Dependencies
- `java.math.BigDecimal`
- `java.time.LocalDate`
- `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext`
- `InvoiceRepository`, `CompletedOrder`, `Invoice` in `shop.billing`

#### Exceptions / Errors
- Empty order lines produce `InvoiceResult.Rejected`; no exception handling is shown.
- Accessing `defaultCurrency` before initialization can fail because it is `lateinit`.

#### Concurrency
- `issue` executes its body on `Dispatchers.IO`.
- Mutable `sequence` has no synchronization; concurrent calls may race.
