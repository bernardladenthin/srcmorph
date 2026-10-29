### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:44:47Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-capacity, single-producer single-consumer byte ring buffer for efficient memory management.

#### Purpose
*   Implements a lock-free, circular buffer for transferring byte streams between one producer and one consumer.
*   Enforces power-of-two capacity constraints to optimize index wrapping logic.

#### Type
*   Header file (`ringbuf.h`) defining C structures and functions for a ring buffer.
*   Defines `ringbuf_t` struct and `ringbuf_status_t` enum.

#### Input
*   `ringbuf_t *rb`: Pointer to the ring buffer instance.
*   `size_t capacity`: Required power-of-two size for initialization.
*   `const uint8_t *src`: Source data buffer for writing.
*   `uint8_t *dst`: Destination buffer for reading.
*   `size_t len`: Maximum number of bytes to transfer.

#### Output
*   `ringbuf_status_t`: Status code indicating success (`RINGBUF_OK`) or errors (`RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
*   `size_t`: Actual number of bytes written or read.
*   `size_t`: Current count of used bytes or available free space.
*   Side effects: Modifies internal `head` and `tail` indices within `ringbuf_t`.

#### Core logic
*   Validates that `capacity` is non-zero and a power of two during `ringbuf_init`.
*   Allocates `data` buffer; returns `RINGBUF_ENOMEM` if allocation fails.
*   Uses `head` index for producer writes and `tail` index for consumer reads.
*   Wraps indices using bitwise AND with `capacity - 1` (implicit in circular logic).
*   Limits write operations to prevent overwriting unread data (one slot reserved).
*   Limits read operations to prevent reading unwritten data.
*   Returns actual bytes transferred, which may be less than requested `len`.

#### Public API
*   `ringbuf_init(rb, capacity) -> ringbuf_status_t`: Initializes buffer with specified power-of-two capacity.
*   `ringbuf_free(rb) -> void`: Deallocates internal data buffer.
*   `ringbuf_write(rb, src, len) -> size_t`: Copies data from source into buffer.
*   `ringbuf_read(rb, dst, len) -> size_t`: Copies data from buffer to destination.
*   `ringbuf_used(rb) -> size_t`: Returns current number of bytes stored.
*   `ringbuf_free_space(rb) -> size_t`: Returns remaining writable capacity.

#### Dependencies
*   `<stddef.h>`: For `size_t`.
*   `<stdint.h>`: For `uint8_t`.

#### Exceptions / Errors
*   `RINGBUF_EINVAL`: Thrown if `capacity` is zero or not a power of two.
*   `RINGBUF_ENOMEM`: Thrown if memory allocation for `data` fails.
