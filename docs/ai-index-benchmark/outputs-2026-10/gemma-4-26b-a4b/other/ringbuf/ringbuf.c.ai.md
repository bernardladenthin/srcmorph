### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:57:45Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a circular buffer implementation for byte-stream data management using power-of-two sizing.

#### Purpose
* Implements a ring buffer (circular buffer) for efficient FIFO data storage and retrieval.
* Optimizes index wrapping using bitwise masking for power-of-two capacities.

#### Type
C implementation file.

#### Input
* `ringbuf_t *rb`: Pointer to the ring buffer structure.
* `size_t capacity`: Requested buffer size (must be power of two).
* `const uint8_t *src`: Source byte array for writes.
* `uint8_t *dst`: Destination byte array for reads.
* `size_t len`: Number of bytes to read or write.

#### Output
* `ringbuf_status_t`: Status codes (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
* `size_t`: Number of bytes actually written or read.
* Side effects: Updates `head`, `tail`, and `data` within `ringbuf_t`.

#### Core logic
* **Initialization**: Validates that `capacity` is a power of two, allocates memory, and resets `head` and `tail` to zero.
* **Index Masking**: Uses `(index & (capacity - 1))` to handle wrap-around, requiring power-of-two sizes.
* **Write Logic**: Calculates available space, performs up to two `memcpy` operations (one to the end of the buffer, one at the start), and updates `head`.
* **Read Logic**: Calculates available data, performs up to two `memcpy` operations (one from the tail to the end, one from the start), and updates `tail`.
* **Space Calculation**: Determines used and free space via bitwise arithmetic on `head` and `tail`.

#### Public API
* `ringbuf_init(rb, capacity) -> ringbuf_status_t` Initializes the ring buffer.
* `ringbuf_free(rb) -> void` Deallocates buffer memory and resets state.
* `ringbuf_used(rb) -> size_t` Returns number of bytes currently stored.
* `ringbuf_free_space(rb) -> size_t` Returns available bytes for writing.
* `ringbuf_write(rb, src, len) -> size_t` Writes bytes into the buffer.
* `ringbuf_read(rb, dst, len) -> size_t` Reads bytes from the buffer.

#### Dependencies
* `ringbuf.h`
* `stdlib.h`
* `string.h`

#### Exceptions / Errors
* `RINGBUF_EINVAL`: Returned if `capacity` is not a power of two.
* `RINGBUF_ENOMEM`: Returned if memory allocation for `rb->data` fails.
