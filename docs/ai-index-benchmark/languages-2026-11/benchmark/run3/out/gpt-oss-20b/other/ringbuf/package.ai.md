### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:06:27Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> A lock‑free, power‑of‑two circular buffer for byte streams, exposing a minimal C API for single‑producer/single‑consumer use.

#### Purpose
- Provide a fixed‑size, thread‑safe ring buffer for byte data.
- Offer initialization, deallocation, and bulk read/write operations.

#### Responsibilities
- **Buffer management** – allocate, free, and track head/tail indices.
- **Data transfer** – copy data in up to two contiguous chunks to handle wrap‑around.
- **Capacity queries** – report used bytes and available space.

#### Key units
- `ringbuf_t` – struct holding `data`, `capacity`, `head`, `tail`.
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocate buffer, validate power‑of‑two.
- `ringbuf_free(ringbuf_t *rb)` – release memory.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – enqueue bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – dequeue bytes.
- `ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb)` – free slots available.

#### Data flow
1. **Initialization** – caller supplies `capacity`; `ringbuf_init` allocates `data` and sets indices.
2. **Write** – producer calls `ringbuf_write`; function copies up to `len` bytes, updates `head`, handles wrap‑around.
3. **Read** – consumer calls `ringbuf_read`; function copies up to `len` bytes, updates `tail`, handles wrap‑around.
4. **Query** – `ringbuf_used` and `ringbuf_free_space` compute metrics via modulo arithmetic.
5. **Cleanup** – `ringbuf_free` releases the allocated array.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Uses `malloc`/`free` for memory management; `memcpy` for data copying.

#### Cross-cutting
- Power‑of‑two capacity enforcement via `is_power_of_two` helper.
- Atomicity implied by single‑producer/single‑consumer usage; no explicit locks.
- Error handling limited to initialization failures (`EINVAL`, `ENOMEM`); read/write return 0 on failure.
