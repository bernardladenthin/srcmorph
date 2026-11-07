### other/kotlin
- H: 1.0
- C: 284EE5DF
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:42:53Z
- G: 
- A: 0.0.0
- X: package
- F: [InvoiceService.kt](InvoiceService.kt.ai.md)
---
> Generates and stores invoices for completed orders, applying tax rates and due dates.

#### Purpose
- Create and persist invoices for finished orders.
- Compute due dates and apply appropriate tax rates.

#### Responsibilities
- **Invoice creation**: validate order, generate number, build invoice, persist.
- **Tax calculation**: determine rate based on product category.
- **Number sequencing**: maintain incremental invoice identifiers.
- **Line total aggregation**: sum amounts of invoice lines.

#### Key units
- `InvoiceService` – orchestrates issuance, number generation, and persistence.
- `InvoiceNumber` – inline value type for invoice identifiers.
- `InvoiceLine` – data holder for SKU, quantity, unit price, and line total.
- `InvoiceResult` (sealed) – outcome of issuance: `Issued` (with number and due date) or `Rejected` (with reason).

#### Data flow
1. `issue(order)` receives a `CompletedOrder`.
2. Validates `order.lines`; rejects if empty.
3. Calls `nextNumber()` → `InvoiceNumber`.
4. Builds `Invoice(number, customerId, lines, dueDate)`.
5. Persists via `InvoiceRepository.save(invoice)`.
6. Returns `Issued(number, dueDate)` or `Rejected(reason)`.

#### Dependencies
- **Internal**: `InvoiceRepository`, `CompletedOrder`, `Invoice`.
- **External**: `java.math.BigDecimal`, `java.time.LocalDate`, `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext`.

#### Cross-cutting
- Uses coroutine `Dispatchers.IO` for repository interaction.
- Simple mutable `sequence` counter; not thread‑safe.
- Tax rates defined as constants; reduced rate for specific categories.
- Extension `List<InvoiceLine>.total()` aggregates line amounts.
