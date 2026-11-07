### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:26:43Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Provides a fixed‑size byte ring buffer for single‑producer/single‑consumer use.

#### Purpose
- Implements a lock‑free ring buffer with power‑of‑two capacity.
- Supports write, read, and space queries.

#### Type
- Header (`ringbuf.h`) in C (C99).  
- Exposes `ringbuf_t` struct, `ringbuf_status_t` enum, and API functions.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity in bytes.  
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – source buffer and length.  
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – destination buffer and length.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` (OK, EINVAL, ENOMEM).  
- `ringbuf_write` returns number of bytes written.  
- `ringbuf_read` returns number of bytes read.  
- `ringbuf_used` and `ringbuf_free_space` return byte counts.

#### Core logic
- `ringbuf_init` validates capacity (non‑zero, power of two), allocates `data` array, sets `head` and `tail` to 0.  
- `ringbuf_write` copies up to `len` bytes from `src` into buffer, wrapping at `capacity`, updates `head`.  
- `ringbuf_read` copies up to `len` bytes from buffer into `dst`, wrapping at `capacity`, updates `tail`.  
- `ringbuf_used` computes `(head - tail) & (capacity - 1)`.  
- `ringbuf_free_space` computes `(capacity - 1) - ringbuf_used`.  
- `ringbuf_free` frees allocated `data`.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.  
- `void ringbuf_free(ringbuf_t *rb)` – release resources.  
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – write bytes.  
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – read bytes.  
- `size_t ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.  
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – available space.

#### Constants and configuration
- `RINGBUF_OK = 0`  
- `RINGBUF_EINVAL = -1` – capacity invalid.  
- `RINGBUF_ENOMEM = -2` – allocation failure.

#### Dependencies
- `<stddef.h>`, `<stdint.h>`.  
- Uses `malloc`, `free` (implicitly via `ringbuf_free`).

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` if capacity is zero or not a power of two.  
- `ringbuf_init` returns `RINGBUF_ENOMEM` if memory allocation fails.
