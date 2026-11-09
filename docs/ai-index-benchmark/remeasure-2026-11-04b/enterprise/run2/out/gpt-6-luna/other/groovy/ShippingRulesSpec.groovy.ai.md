### ShippingRulesSpec.groovy
- H: 1.0
- C: 77E70BE5
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T12:29:14Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
- N: FA8232C2
---
**Facts (exact, whole file):** type declarations: 2; methods (approx): 2; Spock feature methods: 3; Spock blocks: 7; closures with parameters (approx): 1; static type checking: 1; imports: 3

> Calculates parcel shipping charges by weight, waives shipping above an order-value threshold, and rejects parcels above the carrier’s weight limit.

#### Purpose
- `ShippingRulesSpec` verifies free shipping, weight-tier pricing, and overweight rejection.
- `ShippingRules` calculates shipping costs for `Order` instances.

#### Type
- `ShippingRulesSpec` — class; extends `spock.lang.Specification`.
- `ShippingRules` — class; annotated `@CompileStatic`; no declared supertypes.

#### Input
- `ShippingRules.carrier: String` and `ShippingRules.maxParcelKg: BigDecimal` configure the carrier and maximum parcel weight.
- `ShippingRules.costFor(Order order)` consumes an order’s `weightKg` and `total`.
- `ShippingRulesSpec` initializes `rules` with `carrier: 'DHL'` and `maxParcelKg: 31.5`.
- `ShippingRulesSpec` test data includes order totals and weights; the data-driven test supplies weights `1`, `10`, and `30`.

#### Output
- `costFor(Order)` returns a `BigDecimal` shipping charge, or throws `ParcelTooHeavyException`.
- `ShippingRulesSpec` produces pass/fail results through Spock assertions and exception expectations.

#### Core logic
- `ShippingRules.costFor(Order order)`:
  - Throws `ParcelTooHeavyException` with the parcel weight and maximum weight if `order.weightKg > maxParcelKg`.
  - Returns `0` if `order.total >= ShippingRulesSpec.FREE_SHIPPING_THRESHOLD`.
  - Otherwise finds the first matching entry in local `tiers` and returns its `price` as `BigDecimal`: up to `2` kg costs `4.99`, up to `10` kg costs `9.99`, and up to `31.5` kg costs `19.99`.
- `ShippingRulesSpec` checks that a `75.00` order ships free; `@Unroll` tests the three weight/price pairs; the overweight test expects `ParcelTooHeavyException`.
- Private helper `heavyOrder(BigDecimal kg)` constructs an `Order(total: 10, weightKg: kg)`; it is not called by the shown tests.
- `FREE_SHIPPING_THRESHOLD` is `50.00`.

#### Public API
- `FREE_SHIPPING_THRESHOLD: BigDecimal` — free-shipping order total.
- `getFREE_SHIPPING_THRESHOLD()` -> `BigDecimal` — reads threshold.
- `getRules()`/`setRules(Object)` -> `rules` property.
- `"orders above the threshold ship for free"()` -> `def` — verifies free shipping.
- `"a parcel of #weight kg costs #expected"()` -> `def` — verifies weight-tier prices.
- `"an overweight parcel is rejected"()` -> `def` — verifies overweight exception.
- `getCarrier()`/`setCarrier(String)` -> `carrier` property.
- `getMaxParcelKg()`/`setMaxParcelKg(BigDecimal)` -> `maxParcelKg` property.
- `costFor(Order)` -> `BigDecimal` — calculates shipping charge.

#### Dependencies
- Imports: `groovy.transform.CompileStatic`, `spock.lang.Specification`, `spock.lang.Unroll`.
- Referenced types: `Order`, `ParcelTooHeavyException`, `BigDecimal`.

#### Exceptions / Errors
- `costFor` throws `ParcelTooHeavyException` when parcel weight exceeds `maxParcelKg`.
- No explicit null handling or caught exceptions.

#### Concurrency
- No concurrency or synchronization behavior shown.
