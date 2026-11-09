### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:57:47Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Provides a fixed‑size byte ring buffer for single‑producer/single‑consumer use.

#### Purpose
- Implements a lock‑free byte buffer with power‑of‑two capacity.
- Supports initialization, deallocation, read/write, and space queries.

#### Type
- Header file (`ringbuf.h`) in C.  
- Defines `ringbuf_t` struct, `ringbuf_status_t` enum, and related functions.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity value.  
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – source buffer and length.  
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – destination buffer and length.

#### Output
- `ringbuf_init` returns `ringbuf_status_t`.  
- `ringbuf_write` and `ringbuf_read` return number of bytes actually transferred.  
- `ringbuf_used` and `ringbuf_free_space` return byte counts.

#### Core logic
- Capacity must be a power of two; one slot unused to distinguish full/empty.  
- `head` (producer) and `tail` (consumer) indices wrap via modulo capacity.  
- `ringbuf_write` copies up to available free space, updates `head`.  
- `ringbuf_read` copies up to available data, updates `tail`.  
- `ringbuf_used` computes `(head - tail) & (capacity - 1)`.  
- `ringbuf_free_space` computes `(capacity - 1) - used`.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocate buffer.  
- `void ringbuf_free(ringbuf_t *rb)` – free allocated memory.  
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – write bytes.  
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – read bytes.  
- `size_t ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.  
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – bytes available for writing.

#### Constants and configuration
- `RINGBUF_OK = 0`  
- `RINGBUF_EINVAL = -1` – capacity invalid.  
- `RINGBUF_ENOMEM = -2` – allocation failure.

#### Dependencies
- `<stddef.h>`, `<stdint.h>` for standard types.  
- Uses `malloc`/`free` implicitly via implementation (not shown).

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` if capacity is zero or not a power of two.  
- Returns `RINGBUF_ENOMEM` if memory allocation fails.
