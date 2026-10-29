### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:41:15Z
- G: 
- A: 0.0.0
- X: file
---
> Fixed-capacity byte ring buffer for a single producer and a single consumer.

#### Purpose
A fixed-capacity, power-of-two capacity byte buffer enabling lock-free single-producer and single-consumer data flow.

#### Type
Header file (`ringbuf.h`) implementing a C struct `ringbuf_t` with volatile indices for thread-safe single-producer/single-consumer access.

#### Input
- `ringbuf_t *rb`: pointer to the buffer instance.
- `size_t capacity`: power-of-two allocation size.
- `const uint8_t *src`: source data pointer for writes.
- `uint8_t *dst`: destination data pointer for reads.

#### Output
- `ringbuf_status_t`: `RINGBUF_OK`, `RINGBUF_EINVAL`, or `RINGBUF_ENOMEM` from init.
- `size_t` from `ringbuf_write`/`ringbuf_read`: actual bytes copied.
- `size_t` from `ringbuf_used`/`ringbuf_free_space`: current occupancy and remaining capacity.

#### Core logic
- Capacity enforced as power of two; one slot permanently reserved, yielding `capacity - 1` usable bytes.
- `head` (volatile) tracks next write position, owned by producer.
- `tail` (volatile) tracks next read position, owned by consumer.
- `ringbuf_write` masks indices with `capacity - 1` to wrap, copies data, and advances `head`.
- `ringbuf_read` masks indices with `capacity - 1` to wrap, copies data, and advances `tail`.
- `ringbuf_used` computes `(head - tail) & (capacity - 1)` to report occupancy.
- `ringbuf_free_space` computes `(capacity - 1) - used` to report available slots.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t`: validates power-of-two capacity, allocates data buffer, sets head/tail to zero.
- `ringbuf_free(ringbuf_t *rb)`: frees the underlying data buffer.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t`: copies up to `len` bytes from `src` into buffer, returns actual bytes written.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t`: copies up to `len` bytes from buffer into `dst`, returns actual bytes read.
- `ringbuf_used(const ringbuf_t *rb) -> size_t`: returns number of bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t`: returns number of bytes that can be written.

#### Dependencies
- `<stddef.h>` for `size_t`.
- `<stdint.h>` for `uint8_t`.
- C11 or later for `volatile` keyword semantics on indices.

#### Exceptions / Errors
- `RINGBUF_EINVAL`: returned if `capacity` is zero or not a power of two during `ringbuf_init`.
- `RINGBUF_ENOMEM`: returned if data buffer allocation fails during `ringbuf_init`.
