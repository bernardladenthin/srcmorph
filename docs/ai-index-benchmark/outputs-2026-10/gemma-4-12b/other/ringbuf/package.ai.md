### other/ringbuf
- H: 1.0
- C: 850DF002
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T04:33:29Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a fixed-capacity circular buffer for thread-safe, single-producer/single-consumer byte stream communication.

#### Purpose
* Implements a FIFO byte storage mechanism for efficient data streaming.
* Manages memory allocation and wrap-around pointer arithmetic for circular data access.

#### Responsibilities
* **Buffer Management**: Handles dynamic memory allocation, initialization, and deallocation of the internal data array.
* **Stream Processing**: Provides methods to write from source arrays and read to destination buffers, handling wrap-around logic via two-chunk copies.
* **State Tracking**: Calculates and reports buffer occupancy and available free space.
* **Concurrency Control**: Uses `volatile` head and tail indices to facilitate communication between independent producer and consumer threads.

#### Key units
* `ringbuf_t`: The primary structure containing the data buffer and volatile head/tail indices.
* `ringbuf_init`: Allocates memory and validates power-of-two capacity constraints.
* `ringbuf_write`: Appends bytes to the buffer and advances the head pointer.
* `ringbuf_read`: Extracts bytes from the buffer and advances the tail pointer.
* `ringbuf_used` / `ringbuf_free_space`: Utility functions to query current buffer state.

#### Data flow
1. **Initialization**: `ringbuf_init` takes a `capacity` (power of two) and allocates the internal `data` array.
2. **Ingestion**: `ringbuf_write` accepts a `src` byte array, performs bitwise masking for wrap-around, and updates the `head` index.
3. **Extraction**: `ringbuf_read` accepts a `dst` byte array, performs wrap-around copies, and updates the `tail` index.
4. **Querying**: `ringbuf_used` and `ringbuf_free_space` calculate status based on the relative positions of `head` and `tail`.

#### Dependencies
* `stdlib.h` (Memory management)
* `string.h` (Data copying)
* `stddef.h` / `stdint.h` (Standard types)

#### Cross-cutting
* **Power-of-Two Constraint**: All operations rely on capacity being a power of two to enable efficient bitwise masking `(head - tail) & (capacity - 1)`.
* **Thread Safety**: Designed for single-producer/single-consumer (SPSC) patterns using `volatile` qualifiers.
* **Error Handling**: Uses `ringbuf_status_t` to communicate `OK`, `EINVAL` (invalid capacity), and `ENOMEM` (allocation failure).
