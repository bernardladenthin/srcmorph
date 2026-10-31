### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:34:10Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded FIFO byte-stream buffering for producer/consumer data transfer, preserving order as reads and writes wrap around the buffer.

#### Purpose
- Declare and implement a fixed-capacity byte ring buffer with initialization, cleanup, occupancy queries, reads, and writes.
- Support single-producer, single-consumer use; reserve one slot to distinguish full from empty.

#### Responsibilities
- **Buffer lifecycle and validation:** allocate storage for nonzero power-of-two capacities and release/reset it.
- **Byte transfer and accounting:** copy bytes in or out up to available space or buffered data, and report used/free byte counts.
- **Wraparound handling:** split transfers at the array boundary and advance indices with a capacity mask.

#### Key units
- `ringbuf_t` — holds the byte storage, capacity, and volatile `head` and `tail` indices.
- `ringbuf_status_t` — defines `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM` initialization results.
- `ringbuf_init` / `ringbuf_free` — initialize allocated state or release storage and reset fields.
- `ringbuf_write` / `ringbuf_read` — write or read available bytes in FIFO order.
- `ringbuf_used` / `ringbuf_free_space` — report occupied and writable byte counts.

#### Data flow
- `ringbuf_init` validates capacity, allocates storage, and initializes indices.
- `ringbuf_write` copies source bytes into free space; `ringbuf_read` copies buffered bytes to the destination. Both handle wraparound and return the number of bytes transferred.
- Occupancy queries derive used and free space from the indices; `ringbuf_free` releases storage and resets state.

#### Dependencies
- `ringbuf.c` uses declarations and types from `ringbuf.h`, plus standard `malloc`, `free`, and `memcpy`.
- `ringbuf.h` uses `<stddef.h>` for `size_t` and `<stdint.h>` for `uint8_t`.

#### Cross-cutting
- Power-of-two capacity enables index wrapping with a capacity mask; usable capacity is `capacity - 1`.
- `head` is producer-owned and `tail` is consumer-owned; both are `volatile` for the stated single-producer/single-consumer design.
- Initialization reports invalid capacity with `RINGBUF_EINVAL` and allocation failure with `RINGBUF_ENOMEM`; other operations do not validate pointers or initialization.
