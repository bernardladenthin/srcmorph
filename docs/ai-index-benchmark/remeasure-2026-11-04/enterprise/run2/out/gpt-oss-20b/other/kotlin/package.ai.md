### other/kotlin
- H: 1.0
- C: 284EE5DF
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T08:18:33Z
- G: 
- A: 0.0.0
- X: package
- F: [InvoiceService.kt](InvoiceService.kt.ai.md)
---
> Generates and stores invoices for completed orders, applying tax rules and payment terms.

#### Purpose
- Issue invoices for completed orders.
- Apply reduced tax rates to specific categories.
- Persist invoices via `InvoiceRepository`.

#### Responsibilities
- **Invoice generation** – create invoice numbers, calculate due dates, and assemble invoice lines.
- **Tax calculation** – determine tax rate per product category.
- **Persistence** – save invoices to the repository.
- **Validation** – reject orders lacking line items.

#### Key units
- `InvoiceService` – orchestrates invoice creation, validation, and persistence.
- `InvoiceNumber` – inline value type holding the invoice identifier string.
- `InvoiceLine` – data class representing a single line item (SKU, quantity, unit price, amount).
- `InvoiceResult` (sealed interface) – outcome of an invoice request; subtypes `Issued` (success) and `Rejected` (failure).
- `List<InvoiceLine>.total()` – extension computing the sum of line amounts.

#### Data flow
1. `InvoiceService.issue(order)` receives a `CompletedOrder`.
2. Validates that `order.lines` is non‑empty; otherwise returns `Rejected`.
3. Generates a new `InvoiceNumber` via `nextNumber()`.
4. Builds an `Invoice` object with customer ID, lines, and a due date (`clock() + PAYMENT_TERM_DAYS`).
5. Persists the invoice using `InvoiceRepository.save`.
6. Returns `Issued(number, dueDate)`.

#### Dependencies
- **Internal**: `InvoiceRepository`, `CompletedOrder`.
- **Standard**: `java.math.BigDecimal`, `java.time.LocalDate`.
- **Coroutines**: `Dispatchers.IO`, `withContext` for IO‑bound persistence.

#### Cross-cutting
- **Concurrency**: `issue` executes within `Dispatchers.IO` to keep IO operations off the main thread.
- **Error handling**: Rejection is represented by the `Rejected` subtype with a reason string, avoiding exceptions for normal control flow.
- **Configuration**: Uses a clock supplier (`() -> LocalDate`) and a constant payment term, enabling testability and flexibility.
