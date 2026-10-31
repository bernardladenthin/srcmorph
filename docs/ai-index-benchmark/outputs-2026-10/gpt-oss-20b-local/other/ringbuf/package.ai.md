### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:22:07Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two circular buffer for single‑producer, single‑consumer byte streams.

#### Purpose
- Offer a fixed‑size ring buffer with non‑blocking read/write.
- Expose a clean C API for initialization, deallocation, and data transfer.

#### Responsibilities
- **API definition** – `ringbuf.h` declares types (`ringbuf_t`, `ringbuf_status_t`) and public functions.
- **Runtime logic** – `ringbuf.c` implements buffer allocation, wrap‑around copy, and space calculations.
- **Error handling** – validate capacity, report allocation failures, and expose status codes.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head/tail indices.
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_init(ringbuf_t*, size_t)` – allocates and sets up buffer.
- `ringbuf_free(ringbuf_t*)` – releases memory.
- `ringbuf_write(ringbuf_t*, const uint8_t*, size_t)` – enqueue bytes.
- `ringbuf_read(ringbuf_t*, uint8_t*, size_t)` – dequeue bytes.
- `ringbuf_used(const ringbuf_t*)` – bytes currently stored.
- `ringbuf_free_space(const ringbuf_t*)` – available capacity.

#### Data flow
1. **Initialization** – caller supplies capacity; `ringbuf_init` allocates array and zeroes indices.
2. **Write** – producer calls `ringbuf_write`; data copied in up to two contiguous chunks, head advanced.
3. **Read** – consumer calls `ringbuf_read`; data copied similarly, tail advanced.
4. **Metrics** – `ringbuf_used` and `ringbuf_free_space` compute values via modulo arithmetic.
5. **Cleanup** – `ringbuf_free` releases the buffer.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Uses `malloc`/`free` for dynamic allocation.
- Relies on power‑of‑two capacity for efficient modulo via bitwise AND.

#### Cross-cutting
- **Power‑of‑two invariant** – enforced in `ringbuf_init` and `is_power_of_two`.
- **Wrap‑around logic** – both read and write handle two‑segment copies to maintain linearity.
- **Single‑producer/single‑consumer** – no locks; indices updated atomically by distinct threads.
- **Error codes** – consistent status enum used across API.
