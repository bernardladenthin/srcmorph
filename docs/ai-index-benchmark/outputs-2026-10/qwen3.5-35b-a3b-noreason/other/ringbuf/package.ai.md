### other/ringbuf
- H: 1.0
- C: 850DF002
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:58:00Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock-free, single-producer single-consumer circular buffer for efficient byte stream management with power-of-two capacity constraints.

#### Purpose
- Manages a fixed-capacity circular buffer for high-performance read/write operations without external dependencies.
- Enforces single-producer/single-consumer semantics using volatile indices and power-of-two sizing.

#### Responsibilities
- **Buffer Lifecycle**: Initialization, memory allocation, and deallocation of the `ringbuf_t` structure.
- **Data Transfer**: Copying byte streams between external buffers and the internal circular array, handling wrap-around logic.
- **State Inspection**: Calculating current usage and available free space based on head/tail indices.
- **Validation**: Enforcing power-of-two capacity constraints and handling memory allocation failures.

#### Key units
- `ringbuf_t`: Struct holding `data` pointer, `capacity`, `head` (write), and `tail` (read) indices.
- `ringbuf_status_t`: Enum defining `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM` error codes.
- `ringbuf_init`: Function allocating memory and validating power-of-two capacity.
- `ringbuf_free`: Function releasing allocated memory and resetting state.
- `ringbuf_write`: Function copying bytes into the buffer with wrap-around handling.
- `ringbuf_read`: Function copying bytes out of the buffer with wrap-around handling.
- `ringbuf_used`: Function returning the count of occupied bytes.
- `ringbuf_free_space`: Function returning the count of available bytes for writing.

#### Data flow
- **Initialization**: `ringbuf_init` validates capacity, allocates `data` array, and sets `head`/`tail` to zero.
- **Write Path**: `ringbuf_write` calculates free space, splits copy into two chunks if wrapping occurs, updates `head` with bitwise masking, and returns bytes written.
- **Read Path**: `ringbuf_read` calculates used space, splits copy into two chunks if wrapping occurs, updates `tail` with bitwise masking, and returns bytes read.
- **State Query**: `ringbuf_used` and `ringbuf_free_space` compute values directly from `head`, `tail`, and `capacity` without modifying state.

#### Dependencies
- **Internal**: `ringbuf.h` (header definitions).
- **Standard Library**: `stdlib.h` (`malloc`, `free`), `string.h` (`memcpy`), `stddef.h` (`size_t`), `stdint.h` (`uint8_t`).

#### Cross-cutting
- **Concurrency Model**: Designed for single-producer/single-consumer; uses `volatile` qualifiers on `head` and `tail` to prevent compiler optimizations.
- **Performance Optimization**: Uses bitwise AND masking (`& (capacity - 1)`) instead of modulo for index wrapping due to power-of-two capacity.
- **Error Handling**: Returns specific status codes (`RINGBUF_EINVAL`, `RINGBUF_ENOMEM`) for invalid inputs or allocation failures.
- **Memory Safety**: Explicitly checks for power-of-two capacity and zero size during initialization.
