### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:51:31Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded FIFO byte-stream storage for transferring data between a single producer and consumer.

#### Purpose
- Implements and declares a fixed-capacity circular byte buffer with partial reads and writes.
- Reserves one slot to distinguish full from empty, so usable capacity is `capacity - 1`.

#### Responsibilities
- Buffer lifecycle and validation: allocate, initialize, and free storage; require a nonzero power-of-two capacity.
- FIFO transfer and occupancy: copy available bytes across wraparound and report used and free byte counts.

#### Key units
- `ringbuf_t` — buffer storage, capacity, and `head`/`tail` indices.
- `ringbuf_status_t` — initialization results: `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `ringbuf_init`, `ringbuf_free` — initialize and release buffer storage.
- `ringbuf_write`, `ringbuf_read` — transfer bytes into and out of the buffer, returning actual byte counts.
- `ringbuf_used`, `ringbuf_free_space` — report occupied and writable bytes.

#### Data flow
- `ringbuf_write` copies source bytes into available space from `head`; `ringbuf_read` copies buffered bytes to the destination from `tail`.
- Transfers use up to two `memcpy` operations when data crosses the array boundary, then advance indices using a power-of-two mask.

#### Dependencies
- `ringbuf.c` uses `ringbuf.h`, `<stdlib.h>` for allocation, and `<string.h>` for copying.
- `ringbuf.h` uses `<stddef.h>` and `<stdint.h>` and provides C linkage for C++ callers.

#### Cross-cutting
- Designed for single-producer, single-consumer ownership of `head` and `tail`.
- Power-of-two capacity enables masked wraparound; one reserved slot distinguishes full from empty.
- Initialization reports invalid capacity with `RINGBUF_EINVAL` and allocation failure with `RINGBUF_ENOMEM`.
