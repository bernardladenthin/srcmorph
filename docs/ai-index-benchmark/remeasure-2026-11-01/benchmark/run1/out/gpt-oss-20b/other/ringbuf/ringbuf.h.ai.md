### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:27:55Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed‑size byte ring buffer for single‑producer, single‑consumer use.

#### Purpose
- Implements a lock‑free ring buffer for byte streams.
- Supports initialization, cleanup, read/write, and space queries.

#### Type
- Header file (`ringbuf.h`) in C (C99).  
- Defines `ringbuf_t` struct, `ringbuf_status_t` enum, and function prototypes.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity in bytes (must be power of two, >0).  
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – source buffer and length.  
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – destination buffer and length.  
- `ringbuf_used(const ringbuf_t *rb)` – reads current used bytes.  
- `ringbuf_free_space(const ringbuf_t *rb)` – reads available space.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` (OK, EINVAL, ENOMEM).  
- `ringbuf_write` returns number of bytes written.  
- `ringbuf_read` returns number of bytes read.  
- `ringbuf_free` frees allocated memory.  
- `ringbuf_used` and `ringbuf_free_space` return byte counts.

#### Core logic
- `ringbuf_init`: validates capacity, allocates `capacity` bytes, sets head/tail to 0.  
- `ringbuf_write`: copies up to `len` bytes from `src` to buffer, wrapping at `capacity`, updates `head`.  
- `ringbuf_read`: copies up to `len` bytes from buffer to `dst`, wrapping at `capacity`, updates `tail`.  
- `ringbuf_used`: computes `(head - tail) & (capacity - 1)`.  
- `ringbuf_free_space`: computes `capacity - 1 - ringbuf_used`.  
- `ringbuf_free`: frees `data` and resets fields.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.  
- `void ringbuf_free(ringbuf_t *rb)` – free resources.  
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – write bytes.  
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – read bytes.  
- `size_t ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.  
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – bytes available for writing.

#### Constants and configuration
- `RINGBUF_OK = 0`  
- `RINGBUF_EINVAL = -1` – capacity invalid.  
- `RINGBUF_ENOMEM = -2` – allocation failure.

#### Dependencies
- `<stddef.h>` for `size_t`.  
- `<stdint.h>` for `uint8_t`.  
- Uses `malloc`/`free` implicitly via implementation (not shown).

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` if capacity is zero or not a power of two.  
- `ringbuf_init` returns `RINGBUF_ENOMEM` if memory allocation fails.
