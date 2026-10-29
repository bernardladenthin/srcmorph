### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:57:23Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte histogram of an image or byte stream using a two-pass OpenCL reduction algorithm.

#### Purpose
- Calculates frequency distribution of byte values (0-255) in a data stream via parallel reduction.
- Implements a two-pass kernel strategy: local counting followed by global summation.

#### Type
- OpenCL C 1.2 kernel functions.
- `histogram_partial`: Work-group based local counting kernel.
- `histogram_reduce`: Global reduction kernel summing partial results.

#### Input
- `input`: `__global const uchar *` pointer to source byte data.
- `length`: `uint` total number of bytes to process.
- `partial`: `__global uint *` buffer to store per-group partial histograms.
- `group_count`: `uint` number of work-groups processed in Pass 1.
- `local_bins`: `__local uint *` local memory array for atomic accumulation.

#### Output
- `histogram`: `__global uint *` final 256-bin frequency array.
- `partial`: `__global uint *` intermediate array containing `group_count * 256` partial sums.

#### Core logic
- **Pass 1 (`histogram_partial`)**:
  - Initializes local memory `local_bins` to zero for all 256 bins.
  - Iterates through input data with stride `gsize`, performing `atomic_inc` on `local_bins` indexed by byte value.
  - Synchronizes via `CLK_LOCAL_MEM_FENCE`.
  - Writes local bin counts to global `partial` buffer at offset `group_id * BINS`.
- **Pass 2 (`histogram_reduce`)**:
  - Assigns one work-item per bin (0-255).
  - Iterates through `group_count` partial histograms.
  - Sums values at `partial[g * BINS + bin]` for the specific bin.
  - Writes final sum to `histogram[bin]`.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void`
  - Counts byte frequencies into local memory and writes partials.
- `histogram_reduce(partial, group_count, histogram) -> void`
  - Sums partial histograms to produce final bin counts.

#### Dependencies
- OpenCL C 1.2 built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`.
- Constants: `BINS` (256).

#### Exceptions / Errors
- None explicitly thrown; logic relies on valid `length` and `group_count` parameters.
- `bin >= BINS` check in `histogram_reduce` prevents out-of-bounds access.
