### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:54:54Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Implements a fixed‑size ring buffer with power‑of‑two capacity, providing init, free, read, write, and status queries.

#### Purpose
- Provides a circular FIFO buffer for byte streams.
- Ensures efficient wrap‑around using bit masking.

#### Type
- C source file (`ringbuf.c`), implements functions declared in `ringbuf.h`.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity in bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – source buffer and length.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – destination buffer and length.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` (OK, EINVAL, ENOMEM).
- `ringbuf_write` returns number of bytes written.
- `ringbuf_read` returns number of bytes read.
- `ringbuf_free` deallocates internal storage.

#### Core logic
- `is_power_of_two` checks capacity validity.
- `min_size` selects smaller of two sizes.
- `ringbuf_init` allocates buffer, sets head/tail to 0.
- `ringbuf_free` frees buffer and resets fields.
- `ringbuf_used` computes bytes in buffer via `(head - tail) & (capacity-1)`.
- `ringbuf_free_space` computes available space.
- `ringbuf_write` writes up to two contiguous chunks: from head to end, then from start.
- `ringbuf_read` reads similarly from tail.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.
- `void ringbuf_free(ringbuf_t *rb)` – free resources.
- `size_t ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – space left.
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – write data.
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – read data.

#### Constants and configuration
- None defined in this file.

#### Dependencies
- `ringbuf.h` (declaration of `ringbuf_t`, `ringbuf_status_t`).
- Standard library: `stdlib.h` (`malloc`, `free`), `string.h` (`memcpy`).

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` if capacity not power of two.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if memory allocation fails.
