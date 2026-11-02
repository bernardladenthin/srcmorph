### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:33:02Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.

#### Purpose
- Offer a fixed‑size, non‑blocking ring buffer for byte data.
- Expose initialization, read/write, and memory management APIs.

#### Responsibilities
- **Buffer Management** – allocate, free, and maintain head/tail indices.
- **Data Transfer** – read/write operations that handle wrap‑around.
- **Capacity Tracking** – compute used bytes and free space.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head, tail.
- `ringbuf_status_t` – enum of `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocate and set up buffer.
- `ringbuf_free(ringbuf_t *rb)` – release memory.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – copy up to `len` bytes, return written count.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – copy up to `len` bytes, return read count.
- `ringbuf_used(const ringbuf_t *rb)` – current byte count.
- `ringbuf_free_space(const ringbuf_t *rb)` – available capacity.

#### Data flow
1. **Initialization** – caller supplies `capacity`; `ringbuf_init` allocates buffer and sets indices to zero.
2. **Write** – producer calls `ringbuf_write`; data is copied into buffer, head advanced modulo capacity.
3. **Read** – consumer calls `ringbuf_read`; data is copied out, tail advanced modulo capacity.
4. **Metrics** – `ringbuf_used` and `ringbuf_free_space` compute values using modulo arithmetic.
5. **Cleanup** – `ringbuf_free` releases the buffer.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Internal: `ringbuf.h` defines types and constants used by `ringbuf.c`.

#### Cross-cutting
- **Power‑of‑two constraint** – all capacity checks enforce this rule; errors returned via `RINGBUF_EINVAL`.
- **Single‑producer/single‑consumer** – design assumes no concurrent producers or consumers; no locks used.
- **Wrap‑around logic** – both read and write handle two contiguous memory copies when crossing the buffer boundary.
- **Error handling** – initialization failures return `RINGBUF_ENOMEM`; invalid capacities return `RINGBUF_EINVAL`.
