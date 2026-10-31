### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:26:11Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a power-of-two sized circular byte buffer for storing and retrieving data with read/write operations.

#### Purpose
- Provides a fixed-capacity ring buffer for efficient byte-level storage and retrieval.
- Validates capacity is a power of two and manages head/tail pointers.

#### Type
- C, functions and struct ringbuf_t (defined in ringbuf.h)
- ringbuf_status_t status codes for initialization and error handling

#### Input
- ringbuf_init: rb pointer, capacity (size_t)
- ringbuf_write: rb pointer, src pointer, len
- ringbuf_read: rb pointer, dst pointer, len
- ringbuf_free: rb pointer

#### Output
- ringbuf_init: RINGBUF_OK or error code
- ringbuf_free: void
- ringbuf_used: size_t
- ringbuf_free_space: size_t
- ringbuf_write: number of bytes written
- ringbuf_read: number of bytes read

#### Core logic
- ringbuf_init: checks power-of-two capacity, allocates buffer, sets head/tail.
- ringbuf_free: frees buffer, resets fields to zero.
- ringbuf_used: computes used bytes as (head - tail) & (capacity - 1).
- ringbuf_free_space: computes free space as capacity - 1 - used.
- ringbuf_write: copies up to two chunks (end of buffer then wrap) into available space.
- ringbuf_read: copies stored data from tail, handling wrap-around.

#### Public API
- ringbuf_init(ringbuf_t *rb, size_t capacity) -> RINGBUF_OK/error: Initializes a power-of-two sized ring buffer.
- ringbuf_free(ringbuf_t *rb) -> void: Releases the buffer and resets state.
- ringbuf_used(const ringbuf_t *rb) -> size_t: Returns bytes currently stored.
- ringbuf_free_space(const ringbuf_t *rb) -> size_t: Returns remaining free space.
- ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t: Copies chunks into buffer, handling wrap.
- ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t: Copies stored data to destination.

#### Dependencies
- ringbuf.h: struct ringbuf_t, ringbuf_status_t
- stdlib.h: malloc, free
- string.h: memcpy
- uint8_t: defined in stdint.h (not included)

#### Exceptions / Errors
- RINGBUF_EINVAL: capacity not a power of two.
- RINGBUF_ENOMEM: allocation failure.
- No other exceptions or error conditions.
