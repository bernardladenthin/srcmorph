### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:41:01Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed‑size byte ring buffer for single‑producer/single‑consumer use.

#### Purpose
- Implements a lock‑free circular buffer for byte streams.
- Supports initialization, cleanup, read/write, and space queries.

#### Type
- Header file (`ringbuf.h`) in C.  
- Defines `ringbuf_t` struct, `ringbuf_status_t` enum, and function prototypes.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity in bytes (must be power of two).  
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – source buffer and length.  
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – destination buffer and length.  
- `ringbuf_used(const ringbuf_t *rb)` – reads current used bytes.  
- `ringbuf_free_space(const ringbuf_t *rb)` – reads available space.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` (OK, EINVAL, ENOMEM).  
- `ringbuf_write` returns number of bytes written.  
- `ringbuf_read` returns number of bytes read.  
- `ringbuf_free` frees allocated memory.  
- `ringbuf_used` and `ringbuf_free_space` return size_t counts.

#### Core logic
- Buffer size must be power of two; one slot unused to distinguish full/empty.  
- `head` (producer) and `tail` (consumer) indices wrap via modulo capacity.  
- `ringbuf_write` copies up to `len` bytes, respecting free space, updates `head`.  
- `ringbuf_read` copies up to `len` bytes, respecting used data, updates `tail`.  
- `ringbuf_used` computes `(head - tail) & (capacity - 1)`.  
- `ringbuf_free_space` computes `(capacity - 1) - used`.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.  
- `void ringbuf_free(ringbuf_t *rb)` – free resources.  
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – write data.  
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – read data.  
- `size_t ringbuf_used(const ringbuf_t *rb)` – query used bytes.  
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – query free space.

#### Constants and configuration
- `RINGBUF_OK = 0`  
- `RINGBUF_EINVAL = -1` – capacity zero or not power of two  
- `RINGBUF_ENOMEM = -2` – allocation failure

#### Dependencies
- `<stddef.h>` – `size_t`  
- `<stdint.h>` – `uint8_t`  
- C standard library for memory allocation (`malloc`, `free` implied in implementation).

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` if capacity invalid.  
- `ringbuf_init` returns `RINGBUF_ENOMEM` if memory allocation fails.  
- No other explicit error handling in the header.
