### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:09:59Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a fixed-capacity, power-of-two byte ring buffer for lock-free single-producer/single-consumer circular data storage.

#### Purpose
- Implements a thread-safe circular byte buffer (`ringbuf_t`) using bitwise masking for efficient index wrapping.
- Manages buffer lifecycle, capacity validation, and data transfer operations for single-producer/single-consumer scenarios.

#### Responsibilities
- **Buffer Lifecycle**: Initialization with power-of-two capacity validation, memory allocation, and deallocation.
- **Data Operations**: Writing and reading byte sequences with wrap-around handling, returning actual bytes transferred.
- **State Inspection**: Querying current usage and available free space.

#### Key units
- `ringbuf_t`: Core structure holding `data` buffer, `head` (producer index), `tail` (consumer index), and `capacity`.
- `ringbuf_status_t`: Enum for status codes (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_init`: Validates capacity (non-zero, power of two) and allocates memory.
- `ringbuf_write`: Copies bytes into buffer in up to two chunks, advancing `head`.
- `ringbuf_read`: Copies bytes out of buffer in up to two chunks, advancing `tail`.
- `ringbuf_used` / `ringbuf_free_space`: Calculate current storage and available capacity.

#### Data flow
- **Write**: Input byte array (`src`) is copied into `ringbuf_t.data` starting at `head`, wrapping around if necessary; `head` advances by the number of bytes written.
- **Read**: Data is copied from `ringbuf_t.data` starting at `tail` into destination array (`dst`), wrapping around if necessary; `tail` advances by the number of bytes read.
- **Index Wrapping**: Indices are wrapped using `(index) & (capacity - 1)` to leverage the power-of-two capacity constraint.

#### Dependencies
- Internal: `ringbuf.h` defines the API and types used by `ringbuf.c`.
- External: `stdlib.h` (`malloc`, `free`), `string.h` (`memcpy`), `stddef.h` (`size_t`), `stdint.h` (`uint8_t`).

#### Cross-cutting
- **Concurrency**: `head` and `tail` are marked `volatile` to ensure visibility across threads without explicit locks, supporting single-producer/single-consumer patterns.
- **Capacity Constraint**: Enforces power-of-two capacity to enable efficient bitwise AND masking for index wrapping.
- **Full/Empty Distinction**: Reserves one slot to distinguish full from empty states; effective storage is `capacity - 1` bytes.
- **Error Handling**: Returns specific status codes for invalid capacity (`RINGBUF_EINVAL`) and memory allocation failures (`RINGBUF_ENOMEM`).
