### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:03:34Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-capacity circular byte buffer for producer-consumer data streaming with power-of-two capacity.

#### Purpose
- Implements a thread-unsafe ring buffer for storing and retrieving byte streams.
- Supports bounded writes and reads with wrap-around memory access.

#### Type
- C implementation file for ring buffer data structure.
- Depends on `ringbuf.h` for type and status definitions.

#### Input
- `ringbuf_init`: `ringbuf_t *rb`, `size_t capacity` (must be power of two).
- `ringbuf_write`: `ringbuf_t *rb`, `const uint8_t *src`, `size_t len`.
- `ringbuf_read`: `ringbuf_t *rb`, `uint8_t *dst`, `size_t len`.
- `ringbuf_used` / `ringbuf_free_space`: `const ringbuf_t *rb`.

#### Output
- `ringbuf_init`: `ringbuf_status_t` indicating OK, EINVAL, or ENOMEM.
- `ringbuf_write`: `size_t` number of bytes written.
- `ringbuf_read`: `size_t` number of bytes read.
- `ringbuf_free`: resets buffer state and releases memory.

#### Core logic
- `is_power_of_two` validates capacity using bitwise check.
- `ringbuf_init` allocates backing array via `malloc` and initializes head/tail to zero.
- `ringbuf_used` computes occupied slots using `(head - tail) & (capacity - 1)`.
- `ringbuf_free_space` returns capacity minus one minus used count.
- `ringbuf_write` copies data in at most two chunks: from head to array end, then wrapping from index 0.
- `ringbuf_read` copies data in at most two chunks: from tail to array end, then wrapping from index 0.
- Head and tail advance with bitwise mask `(capacity - 1)` for wrap-around.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` initializes buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` deallocates buffer memory.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` returns byte count in buffer.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` returns writable byte count.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` writes bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` reads bytes.

#### Dependencies
- `ringbuf.h`
- `stdlib.h` for `malloc` / `free`
- `string.h` for `memcpy`

#### Exceptions / Errors
- `RINGBUF_EINVAL` returned if capacity is not a power of two.
- `RINGBUF_ENOMEM` returned if `malloc` fails.
- No runtime bounds checking in write/read; callers must respect free space and used counts.
