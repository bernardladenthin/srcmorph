### other/kernels
- H: 1.0
- C: 0E82DBF3
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T10:00:39Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Provides OpenCL GPU kernels for computing a 256-bin byte histogram of image or stream data via a two-pass parallel reduction strategy.

#### Purpose
- Implements high-performance parallel byte frequency counting for large datasets using GPU acceleration.
- Separates computation into partial counting and final reduction phases to optimize memory access and parallelism.

#### Responsibilities
- **Partial Histogram Computation**: `histogram_partial` kernel aggregates byte counts per work-group using shared memory and atomic operations.
- **Final Reduction**: `histogram_reduce` kernel sums partial results across all work-groups to produce the final 256-bin histogram.

#### Key units
- `histogram_partial`: OpenCL kernel that processes input bytes in strided fashion, accumulating counts in `__local` memory before writing to `__global` partial buffers.
- `histogram_reduce`: OpenCL kernel that performs element-wise summation of partial histograms across work-groups to generate the final output.
- `BINS`: Constant defining the histogram size (256 bins).

#### Data flow
1. **Input**: Raw byte stream (`__global const uchar *`) and length.
2. **Pass 1**: `histogram_partial` distributes work-items across the input, using `atomic_inc` on shared `local_bins` to count frequencies per work-group, then writes 256 counters per group to the `partial` global buffer.
3. **Pass 2**: `histogram_reduce` reads the `partial` buffer, sums the corresponding bin indices across all `group_count` work-groups, and writes the final 256 values to the `histogram` global buffer.

#### Dependencies
- OpenCL C 1.2 runtime.
- Built-in OpenCL functions: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.

#### Cross-cutting
- **Two-Phase Pattern**: Consistent separation of local accumulation (shared memory) and global reduction (global memory) to handle large data volumes.
- **Guard Clauses**: `histogram_reduce` includes a bounds check (`bin >= BINS`) to prevent out-of-bounds writes if the global work size exceeds the bin count.
- **No Error Handling**: Relies on correct host-side launch configuration for work-group sizes and buffer allocations.
