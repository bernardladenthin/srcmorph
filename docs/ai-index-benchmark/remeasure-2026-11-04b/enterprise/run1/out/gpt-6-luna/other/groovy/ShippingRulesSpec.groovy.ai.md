### ShippingRulesSpec.groovy
- H: 1.0
- C: 77E70BE5
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T12:22:45Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
- N: FA8232C2
---
**Facts (exact, whole file):** type declarations: 2; methods (approx): 2; Spock feature methods: 3; Spock blocks: 7; closures with parameters (approx): 1; static type checking: 1; imports: 3

> Calculates shipping charges from order value and parcel weight, including free delivery, weight-based rates, and rejection of parcels above the configured limit.

#### Purpose
- `ShippingRulesSpec` verifies free shipping, weight-tier pricing, and rejection of overweight orders.
- `ShippingRules` calculates an order’s shipping cost using a carrier and maximum parcel weight.

#### Type
- `ShippingRulesSpec` — class; extends `Specification`.
- `ShippingRules` — class; annotated `@CompileStatic`.

#### Input
- `ShippingRules.carrier`: carrier name; `ShippingRules.maxParcelKg`: maximum parcel weight.
- `ShippingRules.costFor(Order order)`: consumes an `Order` with `weightKg` and `total`.
- `ShippingRulesSpec` initializes rules with carrier `'DHL'` and maximum weight `31.5`; test orders use varying totals and weights.

#### Output
- `ShippingRules.costFor` returns a `BigDecimal` shipping cost.
- Orders at or above `ShippingRulesSpec.FREE_SHIPPING_THRESHOLD` receive cost `0`; other orders receive a weight-tier price.
- `costFor` throws `ParcelTooHeavyException` when the order exceeds `maxParcelKg`.

#### Core logic
- `costFor` first compares `order.weightKg` with `maxParcelKg`; if heavier, throws `ParcelTooHeavyException` with a message containing actual and maximum weights.
- If `order.total >= ShippingRulesSpec.FREE_SHIPPING_THRESHOLD`, returns `0`.
- Otherwise defines `tiers` at 2 kg (`4.99`), 10 kg (`9.99`), and 31.5 kg (`19.99`); selects the first tier where `order.weightKg <= tier.upTo` and casts its `price` to `BigDecimal`.
- `ShippingRulesSpec` checks an order totaling `75.00` ships free; `@Unroll` tests weights 1, 10, and 30 against their expected prices; verifies a 40 kg order throws `ParcelTooHeavyException`.
- Private helper `heavyOrder(BigDecimal kg)` constructs an `Order` totaling 10; it is not used by the shown tests.

#### Public API
- `ShippingRulesSpec.FREE_SHIPPING_THRESHOLD: BigDecimal` — free-shipping minimum (`50.00`).
- `getRules()`/`setRules(Object)` — rules fixture property.
- `"orders above the threshold ship for free"()` -> `void` — verifies free shipping.
- `"a parcel of #weight kg costs #expected"()` -> `void` — verifies weight-tier prices; unrolled.
- `"an overweight parcel is rejected"()` -> `void` — verifies overweight rejection.
- `getCarrier()`/`setCarrier(String)` — carrier property.
- `getMaxParcelKg()`/`setMaxParcelKg(BigDecimal)` — maximum parcel weight.
- `costFor(Order order)` -> `BigDecimal` — calculate shipping charge.

#### Dependencies
- `groovy.transform.CompileStatic`
- `spock.lang.Specification`
- `spock.lang.Unroll`
- `Order`, `ParcelTooHeavyException` (`shop.shipping` package)

#### Exceptions / Errors
- Throws `ParcelTooHeavyException` when `order.weightKg > maxParcelKg`.
- If a parcel passes the configured maximum but exceeds every hard-coded tier, `tiers.find { ... }` can return `null` before `.price` access.
