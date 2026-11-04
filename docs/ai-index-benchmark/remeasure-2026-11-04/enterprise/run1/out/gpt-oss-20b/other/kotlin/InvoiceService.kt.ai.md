### InvoiceService.kt
- H: 1.0
- C: 4ED52ECD
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:10:49Z
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
- Persists invoices via `InvoiceRepository`.

#### Type
- `final class InvoiceService` (class)  
  - Extends `Any`.  
  - Implements no interfaces.  
  - Modifiers: none.  
  - Annotations: none.  
- `value class InvoiceNumber` (value)  
  - Extends `Any`.  
  - Modifiers: `@JvmInline`.  
- `data class InvoiceLine` (data)  
  - Extends `Any`.  
  - Modifiers: none.  
- `sealed interface InvoiceResult` (interface)  
  - Extends `Any`.  
  - Modifiers: none.  
  - Nested types:  
    - `data class Issued` (data) – implements `InvoiceResult`.  
    - `data class Rejected` (data) – implements `InvoiceResult`.  
- Top‑level function `fun List<InvoiceLine>.total()` (extension function).

#### Input
- `InvoiceService` constructor:  
  - `repository: InvoiceRepository` – injected persistence.  
  - `clock: () -> LocalDate` – default `LocalDate::now`.  
- `issue(order: CompletedOrder)` parameters: `order: CompletedOrder`.  
- `taxRate(category: String)` parameter: `category: String`.  
- `nextNumber()` uses internal `sequence` and `PREFIX`.  
- `total()` operates on `List<InvoiceLine>`.

#### Output
- `issue` returns `InvoiceResult` (`Issued` or `Rejected`).  
- `taxRate` returns `BigDecimal`.  
- `total` returns `BigDecimal`.  
- `repository.save(invoice)` writes invoice to storage.  
- `InvoiceResult.Issued` contains `InvoiceNumber` and `dueDate`.  
- `InvoiceResult.Rejected` contains rejection reason.

#### Core logic
- `issue`  
  1. Validate non‑empty `order.lines`.  
  2. Generate next invoice number via `nextNumber()`.  
  3. Create `Invoice` with due date `clock().plusDays(PAYMENT_TERM_DAYS)`.  
  4. Persist invoice with `repository.save`.  
  5. Return `Issued(number, dueDate)`.  
- `taxRate`  
  - Return `0.07` if `category` in `REDUCED_TAX_CATEGORIES`; else `0.19`.  
- `total`  
  - Sum `line.amount` over list.

#### Public API
- `InvoiceService.issue(order: CompletedOrder) -> InvoiceResult` – issue invoice.  
- `InvoiceService.taxRate(category: String) -> BigDecimal` – tax rate lookup.  
- `InvoiceNumber.value: String` – invoice identifier.  
- `InvoiceLine.amount: BigDecimal` – line total.  
- `InvoiceLine.sku: String` – product code.  
- `InvoiceLine.quantity: Int` – quantity.  
- `InvoiceLine.unitPrice: BigDecimal` – unit price.  
- `InvoiceResult.Issued.number: InvoiceNumber` – issued number.  
- `InvoiceResult.Issued.dueDate: LocalDate` – due date.  
- `InvoiceResult.Rejected.reason: String` – rejection reason.  
- `List<InvoiceLine>.total() -> BigDecimal` – total amount.

#### Dependencies
- `java.math.BigDecimal`  
- `java.time.LocalDate`  
- `kotlinx.coroutines.Dispatchers`  
- `kotlinx.coroutines.withContext`  
- `shop.billing.InvoiceRepository`  
- `shop.billing.CompletedOrder`  
- `shop.billing.Invoice` (used in `issue`)

#### Exceptions / Errors
- `issue` returns `Rejected` if `order.lines.isEmpty()`.  
- No explicit exceptions thrown; relies on repository and coroutine context.

#### Concurrency
- `issue` runs in `Dispatchers.IO` context.  
- No shared mutable state beyond `sequence` (private, not thread‑safe).  
- `defaultCurrency` is `lateinit` and mutable; no synchronization.
