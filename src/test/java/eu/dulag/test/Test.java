package eu.dulag.test;

import eu.dulag.buffer.Buffer;
import eu.dulag.buffer.ByteBuf;

import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        Buffer heap = ByteBuf.heap();
        heap.writeInt(2022);

        System.out.println(Arrays.toString(heap.array()));
        System.out.println("available heap buffers: " + ByteBuf.free(false));

        System.out.println(heap + " free=" + ByteBuf.isFree(heap));

        System.out.println(Arrays.toString(heap.array()));

        heap.detach();
        System.out.println(heap + " was detached");

        System.out.println("available heap buffers: " + ByteBuf.free(false));
    }
}