### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:48:45Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte histogram of an image or byte stream using a two-pass OpenCL kernel strategy.

#### Purpose
- Parallel histogram computation for `uchar` data streams (e.g., images) with 256 bins.
- Splits work into partial per-work-group counts followed by a global reduction.

#### Type
- OpenCL C 1.2 kernel file.
- Defines constant `BINS` as 256.

#### Input
- `histogram_partial`: `__global const uchar *input` (source bytes), `const uint length` (byte count), `__local uint *local_bins` (scratch memory).
- `histogram_reduce`: `__global const uint *partial` (intermediate counts), `const uint group_count` (number of work-groups).

#### Output
- `histogram_partial`: Writes `BINS` counts per work-group to `__global uint *partial`.
- `histogram_reduce`: Writes final 256-bin counts to `__global uint *histogram`.

#### Core logic
- **Pass 1 (`histogram_partial`)**:
  - Each work-group initializes `local_bins` to zero.
  - Strides through `input` using global ID; increments `local_bins[input[i]]` via `atomic_inc`.
  - Synchronizes with `barrier(CLK_LOCAL_MEM_FENCE)`.
  - Writes local counts to global `partial` array at offset `group_id * BINS`.
- **Pass 2 (`histogram_reduce`)**:
  - One work-item per bin (0–255).
  - Sums `partial[g * BINS + bin]` across all `group_count` work-groups.
  - Stores final sum in `histogram[bin]`.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` - Computes partial per-group counts.
- `histogram_reduce(partial, group_count, histogram) -> void` - Reduces partials to final histogram.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL types: `uchar`, `uint`, `__global`, `__local`.

#### Exceptions / Errors
- None explicitly handled; relies on correct `length` and `group_count` to avoid out-of-bounds access.
