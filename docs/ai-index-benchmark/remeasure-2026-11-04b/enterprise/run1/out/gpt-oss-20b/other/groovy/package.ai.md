### other/groovy
- H: 1.0
- C: 1C9C7B86
- D: 2026-11-04T06:47:09Z
- T: 2026-11-04T12:27:14Z
- G: 
- A: 0.0.0
- X: package
- F: [ShippingRulesSpec.groovy](ShippingRulesSpec.groovy.ai.md)
---
> Calculates shipping cost for orders, applying free‑shipping thresholds and tiered rates while rejecting parcels that exceed a maximum weight.

#### Purpose
- Provide a shipping cost engine that determines fees based on order weight and total.
- Offer unit tests validating the shipping logic.

#### Responsibilities
- **Shipping cost calculation** – determine fee or free shipping, enforce weight limits.
- **Testing** – verify free‑shipping threshold, tiered pricing, and overweight rejection.

#### Key units
- **`ShippingRules`** – statically compiled engine; constructor takes `carrier` and `maxParcelKg`; method `costFor(Order)` returns `BigDecimal` or throws `ParcelTooHeavyException`.
- **`ShippingRulesSpec`** – Spock specification testing `ShippingRules`; contains parameterized tests for tiered pricing, free‑shipping, and overweight handling.

#### Data flow
1. `ShippingRulesSpec` creates a `ShippingRules` instance with a carrier and `maxParcelKg`.
2. For each test order, `ShippingRules.costFor(order)` is invoked.
3. The method checks weight against `maxParcelKg`; if exceeded, throws `ParcelTooHeavyException`.
4. If the order total meets or exceeds `FREE_SHIPPING_THRESHOLD`, returns `0`.
5. Otherwise, selects the first tier where `order.weightKg <= tier.upTo` and returns the tier’s price.

#### Dependencies
- **Groovy**: `@CompileStatic` for type safety.
- **Spock**: `Specification`, `Unroll` for testing.
- **External types**: `Order`, `ParcelTooHeavyException`.

#### Cross-cutting
- No concurrency; all operations are synchronous and stateless.
- Uses Groovy’s null‑safe calls; no explicit null checks.
- Exception handling is limited to `ParcelTooHeavyException` for overweight parcels.
