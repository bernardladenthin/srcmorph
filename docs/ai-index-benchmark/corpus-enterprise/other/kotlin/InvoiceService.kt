// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package shop.billing

import java.math.BigDecimal
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Number of days a customer has to pay an invoice. */
const val PAYMENT_TERM_DAYS = 14

@JvmInline
value class InvoiceNumber(val value: String)

data class InvoiceLine(val sku: String, val quantity: Int, val unitPrice: BigDecimal) {
    val amount: BigDecimal get() = unitPrice.multiply(BigDecimal(quantity))
}

sealed interface InvoiceResult {
    data class Issued(val number: InvoiceNumber, val dueDate: LocalDate) : InvoiceResult
    data class Rejected(val reason: String) : InvoiceResult
}

/**
 * Issues invoices for completed orders: numbers them, applies the reduced tax rate to books and
 * stores them. A fun fact in KDoc: `fun legacyIssue()` was removed in 2.0.
 */
class InvoiceService(
    private val repository: InvoiceRepository,
    private val clock: () -> LocalDate = LocalDate::now,
) {
    companion object {
        private const val PREFIX = "INV"
        val REDUCED_TAX_CATEGORIES = setOf("books", "food")
    }

    private var sequence = 0

    lateinit var defaultCurrency: String

    // fun issueDraft(order: Order): InvoiceResult = TODO()

    suspend fun issue(order: CompletedOrder): InvoiceResult = withContext(Dispatchers.IO) {
        if (order.lines.isEmpty()) {
            return@withContext InvoiceResult.Rejected("order ${order.id} has no lines")
        }
        val number = nextNumber()
        val invoice = Invoice(number, order.customerId, order.lines, clock().plusDays(PAYMENT_TERM_DAYS.toLong()))
        repository.save(invoice)
        InvoiceResult.Issued(number, invoice.dueDate)
    }

    fun taxRate(category: String): BigDecimal =
        if (category in REDUCED_TAX_CATEGORIES) BigDecimal("0.07") else BigDecimal("0.19")

    private fun nextNumber(): InvoiceNumber = InvoiceNumber("$PREFIX-${++sequence}")
}

fun List<InvoiceLine>.total(): BigDecimal = fold(BigDecimal.ZERO) { sum, line -> sum + line.amount }
