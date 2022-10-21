package eu.dulag.buffer;

import eu.dulag.buffer.exception.BufferException;

import java.nio.ByteBuffer;

public class ByteBuf implements Buffer {

    private static final byte[] EMPTY_ARRAY = new byte[0];

    public static final ByteBuf HEAP_EMPTY = new ByteBuf(0, false);
    public static final ByteBuf DIRECT_EMPTY = new ByteBuf(0, true);

    private static final Blocking<Buffer> HEAP_BLOCKING = new Blocking<Buffer>() {
        {
            for (int i = 0; i < 512; i++) add(new ByteBuf(1024, false));
        }
    };

    private static final Blocking<Buffer> DIRECT_BLOCKING = new Blocking<Buffer>() {
        {
            for (int i = 0; i < 512; i++) add(new ByteBuf(1024, true));
        }
    };

    private static BufferException cause(String message) {
        return new BufferException(message);
    }

    private static BufferException toss() {
        return new BufferException();
    }

    public static Buffer heap() {
        Buffer poll = HEAP_BLOCKING.poll();
        if (poll == null) return alloc(1024).hold();
        return poll;
    }

    public static Buffer direct() {
        Buffer poll = DIRECT_BLOCKING.poll();
        if (poll == null) return allocDirect(1024).hold();
        return poll;
    }

    public static Blocking<Buffer> alloc(int capacity) {
        return HEAP_BLOCKING.add(new ByteBuf(capacity, false));
    }

    public static Blocking<Buffer> allocDirect(int capacity) {
        return DIRECT_BLOCKING.add(new ByteBuf(capacity, true));
    }

    public static int free(boolean direct) {
        if (direct) return DIRECT_BLOCKING.free();
        return HEAP_BLOCKING.free();
    }

    public static boolean isFree(Buffer buffer) {
        return buffer.isDirect() ? DIRECT_BLOCKING.isFree(buffer) : HEAP_BLOCKING.isFree(buffer);
    }

    private ByteBuffer buffer;

    private int capacity;

    private int writeIndex, readIndex;
    private final boolean direct;

    protected ByteBuf(int capacity, boolean direct) {
        if (capacity < 0) capacity = 0;
        this.capacity = capacity;
        this.direct = direct;

        this.buffer = direct ? ByteBuffer.allocateDirect(capacity) : ByteBuffer.allocate(capacity);
    }

    @Override
    public Buffer enlarge(int size) {
        capacity += size;

        ByteBuffer enlarge = direct ? ByteBuffer.allocateDirect(capacity) : ByteBuffer.allocate(capacity);

        buffer = enlarge.put(buffer);

        return this;
    }

    @Override
    public Buffer clear() {
        buffer.clear();
        writeIndex = 0;
        readIndex = 0;
        return this;
    }

    @Override
    public Buffer flip() {
        writeIndex = 0;
        readIndex = 0;
        return this;
    }

    @Override
    public void delete() {
        clear();
        if (direct) DIRECT_BLOCKING.remove(this);
        else HEAP_BLOCKING.remove(this);
    }

    @Override
    public void detach() {
        clear();
        if (direct) DIRECT_BLOCKING.detach(this);
        else HEAP_BLOCKING.detach(this);
    }

    @Override
    public Buffer write(ByteBuffer value) {
        int readable = value.remaining();
        if (!isWritable(readable)) enlarge(readable - (capacity - readable()));
        while (value.hasRemaining()) writeByte(value.get());
        return this;
    }

    @Override
    public Buffer write(Buffer value) {
        if (!isWritable(value.readable())) enlarge(value.readable() - (capacity - readable()));
        while (value.isReadable()) writeByte(value.readByte());
        return this;
    }

    @Override
    public Buffer writeBytes(byte[] bytes, int offset, int length) {
        if (offset < 0 || length < 0 || offset == length || length > bytes.length || offset > length) throw toss();

        enlarge(length);
        for (int i = offset; i < length; i++) buffer.put(i, bytes[i]);
        return this;
    }

    @Override
    public Buffer readBytes(byte[] bytes, int offset, int length) {
        if (offset < 0 || length < 0 || offset == length || length > bytes.length || offset > length) throw toss();

        for (int i = offset; i < length; i++) bytes[i] = buffer.get(i);
        return this;
    }

    @Override
    public Buffer writeByte(byte value) {
        if (!isWritable()) enlarge(Byte.BYTES);
        buffer.put(writeIndex++, value);
        return this;
    }

    @Override
    public byte readByte() {
        int i = readable();
        if (i - 1 < 0) throw toss();
        return buffer.get(readIndex++);
    }

    @Override
    public Buffer writeInt(int value) {
        if (!isWritable()) enlarge(Integer.BYTES);
        buffer.putInt(writeIndex, value);
        writeIndex += Integer.BYTES;
        return this;
    }

    @Override
    public int readInt() {
        int i = readable();
        int bytes = Integer.BYTES;
        if (i - bytes < 0) throw toss();

        int result = buffer.getInt(readIndex);
        readIndex += bytes;
        return result;
    }

    @Override
    public Buffer writeLong(long value) {
        if (!isWritable()) enlarge(Long.BYTES);
        buffer.putLong(writeIndex, value);
        writeIndex += Long.BYTES;
        return this;
    }

    @Override
    public long readLong() {
        int i = readable();
        int bytes = Long.BYTES;
        if (i - bytes < 0) throw toss();

        long result = buffer.getLong(readIndex);
        readIndex += bytes;
        return result;
    }

    @Override
    public Buffer writeShort(short value) {
        if (!isWritable()) enlarge(Short.BYTES);
        buffer.putShort(writeIndex, value);
        writeIndex += Short.BYTES;
        return this;
    }

    @Override
    public short readShort() {
        int i = readable();
        int bytes = Short.BYTES;
        if (i - bytes < 0) throw toss();

        short result = buffer.getShort(readIndex);
        readIndex += bytes;
        return result;
    }

    @Override
    public Buffer writeBool(boolean value) {
        if (!isWritable()) enlarge(1);
        buffer.put(writeIndex, (byte) (value ? 1 : 0));
        writeIndex += 1;
        return this;
    }

    @Override
    public boolean readBool() {
        int i = readable();
        if (i - 1 < 0) throw toss();

        boolean result = buffer.get(readIndex) != 0;
        readIndex += 1;
        return result;
    }

    @Override
    public Buffer writeChar(char value) {
        if (!isWritable()) enlarge(Character.BYTES);
        buffer.putChar(writeIndex, value);
        writeIndex += Character.BYTES;
        return this;
    }

    @Override
    public char readChar() {
        int i = readable();
        int bytes = Character.BYTES;
        if (i - bytes < 0) throw toss();

        char result = buffer.getChar(readIndex);
        readIndex += bytes;
        return result;
    }

    @Override
    public ByteBuffer impl() {
        return buffer;
    }

    @Override
    public byte[] array() {
        if (direct) throw new UnsupportedOperationException();
        byte[] array = buffer.array();

        byte[] bytes = new byte[readable()];
        System.arraycopy(array, 0, bytes, 0, bytes.length);
        return bytes;
    }

    @Override
    public boolean isDirect() {
        return direct;
    }

    @Override
    public boolean isWritable() {
        return writeIndex < capacity;
    }

    @Override
    public boolean isWritable(int bytes) {
        return writeIndex <= (capacity) - bytes;
    }

    @Override
    public boolean isReadable() {
        return readable() > 0;
    }

    @Override
    public int readable() {
        return (writeIndex) - (readIndex);
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "ByteBuf[direct=" + direct + ", index=" + readable() + ", cap=" + capacity + "]";
    }
}