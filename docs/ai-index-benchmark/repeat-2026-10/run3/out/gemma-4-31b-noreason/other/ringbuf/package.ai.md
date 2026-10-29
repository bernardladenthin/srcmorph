### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:32:16Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a fixed-capacity, single-producer single-consumer (SPSC) circular buffer for efficient byte-stream data storage and retrieval.

#### Purpose
* Implement a lock-free FIFO queue for `uint8_t` data.
* Optimize wrap-around indexing using power-of-two capacity masking.

#### Responsibilities
* **Memory Management**: Allocation and deallocation of the buffer storage.
* **Buffer Synchronization**: Managing `head` (producer) and `tail` (consumer) indices to track data availability.
* **Data Transfer**: Handling contiguous and wrapped memory copies for reading and writing byte streams.

#### Key units
* `ringbuf_t`: Central structure managing the data buffer, capacity, and read/write indices.
* `ringbuf_status_t`: Enum for initialization and error reporting (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
* `ringbuf_init`: Initializes the buffer and validates power-of-two capacity.
* `ringbuf_write`: Copies bytes into the buffer, handling wrap-around via `memcpy`.
* `ringbuf_read`: Extracts bytes from the buffer, handling wrap-around via `memcpy`.
* `ringbuf_used` / `ringbuf_free_space`: Utility functions to calculate current buffer occupancy.

#### Data flow
1. **Initialization**: `ringbuf_init` allocates memory based on a power-of-two capacity.
2. **Write Path**: `ringbuf_write` receives a `uint8_t` source buffer $\rightarrow$ calculates available space $\rightarrow$ performs one or two `memcpy` operations to the internal buffer $\rightarrow$ updates the `head` index.
3. **Read Path**: `ringbuf_read` identifies available data $\rightarrow$ performs one or two `memcpy` operations from the internal buffer to a destination $\rightarrow$ updates the `tail` index.

#### Dependencies
* `stdlib.h`: Memory allocation (`malloc`, `free`).
* `string.h`: Memory copying (`memcpy`).
* `stdint.h` / `stddef.h`: Standard integer and size types.

#### Cross-cutting
* **Indexing Strategy**: Uses bitwise AND (`& (capacity - 1)`) instead of modulo for performance, requiring capacity to be a power of two.
* **Full/Empty Distinction**: Reserves one byte of the buffer to differentiate between a completely full and completely empty state.
* **Error Handling**: Uses a dedicated status enum (`ringbuf_status_t`) for initialization failures.
