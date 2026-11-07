### InvoiceService.kt
- H: 1.0
- C: 4ED52ECD
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:17:26Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
- N: 579A2910
---
**Facts (exact, whole file):** type declarations: 6; companion objects: 1; data classes: 3; sealed types: 1; functions (approx): 4; extension functions: 1; suspend functions: 1; secondary constructors: 0; constructor properties (approx): 9; const vals: 2; lateinit properties: 1; imports: 4

> Generates and stores invoices for completed orders, applying tax rules and payment terms.

#### Purpose
- Issues invoices for completed orders.
- Applies reduced tax rate to specific categories.
- Stores invoices via `InvoiceRepository`.

#### Type
- `final class InvoiceService` (class)  
  - Extends `Any`.  
  - Implements no interfaces.  
  - Modifiers: none.  
  - Annotations: none.  
- `value class InvoiceNumber` (value)  
  - Modifiers: `@JvmInline`.  
- `data class InvoiceLine` (record)  
  - Modifiers: none.  
- `sealed interface InvoiceResult` (interface)  
  - Subtypes: `data class Issued`, `data class Rejected`.  
- `fun List<InvoiceLine>.total()` (extension function)

#### Input
- `InvoiceService` constructor: `repository: InvoiceRepository`, `clock: () -> LocalDate = LocalDate::now`.
- `issue(order: CompletedOrder)` parameter: `order: CompletedOrder`.
- `taxRate(category: String)` parameter: `category: String`.
- `nextNumber()` uses internal `sequence` and `PREFIX`.
- `total()` operates on `List<InvoiceLine>`.

#### Output
- `issue` returns `InvoiceResult` (`Issued` or `Rejected`).
- `taxRate` returns `BigDecimal`.
- `total` returns `BigDecimal`.
- `repository.save(invoice)` writes invoice to persistence.

#### Core logic
- `issue`:
  1. Validate `order.lines` non‑empty; reject if empty.
  2. Generate next invoice number via `nextNumber()`.
  3. Create `Invoice` with number, customerId, lines, dueDate (`clock() + PAYMENT_TERM_DAYS`).
  4. Persist invoice with `repository.save`.
  5. Return `Issued(number, dueDate)`.
- `taxRate`:
  - Return `0.07` if category in `REDUCED_TAX_CATEGORIES`; else `0.19`.
- `total`:
  - Sum `line.amount` over list.

#### Public API
- `InvoiceService.issue(order: CompletedOrder) -> InvoiceResult` – issue invoice for order.
- `InvoiceService.taxRate(category: String) -> BigDecimal` – get tax rate for category.
- `InvoiceNumber.value: String` – invoice number string.
- `InvoiceLine.sku: String` – product SKU.
- `InvoiceLine.quantity: Int` – quantity.
- `InvoiceLine.unitPrice: BigDecimal` – unit price.
- `InvoiceLine.amount: BigDecimal` – line total.
- `InvoiceResult.Issued.number: InvoiceNumber` – issued number.
- `InvoiceResult.Issued.dueDate: LocalDate` – due date.
- `InvoiceResult.Rejected.reason: String` – rejection reason.
- `List<InvoiceLine>.total() -> BigDecimal` – total amount of lines.

#### Dependencies
- `java.math.BigDecimal`
- `java.time.LocalDate`
- `kotlinx.coroutines.Dispatchers`
- `kotlinx.coroutines.withContext`
- `shop.billing.InvoiceRepository`
- `shop.billing.CompletedOrder`

#### Exceptions / Errors
- `issue` rejects orders with no lines, returning `Rejected` with message.

#### Concurrency
- `issue` runs in `Dispatchers.IO` context, ensuring IO‑bound work off the main thread.
