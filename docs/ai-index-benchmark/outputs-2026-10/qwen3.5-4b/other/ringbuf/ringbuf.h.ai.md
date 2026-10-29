### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:05:03Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-capacity byte ring buffer for single-producer/single-consumer data transfer with power-of-two capacity validation.

#### Purpose
- Defines a fixed-capacity byte ring buffer structure for single-producer/single-consumer data transfer.
- Exposes initialization, cleanup, read/write, and space monitoring functions for efficient memory management.

#### Type
Header file (C) defining `ringbuf_t` struct and `ringbuf_status_t` enum with exported C API functions.

#### Input
- `ringbuf_t *rb`: pointer to ring buffer structure.
- `size_t capacity`: buffer size (must be power of two).
- `const uint8_t *src`: source data for writes.
- `uint8_t *dst`: destination buffer for reads.
- `size_t len`: number of bytes to process.

#### Output
- `ringbuf_status_t`: success (0), invalid capacity (-1), or allocation failure (-2).
- `size_t`: actual bytes copied or current buffer usage/free space.
- Modified `rb->head` and `rb->tail` indices within the buffer structure.

#### Core logic
- Validates capacity is power of two and non-zero during initialization.
- Allocates internal data buffer of size `capacity - 1` bytes.
- Maintains `head` (next write index) and `tail` (next read index) as volatile counters.
- Implements circular write logic wrapping `head` modulo `capacity`.
- Implements circular read logic wrapping `tail` modulo `capacity`.
- Calculates used space as `head - tail` and free space as `capacity - used`.

#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t`: initializes buffer with power-of-two capacity.
- `ringbuf_free(rb) -> void`: deallocates buffer memory.
- `ringbuf_write(rb, src, len) -> size_t`: writes up to `len` bytes from `src`.
- `ringbuf_read(rb, dst, len) -> size_t`: reads up to `len` bytes into `dst`.
- `ringbuf_used(rb) -> size_t`: returns current bytes consumed.
- `ringbuf_free_space(rb) -> size_t`: returns available bytes.

#### Dependencies
- `<stddef.h>`: size_t definition.
- `<stdint.h>`: uint8_t and size_t types.

#### Exceptions / Errors
- Returns `RINGBUF_EINVAL` if capacity is zero or not a power of two.
- Returns `RINGBUF_ENOMEM` if internal allocation fails.
