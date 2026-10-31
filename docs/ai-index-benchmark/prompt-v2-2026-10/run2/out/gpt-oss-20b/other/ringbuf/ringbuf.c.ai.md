### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:54:10Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a power‑of‑two sized circular buffer with read/write and memory management.

#### Purpose
- Provides a fixed‑size ring buffer for byte streams.
- Supports initialization, deallocation, querying usage, and bulk read/write.

#### Type
- C source file (`ringbuf.c`).

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity value.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – source buffer and length.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – destination buffer and length.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` status.
- `ringbuf_write` returns number of bytes written.
- `ringbuf_read` returns number of bytes read.
- `ringbuf_free` releases allocated memory.

#### Core logic
- `is_power_of_two` checks capacity validity.
- `min_size` selects smaller of two sizes.
- `ringbuf_init` allocates memory, sets head/tail to 0, validates capacity.
- `ringbuf_free` frees memory and resets fields.
- `ringbuf_used` computes used bytes via modulo arithmetic.
- `ringbuf_free_space` calculates remaining capacity.
- `ringbuf_write` copies data in up to two chunks to wrap around.
- `ringbuf_read` copies data in up to two chunks to wrap around.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.
- `void ringbuf_free(ringbuf_t *rb)` – free buffer.
- `size_t ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – free space available.
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
- `ringbuf_init` returns `RINGBUF_ENOMEM` on allocation failure.
