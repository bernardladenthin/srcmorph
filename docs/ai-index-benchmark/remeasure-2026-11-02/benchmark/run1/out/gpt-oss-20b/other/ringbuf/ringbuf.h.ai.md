### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:32:30Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> A fixed‑size byte ring buffer for single‑producer/single‑consumer use.

#### Purpose
- Provides a lock‑free circular buffer for byte streams.
- Supports non‑blocking read/write with capacity checks.

#### Type
- Header file (`ringbuf.h`) in C.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity (must be power of two, >0).
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – source buffer and length.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – destination buffer and length.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` (OK, EINVAL, ENOMEM).
- `ringbuf_write` returns number of bytes written.
- `ringbuf_read` returns number of bytes read.
- `ringbuf_used` and `ringbuf_free_space` return current usage metrics.

#### Core logic
- Buffer size is `capacity - 1` bytes; one slot unused to distinguish full/empty.
- `head` (producer) and `tail` (consumer) indices wrap modulo `capacity`.
- `ringbuf_write` copies up to `len` bytes, respecting free space; updates `head`.
- `ringbuf_read` copies up to `len` bytes, respecting used space; updates `tail`.
- `ringbuf_used` computes `(head - tail) & (capacity - 1)`.
- `ringbuf_free_space` computes `(tail - head - 1) & (capacity - 1)`.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.
- `void ringbuf_free(ringbuf_t *rb)` – free allocated memory.
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – write bytes.
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – read bytes.
- `size_t ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – available space.

#### Constants and configuration
- `RINGBUF_OK = 0`
- `RINGBUF_EINVAL = -1` – capacity invalid.
- `RINGBUF_ENOMEM = -2` – allocation failure.

#### Dependencies
- `<stddef.h>`, `<stdint.h>` for types.
- Uses `size_t`, `uint8_t`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` if capacity is zero or not a power of two.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if memory allocation fails.
