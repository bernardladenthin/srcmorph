### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:38:25Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Buffers byte streams between a producer and consumer while preserving FIFO order within bounded storage.

#### Purpose
- Provide fixed-capacity byte buffering with partial reads and writes limited by available data and space.
- Distinguish full from empty by reserving one slot in a power-of-two allocation.

#### Responsibilities
- **Buffer lifecycle:** initialize storage with validated capacity and allocation-error reporting; free storage and reset buffer state.
- **FIFO transfer and capacity tracking:** report used bytes and free space; copy available bytes in or out, including across wraparound.

#### Key units
- `ringbuf_t` — stores the byte allocation, capacity, producer write index `head`, and consumer read index `tail`.
- `ringbuf_status_t` — initialization result codes: `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `ringbuf_init` / `ringbuf_free` — allocate and reset the buffer lifecycle.
- `ringbuf_used` / `ringbuf_free_space` — report occupied and writable byte counts.
- `ringbuf_write` / `ringbuf_read` — transfer up to the requested number of bytes in FIFO order.
- `is_power_of_two` / `min_size` — internal capacity-validation and transfer-size helpers.

#### Data flow
- `ringbuf_init` validates a nonzero power-of-two capacity, allocates storage, and initializes indices.
- `ringbuf_write` copies up to the free-space limit and advances `head`; `ringbuf_read` copies up to the used-byte limit and advances `tail`.
- Both transfers handle wraparound with up to two `memcpy` operations; index advancement uses a capacity mask.
- `ringbuf_free` releases allocation and resets buffer fields.

#### Dependencies
- `ringbuf.c` implements the API declared in `ringbuf.h`.
- Uses `<stdlib.h>` for allocation and deallocation, `<string.h>` for copying, and `size_t` / `uint8_t` for sizes and bytes.
- `ringbuf.h` uses `<stddef.h>` and `<stdint.h>` and supports C++ linkage.

#### Cross-cutting
- Capacity must be a nonzero power of two; usable capacity is `capacity - 1`.
- `ringbuf_init` reports invalid capacity with `RINGBUF_EINVAL` and allocation failure with `RINGBUF_ENOMEM`.
- The header describes producer-owned `head` and consumer-owned `tail`; no synchronization behavior is specified.
