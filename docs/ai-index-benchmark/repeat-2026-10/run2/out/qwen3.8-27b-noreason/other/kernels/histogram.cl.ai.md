### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:08:47Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte histogram for an image or byte stream using a two-pass OpenCL kernel strategy.

#### Purpose
- Parallel histogram computation for `uchar` data streams.
- Two-pass algorithm: partial per-group counting followed by global reduction.

#### Type
- OpenCL C 1.2 kernel file.
- Defines constant `BINS` as 256.

#### Input
- `input`: `__global const uchar *` source byte array.
- `length`: `uint` total number of bytes to process.
- `partial`: `__global uint *` intermediate buffer for per-group counts.
- `local_bins`: `__local uint *` shared memory for intra-group accumulation.
- `group_count`: `uint` number of work-groups in the reduction pass.

#### Output
- `partial`: Written in Pass 1 with 256 counters per work-group.
- `histogram`: `__global uint *` final 256-bin histogram written in Pass 2.

#### Core logic
- **Pass 1 (`histogram_partial`)**:
  - Each work-group initializes `local_bins` to zero using local ID stride.
  - Synchronizes with `barrier(CLK_LOCAL_MEM_FENCE)`.
  - Each work-item iterates over global input indices with global stride.
  - Uses `atomic_inc` on `local_bins[input[i]]` to count byte values.
  - Synchronizes again, then writes 256 local counts to `partial[group_id * BINS + bin]`.
- **Pass 2 (`histogram_reduce`)**:
  - One work-item per bin (global ID maps to bin index).
  - Early return if `bin >= BINS`.
  - Sums `partial[g * BINS + bin]` across all `group_count` work-groups.
  - Writes final sum to `histogram[bin]`.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` - Computes per-group partial histograms.
- `histogram_reduce(partial, group_count, histogram) -> void` - Reduces partials into final histogram.

#### Dependencies
- OpenCL C 1.2 built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- Data types: `uchar`, `uint`, `__global`, `__local`.

#### Exceptions / Errors
- None explicitly handled; relies on correct buffer sizing and work-group dimensions.
