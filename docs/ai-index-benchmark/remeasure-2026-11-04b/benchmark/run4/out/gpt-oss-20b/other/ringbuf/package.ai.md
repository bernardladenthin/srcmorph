### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:52:49Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two sized byte ring buffer for single‑producer/single‑consumer scenarios.

#### Purpose
- Offer a lightweight circular buffer for byte streams.
- Expose a C API for init, free, read, write, and space queries.

#### Responsibilities
- **Buffer management** – allocate, deallocate, and maintain head/tail indices.
- **Data movement** – copy data into/out of the buffer with wrap‑around handling.
- **Capacity validation** – enforce power‑of‑two size and non‑zero capacity.
- **Metrics** – report used bytes and free space.

#### Key units
- `ringbuf_t` – struct holding `data`, `capacity`, `head`, `tail`.
- `ringbuf_status_t` enum – `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocate buffer, validate capacity.
- `ringbuf_free(ringbuf_t *rb)` – release allocated memory.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – write up to `len` bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – read up to `len` bytes.
- `ringbuf_used(const ringbuf_t *rb)` – compute bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb)` – compute available space.

#### Data flow
1. **Initialization** – caller supplies `capacity`; `ringbuf_init` allocates `data` and sets indices.
2. **Writing** – producer calls `ringbuf_write`; function copies data in up to two contiguous chunks, updates `head`.
3. **Reading** – consumer calls `ringbuf_read`; function copies data in up to two contiguous chunks, updates `tail`.
4. **Metrics** – `ringbuf_used` and `ringbuf_free_space` derive values from `head`/`tail` modulo `capacity`.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- No external libraries; purely self‑contained.

#### Cross-cutting
- **Power‑of‑two enforcement** – `is_power_of_two` helper ensures capacity validity.
- **Modulo arithmetic** – head/tail wrap using `& (capacity - 1)`.
- **Error handling** – `ringbuf_init` returns status codes; write/read return actual byte counts.
- **Thread safety** – designed for single‑producer/single‑consumer; no locks required.
