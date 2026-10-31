### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:12:08Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Implements a lock‑free, power‑of‑two sized circular buffer for byte streams, providing efficient single‑producer/single‑consumer read and write operations.

#### Purpose
- Provide a fixed‑size, thread‑safe ring buffer for byte data.
- Expose a clean API for initialization, cleanup, and data transfer.

#### Responsibilities
- **Buffer management** – allocate, free, and maintain head/tail indices.
- **Data transfer** – copy data in up to two contiguous chunks to handle wrap‑around.
- **Space querying** – expose used bytes and free space calculations.
- **Error handling** – validate capacity and report allocation failures.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head, and tail indices.
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`) for init status.
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocate and set up buffer.
- `ringbuf_free(ringbuf_t *rb)` – release allocated memory.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – write bytes, return count.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – read bytes, return count.
- `ringbuf_used(const ringbuf_t *rb)` – compute bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb)` – compute available capacity.

#### Data flow
1. **Initialization** – caller supplies a power‑of‑two `capacity`; `ringbuf_init` allocates the buffer and zeroes indices.
2. **Writing** – producer calls `ringbuf_write`; data is copied into the buffer, head advanced modulo capacity.
3. **Reading** – consumer calls `ringbuf_read`; data is copied out, tail advanced modulo capacity.
4. **Querying** – `ringbuf_used` and `ringbuf_free_space` compute values using `(head - tail) & (capacity - 1)`.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Uses `malloc`, `free`, and `memcpy` for memory management and data movement.

#### Cross-cutting
- **Power‑of‑two enforcement** – `is_power_of_two` helper validates capacity; errors returned via `RINGBUF_EINVAL`.
- **Modulo arithmetic** – all index updates use bitwise AND with `capacity - 1` for wrap‑around.
- **Single‑producer/single‑consumer** – design assumes no concurrent writes or reads; no locks required.
- **Error codes** – only `ringbuf_init` reports errors; read/write return actual byte counts.
