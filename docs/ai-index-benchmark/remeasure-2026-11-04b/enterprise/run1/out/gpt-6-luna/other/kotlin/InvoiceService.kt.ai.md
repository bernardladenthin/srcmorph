### InvoiceService.kt
- H: 1.0
- C: 4ED52ECD
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:23:22Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
- N: 579A2910
---
**Facts (exact, whole file):** type declarations: 6; companion objects: 1; data classes: 3; sealed types: 1; functions (approx): 4; extension functions: 1; suspend functions: 1; secondary constructors: 0; constructor properties (approx): 9; const vals: 2; lateinit properties: 1; imports: 4

> Issues and stores invoices for completed orders, assigning invoice numbers and payment due dates while rejecting orders without lines.

#### Purpose
- `InvoiceService` issues invoices from completed orders and applies payment terms.
- `InvoiceLine` calculates line amounts; `List<InvoiceLine>.total()` sums them.

#### Type
- `InvoiceNumber` — value class; wraps `String`; annotated `@JvmInline`.
- `InvoiceLine` — data class; fields `sku: String`, `quantity: Int`, `unitPrice: BigDecimal`.
- `InvoiceResult` — sealed interface.
- `InvoiceResult.Issued` — nested data class implementing `InvoiceResult`; fields `number: InvoiceNumber`, `dueDate: LocalDate`.
- `InvoiceResult.Rejected` — nested data class implementing `InvoiceResult`; field `reason: String`.
- `InvoiceService` — class; constructor dependencies `repository: InvoiceRepository`, `clock: () -> LocalDate`.

#### Input
- `InvoiceService` constructor receives `InvoiceRepository` and a date supplier, defaulting to `LocalDate::now`.
- `issue(order: CompletedOrder)` consumes order ID, customer ID, and lines.
- `taxRate(category: String)` consumes a category.
- `InvoiceLine` receives SKU, quantity, and unit price.
- `PAYMENT_TERM_DAYS` is 14; `REDUCED_TAX_CATEGORIES` contains `"books"` and `"food"`.

#### Output
- `issue` returns `InvoiceResult`; successful issuance saves an `Invoice` and returns its number and due date.
- Empty-line orders return `InvoiceResult.Rejected`.
- `taxRate` returns `BigDecimal`; reduced categories receive `0.07`, others `0.19`.
- `InvoiceLine.amount` returns `unitPrice * quantity`; `List<InvoiceLine>.total()` returns the sum of line amounts.
- `InvoiceService.defaultCurrency` is a public mutable property.

#### Core logic
- `issue` switches to `Dispatchers.IO` with `withContext`.
- If `order.lines.isEmpty()`, returns `Rejected("order ${order.id} has no lines")` without numbering or saving.
- Otherwise calls private `nextNumber()`, which increments `sequence` and formats `"$PREFIX-${++sequence}"` as an `InvoiceNumber`.
- Builds `Invoice(number, order.customerId, order.lines, clock().plusDays(PAYMENT_TERM_DAYS.toLong()))`.
- Calls `repository.save(invoice)` and returns `Issued(number, invoice.dueDate)`.
- `taxRate` checks whether `category` is in `REDUCED_TAX_CATEGORIES`.
- `InvoiceLine.amount` multiplies `unitPrice` by `BigDecimal(quantity)`; `total()` folds from `BigDecimal.ZERO` using each `line.amount`.

#### Public API
- `InvoiceNumber(value: String)` — wraps an invoice number.
- `InvoiceLine(sku: String, quantity: Int, unitPrice: BigDecimal)` — represents an invoice line.
- `amount: BigDecimal` — calculated line amount.
- `InvoiceResult.Issued(number: InvoiceNumber, dueDate: LocalDate)` — represents successful issuance.
- `InvoiceResult.Rejected(reason: String)` — represents rejected issuance.
- `InvoiceService(repository: InvoiceRepository, clock: () -> LocalDate)` — creates the service.
- `REDUCED_TAX_CATEGORIES: Set<String>` — reduced-rate categories.
- `defaultCurrency: String` — configured currency property.
- `issue(order: CompletedOrder): InvoiceResult` — issues an invoice.
- `taxRate(category: String): BigDecimal` — selects the tax rate.
- `PAYMENT_TERM_DAYS: Int` — payment term in days.
- `List<InvoiceLine>.total(): BigDecimal` — sums line amounts.

#### Dependencies
- `BigDecimal`, `LocalDate`
- `Dispatchers`, `withContext`
- `InvoiceRepository`, `CompletedOrder`, `Invoice`

#### Exceptions / Errors
- No explicit exception handling or precondition checks.
- `defaultCurrency` is `lateinit`; reading it before initialization can fail.
- `repository.save(invoice)` and the date supplier may propagate failures.

#### Concurrency
- `issue` executes its block on `Dispatchers.IO`.
- `sequence` is mutable and unsynchronized; concurrent calls may race.
