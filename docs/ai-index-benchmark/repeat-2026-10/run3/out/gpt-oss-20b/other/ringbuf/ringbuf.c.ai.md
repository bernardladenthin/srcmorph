### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:54:52Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a power‑of‑two sized circular buffer with read/write and status APIs.

#### Purpose
- Provides a fixed‑size ring buffer for byte streams.
- Supports efficient enqueue/dequeue with wrap‑around logic.

#### Type
- C source file implementing `ringbuf_t` operations.  
- Functions: `ringbuf_init`, `ringbuf_free`, `ringbuf_used`, `ringbuf_free_space`, `ringbuf_write`, `ringbuf_read`.  
- Uses helper functions `is_power_of_two`, `min_size`.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity in bytes.  
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – source buffer and length.  
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – destination buffer and length.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` (OK, EINVAL, ENOMEM).  
- `ringbuf_write` returns number of bytes written.  
- `ringbuf_read` returns number of bytes read.  
- `ringbuf_used` and `ringbuf_free_space` return sizes.

#### Core logic
- `is_power_of_two` checks capacity validity.  
- `ringbuf_init` allocates memory, sets head/tail to 0.  
- `ringbuf_free` frees memory and resets fields.  
- `ringbuf_used` computes used bytes via modulo arithmetic.  
- `ringbuf_free_space` calculates remaining capacity.  
- `ringbuf_write` writes up to two contiguous chunks: from current head to end, then from start. Updates head.  
- `ringbuf_read` reads similarly from tail, updates tail.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.  
- `void ringbuf_free(ringbuf_t *rb)` – free resources.  
- `size_t ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.  
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – available space.  
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – enqueue bytes.  
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – dequeue bytes.

#### Dependencies
- `<stdlib.h>`, `<string.h>`  
- Header `"ringbuf.h"` defining `ringbuf_t`, `ringbuf_status_t`, and status constants (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` if capacity not power of two.  
- `ringbuf_init` returns `RINGBUF_ENOMEM` on allocation failure.
