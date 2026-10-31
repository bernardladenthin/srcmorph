### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:36:11Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte histogram of an image or byte stream on the GPU in two OpenCL kernel passes.

#### Purpose
- Two-pass parallel byte-frequency counting: per-work-group partial histograms, then global reduction.
- Targets image pixel data or any raw `uchar` buffer.

#### Type
- OpenCL C 1.2 source file (kernel file, `.cl`), containing two `__kernel` functions.
- Macro: `BINS` defined as 256.

#### Input
- `histogram_partial`:
  - `__global const uchar *input` byte stream to count.
  - `const uint length` number of bytes to process.
  - `__global uint *partial` output buffer sized for group_count * BINS counters.
  - `__local uint *local_bins` local memory scratch of BINS counters.
  - Work-item ids: `get_local_id(0)`, `get_local_size(0)`, `get_global_id(0)`, `get_global_size(0)`, `get_group_id(0)`.
- `histogram_reduce`:
  - `__global const uint *partial` partial histograms from pass 1.
  - `const uint group_count` number of work-groups that produced partials.
  - Work-item id `get_global_id(0)` selects bin.

#### Output
- `histogram_partial` writes BINS counters per work-group to `partial[group_id * BINS + bin]`.
- `histogram_reduce` writes `histogram[bin]` for each bin in 0..255.
- Side effects: local memory initialized, filled via atomics, copied out; barriers used with `CLK_LOCAL_MEM_FENCE`.

#### Core logic
- Pass 1 (`histogram_partial`):
  - Zero `local_bins` in strided loop over BINS by local size.
  - Barrier `CLK_LOCAL_MEM_FENCE`.
  - Grid-stride loop over input from global id with stride global size; `atomic_inc` on `local_bins[input[i]]`.
  - Barrier.
  - Copy local histogram to `partial` slice at `get_group_id(0) * BINS`, strided by local size.
- Pass 2 (`histogram_reduce`):
  - One work-item per bin; out-of-range ids return early.
  - Sum `partial[g * BINS + bin]` over all groups g.
  - Store total in `histogram[bin]`.
- Launch order: pass 1 first, pass 2 after with group_count equal to pass 1 work-group count.

#### Public API
- `histogram_partial(input: __global const uchar*, length: uint, partial: __global uint*, local_bins: __local uint*) -> void` per-group partial byte counts.
- `histogram_reduce(partial: __global const uint*, group_count: uint, histogram: __global uint*) -> void` sums partials into final 256-bin histogram.

#### Dependencies
- OpenCL C built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `CLK_LOCAL_MEM_FENCE`, `atomic_inc`.
- Types: `uchar`, `uint`.
- Macro: `BINS`.

#### Exceptions / Errors
- No explicit error handling; invalid `length` or undersized `partial`/`histogram` buffers cause out-of-bounds access.
- `histogram_partial` requires `local_bins` sized to at least BINS uints per work-group.
- `histogram_reduce` requires `group_count` to match the number of pass-1 work-groups; mismatch yields wrong sums.
- Barrier use requires all work-items in a group to reach both barriers.
