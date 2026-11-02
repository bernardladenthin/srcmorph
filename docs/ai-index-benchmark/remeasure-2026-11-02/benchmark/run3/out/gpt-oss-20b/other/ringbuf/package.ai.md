### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:03:41Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, enabling efficient single‑producer, single‑consumer data transfer.

#### Purpose
- Implement a fixed‑size ring buffer with non‑blocking read/write.
- Offer memory management and usage metrics for byte streams.

#### Responsibilities
- **Buffer lifecycle**: initialization, allocation, deallocation.
- **Data movement**: bulk write and read with wrap‑around handling.
- **State querying**: current usage and free space calculations.
- **Error handling**: validate capacity and report allocation failures.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head/tail indices.
- `ringbuf_status_t` – enum for `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocate and set up buffer.
- `ringbuf_free(ringbuf_t *rb)` – release allocated memory.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – copy data into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – copy data out of buffer.
- `ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb)` – available space.

#### Data flow
1. **Init** → allocate `data` array, set `head = tail = 0`.
2. **Write** → copy up to `len` bytes from `src` into `data` at `head`, wrap using modulo, update `head`.
3. **Read** → copy up to `len` bytes from `data` at `tail` into `dst`, wrap, update `tail`.
4. **Query** → `ringbuf_used` and `ringbuf_free_space` compute metrics via `(head - tail) & (capacity - 1)`.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Internal: `ringbuf.h` defines types and constants used by `ringbuf.c`.

#### Cross-cutting
- **Power‑of‑two constraint**: `is_power_of_two` validates capacity; all arithmetic uses bitwise masking for wrap‑around.
- **Single‑producer/single‑consumer**: no locks; relies on atomicity of pointer updates.
- **Error codes**: `RINGBUF_EINVAL` for invalid capacity, `RINGBUF_ENOMEM` for allocation failure, `RINGBUF_OK` for success.
