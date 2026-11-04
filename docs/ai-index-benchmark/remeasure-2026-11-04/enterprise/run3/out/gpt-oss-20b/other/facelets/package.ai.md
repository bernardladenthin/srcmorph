### other/facelets
- H: 1.0
- C: 2CC30F84
- D: 2026-11-04T06:55:10Z
- T: 2026-11-04T08:24:19Z
- G: 
- A: 0.0.0
- X: package
- F: [checkout.xhtml](checkout.xhtml.ai.md)
---
> A JSF checkout page that gathers shipping details, lets the user pick a shipping method, shows the order total, and submits the order.

#### Purpose
- Capture shipping address and method.
- Display calculated order total.
- Submit the order to the server.

#### Responsibilities
- **User input collection** – shipping address fields, shipping method selector.  
- **Order calculation** – recalculate total when shipping changes.  
- **Order submission** – invoke `checkoutBean.placeOrder`.  
- **UI composition** – embed payment hints and use a common shop layout.

#### Key units
- **`checkout.xhtml`** – Facelets page, main UI.  
- **`checkoutBean`** – backing bean (not shown) providing `address`, `shippingMethod`, `shippingMethods`, `total`, `recalculate()`, `placeOrder()`.  
- **`/WEB-INF/templates/shop.xhtml`** – master template providing the page skeleton.  
- **`/WEB-INF/includes/payment-hints.xhtml`** – reusable fragment with payment guidance.

#### Data flow
1. User enters `address.street`, `address.zip` (validated by `[0-9]{5}`) and selects `shippingMethod`.  
2. Changing `shippingMethod` triggers `checkoutBean.recalculate()` via Ajax, updating the `total` component.  
3. Clicking the submit button calls `checkoutBean.placeOrder()`, sending the collected data to the server.

#### Dependencies
- **Tag libraries**: `jakarta.faces.html`, `jakarta.faces.core`, `jakarta.faces.facelets`.  
- **Template**: `/WEB-INF/templates/shop.xhtml`.  
- **Include**: `/WEB-INF/includes/payment-hints.xhtml`.  
- **Backing bean**: `checkoutBean` (assumed to be a CDI/JSF managed bean).

#### Cross-cutting
- **EL expressions** used throughout for data binding and method calls.  
- **Ajax** employed for dynamic total recalculation.  
- **Validation** applied to ZIP code via regex.  
- **Currency formatting** applied to `total` (EUR).
