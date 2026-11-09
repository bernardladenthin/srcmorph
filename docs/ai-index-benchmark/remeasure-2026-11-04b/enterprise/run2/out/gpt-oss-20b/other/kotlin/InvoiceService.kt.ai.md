### InvoiceService.kt
- H: 1.0
- C: 4ED52ECD
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:33:03Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
- N: 579A2910
---
**Facts (exact, whole file):** type declarations: 6; companion objects: 1; data classes: 3; sealed types: 1; functions (approx): 4; extension functions: 1; suspend functions: 1; secondary constructors: 0; constructor properties (approx): 9; const vals: 2; lateinit properties: 1; imports: 4

> Issues invoices for completed orders, assigns numbers, applies tax rates, and stores them.

#### Purpose
- Assigns sequential invoice numbers.
- Calculates due dates using a 14‑day payment term.
- Stores invoices via `InvoiceRepository`.
- Applies reduced tax rates for books and food.

#### Type
- `value class InvoiceNumber(val value: String)` – value class, final, property `value: String`.
- `data class InvoiceLine(val sku: String, val quantity: Int, val unitPrice: BigDecimal)` – data class, final, properties `sku`, `quantity`, `unitPrice`; computed property `amount: BigDecimal`.
- `sealed interface InvoiceResult` – sealed interface, final, nested data classes:
  - `data class Issued(val number: InvoiceNumber, val dueDate: LocalDate)` – data class, final, properties `number`, `dueDate`.
  - `data class Rejected(val reason: String)` – data class, final, property `reason`.
- `class InvoiceService` – final class, constructor `private val repository: InvoiceRepository`, `private val clock: () -> LocalDate = LocalDate::now`; companion object:
  - `private const val PREFIX = "INV"`
  - `val REDUCED_TAX_CATEGORIES = setOf("books", "food")`
  - private var `sequence = 0`
  - `lateinit var defaultCurrency: String`
  - `suspend fun issue(order: CompletedOrder): InvoiceResult`
  - `fun taxRate(category: String): BigDecimal`
  - `private fun nextNumber(): InvoiceNumber`
- `fun List<InvoiceLine>.total(): BigDecimal` – top‑level extension function.
- `const val PAYMENT_TERM_DAYS = 14` – top‑level constant.

#### Input
- Constructor: `repository: InvoiceRepository`, `clock: () -> LocalDate`.
- `issue(order: CompletedOrder)` – uses `order.lines`, `order.id`, `order.customerId`.
- `taxRate(category: String)` – uses `REDUCED_TAX_CATEGORIES`.
- `nextNumber()` – no parameters.
- `total()` – no parameters.

#### Output
- `issue` returns `InvoiceResult.Issued` or `InvoiceResult.Rejected`; mutates `repository` via `save`; increments `sequence`.
- `taxRate` returns `BigDecimal`.
- `total` returns `BigDecimal`.
- `nextNumber` returns `InvoiceNumber`.
- `defaultCurrency` is a mutable public property.

#### Core logic
- `issue`:
  1. If `order.lines.isEmpty()` → `InvoiceResult.Rejected("order ${order.id} has no lines")`.
  2. `number = nextNumber()`.
  3. `invoice = Invoice(number, order.customerId, order.lines, clock().plusDays(PAYMENT_TERM_DAYS.toLong()))`.
  4. `repository.save(invoice)`.
  5. Return `InvoiceResult.Issued(number, invoice.dueDate)`.
- `taxRate`:
  - If `category in REDUCED_TAX_CATEGORIES` → `BigDecimal("0.07")`; else `BigDecimal("0.19")`.
- `nextNumber`:
  - Increment `sequence`; return `InvoiceNumber("$PREFIX-${++sequence}")`.
- `total`:
  - `fold(BigDecimal.ZERO) { sum, line -> sum + line.amount }`.

#### Public API
- `InvoiceNumber(value: String)` → value class
- `InvoiceLine(sku: String, quantity: Int, unitPrice: BigDecimal)` → data class
- `InvoiceLine.amount: BigDecimal` → property
- `InvoiceResult.Issued(number: InvoiceNumber, dueDate: LocalDate)` → data class
- `InvoiceResult.Rejected(reason: String)` → data class
- `InvoiceService(issue(order: CompletedOrder): InvoiceResult)` → suspend
- `InvoiceService(taxRate(category: String): BigDecimal)` → function
- `InvoiceService.defaultCurrency: String` → property
- `InvoiceService.REDUCED_TAX_CATEGORIES: Set<String>` → property
- `List<InvoiceLine>.total(): BigDecimal` → extension function
- `PAYMENT_TERM_DAYS: Int` → constant

#### Dependencies
- `java.math.BigDecimal`
- `java.time.LocalDate`
- `kotlinx.coroutines.Dispatchers`
- `kotlinx.coroutines.withContext`
- `InvoiceRepository`
- `CompletedOrder`
- `Invoice`

#### Exceptions / Errors
- `issue` returns `InvoiceResult.Rejected` if `order.lines.isEmpty()`.
- `defaultCurrency` is `lateinit`; accessing before initialization throws `UninitializedPropertyAccessException`.

#### Concurrency
- `issue` executes in `withContext(Dispatchers.IO)`; coroutine context for I/O.
