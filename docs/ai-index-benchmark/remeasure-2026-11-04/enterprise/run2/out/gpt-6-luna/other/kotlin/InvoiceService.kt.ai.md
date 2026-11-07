### InvoiceService.kt
- H: 1.0
- C: 4ED52ECD
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:14:13Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
- N: 579A2910
---
**Facts (exact, whole file):** type declarations: 6; companion objects: 1; data classes: 3; sealed types: 1; functions (approx): 4; extension functions: 1; suspend functions: 1; secondary constructors: 0; constructor properties (approx): 9; const vals: 2; lateinit properties: 1; imports: 4

> Issues invoices for completed orders, assigns payment deadlines, and calculates line totals and category-based tax rates.

#### Purpose
- `InvoiceService` creates invoices for non-empty completed orders, persists them, and returns an issued or rejected result.
- `InvoiceLine` calculates a line amount; `List<InvoiceLine>.total()` sums line amounts.

#### Type
- `InvoiceNumber` — value class; wraps `String`; annotated `@JvmInline`.
- `InvoiceLine` — data class; fields `sku: String`, `quantity: Int`, `unitPrice: BigDecimal`.
- `InvoiceResult` — sealed interface.
- `InvoiceResult.Issued` — data class implementing `InvoiceResult`; fields `number: InvoiceNumber`, `dueDate: LocalDate`.
- `InvoiceResult.Rejected` — data class implementing `InvoiceResult`; field `reason: String`.
- `InvoiceService` — class; constructor-injected `InvoiceRepository` and `clock: () -> LocalDate` (default `LocalDate::now`); contains companion object.

#### Input
- `InvoiceService` constructor: `repository: InvoiceRepository`; `clock: () -> LocalDate`, defaulting to `LocalDate::now`.
- `issue(order: CompletedOrder)` reads `order.lines`, `order.id`, and `order.customerId`.
- `taxRate(category: String)` checks the category against `REDUCED_TAX_CATEGORIES`.
- `InvoiceLine.total()` receives a `List<InvoiceLine>`.
- `InvoiceLine` receives `sku`, `quantity`, and `unitPrice`.

#### Output
- `issue` returns `InvoiceResult`; successful issuance saves an `Invoice` through `repository.save`.
- Issued invoices receive a due date `PAYMENT_TERM_DAYS` after `clock()`.
- `taxRate` returns `BigDecimal`: `0.07` for reduced-tax categories, otherwise `0.19`.
- `InvoiceLine.amount` and `List<InvoiceLine>.total()` return `BigDecimal`.
- `defaultCurrency` is publicly mutable state; `sequence` increments when a number is generated.

#### Core logic
- `issue` runs within `withContext(Dispatchers.IO)`.
- Rejects orders with no lines, returning a reason containing the order ID; rejection occurs before numbering or saving.
- For a non-empty order, increments the sequence to form `INV-<sequence>`, calculates the due date, constructs an `Invoice`, saves it, then returns `Issued`.
- Reduced-tax categories are `"books"` and `"food"`.
- `InvoiceLine.amount` multiplies `unitPrice` by `quantity`; `total()` folds line amounts from `BigDecimal.ZERO`.

#### Public API
- `PAYMENT_TERM_DAYS: Int` — payment term in days.
- `InvoiceNumber(value: String)` — wraps an invoice number.
- `InvoiceLine(sku: String, quantity: Int, unitPrice: BigDecimal)` — creates an invoice line.
- `amount: BigDecimal` — calculated line amount.
- `InvoiceResult.Issued(number: InvoiceNumber, dueDate: LocalDate)` — successful result.
- `InvoiceResult.Rejected(reason: String)` — rejected result.
- `InvoiceService(repository: InvoiceRepository, clock: () -> LocalDate)` — creates the service.
- `REDUCED_TAX_CATEGORIES: Set<String>` — reduced-tax category names.
- `defaultCurrency: String` — default currency property.
- `issue(order: CompletedOrder): InvoiceResult` — issues and saves an invoice.
- `taxRate(category: String): BigDecimal` — returns category tax rate.
- `List<InvoiceLine>.total(): BigDecimal` — sums line amounts.

#### Dependencies
- `BigDecimal`, `LocalDate`
- `Dispatchers`, `withContext`
- Referenced domain types: `InvoiceRepository`, `CompletedOrder`, `Invoice`

#### Exceptions / Errors
- Empty orders produce `InvoiceResult.Rejected`; no exception is thrown in this branch.
- `defaultCurrency` is `lateinit`; access before initialization can fail.
- Repository and clock failures are not caught.

#### Concurrency
- `issue` switches execution to `Dispatchers.IO`.
- `sequence` is mutable and incremented without synchronization; concurrent issuance is not protected against races.
