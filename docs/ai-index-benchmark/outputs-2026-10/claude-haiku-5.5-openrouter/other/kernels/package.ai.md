### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:37:06Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Two-pass OpenCL GPU kernels that count byte frequencies into a 256-bin histogram for image or raw byte data.

#### Purpose
- Parallel byte-frequency counting: per-work-group partial histograms followed by a global reduction.
- Targets image pixel data or any raw `uchar` buffer.

#### Responsibilities
- Partial counting: `histogram_partial` builds a 256-bin histogram per work-group in local memory and writes it to a partial buffer.
- Final reduction: `histogram_reduce` sums per-group partials into the final `histogram[bin]` output, one work-item per bin.
- Constants: macro `BINS` = 256 defines the bin count shared by both passes.

#### Key units
- `histogram_partial`: `__kernel` pass 1; grid-stride atomic counting of `uchar` input into `__local uint *local_bins`, then copy to `partial[group_id * BINS + bin]`.
- `histogram_reduce`: `__kernel` pass 2; sums `partial[g * BINS + bin]` over `group_count` groups into `histogram[bin]`.
- `BINS`: macro, 256 bins.

#### Data flow
- Input bytes (`__global const uchar *input`, `length`) → per-group local histogram via `atomic_inc` → `partial` buffer (BINS counters per group) → per-bin sum across groups → `histogram` (BINS uints).
- Launch order: pass 1 first, then pass 2 with `group_count` equal to pass 1 work-group count.

#### Dependencies
- OpenCL C built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `CLK_LOCAL_MEM_FENCE`, `atomic_inc`.
- Types: `uchar`, `uint`.
- Host side (not in this package): buffer allocation and kernel launch sequencing.

#### Cross-cutting
- Concurrency: work-group barriers (`CLK_LOCAL_MEM_FENCE`) separate local-memory zeroing, counting, and copy-out; all work-items must reach both barriers.
- Atomics: `atomic_inc` on local bins for in-group contention.
- Error handling: none in-kernel; out-of-range ids in pass 2 return early, but undersized buffers, bad `length`, or a `group_count` mismatch cause out-of-bounds access or wrong sums.
- Memory sizing contract: `local_bins` needs at least BINS uints per group; `partial` needs group_count × BINS counters.
