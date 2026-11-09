### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:57:44Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> A circular buffer implementation that stores bytes in a power‑of‑two sized array and supports write/read with wrap‑around.

#### Purpose
- Provides a fixed‑size ring buffer for byte streams.
- Supports initialization, deallocation, and querying of used/free space.

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
- `ringbuf_free` deallocates internal buffer.

#### Core logic
- `is_power_of_two` checks capacity validity.
- `min_size` selects smaller of two sizes.
- `ringbuf_init` allocates buffer, sets head/tail to 0.
- `ringbuf_free` frees buffer and resets fields.
- `ringbuf_used` computes bytes in buffer via modulo arithmetic.
- `ringbuf_free_space` computes available space.
- `ringbuf_write` writes up to two contiguous chunks: from current head to end, then from start.
- `ringbuf_read` reads similarly from tail, updating tail pointer.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.
- `void ringbuf_free(ringbuf_t *rb)` – free buffer.
- `size_t ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – bytes available.
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – write data.
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – read data.

#### Constants and configuration
- None defined in this file.

#### Dependencies
- `ringbuf.h` (declaration of `ringbuf_t`, `ringbuf_status_t`, status codes).
- Standard library: `stdlib.h` (`malloc`, `free`), `string.h` (`memcpy`).

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` if capacity not power of two.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if memory allocation fails.
