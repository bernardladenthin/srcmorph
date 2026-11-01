### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:01:27Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded byte storage for transferring streaming data between a producer and consumer.

#### Purpose
- Implements and declares a fixed-capacity ring buffer with wraparound byte reads and writes.
- Limits transfers to available data or space; reserves one slot to distinguish full from empty.

#### Responsibilities
- **Buffer lifecycle and status:** initialize power-of-two capacity, allocate storage, report invalid capacity or allocation failure, and free/reset state.
- **Byte transfer and capacity queries:** copy bytes across the wrap boundary and report actual transfer counts, used bytes, and free space.

#### Key units
- `ringbuf_t` — stores the byte array, capacity, and producer (`head`) and consumer (`tail`) indices.
- `ringbuf_status_t` — defines `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `ringbuf_init`, `ringbuf_free` — allocate and initialize the buffer, or release storage and reset fields.
- `ringbuf_write`, `ringbuf_read` — append and consume up to the available byte count.
- `ringbuf_used`, `ringbuf_free_space` — report occupied and writable bytes.
- `is_power_of_two`, `min_size` — internal implementation helpers.

#### Data flow
- `ringbuf_init` validates a nonzero power-of-two capacity, allocates storage, and initializes indices.
- `ringbuf_write` copies source bytes into available space; `ringbuf_read` copies stored bytes to the destination. Both split copies at the array boundary and advance indices using a capacity mask.
- `ringbuf_used` and `ringbuf_free_space` expose current occupancy and writable capacity; usable capacity is `capacity - 1`.

#### Dependencies
- `ringbuf.c` uses `ringbuf.h`, `<stdlib.h>` for allocation, and `<string.h>` for copying.
- `ringbuf.h` uses `<stddef.h>` and `<stdint.h>`; its declarations support C++ inclusion via `extern "C"`.

#### Cross-cutting
- Power-of-two capacity enables mask-based index wraparound.
- The API describes producer-owned `head` and consumer-owned `tail`; no additional concurrency guarantees are stated.
- Initialization errors use `RINGBUF_EINVAL` for invalid capacity and `RINGBUF_ENOMEM` for allocation failure.
