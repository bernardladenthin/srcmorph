### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:29:24Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Implements a lock‑free, power‑of‑two circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.

#### Purpose
- Provide a reusable ring buffer API for byte streams.
- Ensure fast, wrap‑around copy without dynamic resizing.

#### Responsibilities
- **API definition** – expose initialization, cleanup, read/write, and status queries.
- **Memory management** – allocate and free buffer storage.
- **Data movement** – copy data in up to two contiguous chunks to handle wrap‑around.
- **State tracking** – maintain head/tail indices and compute used/free space.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head, tail.
- `ringbuf_status_t` enum – `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocate and validate buffer.
- `ringbuf_free(ringbuf_t *rb)` – release resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – enqueue bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – dequeue bytes.
- `ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb)` – available capacity.

#### Data flow
1. **Initialization** – caller supplies `capacity`; `ringbuf_init` allocates `capacity` bytes, sets head/tail to 0.
2. **Write** – producer calls `ringbuf_write`; data copied into buffer, head advanced modulo capacity.
3. **Read** – consumer calls `ringbuf_read`; data copied out, tail advanced modulo capacity.
4. **Status** – `ringbuf_used` and `ringbuf_free_space` compute values via `(head - tail) & (capacity - 1)`.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Internal header `ringbuf.h` defines types and prototypes used by `ringbuf.c`.

#### Cross-cutting
- **Power‑of‑two constraint** – all capacity checks enforce this rule; errors returned via `RINGBUF_EINVAL`.
- **Single‑producer/single‑consumer** – design assumes no concurrent writes or reads; no locks required.
- **Error handling** – `ringbuf_init` returns status codes; write/read return byte counts, allowing partial operations.
- **Memory safety** – `ringbuf_free` resets fields to avoid dangling pointers.
