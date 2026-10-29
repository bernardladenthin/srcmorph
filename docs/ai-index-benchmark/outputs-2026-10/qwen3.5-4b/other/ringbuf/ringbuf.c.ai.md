### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:04:56Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-capacity circular buffer (ring buffer) for efficient zero-copy data transfer between producers and consumers in C.

#### Purpose
- Manages a circular buffer with power-of-two capacity for efficient bitwise indexing.
- Provides atomic read/write operations that wrap around the buffer boundaries.

#### Type
Implementation file (`.c`) for a ring buffer structure (`ringbuf_t`) in C.

#### Input
- `ringbuf_t *rb`: Pointer to the ring buffer instance.
- `size_t capacity`: Buffer size (must be a power of two).
- `const uint8_t *src`: Source data for writing.
- `uint8_t *dst`: Destination buffer for reading.
- `size_t len`: Number of bytes to write or read.

#### Output
- `ringbuf_status_t`: Status code from `ringbuf_init` (success or error).
- `void`: Cleanup performed by `ringbuf_free`.
- `size_t`: Number of bytes actually written or read from `ringbuf_write`/`ringbuf_read`.
- Updated internal state (`head`, `tail`, `data` pointer).

#### Core logic
- Validates capacity is a power of two during initialization to enable bitwise masking.
- Allocates a contiguous memory block for the buffer data.
- Tracks `head` (write position) and `tail` (read position) using modulo arithmetic via bitwise AND.
- Wraps writes across the buffer boundary by splitting into two `memcpy` chunks.
- Wraps reads across the buffer boundary by splitting into two `memcpy` chunks.
- Calculates used space and free space using bitwise operations for performance.

#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t`: Initializes buffer with power-of-two capacity.
- `ringbuf_free(rb) -> void`: Frees allocated buffer memory and resets state.
- `ringbuf_used(rb) -> size_t`: Returns current number of bytes stored.
- `ringbuf_free_space(rb) -> size_t`: Returns available bytes for writing.
- `ringbuf_write(rb, src, len) -> size_t`: Writes up to `len` bytes, wrapping if needed.
- `ringbuf_read(rb, dst, len) -> size_t`: Reads up to `len` bytes, wrapping if needed.

#### Dependencies
- `ringbuf.h`: Header defining structure and types.
- `<stdlib.h>`: For `malloc` and `free`.
- `<string.h>`: For `memcpy`.

#### Exceptions / Errors
- `RINGBUF_EINVAL`: Returned if `capacity` is not a power of two.
- `RINGBUF_ENOMEM`: Returned if memory allocation fails in `ringbuf_init`.
