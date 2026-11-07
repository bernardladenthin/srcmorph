### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:54:37Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Provides a fixed‑size byte ring buffer for single‑producer/single‑consumer use.

#### Purpose
- Implements a lock‑free circular buffer.
- Supports byte‑level read/write with capacity checks.

#### Type
- Header file (`ringbuf.h`) in C.  
- Declares `ringbuf_t` struct, `ringbuf_status_t` enum, and public API functions.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity in bytes.  
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – source buffer and length.  
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – destination buffer and length.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).  
- `ringbuf_write` and `ringbuf_read` return number of bytes actually transferred.  
- `ringbuf_free` releases allocated memory.

#### Core logic
- `ringbuf_init` validates capacity (non‑zero, power of two), allocates `capacity` bytes, sets `head` and `tail` to 0.  
- `ringbuf_write` copies up to `len` bytes from `src` to buffer, wrapping at `capacity`, updates `head`.  
- `ringbuf_read` copies up to `len` bytes from buffer to `dst`, wrapping at `capacity`, updates `tail`.  
- `ringbuf_used` returns `(head - tail) & (capacity - 1)`.  
- `ringbuf_free_space` returns `(capacity - 1) - ringbuf_used`.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.  
- `void ringbuf_free(ringbuf_t *rb)` – free resources.  
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – write bytes.  
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – read bytes.  
- `size_t ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.  
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – available space.

#### Constants and configuration
- `RINGBUF_OK = 0`  
- `RINGBUF_EINVAL = -1` – capacity invalid.  
- `RINGBUF_ENOMEM = -2` – allocation failure.

#### Dependencies
- `<stddef.h>` for `size_t`.  
- `<stdint.h>` for `uint8_t`.  
- Standard C library functions for memory allocation (`malloc`, `free`).

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` if capacity is zero or not a power of two.  
- `ringbuf_init` returns `RINGBUF_ENOMEM` if memory allocation fails.
