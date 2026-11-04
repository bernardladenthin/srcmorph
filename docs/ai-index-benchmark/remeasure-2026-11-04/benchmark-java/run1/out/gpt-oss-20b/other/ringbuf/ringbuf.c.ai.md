### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:17:42Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Implements a fixed‑size circular buffer with power‑of‑two capacity, providing read/write and status utilities.

#### Purpose
- Provides a ring buffer for byte streams.
- Supports initialization, deallocation, reading, writing, and space queries.

#### Type
- C source file implementing `ringbuf_t` operations defined in `ringbuf.h`.

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
- `ringbuf_init` allocates buffer, sets indices to 0.
- `ringbuf_free` frees buffer and resets fields.
- `ringbuf_used` computes used bytes via wrap‑around arithmetic.
- `ringbuf_free_space` calculates available space.
- `ringbuf_write` copies up to two contiguous chunks to handle wrap‑around.
- `ringbuf_read` copies up to two contiguous chunks from buffer.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.
- `void ringbuf_free(ringbuf_t *rb)` – free resources.
- `size_t ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – bytes available for writing.
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – write data.
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – read data.

#### Constants and configuration
- `RINGBUF_EINVAL` – returned when capacity not power of two.
- `RINGBUF_ENOMEM` – returned when memory allocation fails.
- `RINGBUF_OK` – successful initialization.

#### Dependencies
- `stdlib.h` – `malloc`, `free`.
- `string.h` – `memcpy`.
- `ringbuf.h` – type and status definitions.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for non‑power‑of‑two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if `malloc` fails.
