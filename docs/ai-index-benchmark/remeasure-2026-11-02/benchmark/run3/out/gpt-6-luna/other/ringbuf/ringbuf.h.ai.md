### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:59:20Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> Provides fixed-capacity byte buffering between a single producer and a single consumer, allowing each to write or read available bytes without sharing an index.

#### Purpose
- Declares the `ringbuf_t` API for bounded byte-stream buffering.
- Specifies power-of-two capacity and usable storage of `capacity - 1` bytes.

#### Type
- C header `ringbuf.h`, with C++ linkage guards.
- Defines `ringbuf_t` structure and `ringbuf_status_t` enum.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: buffer, source byte pointer, and requested length.
- `ringbuf_read`: buffer, destination byte pointer, and requested length.
- `ringbuf_used` and `ringbuf_free_space`: buffer pointer.

#### Output
- Initialization status, copied byte counts, and used/free-space byte counts.
- `ringbuf_init` may report invalid capacity or allocation failure.

#### Core logic
- Describes a byte ring buffer with producer-owned `head` (next write index) and consumer-owned `tail` (next read index).
- Reserves one slot to distinguish full from empty; usable capacity is one less than `capacity`.
- Write and read operations copy up to the requested length and return the actual byte count.
- Capacity must be nonzero and a power of two.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report bytes currently used.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available bytes.

#### Constants and configuration
- `RINGBUF_OK = 0` — success.
- `RINGBUF_EINVAL = -1` — capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2` — allocation failed.

#### Dependencies
- Standard headers: `<stddef.h>`, `<stdint.h>`.
- Uses `size_t` and `uint8_t`.

#### Exceptions / Errors
- `ringbuf_init` reports invalid capacity with `RINGBUF_EINVAL` and allocation failure with `RINGBUF_ENOMEM`.
