### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T09:58:55Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte histogram of an image or byte stream using a two-pass OpenCL kernel strategy.

#### Purpose
- Implements a two-pass parallel histogram algorithm for counting byte frequencies in an input buffer.
- Separates partial counting (per work-group) from final reduction (per bin) to handle large data sets efficiently.

#### Type
- OpenCL C 1.2 kernel file (`.cl`)
- Contains two `__kernel` functions: `histogram_partial` and `histogram_reduce`

#### Input
- `histogram_partial`:
  - `input`: `__global const uchar *` – source byte stream
  - `length`: `uint` – total number of bytes to process
  - `partial`: `__global uint *` – output buffer for per-group partial histograms
  - `local_bins`: `__local uint *` – shared memory for intra-group accumulation
- `histogram_reduce`:
  - `partial`: `__global const uint *` – array of partial histograms from pass 1
  - `group_count`: `uint` – number of work-groups that contributed to `partial`
  - `histogram`: `__global uint *` – final output histogram buffer

#### Output
- `histogram_partial`: Writes `BINS` (256) `uint` counters per work-group into `partial[group_id * BINS + bin]`
- `histogram_reduce`: Writes final 256-bin histogram into `histogram[bin]`

#### Core logic
- **Pass 1 (`histogram_partial`)**:
  - Each work-group initializes its `local_bins` to zero using a strided loop over `BINS`.
  - Each work-item atomically increments `local_bins[input[i]]` for its assigned slice of `input` (strided by `gsize`).
  - After a barrier, each work-group writes its 256 local counters to the corresponding slice of `partial` in global memory.
- **Pass 2 (`histogram_reduce`)**:
  - One work-item per bin (max 256 work-items).
  - Each work-item sums the `bin`-th counter across all `group_count` partial histograms.
  - Writes the final sum to `histogram[bin]`.

#### Public API
- `histogram_partial(__global const uchar *input, const uint length, __global uint *partial, __local uint *local_bins) -> void` – Computes per-group partial byte histogram
- `histogram_reduce(__global const uint *partial, const uint group_count, __global uint *histogram) -> void` – Reduces partial histograms into final result

#### Dependencies
- OpenCL C 1.2 runtime
- Built-in functions: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`
- Constant: `BINS` = 256

#### Exceptions / Errors
- No explicit error handling; relies on correct launch configuration (work-group size, global size, buffer sizes)
- `histogram_reduce` early-returns if `bin >= BINS` (guards against oversized global work size)
