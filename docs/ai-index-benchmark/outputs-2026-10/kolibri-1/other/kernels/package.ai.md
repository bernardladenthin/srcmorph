### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:40:10Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256-bin byte histogram of an image or byte stream using two-pass OpenCL kernels.

#### Purpose
- Builds a byte histogram of an input stream
- Uses two-pass OpenCL kernel strategy for scalability

#### Responsibilities
- Partial histogram computation per work-group
- Reduction of per-group partials into a final histogram

#### Key units
- histogram_partial: partial histogram per work-group
- histogram_reduce: sums partials into final histogram
- local_bins: local uint buffer for per-work-group bin counts
- partial: global uint array of group_count * 256 partial counts
- histogram: global uint array of 256 bin counts

#### Data flow
- input global uchar buffer of byte stream flows into histogram_partial
- each work-group accumulates local bin counts then writes its slice of partial array
- partial array flows into histogram_reduce
- each work-item sums corresponding bins across all group partials into final histogram

#### Dependencies
- OpenCL C 1.2 built-ins: get_local_id, get_local_size, get_global_id, get_global_size, get_group_id, barrier
- CLK_LOCAL_MEM_FENCE barrier flag

#### Cross-cutting
- Two-pass OpenCL kernel strategy
- No explicit exception handling in source
