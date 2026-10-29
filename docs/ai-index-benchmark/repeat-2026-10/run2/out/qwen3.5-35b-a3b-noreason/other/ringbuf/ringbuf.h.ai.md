### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:05:14Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-capacity, single-producer single-consumer byte ring buffer for efficient memory management.

#### Purpose
*   Implements a lock-free, fixed-capacity circular buffer for single-producer/single-consumer data transfer.
*   Manages dynamic memory allocation and deallocation for the buffer data array.

#### Type
Header file (`ringbuf.h`) in C language.
*   Defines `ringbuf_t` struct with `data`, `capacity`, `head`, and `tail` fields.
*   Defines `ringbuf_status_t` enum for operation results.
*   Declares functions with `extern "C"` linkage for C++ compatibility.

#### Input
*   `ringbuf_t *rb`: Pointer to the ring buffer structure instance.
*   `size_t capacity`: Required power-of-two size for initialization.
*   `const uint8_t *src`: Source byte array for writing data.
*   `uint8_t *dst`: Destination byte array for reading data.
*   `size_t len`: Maximum number of bytes to transfer in read/write operations.

#### Output
*   `ringbuf_status_t`: Status code indicating success (`RINGBUF_OK`) or errors (`RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
*   `size_t`: Actual number of bytes copied by `ringbuf_write` or `ringbuf_read`.
*   `size_t`: Current number of occupied bytes (`ringbuf_used`) or available space (`ringbuf_free_space`).
*   Side effects: Modifies `head` and `tail` indices within the `ringbuf_t` structure.

#### Core logic
*   Enforces power-of-two capacity constraint during initialization to enable bitwise masking for index wrapping.
*   Reserves one slot to distinguish between empty and full buffer states.
*   Updates `head` index for producer writes and `tail` index for consumer reads using volatile semantics.
*   Calculates actual transfer count based on available space (write) or available data (read).
*   Returns error codes if capacity is invalid or memory allocation fails.

#### Public API
*   `ringbuf_init(rb, capacity) -> ringbuf_status_t`: Allocates memory and initializes buffer with specified capacity.
*   `ringbuf_free(rb) -> void`: Deallocates buffer memory and resets structure.
*   `ringbuf_write(rb, src, len) -> size_t`: Copies up to `len` bytes from source into the buffer.
*   `ringbuf_read(rb, dst, len) -> size_t`: Copies up to `len` bytes from buffer to destination.
*   `ringbuf_used(rb) -> size_t`: Returns current number of bytes stored in the buffer.
*   `ringbuf_free_space(rb) -> size_t`: Returns remaining capacity available for writing.

#### Dependencies
*   `<stddef.h>`: For `size_t`.
*   `<stdint.h>`: For `uint8_t`.

#### Exceptions / Errors
*   `RINGBUF_EINVAL`: Triggered if capacity is zero or not a power of two.
*   `RINGBUF_ENOMEM`: Triggered if memory allocation for the data array fails.
