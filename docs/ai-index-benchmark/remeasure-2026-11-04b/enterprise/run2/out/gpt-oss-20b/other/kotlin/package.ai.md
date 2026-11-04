### other/kotlin
- H: 1.0
- C: 284EE5DF
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:36:46Z
- G: 
- A: 0.0.0
- X: package
- F: [InvoiceService.kt](InvoiceService.kt.ai.md)
---
> Generates and stores invoices for completed orders, applying tax rules and payment terms.

#### Purpose
- Issue sequential invoice numbers.
- Compute due dates (14‑day term).
- Persist invoices via `InvoiceRepository`.
- Apply reduced tax rates for books and food.

#### Responsibilities
- **Invoice numbering** – generates unique `InvoiceNumber` values.
- **Tax calculation** – provides tax rates per category.
- **Invoice creation** – builds `Invoice` objects from `CompletedOrder` data.
- **Persistence** – saves invoices asynchronously to the repository.
- **Utility** – calculates line totals and overall invoice totals.

#### Key units
- `InvoiceNumber` – value class wrapping a string identifier.
- `InvoiceLine` – data class for line items; `amount` computed from `quantity * unitPrice`.
- `InvoiceResult` (sealed) – `Issued` (success) and `Rejected` (failure) outcomes.
- `InvoiceService` – core service with `issue`, `taxRate`, `nextNumber`; holds `repository`, `clock`, `defaultCurrency`, and tax‑category set.
- `List<InvoiceLine>.total()` – extension for summing line amounts.
- `PAYMENT_TERM_DAYS` – constant (14).

#### Data flow
1. `issue(order)` receives a `CompletedOrder`.
2. Validates non‑empty lines; otherwise returns `Rejected`.
3. Calls `nextNumber()` to get a new `InvoiceNumber`.
4. Builds an `Invoice` with customer ID, lines, and due date (`clock() + 14 days`).
5. Persists the invoice via `repository.save` inside `withContext(Dispatchers.IO)`.
6. Returns `Issued(number, dueDate)`.

#### Dependencies
- **Internal**: `InvoiceRepository`, `CompletedOrder`, `Invoice`.
- **External**: `java.math.BigDecimal`, `java.time.LocalDate`, `kotlinx.coroutines.Dispatchers`, `kotlinx.coroutines.withContext`.

#### Cross-cutting
- **Concurrency**: `issue` runs in `Dispatchers.IO` for I/O safety.
- **Error handling**: `Rejected` encapsulates validation failures; `defaultCurrency` is a `lateinit` property, throwing if accessed before init.
- **Configuration**: `REDUCED_TAX_CATEGORIES` and `PREFIX` are static constants; `defaultCurrency` is mutable and public.
- **Utility**: `total()` aggregates line amounts, used for invoice totals.
