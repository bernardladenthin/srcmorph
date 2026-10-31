### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:33:25Z
- G: 
- A: 0.0.0
- X: file
---
> Stores and transfers bytes through a fixed-capacity buffer shared by one producer and one consumer.

#### Purpose
- Declares the `ringbuf` API for bounded byte buffering.
- Reserves one slot to distinguish full from empty; usable capacity is `capacity - 1`.

#### Type
- C header; exposes `ringbuf_t` structure and `ringbuf_status_t` enum.
- `ringbuf_t` contains a byte pointer, capacity, and volatile producer-owned `head` and consumer-owned `tail`.

#### Input
- `ringbuf_init`: buffer pointer and capacity.
- `ringbuf_write`: buffer, source bytes, and requested length.
- `ringbuf_read`: buffer, destination bytes, and requested length.
- `ringbuf_used` / `ringbuf_free_space`: buffer pointer.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`; declares initialization of a ring buffer.
- `ringbuf_write` and `ringbuf_read` return the number of bytes actually copied, up to the requested length.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases buffer resources.

#### Core logic
- Declares a fixed-capacity byte ring buffer intended for a single producer and a single consumer.
- Requires capacity to be a power of two; one slot is unused.
- Exposes operations to initialize and free the buffer, copy bytes in or out, and query occupancy or available space.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize ring buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release ring buffer
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available bytes

#### Constants and configuration
- `RINGBUF_OK = 0` — success status.
- `RINGBUF_EINVAL = -1` — capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2` — allocation failed.

#### Dependencies
- `<stddef.h>` — `size_t`.
- `<stdint.h>` — `uint8_t`.

#### Exceptions / Errors
- `ringbuf_init` reports invalid capacity with `RINGBUF_EINVAL` and allocation failure with `RINGBUF_ENOMEM`.
