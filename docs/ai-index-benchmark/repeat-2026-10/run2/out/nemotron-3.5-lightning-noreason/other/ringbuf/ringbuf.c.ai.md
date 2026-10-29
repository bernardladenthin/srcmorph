### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:02:22Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates and manages a fixed-size circular buffer for streaming data with power-of-two capacity constraints.

#### Purpose
Implements a fixed-size, power-of-two-constrained circular buffer for streaming data with read and write operations.

#### Type
C source file (ringbuf.c); implements ringbuf_t from ringbuf.h. Exported functions: ringbuf_init, ringbuf_free, ringbuf_used, ringbuf_free_space, ringbuf_write, ringbuf_read.

#### Input
- ringbuf_init: capacity (must be power of two).
- ringbuf_write: rb (ringbuf_t*), src (uint8_t*), len (size_t).
- ringbuf_read: rb (ringbuf_t*), dst (uint8_t*), len (size_t).

#### Output
- ringbuf_init: returns ringbuf_status_t (RINGBUF_OK, RINGBUF_EINVAL, RINGBUF_ENOMEM).
- ringbuf_write: returns size_t number of bytes written.
- ringbuf_read: returns size_t number of bytes read.
- ringbuf_used: returns size_t number of used slots.
- ringbuf_free_space: returns size_t free slots.

#### Core logic
- Initialization validates power-of-two capacity; allocates data buffer; sets head/tail to 0.
- Used space computed as `(head - tail) & (capacity - 1)` leveraging power-of-two mask.
- Free space calculated as `capacity - 1 - used`.
- Write copies data in up to two chunks: first segment from head to end of array, remainder from index 0; updates head with mask.
- Read copies data in up to two chunks: first segment from tail to end of array, remainder from index 0; updates tail with mask.
- Free and cleanup reset all state and release buffer memory.

#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t`: initializes ring buffer with power-of-two capacity.
- `ringbuf_free(rb) -> void`: frees buffer memory and resets state.
- `ringbuf_used(rb) -> size_t`: returns number of used slots.
- `ringbuf_free_space(rb) -> size_t`: returns number of free slots.
- `ringbuf_write(rb, src, len) -> size_t`: writes up to len bytes, respecting free space.
- `ringbuf_read(rb, dst, len) -> size_t`: reads up to len bytes, respecting used space.

#### Dependencies
- ringbuf.h (defines ringbuf_t, ringbuf_status_t, RINGBUF_OK, RINGBUF_EINVAL, RINGBUF_ENOMEM).
- <stdlib.h> (malloc, free).
- <string.h> (memcpy).

#### Exceptions / Errors
- RINGBUF_EINVAL: returned if capacity is not a power of two.
- RINGBUF_ENOMEM: returned if malloc fails to allocate buffer.
