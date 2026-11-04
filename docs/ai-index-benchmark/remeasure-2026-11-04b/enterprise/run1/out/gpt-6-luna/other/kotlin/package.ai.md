### other/kotlin
- H: 1.0
- C: 284EE5DF
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:24:57Z
- G: 
- A: 0.0.0
- X: package
- F: [InvoiceService.kt](InvoiceService.kt.ai.md)
---
> Issues invoices for completed orders, applying payment terms and tax rates while rejecting orders without invoice lines.

#### Purpose
- Create and persist invoices from completed orders, returning either issuance details or a rejection reason.
- Calculate invoice-line totals and select tax rates by category.

#### Responsibilities
- **Invoice issuance:** Validate that orders contain lines, assign invoice numbers and due dates, and save invoices.
- **Invoice calculations and policy:** Calculate line and list totals; apply reduced tax rates to configured categories and set a 14-day payment term.

#### Key units
- `InvoiceService` — issues invoices using an `InvoiceRepository` and an injectable date supplier.
- `InvoiceNumber` — value class wrapping an invoice-number string.
- `InvoiceLine` — represents an SKU, quantity, and unit price; exposes a calculated `amount`.
- `InvoiceResult` — sealed result type with `Issued` and `Rejected` variants.
- `InvoiceResult.Issued` — returns the invoice number and due date.
- `InvoiceResult.Rejected` — returns the reason issuance was rejected.
- `List<InvoiceLine>.total()` — sums line amounts.
- `PAYMENT_TERM_DAYS`, `REDUCED_TAX_CATEGORIES` — define the payment period and reduced-rate categories.

#### Data flow
- `issue(CompletedOrder)` rejects orders with no lines; otherwise generates an `InvoiceNumber`, builds an `Invoice` using the customer, lines, and clock-derived due date, saves it through `InvoiceRepository`, and returns `Issued`.
- `InvoiceLine.amount` multiplies unit price by quantity; `total()` folds line amounts from `BigDecimal.ZERO`.
- `taxRate(category)` returns `0.07` for `"books"` and `"food"`, otherwise `0.19`.

#### Dependencies
- Domain types: `CompletedOrder`, `Invoice`, `InvoiceRepository`.
- Kotlin coroutine dispatch: `Dispatchers.IO`, `withContext`.
- Date and monetary values: `LocalDate`, `BigDecimal`.

#### Cross-cutting
- `issue` runs on `Dispatchers.IO`; the mutable invoice-number sequence is unsynchronized and may race under concurrent calls.
- The date supplier defaults to `LocalDate::now`; `defaultCurrency` is mutable and `lateinit`.
- No explicit exception handling: repository-save and date-supplier failures may propagate; accessing `defaultCurrency` before initialization can fail.
