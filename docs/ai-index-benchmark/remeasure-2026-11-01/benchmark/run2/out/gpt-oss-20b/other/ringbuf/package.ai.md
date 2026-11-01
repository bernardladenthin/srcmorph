### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:48:19Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, enabling efficient single‑producer single‑consumer data transfer.

#### Purpose
- Offer a reusable ring buffer API for byte‑level data.
- Ensure fast, thread‑safe read/write without locks.

#### Responsibilities
- **API definition** – `ringbuf.h` declares types, status codes, and public functions.
- **Runtime implementation** – `ringbuf.c` implements initialization, memory management, and read/write logic.
- **Capacity validation** – enforce power‑of‑two sizes and non‑zero capacity.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head, tail.
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocates and sets up buffer.
- `ringbuf_free(ringbuf_t *rb)` – releases memory.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – writes up to `len` bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – reads up to `len` bytes.
- `ringbuf_used(const ringbuf_t *rb)` – returns bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb)` – returns available capacity.

#### Data flow
1. **Initialization** – caller supplies `capacity`; `ringbuf_init` allocates buffer and sets indices.
2. **Writing** – producer calls `ringbuf_write`; data copied into buffer, head advanced with wrap‑around.
3. **Reading** – consumer calls `ringbuf_read`; data copied out, tail advanced.
4. **Querying** – `ringbuf_used` and `ringbuf_free_space` compute state via `(head - tail) & (capacity - 1)`.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Uses `malloc`, `free`, `memcpy` for memory handling.
- No external libraries; purely self‑contained.

#### Cross-cutting
- **Power‑of‑two enforcement** – `is_power_of_two` helper ensures efficient modulo via bitmask.
- **Wrap‑around arithmetic** – head/tail indices use bitwise AND with `capacity - 1`.
- **Error handling** – `ringbuf_init` returns specific status codes for invalid capacity or allocation failure.
- **Single‑producer single‑consumer** – design assumes no concurrent writes or reads, enabling lock‑free operation.
