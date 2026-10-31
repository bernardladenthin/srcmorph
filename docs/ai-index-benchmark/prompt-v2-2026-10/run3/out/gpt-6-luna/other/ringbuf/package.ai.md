### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:00:47Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded circular byte storage for producer-consumer data transfer, with partial reads and writes when capacity is limited.

#### Purpose
- Initialize, manage, and transfer bytes through a fixed-capacity ring buffer.
- Support a single-producer/single-consumer usage model with one slot reserved to distinguish full from empty.

#### Responsibilities
- Buffer lifecycle and validation: allocate storage for nonzero power-of-two capacity and release/reset it.
- Occupancy and transfer: report used and writable bytes, and copy available data across wraparound.

#### Key units
- `ringbuf_t` — stores the byte allocation, capacity, and producer (`head`) and consumer (`tail`) indices.
- `ringbuf_status_t` — reports initialization success or invalid capacity/allocation failure.
- `ringbuf_init` / `ringbuf_free` — initialize and release buffer storage.
- `ringbuf_used` / `ringbuf_free_space` — report occupied and writable byte counts.
- `ringbuf_write` / `ringbuf_read` — transfer up to the available capacity or requested length.
- `is_power_of_two`, `min_size` — internal capacity-validation and length-selection helpers.

#### Data flow
- `ringbuf_init` validates capacity, allocates storage, and resets indices.
- Writes copy available source bytes into the buffer, wrapping as needed, then advance `head`; reads copy available bytes to the destination, wrapping as needed, then advance `tail`.
- Occupancy is derived from the indices; usable capacity is `capacity - 1`.

#### Dependencies
- `ringbuf.h` declares the API, `ringbuf_t`, and status constants.
- Standard C library: `<stdlib.h>` for allocation and `<string.h>` for copying; `<stddef.h>` and `<stdint.h>` provide `size_t` and `uint8_t`.

#### Cross-cutting
- Capacity must be a nonzero power of two; index wraparound uses a capacity mask.
- `ringbuf_write` and `ringbuf_read` are partial operations and return the number of bytes actually transferred.
- The header describes producer-owned `head` and consumer-owned `tail` for SPSC use; no synchronization mechanism is specified.
- Initialization errors use `RINGBUF_EINVAL` for invalid capacity and `RINGBUF_ENOMEM` for allocation failure.
