# buffer

an extension of the nio buffer

## Example


## Poll buffer

Heap
````java
Buffer buffer = ByteBuf.heap();
````

Direct
````java
Buffer buffer = ByteBuf.direct();
````

Use of the buffer
````java
//poll a buffer
buffer.writeInt(2022);

//fetch readable bytes to array
System.out.println(Arrays.toString(buffer.array()));

//disconnect buffer instance
buffer.detach();
````