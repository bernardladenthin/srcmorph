### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:44:32Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte frequency histogram from an image or byte stream using a two-pass OpenCL reduction.

#### Purpose
- Calculates a frequency distribution of byte values (0-255) from input data using parallel OpenCL kernels.
- Implements a two-pass reduction strategy: local counting followed by global summation of partial results.

#### Type
- OpenCL C 1.2 kernel source file.
- Contains two kernel functions: `histogram_partial` and `histogram_reduce`.

#### Input
- `input`: Global pointer to `uchar` data stream (image or byte array).
- `length`: Total number of bytes in the input stream.
- `partial`: Global buffer for storing per-group partial histograms.
- `group_count`: Number of work-groups processed in the first pass.
- `partial`: Global buffer containing partial histogram sums from the first pass.

#### Output
- `histogram`: Global buffer containing the final 256-bin frequency counts.
- `partial`: Intermediate global buffer populated by `histogram_partial`.
- `local_bins`: Local memory array used for atomic accumulation within work-groups.

#### Core logic
- **Pass 1 (`histogram_partial`)**:
  - Initializes local memory `local_bins` to zero for all 256 bins.
  - Each work-item iterates through its assigned slice of the input data.
  - Uses `atomic_inc` on `local_bins` indexed by input byte value to count frequencies.
  - Synchronizes work-group via `barrier`.
  - Writes the local `local_bins` array to the corresponding slice in the global `partial` buffer.
- **Pass 2 (`histogram_reduce`)**:
  - Assigns one work-item per bin (0 to 255).
  - Iterates through all `group_count` partial histogram entries for the specific bin.
  - Sums the partial values into a single total.
  - Writes the final sum to `histogram[bin]`.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void`: Counts byte frequencies per work-group into local memory and writes partial sums to global buffer.
- `histogram_reduce(partial, group_count, histogram) -> void`: Sums partial histograms across all groups to produce the final global histogram.

#### Dependencies
- OpenCL C 1.2 primitives: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- Constants: `BINS` (256).

#### Exceptions / Errors
- None explicitly thrown; relies on OpenCL runtime for error handling.
- Logic assumes `length` is non-negative and `group_count` matches the number of work-groups in Pass 1.
