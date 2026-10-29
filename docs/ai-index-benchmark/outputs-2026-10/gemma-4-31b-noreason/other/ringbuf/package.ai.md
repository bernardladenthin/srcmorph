### other/ringbuf
- H: 1.0
- C: 850DF002
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T09:55:32Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a fixed-capacity, single-producer single-consumer (SPSC) circular buffer for efficient byte-stream data storage and retrieval.

#### Purpose
* Implement a lock-free FIFO queue for `uint8_t` data.
* Enable high-performance index wrapping using power-of-two capacity masking.

#### Responsibilities
* **Buffer Management**: Handling allocation, initialization, and deallocation of the underlying heap memory.
* **Data I/O**: Managing the copying of byte arrays into (`ringbuf_write`) and out of (`ringbuf_read`) the buffer, including wrap-around logic.
* **State Tracking**: Monitoring current occupancy (`ringbuf_used`) and available capacity (`ringbuf_free_space`).

#### Key units
* `ringbuf_t`: Central state structure containing the data pointer, capacity, and volatile `head`/`tail` indices.
* `ringbuf_status_t`: Enumeration for initialization results (e.g., `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).

#### Data flow
1. **Initialization**: `ringbuf_init` validates that the requested capacity is a power of two and allocates memory.
2. **Write Path**: `ringbuf_write` calculates available space, performs up to two `memcpy` operations to handle the wrap-around boundary, and advances the `head` index.
3. **Read Path**: `ringbuf_read` calculates available data, performs up to two `memcpy` operations to handle the wrap-around boundary, and advances the `tail` index.

#### Dependencies
* `stdlib.h`: Memory management (`malloc`, `free`).
* `string.h`: Memory copying (`memcpy`).
* `stdint.h` / `stddef.h`: Standard type definitions.

#### Cross-cutting
* **Performance Optimization**: Uses bitwise masking (`& (rb->capacity - 1)`) instead of modulo operators for index wrapping.
* **Concurrency**: Employs volatile indexing to support SPSC (Single-Producer Single-Consumer) patterns across different threads or contexts.
* **Capacity Constraint**: Enforces a "one slot empty" rule to differentiate between a completely full and completely empty buffer.
