### other/kotlin
- H: 1.0
- C: 284EE5DF
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:12:08Z
- G: 
- A: 0.0.0
- X: package
- F: [InvoiceService.kt](InvoiceService.kt.ai.md)
---
> Generates and stores invoices for completed orders, applying tax rules and payment terms.

#### Purpose
- Issues invoices for completed orders.
- Applies reduced tax rate to specific categories.
- Persists invoices via `InvoiceRepository`.

#### Responsibilities
- **Invoice generation** – create `Invoice` objects with sequential numbers and due dates.
- **Tax calculation** – provide tax rates per product category.
- **Invoice line aggregation** – compute total amounts from line items.
- **Persistence** – store issued invoices and report rejections.

#### Key units
- `InvoiceService` – orchestrates invoice creation, validation, and persistence.
- `InvoiceNumber` – inline value type for unique invoice identifiers.
- `InvoiceLine` – data holder for SKU, quantity, unit price, and line total.
- `InvoiceResult` (sealed) – outcome of an issuance attempt (`Issued` or `Rejected`).
- `List<InvoiceLine>.total()` – extension to sum line amounts.

#### Data flow
1. `InvoiceService.issue(order)` receives a `CompletedOrder`.
2. Validates `order.lines`; if empty → `Rejected`.
3. Generates next `InvoiceNumber` via internal sequence.
4. Builds `Invoice` with due date (`clock().plusDays(PAYMENT_TERM_DAYS)`).
5. Persists via `InvoiceRepository.save`.
6. Returns `InvoiceResult.Issued(number, dueDate)`.

#### Dependencies
- **Internal**: `InvoiceRepository`, `CompletedOrder`, `Invoice`.
- **External**: `java.math.BigDecimal`, `java.time.LocalDate`, `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext`.

#### Cross-cutting
- Uses coroutine `Dispatchers.IO` for I/O operations.
- Tax rates are hard‑coded with a reduced‑rate list (`REDUCED_TAX_CATEGORIES`).
- No shared mutable state beyond a non‑thread‑safe sequence counter.
- Rejections are communicated via the `Rejected` sealed data class rather than exceptions.
