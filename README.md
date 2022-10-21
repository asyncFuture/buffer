# buffer

an extension of the nio buffer

This is a small library based on Nio. It allows you to take a buffer from a survey, the condition is that the buffer must be free. The good thing is that the buffers are reused, so you save more memory.
###
![](../../Desktop/showcase.png)


## Poll buffer
#### Heap
````java
Buffer buffer = ByteBuf.heap();
````
#### Direct
````java
Buffer buffer = ByteBuf.direct();
````

#### 1Use of the buffer
````java
//poll a buffer
buffer.writeInt(2022);

//fetch readable bytes to array
System.out.println(Arrays.toString(buffer.array()));

//disconnect buffer instance
buffer.detach();
````