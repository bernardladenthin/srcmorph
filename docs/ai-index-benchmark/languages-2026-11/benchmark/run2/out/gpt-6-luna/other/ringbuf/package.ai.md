### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:39:25Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides bounded FIFO byte-stream buffering for producer/consumer code, with partial reads and writes across circular-buffer wraparound.

#### Purpose
- Implements and declares a fixed-capacity, single-producer/single-consumer (SPSC) byte ring buffer.
- Preserves FIFO order while limiting transfers to available data or space.

#### Responsibilities
- **Buffer lifecycle and validation:** initialize only with a nonzero power-of-two capacity, allocate storage, and release/reset it.
- **Occupancy and transfer:** report used/free byte counts and copy requested bytes up to available capacity or stored data, including wraparound.
- **Capacity semantics:** reserve one slot to distinguish full from empty, so usable capacity is `capacity - 1`.

#### Key units
- `ringbuf_t` — stores the byte buffer, capacity, and producer (`head`) and consumer (`tail`) indices.
- `ringbuf_status_t` — defines initialization results `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `ringbuf_init` — validates capacity, allocates storage, and initializes indices.
- `ringbuf_free` — releases storage and resets buffer state.
- `ringbuf_used` / `ringbuf_free_space` — report stored bytes and writable bytes.
- `ringbuf_write` / `ringbuf_read` — copy partial byte transfers into or out of the ring.
- `is_power_of_two` / `min_size` — internal helpers for capacity validation and transfer sizing.

#### Data flow
- Initialization validates the requested capacity and allocates the byte array.
- Writes copy up to available free space and advance `head`; reads copy up to stored data and advance `tail`.
- Transfers split into up to two copies when crossing the buffer boundary; occupancy uses masked index differences.
- Cleanup frees the allocation and clears the buffer fields.

#### Dependencies
- `ringbuf.c` uses declarations from `ringbuf.h`, plus `<stdlib.h>` for allocation and `<string.h>` for copying.
- `ringbuf.h` uses `<stddef.h>` and `<stdint.h>` for `size_t` and `uint8_t`.

#### Cross-cutting
- Power-of-two capacity enables index updates with a capacity mask.
- Initialization reports `RINGBUF_EINVAL` for zero or non-power-of-two capacity and `RINGBUF_ENOMEM` on allocation failure.
- The API reports actual bytes copied for reads and writes; the interface is declared with C linkage when included from C++.
