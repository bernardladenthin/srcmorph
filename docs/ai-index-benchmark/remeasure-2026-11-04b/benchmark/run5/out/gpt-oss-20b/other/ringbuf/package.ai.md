### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:55:27Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> A lock‑free, power‑of‑two sized circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.

#### Purpose
- Provide a lightweight FIFO for byte streams.
- Offer non‑blocking read/write with minimal overhead.

#### Responsibilities
- **Buffer lifecycle** – initialization, allocation, deallocation.
- **Data movement** – contiguous or split writes/reads with wrap‑around.
- **State reporting** – used bytes and free space queries.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head/tail indices.
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_init` – validates capacity, allocates memory, sets indices.
- `ringbuf_free` – releases allocated memory.
- `ringbuf_write` – copies up to `len` bytes, updates head, returns written count.
- `ringbuf_read` – copies up to `len` bytes, updates tail, returns read count.
- `ringbuf_used` – computes `(head - tail) & (capacity-1)`.
- `ringbuf_free_space` – returns `capacity - 1 - used`.

#### Data flow
1. **Init** → allocate `data` array, set `head = tail = 0`.
2. **Write** → copy from source to buffer, split if wrap‑around, advance `head`.
3. **Read** → copy from buffer to destination, split if wrap‑around, advance `tail`.
4. **Query** → `ringbuf_used`/`ringbuf_free_space` compute metrics via bit masking.

#### Dependencies
- Standard C library: `stdlib.h` (`malloc`, `free`), `string.h` (`memcpy`).
- Header `ringbuf.h` defines types and API.

#### Cross-cutting
- Power‑of‑two capacity enforced via `is_power_of_two`.
- Bit‑masking (`capacity-1`) used for efficient wrap‑around.
- Single‑producer/single‑consumer assumption; no explicit locking required.
- Error handling limited to initialization failures (`EINVAL`, `ENOMEM`).
