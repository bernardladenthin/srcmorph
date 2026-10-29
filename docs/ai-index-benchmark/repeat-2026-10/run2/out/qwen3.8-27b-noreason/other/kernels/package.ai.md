### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:09:55Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Provides a two-pass OpenCL kernel implementation for computing a 256-bin byte histogram from image or stream data.

#### Purpose
- Parallel computation of byte frequency distributions for `uchar` data.
- Implements a scalable two-stage reduction strategy for global histogram aggregation.

#### Responsibilities
- **Partial Counting**: Distributes input bytes across work-groups to generate intermediate per-group counts.
- **Global Reduction**: Aggregates partial counts from all work-groups into a single final histogram.

#### Key units
- `histogram_partial`: OpenCL kernel that initializes local shared memory, iterates over global input indices with stride, and uses `atomic_inc` on `local_bins` to produce per-group partial histograms in the `partial` buffer.
- `histogram_reduce`: OpenCL kernel that maps one work-item per bin to sum the corresponding entries across all `group_count` work-groups in the `partial` buffer, writing the result to the `histogram` buffer.
- `BINS`: Constant defined as 256, representing the number of histogram bins.

#### Data flow
1. **Pass 1**: `input` bytes are processed by `histogram_partial`, where each work-group accumulates counts in `local_bins` (shared memory) and writes 256 counters to the `partial` global buffer.
2. **Pass 2**: `histogram_reduce` reads the `partial` buffer, summing values for each bin index across all work-groups to produce the final 256-bin `histogram` output.

#### Dependencies
- **OpenCL C 1.2 Built-ins**: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- **Memory Types**: `__global` for input/output buffers, `__local` for intra-group shared memory.
- **Data Types**: `uchar` for input data, `uint` for counters and indices.

#### Cross-cutting
- **Synchronization**: Relies on `barrier(CLK_LOCAL_MEM_FENCE)` to ensure correct initialization and finalization of shared memory within work-groups.
- **Atomic Operations**: Uses `atomic_inc` for thread-safe counting in shared memory during the partial pass.
- **Error Handling**: No explicit error handling; correctness depends on proper buffer sizing and work-group dimension configuration.
