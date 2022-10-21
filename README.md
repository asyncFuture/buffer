# buffer

an extension of the nio buffer

This is a small library based on Nio. It allows you to take a buffer from a survey, the condition is that the buffer must be free. The good thing is that the buffers are reused, so you save more memory.
##
![showcase](https://user-images.githubusercontent.com/114350382/197295112-7f3a9b60-7858-4d39-be77-cfaabfb35938.png)


## Poll buffer
#### Heap
````java
Buffer buffer = ByteBuf.heap();
````
#### Direct
````java
Buffer buffer = ByteBuf.direct();
````

#### Use of the buffer
````java
//poll a buffer
buffer.writeInt(2022);

//fetch readable bytes to array
System.out.println(Arrays.toString(buffer.array()));

//disconnect buffer instance
buffer.detach();
````
