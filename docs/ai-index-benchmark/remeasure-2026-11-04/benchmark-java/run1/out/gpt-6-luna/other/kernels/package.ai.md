### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:13:31Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes byte-value frequency distributions for image or byte-stream data using parallel OpenCL histogram kernels.

#### Purpose
- Builds a 256-bin histogram in two passes, first producing per-work-group counts and then reducing them into final counts.

#### Responsibilities
- **Partial histogram:** Count input bytes into work-group-local bins and write each group’s 256 counters to a partial buffer.
- **Reduction:** Sum each bin across work-group partial histograms.

#### Key units
- `histogram_partial` — OpenCL kernel that counts byte values into per-work-group histograms.
- `histogram_reduce` — OpenCL kernel that combines partial counts into the final `histogram`.
- `BINS` — Defines the 256 byte-value bins.

#### Data flow
- `input` bytes and `length` feed `histogram_partial`; work-groups accumulate counts in `local_bins` and write to `partial`.
- `histogram_reduce` reads `partial` and `group_count`, then writes the combined counts to `histogram`.

#### Dependencies
- OpenCL C 1.2 types, built-ins, barriers, atomic increments, and local-memory fence.

#### Cross-cutting
- Uses local-memory accumulation with atomic increments, followed by a separate reduction pass.
- Provides no explicit error handling or status codes.
