### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:55:46Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.

#### Purpose
- Expose a minimal, thread‑safe ring buffer API for byte‑level I/O.
- Offer allocation, deallocation, and capacity‑aware read/write operations.

#### Responsibilities
- **Buffer lifecycle**: initialization, memory allocation, and cleanup.
- **Data movement**: enqueue (`write`) and dequeue (`read`) with wrap‑around handling.
- **State inspection**: query used bytes and free space.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head/tail indices.
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`) for init results.
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocate and set up buffer.
- `ringbuf_free(ringbuf_t *rb)` – release resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – enqueue data.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – dequeue data.
- `ringbuf_used(const ringbuf_t *rb)` – current occupancy.
- `ringbuf_free_space(const ringbuf_t *rb)` – available capacity.

#### Data flow
1. **Init** → allocate `data` array, set `head = tail = 0`.
2. **Write** → copy up to `len` bytes from `src` into buffer, split into two chunks if wrap‑around occurs; update `head`.
3. **Read** → copy up to `len` bytes from buffer into `dst`, split into two chunks if wrap‑around occurs; update `tail`.
4. **Status queries** → compute `(head - tail) & (capacity - 1)` for used bytes; derive free space.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Internal header `ringbuf.h` defines types and API.

#### Cross-cutting
- **Power‑of‑two enforcement**: `is_power_of_two` validates capacity; errors returned via `RINGBUF_EINVAL`.
- **Modulo arithmetic**: wrap‑around handled by bitwise AND with `capacity - 1`.
- **Error handling**: `ringbuf_init` reports `RINGBUF_ENOMEM` on allocation failure; all other functions return byte counts, never error codes.
