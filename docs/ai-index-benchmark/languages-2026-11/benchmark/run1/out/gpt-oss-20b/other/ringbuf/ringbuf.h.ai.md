### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:20:45Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed‑size byte ring buffer for single‑producer/single‑consumer use.

#### Purpose
- Implements a lock‑free ring buffer with power‑of‑two capacity.
- Supports byte‑level read/write with capacity checks.

#### Type
- Header file (`ringbuf.h`) in C.  
- Defines `ringbuf_t` struct, `ringbuf_status_t` enum, and public API functions.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity value.  
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – source buffer and length.  
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – destination buffer and length.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` (OK, EINVAL, ENOMEM).  
- `ringbuf_write` returns number of bytes written.  
- `ringbuf_read` returns number of bytes read.  
- `ringbuf_used` and `ringbuf_free_space` return size_t counts.

#### Core logic
- `ringbuf_init`: validates capacity (power of two, non‑zero), allocates data array, initializes head/tail.  
- `ringbuf_write`: copies up to `len` bytes from `src` to buffer, respecting free space, updates head atomically.  
- `ringbuf_read`: copies up to `len` bytes from buffer to `dst`, respecting used bytes, updates tail atomically.  
- `ringbuf_used`: calculates `(head - tail) & (capacity - 1)`.  
- `ringbuf_free_space`: returns `capacity - 1 - ringbuf_used`.  
- `ringbuf_free`: frees allocated data array.

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
- `<stddef.h>`, `<stdint.h>` for types.  
- Uses `malloc`/`free` implicitly via implementation (not shown in header).

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` if capacity is zero or not a power of two.  
- `ringbuf_init` returns `RINGBUF_ENOMEM` if memory allocation fails.
