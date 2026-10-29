### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:05:07Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte histogram of an image or byte stream using a two-pass OpenCL reduction algorithm.

#### Purpose
- Calculates frequency distribution of byte values (0-255) in a data stream using parallel OpenCL kernels.
- Implements a two-pass reduction: local counting followed by global summation of partial histograms.

#### Type
- OpenCL C 1.2 kernel source file.
- Contains two `__kernel` functions: `histogram_partial` and `histogram_reduce`.

#### Input
- `input`: `__global const uchar *` pointer to source byte data.
- `length`: `uint` total number of bytes to process.
- `partial`: `__global uint *` buffer for storing per-group partial histograms.
- `group_count`: `uint` number of work-groups processed in Pass 1.
- `local_bins`: `__local uint *` local memory array for atomic accumulation.

#### Output
- `histogram`: `__global uint *` final 256-element array containing total bin counts.
- `partial`: `__global uint *` intermediate buffer populated by `histogram_partial`.
- Side effects: Updates global memory buffers with computed frequency data.

#### Core logic
- **Pass 1 (`histogram_partial`)**:
  - Initializes local memory `local_bins` to zero for all 256 bins.
  - Iterates through input data with stride `gsize` using atomic increments on `local_bins`.
  - Synchronizes work-items via `barrier(CLK_LOCAL_MEM_FENCE)`.
  - Writes local bin counts to global `partial` buffer at offset `group_id * BINS`.
- **Pass 2 (`histogram_reduce`)**:
  - Assigns one work-item per bin (0-255).
  - Iterates through `group_count` partial histograms to sum values for the specific bin.
  - Writes final sum to `histogram[bin]`.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void`
  - Computes per-group partial byte histograms.
- `histogram_reduce(partial, group_count, histogram) -> void`
  - Reduces partial histograms into a final global histogram.

#### Dependencies
- OpenCL C 1.2 standard library (`get_local_id`, `get_global_id`, `atomic_inc`, `barrier`).
- `BINS` constant defined as 256.

#### Exceptions / Errors
- No explicit exception handling; relies on OpenCL runtime for error propagation.
- `histogram_reduce` guards against out-of-bounds access via `if (bin >= BINS)`.
