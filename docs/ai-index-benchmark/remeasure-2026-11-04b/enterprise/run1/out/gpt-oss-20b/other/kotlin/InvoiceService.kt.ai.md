### InvoiceService.kt
- H: 1.0
- C: 4ED52ECD
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:26:22Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
- N: 579A2910
---
**Facts (exact, whole file):** type declarations: 6; companion objects: 1; data classes: 3; sealed types: 1; functions (approx): 4; extension functions: 1; suspend functions: 1; secondary constructors: 0; constructor properties (approx): 9; const vals: 2; lateinit properties: 1; imports: 4

> Generates and stores invoices for completed orders, applying tax rules and payment terms.

#### Purpose
- Issues invoices for completed orders.
- Applies reduced tax rate for specific categories.
- Stores invoices via `InvoiceRepository`.

#### Type
- `final class InvoiceService` (class)  
  - Extends `Any`.  
  - Implements no interfaces.  
  - Modifiers: none.  
  - Annotations: none.  
- `value class InvoiceNumber` (value class)  
  - Modifiers: `@JvmInline`.  
- `data class InvoiceLine` (data class)  
  - Modifiers: none.  
- `sealed interface InvoiceResult` (interface)  
  - Modifiers: none.  
  - Subtypes:  
    - `data class Issued` (data class)  
    - `data class Rejected` (data class)

#### Input
- `InvoiceService` constructor: `repository: InvoiceRepository`, `clock: () -> LocalDate = LocalDate::now`.
- `issue(order: CompletedOrder)` parameter: `order: CompletedOrder`.
- `taxRate(category: String)` parameter: `category: String`.
- `nextNumber()` uses internal `sequence` and `PREFIX`.
- `List<InvoiceLine>.total()` operates on list of `InvoiceLine`.

#### Output
- `issue(order)` returns `InvoiceResult` (`Issued` or `Rejected`).
- `taxRate` returns `BigDecimal`.
- `nextNumber` returns `InvoiceNumber`.
- `List<InvoiceLine>.total()` returns `BigDecimal`.
- Side effect: `repository.save(invoice)` writes invoice.

#### Core logic
- `issue(order)`  
  - If `order.lines.isEmpty()` → return `Rejected` with message.  
  - Generate `number` via `nextNumber()`.  
  - Create `Invoice(number, order.customerId, order.lines, clock().plusDays(PAYMENT_TERM_DAYS.toLong()))`.  
  - Call `repository.save(invoice)`.  
  - Return `Issued(number, invoice.dueDate)`.  
- `taxRate(category)`  
  - If `category` in `REDUCED_TAX_CATEGORIES` → `0.07`; else `0.19`.  
- `nextNumber()`  
  - Increment `sequence`; format `"INV-${sequence}"`.  
- `List<InvoiceLine>.total()`  
  - Fold over lines summing `line.amount`.

#### Public API
- `InvoiceService.issue(order: CompletedOrder) -> InvoiceResult` – issue invoice for order.  
- `InvoiceService.taxRate(category: String) -> BigDecimal` – get tax rate.  
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
- `shop.billing.InvoiceRepository` (assumed)  
- `shop.billing.CompletedOrder` (assumed)  
- `shop.billing.Invoice` (assumed)

#### Exceptions / Errors
- None declared; `issue` returns `Rejected` for empty lines.

#### Concurrency
- `issue` runs in `Dispatchers.IO` context.  
- No explicit synchronization; `sequence` is mutable but accessed only within coroutine context.
