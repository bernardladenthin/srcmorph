### InvoiceService.kt
- H: 1.0
- C: 4ED52ECD
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:23:42Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
- N: 579A2910
---
**Facts (exact, whole file):** type declarations: 6; companion objects: 1; data classes: 3; sealed types: 1; functions (approx): 4; extension functions: 1; suspend functions: 1; secondary constructors: 0; constructor properties (approx): 9; const vals: 2; lateinit properties: 1; imports: 4

> Issues invoices for completed orders, assigning numbers, calculating due dates, and storing them.

#### Purpose
- Generates and persists invoices for completed orders.
- Calculates due dates based on a payment term.
- Provides tax rate lookup for product categories.

#### Type
- `final class InvoiceService` (class)  
  - Extends `Any`.  
  - Implements no interfaces.  
  - Modifiers: none.  
  - Annotations: none.  
- `value class InvoiceNumber` (value)  
  - Modifiers: `@JvmInline`.  
  - Extends `Any`.  
- `data class InvoiceLine` (data)  
  - Modifiers: none.  
  - Extends `Any`.  
- `sealed interface InvoiceResult` (interface)  
  - Modifiers: none.  
  - Extends `Any`.  
  - Nested types:  
    - `data class Issued` (data) – implements `InvoiceResult`.  
    - `data class Rejected` (data) – implements `InvoiceResult`.

#### Input
- `InvoiceService` constructor: `repository: InvoiceRepository`, `clock: () -> LocalDate = LocalDate::now`.
- `issue(order: CompletedOrder)` parameter: `order: CompletedOrder`.
- `taxRate(category: String)` parameter: `category: String`.
- `nextNumber()` uses internal `sequence` and `PREFIX`.
- `List<InvoiceLine>.total()` operates on a list of `InvoiceLine`.

#### Output
- `issue(order)` returns `InvoiceResult.Issued` with `InvoiceNumber` and `dueDate` or `InvoiceResult.Rejected` with reason.
- `taxRate` returns `BigDecimal` tax rate.
- `nextNumber` returns `InvoiceNumber`.
- `List<InvoiceLine>.total()` returns `BigDecimal` total amount.

#### Core logic
- `issue(order)`  
  1. Reject if `order.lines` empty.  
  2. Generate next invoice number via `nextNumber()`.  
  3. Create `Invoice` with number, customerId, lines, dueDate (`clock().plusDays(PAYMENT_TERM_DAYS)`).
  4. Persist invoice via `repository.save(invoice)`.  
  5. Return `InvoiceResult.Issued(number, invoice.dueDate)`.
- `taxRate(category)` returns 0.07 for reduced categories, else 0.19.
- `nextNumber()` increments `sequence` and formats `"INV-${sequence}"`.
- `InvoiceLine.amount` multiplies `unitPrice` by `quantity`.
- `List<InvoiceLine>.total()` sums `amount` of each line.

#### Public API
- `InvoiceService.issue(order: CompletedOrder) -> InvoiceResult` – issue invoice for order.  
- `InvoiceService.taxRate(category: String) -> BigDecimal` – get tax rate.  
- `InvoiceNumber.value: String` – underlying string.  
- `InvoiceLine.sku: String` – product SKU.  
- `InvoiceLine.quantity: Int` – quantity.  
- `InvoiceLine.unitPrice: BigDecimal` – unit price.  
- `InvoiceLine.amount: BigDecimal` – line total.  
- `InvoiceResult.Issued.number: InvoiceNumber` – invoice number.  
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
- `shop.billing.Invoice` (used in `issue`)

#### Exceptions / Errors
- `issue` returns `InvoiceResult.Rejected` if order has no lines; no exceptions thrown.

#### Concurrency
- `issue` runs in `Dispatchers.IO` context, ensuring IO-bound work off the main thread.  
- No explicit synchronization; `sequence` is not thread‑safe.
