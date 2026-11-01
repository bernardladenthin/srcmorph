### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:02:00Z
- G: 
- A: 0.0.0
- X: file
---
> Provides fixed-capacity byte buffering between a single producer and a single consumer.

#### Purpose
- Declares the `ringbuf_t` API for initializing, reading, writing, measuring, and freeing a byte ring buffer.
- Capacity must be a power of two; one slot is reserved, so usable capacity is `capacity - 1` bytes.

#### Type
- C header; declares `ringbuf_t` and `ringbuf_status_t`.
- `ringbuf_t` contains `uint8_t *data`, `size_t capacity`, and volatile `size_t head` and `tail`.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: buffer, source byte pointer, and requested byte count.
- `ringbuf_read`: buffer, destination byte pointer, and requested byte count.
- `ringbuf_used` and `ringbuf_free_space`: buffer pointer.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`; `ringbuf_write` and `ringbuf_read` return the number of bytes copied.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` frees buffer resources.

#### Core logic
- Defines producer-owned `head` as the next write index and consumer-owned `tail` as the next read index.
- Declares reads and writes that copy up to the requested length.
- Reserves one slot to distinguish full from empty.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize ring buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — free ring buffer.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out of buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available bytes.

#### Constants and configuration
- `RINGBUF_H` — include guard.
- `RINGBUF_OK = 0` — success status.
- `RINGBUF_EINVAL = -1` — capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2` — allocation failed.
- `capacity` — must be a power of two; one slot is unused.

#### Dependencies
- `<stddef.h>` — `size_t`.
- `<stdint.h>` — `uint8_t`.
- `extern "C"` linkage guards when compiled as C++.

#### Exceptions / Errors
- Initialization reports invalid capacity with `RINGBUF_EINVAL` and allocation failure with `RINGBUF_ENOMEM`.
