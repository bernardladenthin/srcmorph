### other/jsp
- H: 1.0
- C: 8415F0DC
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:36:42Z
- G: 
- A: 0.0.0
- X: package
- F: [order-list.jsp](order-list.jsp.ai.md)
---
> Provides a JSP view for listing, searching, and canceling open orders in a shop application.

#### Purpose
- Render a searchable table of open orders.
- Offer a cancel link for each order.

#### Responsibilities
- **Order display** – iterate over `request.attribute.orders` to show order number, total, overdue flag, and cancel link.
- **Search form** – capture `customerEmail` and `status` parameters and submit to `/orders/search`.
- **Pagination support** – use `OrderFilter.pageSize` (default 25) to control list size.

#### Key units
- **`order-list.jsp`** – JSP page that builds the UI, uses JSTL core (`c`) and fmt (`fmt`) tags, and includes header/footer fragments.
- **`OrderFilter`** – bean providing `pageSize` and other filter criteria.
- **`Order`** – domain model with `number`, `total`, `overdue`, `id`.

#### Data flow
1. **Request** → `orderFilter` attribute → determines `pageSize`.
2. **Request parameters** (`customerEmail`, `status`) → submitted via search form to `/orders/search`.
3. **Controller** populates `orders` attribute → JSP iterates and renders each order.
4. **Cancel link** → `orders/{id}/cancel` triggers order cancellation.

#### Dependencies
- **Tag libraries**: `jakarta.tags.core` (`c`), `jakarta.tags.fmt` (`fmt`).
- **Included fragments**: `/WEB-INF/jspf/header.jspf`, `/WEB-INF/jspf/footer.jspf`.
- **Domain classes**: `shop.Order`, `shop.OrderFilter`.

#### Cross-cutting
- Uses JSTL for conditional rendering and iteration.
- Relies on standard JSP request attributes for data passing.
- Consistent use of context path in URLs for portability.
