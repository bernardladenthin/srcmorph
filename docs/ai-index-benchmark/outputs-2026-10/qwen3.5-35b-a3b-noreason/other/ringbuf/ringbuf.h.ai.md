### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:57:34Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-capacity, single-producer single-consumer byte ring buffer for efficient memory management.

#### Purpose
*   Provides a lock-free, fixed-capacity circular buffer for transferring byte streams between one producer and one consumer.
*   Manages memory allocation and deallocation for the buffer data structure.

#### Type
Header file (`ringbuf.h`) in C.
*   Defines `ringbuf_t` struct with `data`, `capacity`, `head`, and `tail` fields.
*   Defines `ringbuf_status_t` enum for error handling.
*   Declares C-compatible API functions.

#### Input
*   `ringbuf_t *rb`: Pointer to the ring buffer instance.
*   `size_t capacity`: Required power-of-two size for initialization.
*   `const uint8_t *src`: Source byte array for writing.
*   `uint8_t *dst`: Destination byte array for reading.
*   `size_t len`: Maximum number of bytes to transfer.

#### Output
*   `ringbuf_status_t`: Status code indicating success (`RINGBUF_OK`) or failure (`RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
*   `size_t`: Actual number of bytes written or read.
*   `size_t`: Current number of used bytes or available free space.
*   Side effects: Modifies `head` and `tail` indices within the `ringbuf_t` struct.

#### Core logic
*   Enforces power-of-two capacity constraint during initialization; rejects zero or invalid sizes.
*   Allocates memory for the `data` buffer; returns `RINGBUF_ENOMEM` on failure.
*   Uses modulo arithmetic (implied by power-of-two capacity) to wrap `head` and `tail` indices.
*   Respects single-producer/single-consumer model using `volatile` `head` (write) and `tail` (read) pointers.
*   Limits write operations to available free space and read operations to available used space.
*   Returns actual transferred byte count, which may be less than requested `len`.

#### Public API
*   `ringbuf_init(rb, capacity) -> ringbuf_status_t`: Initializes buffer with specified capacity.
*   `ringbuf_free(rb) -> void`: Deallocates buffer memory.
*   `ringbuf_write(rb, src, len) -> size_t`: Copies bytes from source into buffer.
*   `ringbuf_read(rb, dst, len) -> size_t`: Copies bytes from buffer to destination.
*   `ringbuf_used(rb) -> size_t`: Returns current number of occupied bytes.
*   `ringbuf_free_space(rb) -> size_t`: Returns current available capacity for writing.

#### Dependencies
*   `<stddef.h>`: For `size_t`.
*   `<stdint.h>`: For `uint8_t`.

#### Exceptions / Errors
*   `RINGBUF_EINVAL`: Triggered if capacity is zero or not a power of two.
*   `RINGBUF_ENOMEM`: Triggered if memory allocation fails during initialization.
