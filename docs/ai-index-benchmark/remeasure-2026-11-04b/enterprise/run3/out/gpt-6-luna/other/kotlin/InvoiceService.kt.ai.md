### InvoiceService.kt
- H: 1.0
- C: 4ED52ECD
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:38:32Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
- N: 579A2910
---
**Facts (exact, whole file):** type declarations: 6; companion objects: 1; data classes: 3; sealed types: 1; functions (approx): 4; extension functions: 1; suspend functions: 1; secondary constructors: 0; constructor properties (approx): 9; const vals: 2; lateinit properties: 1; imports: 4

> Completed orders are turned into numbered invoices with payment deadlines, stored for billing, and assigned standard or reduced tax rates.

#### Purpose
- `InvoiceService` rejects empty orders, creates and stores invoices, and calculates category tax rates.
- `InvoiceLine` computes line amounts; `List<InvoiceLine>.total()` sums them.

#### Type
- `PAYMENT_TERM_DAYS` — top-level `const val`; public `Int`.
- `InvoiceNumber` — `value class`; `@JvmInline`; wraps `String`.
- `InvoiceLine` — `data class`; properties `sku: String`, `quantity: Int`, `unitPrice: BigDecimal`.
- `InvoiceResult` — `sealed interface`.
- `InvoiceResult.Issued` — nested `data class`; implements `InvoiceResult`; properties `number: InvoiceNumber`, `dueDate: LocalDate`.
- `InvoiceResult.Rejected` — nested `data class`; implements `InvoiceResult`; property `reason: String`.
- `InvoiceService` — `class`; constructor-injected `InvoiceRepository` and `clock: () -> LocalDate`; contains companion object.

#### Input
- `InvoiceService(repository, clock)` — `repository: InvoiceRepository`; `clock` defaults to `LocalDate::now`.
- `issue(order: CompletedOrder)` — reads `order.lines`, `order.id`, and `order.customerId`.
- `taxRate(category: String)` — category name.
- `InvoiceLine(sku, quantity, unitPrice)` — `String`, `Int`, and `BigDecimal`.
- `InvoiceNumber(value: String)`; `InvoiceResult.Issued(number, dueDate)`; `InvoiceResult.Rejected(reason)`.
- `List<InvoiceLine>.total()` — receiver list of invoice lines.
- Public mutable `defaultCurrency: String` is declared `lateinit`.

#### Output
- `issue` returns `InvoiceResult.Issued` with invoice number and due date, or `InvoiceResult.Rejected` with a reason; saves issued invoices through `repository.save(invoice)`.
- `taxRate` returns `BigDecimal("0.07")` for reduced-tax categories, otherwise `BigDecimal("0.19")`.
- `InvoiceLine.amount` returns `unitPrice * quantity`; `total()` returns the sum of line amounts.
- `nextNumber()` increments the private `sequence`; invoice numbers use the `INV-` prefix.

#### Core logic
- `issue` runs inside `withContext(Dispatchers.IO)`.
- If `order.lines.isEmpty()`, returns `Rejected("order ${order.id} has no lines")` without numbering or saving.
- Otherwise calls `nextNumber()`, constructs `Invoice` from the number, customer ID, order lines, and `clock().plusDays(PAYMENT_TERM_DAYS.toLong())`, then calls `repository.save(invoice)`.
- Returns `Issued(number, invoice.dueDate)` after saving.
- `taxRate` checks membership in `REDUCED_TAX_CATEGORIES`, which contains `"books"` and `"food"`.
- `nextNumber()` creates `InvoiceNumber("$PREFIX-${++sequence}")`; `PREFIX` is `"INV"`.
- `total()` folds from `BigDecimal.ZERO`, adding each `line.amount`.

#### Public API
- `InvoiceNumber(value: String)` — wraps invoice identifier.
- `InvoiceLine(sku: String, quantity: Int, unitPrice: BigDecimal)` — creates line item.
- `amount: BigDecimal` — calculated line amount.
- `InvoiceResult.Issued(number: InvoiceNumber, dueDate: LocalDate)` — issued outcome.
- `InvoiceResult.Rejected(reason: String)` — rejected outcome.
- `InvoiceService(repository: InvoiceRepository, clock: () -> LocalDate)` — configures invoice issuance.
- `defaultCurrency: String` — get/set default currency.
- `REDUCED_TAX_CATEGORIES: Set<String>` — reduced-rate categories.
- `issue(order: CompletedOrder): InvoiceResult` — issues and stores invoice.
- `taxRate(category: String): BigDecimal` — returns category tax rate.
- `PAYMENT_TERM_DAYS: Int` — payment term constant.
- `List<InvoiceLine>.total(): BigDecimal` — sums line amounts.

#### Dependencies
- `java.math.BigDecimal`, `java.time.LocalDate`
- `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext`
- `InvoiceRepository`, `CompletedOrder`, `Invoice` (referenced, not declared in this file)

#### Concurrency
- `issue` switches execution to `Dispatchers.IO`.
- `sequence` is mutable and unsynchronized; concurrent calls have no explicit sequence-safety mechanism.
