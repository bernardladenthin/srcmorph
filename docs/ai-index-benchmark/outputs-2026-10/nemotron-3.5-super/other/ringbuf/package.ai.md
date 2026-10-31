### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:55:42Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> A fixed-capacity circular byte buffer for single-producer/single-consumer streaming with power-of-two wrap-around indexing.

#### Purpose
- Provides a non-blocking circular byte queue for producer-consumer data transfer.
- Manages buffer allocation, occupancy queries, and wrap-around enqueue/dequeue of bytes.

#### Responsibilities
- Buffer lifecycle: initialize with a power-of-two capacity, allocate backing storage, and release memory on teardown.
- Occupancy tracking: report used bytes and free writable space from producer head and consumer tail indices.
- Byte enqueue: copy up to requested bytes from a source buffer into the ring in at most two memcpy chunks, advancing head.
- Byte dequeue: copy up to requested bytes from the ring into a destination buffer in at most two memcpy chunks, advancing tail.
- Status reporting: return success, invalid capacity, or allocation failure from initialization.

#### Key units
- ringbuf_t (struct in ringbuf.h): holds the data pointer, capacity, and volatile head and tail indices.
- ringbuf_status_t (enum in ringbuf.h): result codes RINGBUF_OK, RINGBUF_EINVAL, RINGBUF_ENOMEM.
- ringbuf.c: implementation of ringbuf_init, ringbuf_free, ringbuf_write, ringbuf_read, ringbuf_used, and ringbuf_free_space.

#### Data flow
- Caller invokes ringbuf_init with a non-zero power-of-two capacity; the module malloc's the data buffer and zeroes head and tail.
- Producer calls ringbuf_write with a source buffer and length; the module clamps to free space, copies in up to two chunks across array wrap, and advances head.
- Consumer calls ringbuf_read with a destination buffer and length; the module clamps to used bytes, copies in up to two chunks across array wrap, and advances tail.
- Caller polls ringbuf_used or ringbuf_free_space for occupancy; ringbuf_free releases the heap buffer and resets the struct.

#### Dependencies
- Internal: ringbuf.c depends on ringbuf.h for ringbuf_t, ringbuf_status_t, and status constants.
- External: stdlib.h for malloc and free; string.h for memcpy; stddef.h for size_t; stdint.h for uint8_t.
- C++ compatibility: extern "C" linkage guard in the header.

#### Cross-cutting
- SPSC ownership: producer exclusively updates head, consumer exclusively updates tail; both indices are volatile for cross-thread visibility.
- Power-of-two capacity with one reserved slot to distinguish full from empty, making usable capacity capacity - 1.
- Wrap-around indexing via bitmask (capacity - 1) instead of modulo division.
- Non-blocking writes and reads that clamp to available space or used bytes rather than returning failure.
- Error surface limited to ringbuf_init; write and read never fail.
