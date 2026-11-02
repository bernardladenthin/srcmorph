### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:44:41Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed‑size byte ring buffer for single‑producer, single‑consumer use.

#### Purpose
- Implements a lock‑free circular buffer.
- Supports byte‑level read/write with capacity‑checked limits.

#### Type
- Header (`ringbuf.h`) – C, with `extern "C"` guard for C++.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity in bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – source buffer and length.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – destination buffer and length.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` (OK, EINVAL, ENOMEM).
- `ringbuf_write` returns number of bytes written.
- `ringbuf_read` returns number of bytes read.
- `ringbuf_used` and `ringbuf_free_space` return current usage metrics.

#### Core logic
- Buffer size must be a power of two; one slot unused to distinguish full/empty.
- `head` (producer) and `tail` (consumer) indices wrap via modulo capacity.
- `ringbuf_write` copies up to `len` bytes, limited by free space.
- `ringbuf_read` copies up to `len` bytes, limited by used space.
- `ringbuf_used` computes `(head - tail) & (capacity - 1)`.
- `ringbuf_free_space` computes `capacity - 1 - used`.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.
- `void ringbuf_free(ringbuf_t *rb)` – free allocated memory.
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – write bytes.
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – read bytes.
- `size_t ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – available space.

#### Constants and configuration
- `RINGBUF_OK = 0`
- `RINGBUF_EINVAL = -1` – capacity zero or not power of two.
- `RINGBUF_ENOMEM = -2` – allocation failure.

#### Dependencies
- `<stddef.h>` – `size_t`.
- `<stdint.h>` – `uint8_t`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` if capacity invalid.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if memory allocation fails.
