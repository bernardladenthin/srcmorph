### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:12:44Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Provides fixed-capacity byte buffering between a single producer and a single consumer.

#### Purpose
- Defines the ring buffer state, status codes, and operations for initializing, reading, writing, measuring, and freeing a byte buffer.
- Uses one reserved slot, so usable capacity is `capacity - 1` bytes.

#### Type
- C header; declares `ringbuf_t` struct and `ringbuf_status_t` enum.
- `ringbuf_t` fields: `data` (`uint8_t *`), `capacity` (`size_t`), `head` and `tail` (`volatile size_t`).
- C++ callers receive C linkage via `extern "C"`.

#### Input
- `ringbuf_init`: `ringbuf_t *rb`, requested `size_t capacity`.
- `ringbuf_write`: buffer, source byte pointer, requested length.
- `ringbuf_read`: buffer, destination byte pointer, requested length.
- `ringbuf_used` / `ringbuf_free_space`: buffer pointer.
- Capacity must be a nonzero power of two.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`.
- Read and write return the number of bytes actually copied, up to the requested length.
- `ringbuf_used` and `ringbuf_free_space` return `size_t` measurements.
- `ringbuf_free` releases buffer resources.

#### Core logic
- Declares a fixed-capacity byte ring buffer for single-producer/single-consumer use.
- Tracks the next write position in producer-owned `head` and the next read position in consumer-owned `tail`.
- Reserves one slot to distinguish full from empty; usable capacity is one less than allocated capacity.
- Declares partial read and write operations that report actual bytes copied.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report bytes in use.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available space.

#### Constants and configuration
- `RINGBUF_H` — include guard.
- `RINGBUF_OK = 0` — success.
- `RINGBUF_EINVAL = -1` — capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2` — allocation failed.

#### Dependencies
- Standard headers: `<stddef.h>`, `<stdint.h>`.
- Types: `size_t`, `uint8_t`.

#### Exceptions / Errors
- `ringbuf_init` reports invalid capacity with `RINGBUF_EINVAL`.
- `ringbuf_init` reports allocation failure with `RINGBUF_ENOMEM`.
