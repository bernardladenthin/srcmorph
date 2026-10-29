### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:05:45Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock-free, single-producer/single-consumer circular buffer for efficient binary data streaming with power-of-two sizing and O(1) operations.

#### Purpose
- Manages fixed-capacity circular buffers for enqueueing and dequeueing byte sequences with wrap-around logic.
- Enables O(1) memory allocation and data movement for high-performance streaming applications.

#### Responsibilities
- **Buffer Lifecycle Management**: Handles initialization, memory allocation, and deallocation of the underlying data array.
- **Data Transfer Operations**: Executes read/write operations that handle split transfers across the buffer boundary (wrap-around).
- **State Validation**: Enforces power-of-two capacity constraints and monitors buffer occupancy vs. free space.

#### Key units
- `ringbuf_t`: Struct holding `data` pointer, `capacity`, `head`, and `tail` indices for tracking buffer state.
- `ringbuf_status_t`: Enum defining operation results including `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `ringbuf_init`: Function allocating memory and validating capacity constraints during setup.
- `ringbuf_write`: Producer function copying bytes into the buffer, updating `head` via bitwise masking.
- `ringbuf_read`: Consumer function extracting bytes from the buffer, updating `tail` via bitwise masking.
- `ringbuf_used`: Query returning the current number of occupied bytes calculated as `(head - tail) & (capacity - 1)`.
- `ringbuf_free_space`: Query calculating available write slots as `capacity - 1 - used`.

#### Data flow
- Input `capacity` is validated to ensure it is a power of two; if valid, `malloc` allocates the `data` array.
- Producer calls `ringbuf_write` with source data; the function calculates free space and copies up to `len` bytes in at most two chunks (end of array then index 0), updating `head`.
- Consumer calls `ringbuf_read` with destination buffer; the function calculates used space and copies up to `len` bytes in at most two chunks (from `tail` then wrapping), updating `tail`.
- Status codes are returned immediately upon validation failure or memory error; actual byte counts are returned on partial transfers due to full/empty states.

#### Dependencies
- **Internal**: `ringbuf.h` defines types and prototypes used by `ringbuf.c`.
- **Standard Library**: `<stdlib.h>` for `malloc`/`free`; `<string.h>` for `memcpy`; `<stddef.h>` for `size_t`; `<stdint.h>` for `uint8_t`.

#### Cross-cutting
- **Performance Optimization**: Uses bitwise AND (`&`) instead of modulo operator for index wrapping to leverage power-of-two sizing.
- **Concurrency Model**: Designed for single-producer/single-consumer scenarios without explicit locking mechanisms.
- **Error Handling**: Returns specific status codes for invalid arguments (`RINGBUF_EINVAL`) or allocation failures (`RINGBUF_ENOMEM`).
- **Boundary Logic**: Reserves one slot in the buffer to distinguish between empty and full states.
