package org.springframework.boot.autoconfigure.netty;

import org.junit.jupiter.api.Test;
import org.springframework.boot.netty.codec.CRC32Validate;

public class CRC32Test
{
    CRC32Validate crc32 = CRC32Validate.getInstance();

    @Test
    public void encode()
    {
        byte[] data = "jianggujin".getBytes();
        long result = crc32.encrypt(data);
        System.err.println(result);
    }
}