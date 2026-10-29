### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T21:37:04Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a circular byte buffer with power-of-two capacity for efficient FIFO data streaming.

#### Purpose
* Provides a ring buffer for managing byte-stream data.
* Ensures fast wrap-around indexing using bitwise masking.

#### Type
Implementation file (C).

#### Input
* `ringbuf_t` pointer for state management.
* `capacity` (size_t) for buffer allocation.
* `src` (uint8_t pointer) for write operations.
* `dst` (uint8_t pointer) for read operations.

#### Output
* `ringbuf_status_t` indicating initialization success or failure.
* `size_t` representing the number of bytes actually read or written.
* Modified `ringbuf_t` state (head, tail, data).

#### Core logic
* Enforces power-of-two capacity requirement via `is_power_of_two` to allow `(index & (capacity - 1))` for wrap-around logic.
* Tracks `head` (write position) and `tail` (read position) indices.
* Calculates `ringbuf_used` by masking the difference between head and tail.
* Handles circular wrap-around during `ringbuf_write` and `ringbuf_read` by splitting the operation into two `memcpy` calls: one to the end of the buffer and one from the start.
* Limits write operations to the available `ringbuf_free_space` to prevent overwriting unread data.

#### Public API
* `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t`: Allocates memory and initializes buffer.
* `ringbuf_free(ringbuf_t *rb) -> void`: Deallocates buffer memory and resets state.
* `ringbuf_used(const ringbuf_t *rb) -> size_t`: Returns number of bytes currently stored.
* `ringbuf_free_space(const ringbuf_t *rb) -> size_t`: Returns available bytes for writing.
* `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t`: Writes bytes to the buffer.
* `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t`: Reads bytes from the buffer.

#### Dependencies
* `ringbuf.h`
* `stdlib.h`
* `string.h`

#### Exceptions / Errors
* `RINGBUF_EINVAL`: Returned if `capacity` is not a power of two.
* `RINGBUF_ENOMEM`: Returned if `malloc` fails to allocate buffer memory.
