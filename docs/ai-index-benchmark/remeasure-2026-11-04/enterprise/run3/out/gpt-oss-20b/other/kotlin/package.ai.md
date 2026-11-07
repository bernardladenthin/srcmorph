### other/kotlin
- H: 1.0
- C: 284EE5DF
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:24:51Z
- G: 
- A: 0.0.0
- X: package
- F: [InvoiceService.kt](InvoiceService.kt.ai.md)
---
> A service that creates, numbers, and persists invoices for completed orders, calculating due dates and tax rates.

#### Purpose
- Issue invoices for completed orders.
- Persist invoices via a repository.
- Compute due dates and tax rates.

#### Responsibilities
- **Invoice generation** – create `Invoice` objects with sequential numbers and due dates.
- **Persistence** – delegate saving to `InvoiceRepository`.
- **Tax lookup** – provide tax rates per product category.
- **Line calculations** – compute line totals and invoice totals.

#### Key units
- `InvoiceService` – orchestrates invoice creation and persistence.
- `InvoiceNumber` – inline value type for invoice identifiers.
- `InvoiceLine` – data class holding SKU, quantity, unit price, and computed amount.
- `InvoiceResult` (sealed) – outcome of issuing an invoice (`Issued` or `Rejected`).
- `InvoiceRepository` – external persistence interface.
- `CompletedOrder` – input domain model representing a finished order.

#### Data flow
1. `issue(order: CompletedOrder)` receives an order.  
2. Validates order lines; rejects if empty.  
3. Calls `nextNumber()` to get a new `InvoiceNumber`.  
4. Builds an `Invoice` with customer ID, lines, number, and due date (`clock().plusDays(PAYMENT_TERM_DAYS)`).  
5. Persists the invoice via `repository.save(invoice)` inside `Dispatchers.IO`.  
6. Returns `InvoiceResult.Issued` with number and due date, or `Rejected` with a reason.

#### Dependencies
- **Internal**: `InvoiceRepository`, `CompletedOrder`, `Invoice`.  
- **External**: `java.math.BigDecimal`, `java.time.LocalDate`, `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext`.

#### Cross-cutting
- Uses a simple in‑memory `sequence` counter for numbering; not thread‑safe.  
- All IO operations are dispatched to `Dispatchers.IO`.  
- Tax rates are hard‑coded: 0.07 for reduced categories, 0.19 otherwise.  
- Extension `List<InvoiceLine>.total()` aggregates line amounts.
