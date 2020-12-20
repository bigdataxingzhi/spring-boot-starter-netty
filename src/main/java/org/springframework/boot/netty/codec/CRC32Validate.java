package org.springframework.boot.netty.codec;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

/**
 * Author: huoxingzhi
 * Date: 2020/12/11
 * Email: hxz_798561819@163.com
 */
public class CRC32Validate
{
    private static CRC32Validate crc32 = new CRC32Validate();

    public static CRC32Validate getInstance()
    {
        return crc32;
    }

    private CRC32Validate() {
    }

    private static final int STREAM_BUFFER_LENGTH = 1024;

    public long encrypt(byte[] data)
    {
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return crc32.getValue();
    }

    public long encrypt(InputStream data) throws IOException
    {
        final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
        int read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);
        CRC32 crc32 = new CRC32();
        while (read > -1)
        {
            crc32.update(buffer, 0, read);
            read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);
        }
        return crc32.getValue();
    }

    public static void main(String[] args) {

    }
}