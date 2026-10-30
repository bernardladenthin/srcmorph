### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:48:23Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes byte-frequency histograms for byte streams or image regions in OpenCL, aggregating per-group counts into a final 256-bin distribution.

#### Purpose
- Counts each byte value from a source buffer into 256 bins for a byte stream or image region.
- Produces per-group partial totals and then a final aggregated histogram for output consumption.

#### Responsibilities
- OpenCL histogram accumulation for byte data using a two-phase reduction pipeline.
- Per-group local binning with shared memory and atomic increments.
- Cross-group aggregation of partial results into a single final histogram buffer.

#### Key units
- `histogram_partial` — counts bytes in a work-group slice into a per-group partial histogram.
- `histogram_reduce` — sums partial histograms across all groups into the final `histogram[bin]` totals.
- `BINS` — fixed 256-bin bucket count used by both kernels.
- `partial` — `__global uint *` buffer storing intermediate per-group histogram totals.
- `local_bins` — `__local uint *` scratch space for each work-group’s local accumulation.
- `histogram` — `__global uint *` output buffer for the final per-bin counts.

#### Data flow
- Input byte data is read from `input` with `length` as the source range.
- `histogram_partial` assigns each work-item to an input index, increments `local_bins[input[i]]` using `atomic_inc`, and flushes local totals into `partial[group_id * BINS + bin]`.
- `histogram_reduce` iterates each bin index, sums `partial[g * BINS + bin]` across `group_count`, and writes the final total to `histogram[bin]`.
- The package output is a final 256-entry histogram with one total per byte value.

#### Dependencies
- OpenCL kernel execution model: `__kernel`, `__global`, `__local`, `uchar`, `uint`.
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`.
- OpenCL synchronization constant: `CLK_LOCAL_MEM_FENCE`.
- Shared macro: `BINS = 256`.

#### Cross-cutting
- Shared pattern: local accumulation followed by global reduction; each phase uses parallel work-group decomposition.
- Memory model: `__local` scratch space for per-group counts, `__global` buffers for cross-group accumulation and final output.
- Error-handling pattern: no explicit exception handling; `histogram_reduce` exits early when `bin >= BINS`.
- Configuration: fixed-size histogram logic driven by `BINS`, with no dynamic histogram sizing in the file.
