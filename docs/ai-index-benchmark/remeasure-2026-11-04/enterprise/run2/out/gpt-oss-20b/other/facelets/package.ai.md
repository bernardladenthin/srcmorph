### other/facelets
- H: 1.0
- C: 2CC30F84
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:18:14Z
- G: 
- A: 0.0.0
- X: package
- F: [checkout.xhtml](checkout.xhtml.ai.md)
---
> A JSF‑based checkout page that gathers shipping details, lets the user pick a shipping method, shows the order total, and submits the order.

#### Purpose
- Capture shipping address and method.
- Display calculated total.
- Submit order to backend.

#### Responsibilities
- **UI rendering** – Facelets page `checkout.xhtml` extends `/WEB-INF/templates/shop.xhtml`.
- **Form handling** – `<h:form>` with input fields for address and shipping method.
- **Dynamic total** – AJAX listener `checkoutBean.recalculate` updates total when shipping method changes.
- **Order submission** – `<h:commandButton>` triggers `checkoutBean.placeOrder`.

#### Key units
- **`checkout.xhtml`** – Facelets page, template section `content`, includes payment hints.
- **`checkoutBean`** – Managed bean with:
  - `address` (street, zip with `[0-9]{5}` validation)
  - `shippingMethod` (selected)
  - `shippingMethods` (select items)
  - `total` (currency EUR)
  - `recalculate()` – updates total on shipping change.
  - `placeOrder()` – processes order submission.

#### Data flow
1. User enters address → bound to `checkoutBean.address`.
2. User selects shipping method → bound to `checkoutBean.shippingMethod`.
3. AJAX listener `recalculate()` runs → updates `checkoutBean.total`.
4. Total displayed in UI.
5. User clicks “Place Order” → `checkoutBean.placeOrder()` processes order.

#### Dependencies
- **Tag libraries**: `jakarta.faces.html`, `jakarta.faces.core`, `jakarta.faces.facelets`.
- **Template**: `/WEB-INF/templates/shop.xhtml`.
- **Include**: `/WEB-INF/includes/payment-hints.xhtml`.
- **Managed bean**: `checkoutBean`.

#### Cross-cutting
- EL expressions for data binding and method calls.
- Validation via regex on ZIP field.
- Currency conversion for total display.
