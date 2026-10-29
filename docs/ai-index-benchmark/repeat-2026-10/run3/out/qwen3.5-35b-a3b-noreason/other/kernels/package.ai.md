### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:45:19Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes 256-bin byte frequency histograms from image or byte streams using a two-pass OpenCL reduction strategy.

#### Purpose
- Calculates frequency distributions of byte values (0-255) from input data via parallel OpenCL kernels.
- Implements a two-pass reduction: local per-group counting followed by global summation of partial results.

#### Responsibilities
- **Local Counting**: Aggregates byte frequencies within work-groups using atomic operations on local memory.
- **Global Reduction**: Sums partial histogram results across all work-groups to produce the final distribution.
- **Memory Management**: Coordinates between global input buffers, local work-group memory, and intermediate global partial buffers.

#### Key units
- `histogram_partial`: Kernel that initializes local memory, counts byte frequencies via `atomic_inc`, and writes per-group partials to a global buffer.
- `histogram_reduce`: Kernel that assigns one work-item per bin to sum partial histogram entries into the final output.
- `local_bins`: Local memory array used for atomic accumulation of 256 bins within a work-group.
- `partial`: Global buffer storing intermediate per-group histogram sums passed between kernel passes.
- `histogram`: Global output buffer containing the final 256-bin frequency counts.

#### Data flow
1. **Input**: `input` byte stream and `length` are read by `histogram_partial`.
2. **Pass 1**: Work-items iterate input slices, increment `local_bins` via `atomic_inc`, synchronize via `barrier`, and write `local_bins` to `partial`.
3. **Pass 2**: `histogram_reduce` reads `partial` and `group_count`, sums values for each bin index, and writes results to `histogram`.

#### Dependencies
- **OpenCL C 1.2 Primitives**: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- **Constants**: `BINS` (256) defining the histogram bin count.

#### Cross-cutting
- **Atomic Operations**: Relies on `atomic_inc` for thread-safe local counting within work-groups.
- **Synchronization**: Uses `barrier` to ensure all work-items complete local counting before writing to global memory.
- **Error Handling**: No explicit exceptions; relies on OpenCL runtime for validation of `length` and `group_count` consistency.
