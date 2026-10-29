### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:59:46Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a fixed-capacity, power-of-two circular byte buffer for efficient single-producer single-consumer (SPSC) data exchange.

#### Purpose
* Implement a FIFO queue for byte streams with optimized wrap-around indexing.
* Manage asynchronous read/write operations using a lock-free approach.

#### Responsibilities
* **Memory Management**: Allocation and deallocation of the internal heap buffer.
* **Buffer Indexing**: Handling head/tail pointers and wrap-around logic via bitwise masking.
* **Data Transfer**: Executing fragmented `memcpy` operations to handle circular boundary crossings.

#### Key units
* `ringbuf_t`: Central structure managing the data buffer, head/tail indices, and capacity.
* `ringbuf_status_t`: Enum for initialization results (e.g., `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
* `ringbuf_init` / `ringbuf_free`: Lifecycle management for the buffer.
* `ringbuf_write` / `ringbuf_read`: Primary data ingress and egress functions.
* `ringbuf_used` / `ringbuf_free_space`: Capacity tracking utilities.

#### Data flow
1. **Initialization**: `ringbuf_init` allocates memory based on a power-of-two capacity.
2. **Ingress**: `ringbuf_write` copies bytes from a source pointer to the buffer, updating the `head` index.
3. **Egress**: `ringbuf_read` copies bytes from the buffer to a destination pointer, updating the `tail` index.
4. **Wrap-around**: Both read/write operations split transfers into two stages if the operation crosses the physical end of the array.

#### Dependencies
* `stdlib.h`: Used for `malloc` and `free`.
* `string.h`: Used for `memcpy`.
* `stdint.h` / `stddef.h`: Used for fixed-width types and size definitions.

#### Cross-cutting
* **Performance Optimization**: Uses bitwise masking `(index & (capacity - 1))` instead of modulo operators for wrap-around.
* **Concurrency**: Employs `volatile` qualifiers on indices to support SPSC (Single-Producer Single-Consumer) patterns without locks.
* **Constraints**: Enforces a power-of-two capacity and maintains one unused slot to differentiate between full and empty states.
