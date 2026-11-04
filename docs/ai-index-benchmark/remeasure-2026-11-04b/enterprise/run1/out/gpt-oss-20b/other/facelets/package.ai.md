### other/facelets
- H: 1.0
- C: 2CC30F84
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T12:27:12Z
- G: 
- A: 0.0.0
- X: package
- F: [checkout.xhtml](checkout.xhtml.ai.md)
---
> A web‑frontend page that gathers a customer’s shipping details, lets them pick a shipping method, shows the order total, and submits the order.

#### Purpose
- Capture shipping address and method.
- Compute and display order total.
- Submit the order to the backend.

#### Responsibilities
- **User input collection** – shipping address fields, shipping method selector.
- **Dynamic total calculation** – AJAX listener updates total when shipping changes.
- **Order submission** – form posts to `checkoutBean.placeOrder`.

#### Key units
- **`checkout.xhtml`** – Facelets page, template `"/WEB-INF/templates/shop.xhtml"`, defines the checkout form and UI.
- **`checkoutBean`** – Managed bean backing the page; properties: `address.street`, `address.zip`, `shippingMethod`, `shippingMethods`, `total`; methods: `recalculate()`, `placeOrder()`.

#### Data flow
1. User enters `address.street` and `address.zip` (validated with regex).
2. User selects `shippingMethod` from `shippingMethods` list.
3. Changing `shippingMethod` triggers `<f:ajax listener="#{checkoutBean.recalculate}" render="total"/>`, updating `checkoutBean.total`.
4. User clicks the submit button, invoking `checkoutBean.placeOrder()` to process the order.

#### Dependencies
- Jakarta Faces tag libraries: `jakarta.faces.html`, `jakarta.faces.core`, `jakarta.faces.facelets`.
- Includes `/WEB-INF/includes/payment-hints.xhtml` for payment guidance.
- Relies on the `checkoutBean` managed bean for state and business logic.

#### Cross-cutting
- Uses EL expressions for binding and method calls.
- AJAX partial rendering for real‑time total updates.
- Validation via EL regex on `address.zip`.
