### InvoiceService.kt
- H: 1.0
- C: 4ED52ECD
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:41:41Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
- N: 579A2910
---
**Facts (exact, whole file):** type declarations: 6; companion objects: 1; data classes: 3; sealed types: 1; functions (approx): 4; extension functions: 1; suspend functions: 1; secondary constructors: 0; constructor properties (approx): 9; const vals: 2; lateinit properties: 1; imports: 4

> Issues invoices for completed orders, assigns numbers, applies tax rates, and persists them.

#### Purpose
- Generates and stores invoices for completed orders.
- Calculates due dates and applies reduced tax rates for specific categories.

#### Type
- `final class InvoiceService` (class)  
  - Extends `Any`.  
  - Implements no interfaces.  
  - Modifiers: none.  
  - Annotations: none.  
- `value class InvoiceNumber` (value)  
  - Modifiers: `@JvmInline`.  
- `data class InvoiceLine` (data)  
  - Modifiers: none.  
- `sealed interface InvoiceResult` (interface)  
  - Modifiers: none.  
  - Subtypes: `data class Issued`, `data class Rejected`.

#### Input
- `InvoiceService` constructor: `repository: InvoiceRepository`, `clock: () -> LocalDate = LocalDate::now`.  
- `issue(order: CompletedOrder)` parameter: `order: CompletedOrder`.  
- `taxRate(category: String)` parameter: `category: String`.  
- `nextNumber()` uses internal `sequence` and `PREFIX`.  
- `List<InvoiceLine>.total()` operates on a list of `InvoiceLine`.

#### Output
- `issue` returns `InvoiceResult` (`Issued` or `Rejected`).  
- `taxRate` returns `BigDecimal`.  
- `nextNumber` returns `InvoiceNumber`.  
- `List<InvoiceLine>.total` returns `BigDecimal`.  
- `repository.save(invoice)` persists an `Invoice`.

#### Core logic
- `issue`  
  1. Checks `order.lines.isEmpty()` → returns `Rejected`.  
  2. Calls `nextNumber()` → `InvoiceNumber`.  
  3. Creates `Invoice(number, order.customerId, order.lines, clock().plusDays(PAYMENT_TERM_DAYS))`.  
  4. Calls `repository.save(invoice)`.  
  5. Returns `Issued(number, invoice.dueDate)`.  
- `taxRate`  
  - If `category` in `REDUCED_TAX_CATEGORIES` → `0.07`; else `0.19`.  
- `nextNumber`  
  - Increments `sequence` and returns `InvoiceNumber("$PREFIX-${sequence}")`.  
- `List<InvoiceLine>.total`  
  - Folds list summing `line.amount`.

#### Public API
- `InvoiceService.issue(order: CompletedOrder) -> InvoiceResult` – issue invoice for order.  
- `InvoiceService.taxRate(category: String) -> BigDecimal` – get tax rate.  
- `InvoiceNumber.value: String` – invoice identifier.  
- `InvoiceLine.sku: String` – product SKU.  
- `InvoiceLine.quantity: Int` – quantity.  
- `InvoiceLine.unitPrice: BigDecimal` – unit price.  
- `InvoiceLine.amount: BigDecimal` – line total.  
- `InvoiceResult.Issued.number: InvoiceNumber` – issued number.  
- `InvoiceResult.Issued.dueDate: LocalDate` – due date.  
- `InvoiceResult.Rejected.reason: String` – rejection reason.  
- `List<InvoiceLine>.total() -> BigDecimal` – total amount.

#### Dependencies
- `java.math.BigDecimal`  
- `java.time.LocalDate`  
- `kotlinx.coroutines.Dispatchers`  
- `kotlinx.coroutines.withContext`  
- `shop.billing.InvoiceRepository` (assumed)  
- `shop.billing.CompletedOrder` (assumed)  
- `shop.billing.Invoice` (assumed)

#### Exceptions / Errors
- `issue` returns `Rejected` if `order.lines.isEmpty()`.  
- No explicit exceptions thrown; relies on repository and date operations.

#### Concurrency
- `issue` runs in `Dispatchers.IO` context.  
- `nextNumber` is not thread‑safe; `sequence` is a mutable `var`.  
- No other synchronization mechanisms.
