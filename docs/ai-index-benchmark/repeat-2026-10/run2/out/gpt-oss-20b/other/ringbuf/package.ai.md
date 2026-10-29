### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:17:47Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two sized byte queue for single‑producer, single‑consumer scenarios.

#### Purpose
- Offer a FIFO byte buffer with efficient wrap‑around.
- Enable non‑blocking read/write operations.

#### Responsibilities
- **API definition** – `ringbuf.h` declares the `ringbuf_t` type, status enum, and public functions.
- **Implementation** – `ringbuf.c` implements initialization, memory management, and data movement logic.
- **Validation** – capacity checks for power‑of‑two and non‑zero values.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head/tail indices.
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_init(ringbuf_t *, size_t)` – allocates and validates buffer.
- `ringbuf_free(ringbuf_t *)` – releases memory.
- `ringbuf_write(ringbuf_t *, const uint8_t *, size_t)` – enqueues bytes.
- `ringbuf_read(ringbuf_t *, uint8_t *, size_t)` – dequeues bytes.
- `ringbuf_used(const ringbuf_t *)` – reports occupied bytes.
- `ringbuf_free_space(const ringbuf_t *)` – reports available space.

#### Data flow
1. **Init** – caller supplies capacity; `ringbuf_init` allocates memory and sets indices.
2. **Write** – producer calls `ringbuf_write`; data copied into buffer, head advanced modulo capacity.
3. **Read** – consumer calls `ringbuf_read`; data copied out, tail advanced modulo capacity.
4. **Status** – `ringbuf_used` and `ringbuf_free_space` compute occupancy via modulo arithmetic.
5. **Cleanup** – `ringbuf_free` deallocates buffer.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Internal: `ringbuf.h` provides type and API declarations for `ringbuf.c`.

#### Cross-cutting
- Power‑of‑two capacity enforcement via `is_power_of_two`.
- Wrap‑around handled by copying data in up to two contiguous chunks.
- Error handling limited to status codes; no exceptions or logging.
- Designed for single‑producer/single‑consumer; no locking required.
