### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:02:25Z
- G: 
- A: 0.0.0
- X: file
---
> Fixed-capacity byte ring buffer for a single producer and a single consumer.

#### Purpose
A fixed-capacity, single-producer/single-consumer byte ring buffer implementation.

#### Type
C header file (`ringbuf.h`). Defines the `ringbuf_t` struct and functions for initialization, freeing, writing, reading, and querying status.

#### Input
- `ringbuf_t *rb`: Pointer to the ring buffer instance.
- `size_t capacity`: Buffer capacity (must be a power of two).
- `const uint8_t *src`: Source data pointer for writing.
- `uint8_t *dst`: Destination data pointer for reading.
- `size_t len`: Number of bytes to read or write.

#### Output
- `ringbuf_status_t`: Status code (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `size_t`: Number of bytes actually copied from `ringbuf_write` or `ringbuf_read`.
- `size_t`: Current used bytes from `ringbuf_used`, free space from `ringbuf_free_space`.

#### Core logic
- Buffer capacity is fixed; one slot is reserved never to be used, so usable capacity is `capacity - 1`.
- `head` (producer-owned) tracks the next write index; `tail` (consumer-owned) tracks the next read index.
- `ringbuf_write` copies data from `src` into the buffer at `head`, advances `head` modulo `capacity`, and returns the actual byte count copied (limited by available space).
- `ringbuf_read` copies data from the buffer at `tail` to `dst`, advances `tail` modulo `capacity`, and returns the actual byte count copied (limited by available data).
- `ringbuf_used` computes the number of bytes currently stored as `(head - tail) & (capacity - 1)`.
- `ringbuf_free_space` computes available space as `capacity - 1 - ringbuf_used`.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t`: Initializes the ring buffer with the given capacity. Returns `RINGBUF_EINVAL` if capacity is zero or not a power of two; `RINGBUF_ENOMEM` if allocation fails.
- `ringbuf_free(ringbuf_t *rb)`: Frees the buffer's data array.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t`: Writes up to `len` bytes from `src` into the buffer. Returns the number of bytes actually written.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t`: Reads up to `len` bytes from the buffer into `dst`. Returns the number of bytes actually read.
- `ringbuf_used(const ringbuf_t *rb) -> size_t`: Returns the number of bytes currently stored in the buffer.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t`: Returns the number of free bytes available for writing.

#### Dependencies
- `<stddef.h>`: For `size_t`.
- `<stdint.h>`: For `uint8_t`.
- Compiler support for `volatile` keyword (used for `head` and `tail` to indicate multi-threaded access).

#### Exceptions / Errors
- `RINGBUF_EINVAL`: Returned if `capacity` is zero or not a power of two during `ringbuf_init`.
- `RINGBUF_ENOMEM`: Returned if memory allocation fails during `ringbuf_init`.
- Writing when buffer is full: Returns 0 bytes written (no exception thrown, limited by available space).
- Reading when buffer is empty: Returns 0 bytes read (no exception thrown).
