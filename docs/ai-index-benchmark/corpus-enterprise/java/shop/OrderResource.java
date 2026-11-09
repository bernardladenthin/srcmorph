// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package shop;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ejb.Schedule;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 * REST resource of the order backoffice: lists open orders, looks one up and cancels it. The
 * nightly purge removes cancelled orders older than {@link #PURGE_AFTER_DAYS} days.
 *
 * <p>An older version used {@code @Stateful} and kept a cart; that moved to the storefront.</p>
 */
@Stateless
@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"backoffice", "admin"})
public class OrderResource {

    /** Days a cancelled order is kept before the nightly purge deletes it. */
    static final int PURGE_AFTER_DAYS = 30;

    @PersistenceContext(unitName = "shop")
    private EntityManager em;

    @Inject
    private PaymentGateway payments;

    @EJB
    private AuditLog audit;

    // @Inject private LegacyCartService carts;

    @GET
    public List<Order> openOrders() {
        return em.createNamedQuery("Order.open", Order.class).getResultList();
    }

    @GET
    @Path("{id}")
    public Response find(@PathParam("id") long id) {
        final Order order = em.find(Order.class, id);
        return order == null ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(order).build();
    }

    @POST
    @Path("{id}/cancel")
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Response cancel(@PathParam("id") long id) {
        final Order order = em.find(Order.class, id);
        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        payments.refund(order.getPaymentReference(), order.getTotal());
        order.setStatus(OrderStatus.CANCELLED);
        audit.record("order.cancelled", id);
        return Response.noContent().build();
    }

    @Schedule(hour = "2", minute = "15", persistent = false)
    void purgeCancelled() {
        final int removed = em.createNamedQuery("Order.purgeCancelled")
                .setParameter("days", PURGE_AFTER_DAYS)
                .executeUpdate();
        audit.record("order.purged", removed);
    }
}
