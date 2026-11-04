### other/jsp
- H: 1.0
- C: 8415F0DC
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:27:28Z
- G: 
- A: 0.0.0
- X: package
- F: [order-list.jsp](order-list.jsp.ai.md)
---
> A JSP page that lists open orders, lets users filter by customer email or status, and cancel individual orders.

#### Purpose
- Render a searchable list of open orders.
- Provide a cancel link for each order.

#### Responsibilities
- **Order filtering** – read `customerEmail` and `status` from request, display them in a search form.
- **Order display** – iterate over `orders` (a collection of `shop.Order`) and show details.
- **Order cancellation** – generate a cancel URL for each order.

#### Key units
- **`order-list.jsp`** – main view page; uses JSTL core (`c`) and fmt tags.
- **`OrderFilter`** – request attribute holding filter criteria and pagination size.
- **`shop.Order`** – domain object representing an order.

#### Data flow
1. **Request** → `order-list.jsp` receives `orderFilter` and `orders` attributes.  
2. **Search form** submits to `/orders/search` with `customerEmail` and `status`.  
3. **Cancel link** points to `/orders/{id}/cancel` for each order.

#### Dependencies
- JSTL core (`jakarta.tags.core`) and fmt (`jakarta.tags.fmt`) libraries.
- Header/footer fragments: `/WEB-INF/jspf/header.jspf`, `/WEB-INF/jspf/footer.jspf`.
- Domain classes: `shop.Order`, `shop.OrderFilter`.

#### Cross-cutting
- Uses EL expressions for dynamic values (`${order.id}`, `${filter.pageSize}`, etc.).
- Standard JSP scriptlet for importing Java classes and retrieving the filter object.
- Consistent page layout via included header/footer fragments.
