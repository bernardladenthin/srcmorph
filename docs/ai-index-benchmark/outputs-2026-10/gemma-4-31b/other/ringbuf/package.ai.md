### other/ringbuf
- H: 1.0
- C: 850DF002
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T21:42:51Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> A circular byte buffer for efficient FIFO data streaming between a single producer and a single consumer.

#### Purpose
* Decouple data production and consumption via a fixed-capacity FIFO byte-stream.
* Provide high-performance wrap-around indexing for asynchronous data transfer.

#### Responsibilities
* Buffer Lifecycle: Managing memory allocation and cleanup for the internal byte array.
* Data I/O: Handling byte-stream writes and reads with automatic circular wrap-around.
* Capacity Management: Tracking available free space and currently stored bytes.

#### Key units
* `ringbuf_t`: State structure managing the data buffer, producer-owned `head` index, and consumer-owned `tail` index.
* `ringbuf_status_t`: Status type for initialization results, including `RINGBUF_EINVAL` and `RINGBUF_ENOMEM`.
* `ringbuf_init` / `ringbuf_free`: Functions for allocating and deallocating the buffer.
* `ringbuf_write` / `ringbuf_read`: Functions for copying bytes into and out of the buffer.
* `ringbuf_used` / `ringbuf_free_space`: Functions for querying buffer occupancy.

#### Data flow
* Producer $\rightarrow$ `ringbuf_write` $\rightarrow$ `ringbuf_t` (internal data buffer) $\rightarrow$ `ringbuf_read` $\rightarrow$ Consumer.
* Wrap-around logic splits transfers into two `memcpy` operations when the data crosses the buffer boundary.

#### Dependencies
* Standard C libraries: `stdlib.h`, `string.h`, `stddef.h`, `stdint.h`.

#### Cross-cutting
* Performance Optimization: Enforces power-of-two capacity to use bitwise masking `(index & (capacity - 1))` instead of modulo for wrap-around.
* Concurrency: Implements a Single-Producer Single-Consumer (SPSC) pattern using volatile indices for thread-safe access.
* Error Handling: Uses `ringbuf_status_t` for initialization errors and returns `size_t` to indicate the number of bytes actually transferred.
