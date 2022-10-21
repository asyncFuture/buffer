package eu.dulag.buffer;

import java.nio.ByteBuffer;

public interface Buffer {

    Buffer enlarge(int capacity);

    Buffer clear();

    Buffer flip();

    void delete();

    void detach();

    Buffer write(ByteBuffer value);

    Buffer write(Buffer value);

    Buffer writeBytes(byte[] bytes, int offset, int length);

    default Buffer writeBytes(byte[] bytes) {
        return writeBytes(bytes, 0, bytes.length);
    }

    Buffer readBytes(byte[] bytes, int offset, int length);

    default Buffer readBytes(byte[] bytes) {
        return readBytes(bytes, 0, bytes.length);
    }

    Buffer writeByte(byte value);

    byte readByte();

    Buffer writeInt(int value);

    int readInt();

    Buffer writeLong(long value);

    long readLong();

    Buffer writeShort(short value);

    short readShort();

    Buffer writeBool(boolean value);

    boolean readBool();

    Buffer writeChar(char value);

    char readChar();

    ByteBuffer impl();

    byte[] array();

    boolean isDirect();

    boolean isWritable();

    boolean isWritable(int bytes);

    boolean isReadable();

    int readable();

    int capacity();
}