### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:52:01Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed‑size byte ring buffer for single‑producer single‑consumer use.

#### Purpose
- Implements a lock‑free ring buffer for byte streams.
- Supports initialization, cleanup, read/write, and space queries.

#### Type
- Header (`ringbuf.h`) – C language, C99 compatible.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity in bytes (must be power of two).
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – source buffer and length.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – destination buffer and length.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` (OK, EINVAL, ENOMEM).
- `ringbuf_write` returns number of bytes written.
- `ringbuf_read` returns number of bytes read.
- `ringbuf_used` and `ringbuf_free_space` return current usage metrics.

#### Core logic
- `ringbuf_init` validates capacity, allocates data array, sets head/tail to 0.
- `ringbuf_write` copies up to `len` bytes from `src` to buffer, wrapping at capacity, updates head atomically.
- `ringbuf_read` copies up to `len` bytes from buffer to `dst`, wrapping, updates tail atomically.
- `ringbuf_used` computes `(head - tail) & (capacity - 1)`.
- `ringbuf_free_space` computes `capacity - 1 - ringbuf_used`.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.
- `void ringbuf_free(ringbuf_t *rb)` – free allocated memory.
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – write bytes.
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – read bytes.
- `size_t ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – available space.

#### Dependencies
- `<stddef.h>`, `<stdint.h>`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` if capacity is zero or not a power of two.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if memory allocation fails.
