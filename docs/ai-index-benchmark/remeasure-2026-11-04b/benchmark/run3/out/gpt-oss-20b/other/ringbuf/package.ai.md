### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:11:52Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, enabling non‑blocking FIFO read/write between a single producer and consumer.

#### Purpose
- Offer a reusable, fixed‑capacity byte ring buffer.
- Support non‑blocking, wrap‑around read/write operations.

#### Responsibilities
- **API definition** – header declares `ringbuf_t`, status enum, and public functions.
- **Runtime logic** – source implements allocation, index arithmetic, and data movement.
- **Error handling** – validates capacity and reports allocation failures.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head/tail indices.
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_init` – allocates buffer, validates power‑of‑two capacity.
- `ringbuf_free` – releases allocated memory.
- `ringbuf_write` – copies up to two contiguous chunks to handle wrap‑around.
- `ringbuf_read` – copies up to two contiguous chunks from buffer.
- `ringbuf_used` – computes bytes currently stored via modulo arithmetic.
- `ringbuf_free_space` – computes available space.

#### Data flow
1. **Initialization** – `ringbuf_init` allocates memory and sets indices to zero.
2. **Writing** – `ringbuf_write` checks free space, copies data, updates head.
3. **Reading** – `ringbuf_read` checks used bytes, copies data, updates tail.
4. **Querying** – `ringbuf_used` / `ringbuf_free_space` provide current state.
5. **Cleanup** – `ringbuf_free` releases resources.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Uses `malloc`/`free` for dynamic buffer allocation.
- Relies on power‑of‑two capacity for efficient modulo via bitmask.

#### Cross-cutting
- **Single‑producer / single‑consumer** assumption; no locks required.
- **Wrap‑around handling** via two‑chunk copy logic in both read and write.
- **Error codes** standardized through `ringbuf_status_t`.
- **Capacity validation** via `is_power_of_two` helper.
