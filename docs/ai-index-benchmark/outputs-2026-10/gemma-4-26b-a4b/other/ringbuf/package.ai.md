### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:06:08Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock-free circular buffer for asynchronous byte-stream data transfer between a single producer and a single consumer.

#### Purpose
* Implements an efficient FIFO (First-In-First-Out) byte-stream storage mechanism.
* Facilitates single-producer single-consumer (SPSC) communication using power-of-two optimized indexing.

#### Responsibilities
* **Buffer Management**: Handles memory allocation, deallocation, and capacity validation for `ringbuf_t`.
* **Data I/O**: Manages byte-level reading and writing, including handling wrap-around logic via split `memcpy` operations.
* **State Tracking**: Monitors used space and available capacity through head and tail index manipulation.

#### Key units
* `ringbuf_t`: The central structure containing the data pointer, capacity, and `volatile` head/tail indices.
* `ringbuf_status_t`: Status enumeration for error reporting (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
* `ringbuf_init`: Function to initialize the buffer and validate power-of-two capacity.
* `ringbuf_write`: Function to insert bytes into the buffer at the `head` index.
* `ringbuf_read`: Function to extract bytes from the buffer at the `tail` index.
* `ringbuf_used`: Function to query the current number of bytes stored.
* `ringbuf_free_space`: Function to query available bytes for upcoming writes.

#### Data flow
* **Input (Write)**: Data is passed via a source pointer (`src`) to `ringbuf_write`, which copies bytes into the internal `data` array, updating the `head` index.
* **Output (Read)**: Data is extracted from the internal `data` array via `ringbuf_read` into a destination pointer (`dst`), updating the `tail` index.
* **Indexing**: Both flows use bitwise masking `(index & (capacity - 1))` to wrap indices around the circular boundary.

#### Dependencies
* **Internal**: `ringbuf.h` defines the interface and types used by `ringbuf.c`.
* **External**: `stdlib.h` and `string.h` for memory management and data copying; `stdint.h` and `stddef.h` for standard integer and size types.

#### Cross-cutting
* **Concurrency**: Uses `volatile` qualifiers on indices to support lock-free single-producer single-consumer patterns.
* **Optimization**: Strictly enforces power-of-two capacities to replace expensive modulo operations with bitwise masking.
* **Error Handling**: Uses specific status codes to signal invalid arguments (non-power-of-two sizes) or memory allocation failures.
