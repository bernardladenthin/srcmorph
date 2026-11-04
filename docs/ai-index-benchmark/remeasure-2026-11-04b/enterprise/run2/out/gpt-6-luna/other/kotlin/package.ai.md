### other/kotlin
- H: 1.0
- C: 284EE5DF
- D: 2026-11-04T06:47:08Z
- T: 2026-11-04T12:31:26Z
- G: 
- A: 0.0.0
- X: package
- F: [InvoiceService.kt](InvoiceService.kt.ai.md)
---
> Completed orders with invoice lines are assigned numbered invoices with payment deadlines, tax rates, and persistent storage.

#### Purpose
- Issue and store invoices for completed orders, rejecting orders with no lines.
- Calculate invoice-line totals and select reduced or standard tax rates by category.

#### Responsibilities
- Invoice creation: number eligible orders, set a 14-day payment deadline, and save the invoice.
- Invoice calculations and policy: compute line amounts and totals, and choose tax rates for configured categories.

#### Key units
- `InvoiceService` — issues invoices through `InvoiceRepository` and selects tax rates.
- `InvoiceNumber` — value type wrapping an invoice number string.
- `InvoiceLine` — stores SKU, quantity, and unit price; exposes its calculated amount.
- `InvoiceResult` — sealed result with `Issued` (number and due date) and `Rejected` (reason) variants.
- `List<InvoiceLine>.total()` — sums line amounts.
- `PAYMENT_TERM_DAYS` — configures the payment term; `REDUCED_TAX_CATEGORIES` — identifies reduced-rate categories (`books`, `food`).
- `defaultCurrency` — mutable `lateinit` property; its use is not shown.

#### Data flow
- `issue(order)` rejects orders with no lines; otherwise it generates an `INV` number, builds an invoice from the order’s customer and lines with a clock-based due date, saves it, and returns `Issued`.
- `InvoiceLine.amount` multiplies unit price by quantity; `total()` adds line amounts from `BigDecimal.ZERO`.
- `taxRate(category)` returns `0.07` for reduced-tax categories and `0.19` otherwise.

#### Dependencies
- `InvoiceService` collaborates with `InvoiceRepository` and consumes `CompletedOrder`, creating an `Invoice` from `shop.billing`.
- Uses `BigDecimal`, `LocalDate`, and Kotlin coroutines (`Dispatchers.IO`, `withContext`).

#### Cross-cutting
- `issue` runs on `Dispatchers.IO`; the injected clock defaults to `LocalDate::now`.
- Invoice sequence mutation is unsynchronized and may race across concurrent calls.
- Empty orders produce `InvoiceResult.Rejected`; accessing `defaultCurrency` before initialization may fail.
