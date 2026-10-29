### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T02:33:20Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices

#### Purpose
- Initialize and manage a ring buffer for data storage.
- Provide methods to write and read data from the ring buffer.

#### Type
- C file

#### Input
- `ringbuf_t *rb`: Pointer to the ring buffer structure.
- `size_t capacity`: Initial capacity of the ring buffer.
- `const uint8_t *src`: Pointer to the source data to be written.
- `size_t len`: Length of the data to be written or read.

#### Output
- `ringbuf_status_t`: Status code indicating success or failure.
- `size_t`: Number of bytes written or read.
- `ringbuf_t`: Updated state of the ring buffer.

#### Core logic
- **ringbuf_init**: Initializes the ring buffer with a given capacity, ensuring it is a power of two.
- **ringbuf_free**: Frees the allocated memory for the ring buffer.
- **ringbuf_used**: Calculates the number of used bytes in the ring buffer.
- **ringbuf_free_space**: Calculates the available free space in the ring buffer.
- **ringbuf_write**: Writes data into the ring buffer, handling wrap-around if necessary.
- **ringbuf_read**: Reads data from the ring buffer, handling wrap-around if necessary.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t`: Initializes the ring buffer with the specified capacity.
- `ringbuf_free(ringbuf_t *rb)`: Frees the memory allocated for the ring buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t`: Returns the number of used bytes in the ring buffer.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t`: Returns the available free space in the ring buffer.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t`: Writes data into the ring buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t`: Reads data from the ring buffer.

#### Dependencies
- `ringbuf.h`: Header file defining the ring buffer structure and status codes.
- `stdlib.h`: For memory allocation and deallocation.
- `string.h`: For memory copying.

#### Exceptions / Errors
- **RINGBUF_EINVAL**: Invalid argument (capacity is not a power of two).
- **RINGBUF_ENOMEM**: Out of memory.
