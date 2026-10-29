### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:02:48Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> A fixed-capacity, single-producer/single-consumer byte ring buffer for streaming data with power-of-two capacity constraints.

#### Purpose
Provides a fixed-size, power-of-two-constrained circular buffer implementation for efficient streaming data transfer between a single producer and consumer.

#### Responsibilities
- **Buffer management**: Fixed-capacity circular buffer with power-of-two constraints for mask-based index arithmetic.
- **Data streaming**: Write and read operations supporting up to two chunks per transfer for wrap-around handling.
- **State querying**: Used space and free space calculation without locks.
- **Memory lifecycle**: Initialization, allocation, and cleanup of buffer resources.

#### Key units
- **ringbuf_t** (struct): Buffer state containing data array, capacity, head and tail indices, and status flag.
- **ringbuf_status_t** (enum): Status codes `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- **ringbuf_init()**: Initializes buffer with power-of-two capacity validation and memory allocation.
- **ringbuf_free()**: Frees buffer data array and resets state.
- **ringbuf_write()**: Writes bytes from source into buffer, returning actual byte count copied.
- **ringbuf_read()**: Reads bytes from buffer into destination, returning actual byte count copied.
- **ringbuf_used()**: Returns number of used slots via `(head - tail) & (capacity - 1)`.
- **ringbuf_free_space()**: Returns free slots as `capacity - 1 - ringbuf_used`.

#### Data flow
Producer calls `ringbuf_write()` with source data and length; data copies into buffer at head index (up to two chunks if wrapping), head advances modulo capacity, returns byte count limited by available space. Consumer calls `ringbuf_read()` with destination buffer and length; data copies from tail index (up to two chunks), tail advances modulo capacity, returns byte count limited by used space. Both operations respect buffer full/empty constraints via used/free space queries.

#### Dependencies
- **ringbuf.h**: Defines `ringbuf_t`, `ringbuf_status_t`, enum constants `RINGBUF_OK`/`RINGBUF_EINVAL`/`RINGBUF_ENOMEM`.
- **<stdlib.h>**: `malloc` and `free` for buffer allocation.
- **<string.h>**: `memcpy` for data transfer in two-chunk operations.
- Compiler support for `volatile` on head/tail indicating multi-threaded access intent.

#### Cross-cutting
- Power-of-two capacity constraint enables mask-based `(index) & (capacity - 1)` arithmetic for modulo operations.
- Single-producer/single-consumer design; `head` and `tail` marked `volatile` for multi-threaded awareness but no atomic operations.
- Write and read both use two-chunk copy strategy for wrap-around: first segment to end of array, remainder from index 0.
- One slot permanently reserved: usable capacity is always `capacity - 1`, preventing full/empty ambiguity.
- Error handling returns specific status codes (`EINVAL` for invalid capacity, `ENOMEM` for allocation failure) with 0 byte count on full/empty conditions.
