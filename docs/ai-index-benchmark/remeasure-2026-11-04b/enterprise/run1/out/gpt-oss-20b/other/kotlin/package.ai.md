### other/kotlin
- H: 1.0
- C: 284EE5DF
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:27:38Z
- G: 
- A: 0.0.0
- X: package
- F: [InvoiceService.kt](InvoiceService.kt.ai.md)
---
> Generates and stores invoices for completed orders, applying tax rules and payment terms.

#### Purpose
- Issue invoices for completed orders.
- Apply reduced tax rates for specific product categories.
- Persist invoices via `InvoiceRepository`.

#### Responsibilities
- **Invoice generation** – create `Invoice` objects with unique numbers and due dates.
- **Tax calculation** – determine tax rate per product category.
- **Validation** – reject orders with no line items.
- **Persistence** – save invoices asynchronously to a repository.

#### Key units
- `InvoiceService` – orchestrates invoice creation, validation, tax lookup, and persistence.
- `InvoiceNumber` – inline value class wrapping the invoice identifier string.
- `InvoiceLine` – data class holding SKU, quantity, unit price, and line total.
- `InvoiceResult` (sealed interface) – outcome of an invoice attempt:
  - `Issued` – contains `InvoiceNumber` and due date.
  - `Rejected` – contains a rejection reason.
- Extension `List<InvoiceLine>.total()` – sums line amounts.

#### Data flow
1. `issue(order: CompletedOrder)` receives a completed order.  
2. Validates that `order.lines` is non‑empty; otherwise returns `Rejected`.  
3. Generates a new `InvoiceNumber` via `nextNumber()`.  
4. Builds an `Invoice` with customer ID, lines, and a due date calculated from the current date plus `PAYMENT_TERM_DAYS`.  
5. Persists the invoice with `repository.save(invoice)` inside a `Dispatchers.IO` coroutine.  
6. Returns `Issued(number, dueDate)`.

#### Dependencies
- **Internal** – `InvoiceRepository`, `CompletedOrder`, `Invoice` (assumed in `shop.billing`).  
- **External** – `java.math.BigDecimal`, `java.time.LocalDate`, `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext`.

#### Cross-cutting
- **Concurrency** – `issue` runs in `Dispatchers.IO`; mutable `sequence` is accessed only within this coroutine context.  
- **Error handling** – no exceptions; business failures are represented by `InvoiceResult.Rejected`.  
- **Configuration** – tax rates are hard‑coded (`REDUCED_TAX_CATEGORIES` → 0.07, others → 0.19); payment term days are a constant.  
- **Utility** – `List<InvoiceLine>.total()` provides a reusable total calculation.
