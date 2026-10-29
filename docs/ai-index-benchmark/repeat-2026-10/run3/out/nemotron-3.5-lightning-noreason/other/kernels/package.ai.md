### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:41:42Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Calculates a 256-bin byte histogram of an image or byte stream using two-pass OpenCL parallel reduction.

#### Purpose
Computes a 256-bin histogram from raw byte data using OpenCL parallel reduction across two kernel passes.

#### Responsibilities
- **Pass 1 (`histogram_partial`)**: Zeroes local work-group bins and atomically increments counters for assigned byte ranges, writing partial results to a global buffer.
- **Pass 2 (`histogram_reduce`)**: One work-item per bin iterates across all partial histograms and sums into the final 256-bin output array.

#### Key units
- `histogram_partial`: OpenCL kernel that performs per-work-group partial histogram computation using atomic increments and local memory accumulation.
- `histogram_reduce`: OpenCL kernel that reduces partial histograms into the final result, with one work-item per bin summing across all partial results.
- `BINS` macro: Defined as 256, governing the histogram bin count.

#### Data flow
Input byte data is processed by `histogram_partial`, where each work-group accumulates partial counts into a global partial buffer. Subsequently, `histogram_reduce` consumes the partial buffers, with each work-item summing across all groups to produce the final 256-bin histogram.

#### Dependencies
- OpenCL API types: `uchar`, `uint`, `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`.
- Macro `BINS` defined as 256.

#### Cross-cutting
- Employs two-pass parallel reduction pattern to balance work-group accumulation and global synchronization.
- Out-of-bounds access guarded in `histogram_reduce` via `if (bin >= BINS) return;`.
