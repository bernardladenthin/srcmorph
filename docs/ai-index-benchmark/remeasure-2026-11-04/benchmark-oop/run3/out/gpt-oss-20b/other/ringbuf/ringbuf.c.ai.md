### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:56:08Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Implements a fixed‑size ring buffer with power‑of‑two capacity, providing init, free, read, write, and status queries.

#### Purpose
- Provides a circular FIFO buffer for byte streams.
- Ensures efficient wrap‑around copying without extra allocations.

#### Type
- C source file implementing `ringbuf_t` operations defined in `ringbuf.h`.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity must be power of two.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – source data buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – destination buffer.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_write` returns number of bytes actually written.
- `ringbuf_read` returns number of bytes actually read.
- `ringbuf_free` deallocates internal buffer.

#### Core logic
- `is_power_of_two` checks capacity validity.
- `min_size` selects smaller of two sizes.
- `ringbuf_init` allocates buffer, sets indices to zero.
- `ringbuf_free` frees buffer and resets fields.
- `ringbuf_used` computes bytes in buffer via modulo arithmetic.
- `ringbuf_free_space` computes available space (`capacity - 1 - used`).
- `ringbuf_write` copies up to two contiguous chunks: from current head to end, then from start if needed; updates head.
- `ringbuf_read` copies similarly from tail; updates tail.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.
- `void ringbuf_free(ringbuf_t *rb)` – release resources.
- `size_t ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – space left for writing.
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – write data.
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – read data.

#### Constants and configuration
- None defined in this file.

#### Dependencies
- `stdlib.h` – `malloc`, `free`.
- `string.h` – `memcpy`.
- `ringbuf.h` – type and status definitions.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` if capacity not power of two.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if `malloc` fails.
