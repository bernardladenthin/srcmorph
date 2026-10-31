### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:04:55Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a fixed-capacity circular byte buffer for producer-consumer data streaming with power-of-two capacity.

#### Purpose
- Implements a thread-unsafe ring buffer for storing and retrieving byte streams.
- Supports bounded writes and reads with wrap-around memory access.

#### Responsibilities
- Buffer lifecycle: initialize with power-of-two capacity, allocate backing memory, and free resources.
- Byte streaming: write bytes into the buffer and read bytes out, advancing head and tail indices with bitwise masking for wrap-around.
- Capacity tracking: report used bytes and free space to callers so they can respect bounded limits.

#### Key units
- `ringbuf_t`: struct holding `data` pointer, `capacity`, volatile `head` producer index, and volatile `tail` consumer index.
- `ringbuf_status_t`: enum with `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM` status codes.
- `ringbuf_init`: validates capacity is a power of two, allocates backing array via `malloc`, initializes head/tail to zero.
- `ringbuf_free`: deallocates buffer memory and resets state.
- `ringbuf_write`: copies data in at most two chunks (head to array end, then wrap from index 0) and advances head.
- `ringbuf_read`: copies data in at most two chunks (tail to array end, then wrap from index 0) and advances tail.
- `ringbuf_used`: computes occupied slots using `(head - tail) & (capacity - 1)`.
- `ringbuf_free_space`: returns capacity minus one minus used count.

#### Data flow
- Producer calls `ringbuf_write` with source bytes and length; bytes are copied into the backing array in up to two wrapped chunks and `head` advances.
- Consumer calls `ringbuf_read` with destination buffer and length; bytes are copied out in up to two wrapped chunks and `tail` advances.
- Callers query `ringbuf_used` and `ringbuf_free_space` before writing or reading to avoid overflow/underflow.

#### Dependencies
- `ringbuf.h` defines types and status codes; `ringbuf.c` implements the logic.
- `stdlib.h` for `malloc` / `free`; `string.h` for `memcpy`; `<stddef.h>` for `size_t`; `<stdint.h>` for `uint8_t`.

#### Cross-cutting
- Single-producer single-consumer design with volatile head/tail indices; documented as thread-unsafe in the implementation despite the header describing it as thread-safe.
- One slot is reserved to distinguish full from empty, yielding capacity - 1 usable bytes.
- No runtime bounds checking in write/read; callers must respect free space and used counts.
- Errors surfaced via `ringbuf_status_t`: `RINGBUF_EINVAL` for invalid or zero capacity, `RINGBUF_ENOMEM` for allocation failure.
