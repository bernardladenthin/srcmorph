### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:00:15Z
- G: 
- A: 0.0.0
- X: file
---
> Provides fixed-capacity byte storage for passing data between a single producer and a single consumer.

#### Purpose
- Declares a ring buffer that copies bytes in and out, up to available capacity.
- Uses one unused slot to distinguish full from empty; usable capacity is `capacity - 1`.

#### Type
- C header; declarations use `extern "C"` when included from C++.
- `ringbuf_t`: struct containing the byte storage pointer, capacity, and producer/consumer indices.
- `ringbuf_status_t`: enum of initialization status codes.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: buffer, source byte pointer, and requested length.
- `ringbuf_read`: buffer, destination byte pointer, and requested length.
- `ringbuf_used` / `ringbuf_free_space`: buffer pointer.
- Capacity must be a nonzero power of two.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`.
- `ringbuf_write` and `ringbuf_read` return the number of bytes copied.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_init` may allocate storage; `ringbuf_free` releases buffer resources.

#### Core logic
- Declares fixed-capacity byte storage with `data`, `capacity`, `head`, and `tail`.
- `head` is the next write index and is owned by the producer; `tail` is the next read index and is owned by the consumer.
- Capacity is constrained to powers of two, with one slot reserved to distinguish full from empty.
- Read and write operations copy up to the requested length and report the actual copied count.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer resources
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report available bytes

#### Constants and configuration
- `RINGBUF_OK = 0` — successful status.
- `RINGBUF_EINVAL = -1` — capacity is zero or not a power of two.
- `RINGBUF_ENOMEM = -2` — allocation failed.
- `RINGBUF_H` — include guard.

#### Dependencies
- `<stddef.h>` — `size_t`.
- `<stdint.h>` — `uint8_t`.

#### Exceptions / Errors
- `ringbuf_init` reports invalid capacity with `RINGBUF_EINVAL`.
- `ringbuf_init` reports allocation failure with `RINGBUF_ENOMEM`.
