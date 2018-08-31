package com.mp4;

public class Bitstream {
    public class BitstreamException extends Exception {
        public static final int BITSTREAM_TOO_MANY_BITS = 0x10;
        public static final int BITSTREAM_PAST_END = 0x11;
        private int mError = 0;

        public BitstreamException(int err) {
            mError = err;
        }
    }

    final int[] masks = { 0x00000000, 0x00000001, 0x00000003, 0x00000007,
            0x0000000f, 0x0000001f, 0x0000003f, 0x0000007f, 0x000000ff,
            0x000001ff, 0x000003ff, 0x000007ff, 0x00000fff, 0x00001fff,
            0x00003fff, 0x00007fff, 0x0000ffff, 0x0001ffff, 0x0003ffff,
            0x0007ffff, 0x000fffff, 0x001fffff, 0x003fffff, 0x007fffff,
            0x00ffffff, 0x01ffffff, 0x03ffffff, 0x07ffffff, 0x0fffffff,
            0x1fffffff, 0x3fffffff, 0x7fffffff, 0xffffffff };

    
    int fBitsInBuffer; // /<
    byte fBitsBuffer; // /<
    int fBufferSize; // /< �����������ݵı�����
    byte[] fBuffer; // /< ������������
    int fBufferOffset;
    int fBufferPos;

    int fBookmarkOn;
    byte fBitsBufferMark;
    int fBitsInBufferMark;
    int fBufferSizeMark;
    byte[] fBufferMark;
    int fBufferOffsetMark;
    int fBufferPosMark;

    Bitstream() {
    }

    Bitstream(byte[] buffer, int offset, int bit_len) {
        init(buffer, offset, bit_len);
    }

    int bits_remain() {
        return fBufferSize + fBitsInBuffer;
    };

    int GetBits(int bitsCount) throws BitstreamException {
        int ret;

        if (bitsCount > 32) {
            throw new BitstreamException(
                    BitstreamException.BITSTREAM_TOO_MANY_BITS);

        } else if (bitsCount == 0) {
            return 0;
        }

        if (fBitsInBuffer >= bitsCount) { // don't need to read from FILE
            fBitsInBuffer -= bitsCount;
            ret = fBitsBuffer >> fBitsInBuffer;
            // wmay - this gets done below...ret &= msk[numBits];
        } else {
            int nbits = bitsCount - fBitsInBuffer;
            if (nbits == 32) {
                ret = 0;
            } else {
                ret = fBitsBuffer << nbits;
            }

            switch ((nbits - 1) / 8) {
            case 3:
                nbits -= 8;
                if (fBufferSize < 8) {
                    throw new BitstreamException(
                            BitstreamException.BITSTREAM_PAST_END);
                }
                ret |= fBuffer[fBufferOffset + fBufferPos] << nbits;
                fBufferPos++;
                fBufferSize -= 8;
                // fall through
            case 2:
                nbits -= 8;
                if (fBufferSize < 8) {
                    throw new BitstreamException(
                            BitstreamException.BITSTREAM_PAST_END);
                }
                ret |= fBuffer[fBufferOffset + fBufferPos] << nbits;
                fBufferPos++;
                fBufferSize -= 8;
            case 1:
                nbits -= 8;
                if (fBufferSize < 8) {
                    throw new BitstreamException(
                            BitstreamException.BITSTREAM_PAST_END);
                }
                ret |= fBuffer[fBufferOffset + fBufferPos] << nbits;
                fBufferPos++;
                fBufferSize -= 8;
            case 0:
                break;
            }

            if (fBufferSize < nbits) {
                throw new BitstreamException(
                        BitstreamException.BITSTREAM_PAST_END);
            }

            fBitsBuffer = fBuffer[fBufferOffset + fBufferPos];
            fBufferPos++;
            fBitsInBuffer = Math.min(8, fBufferSize) - nbits;
            fBufferSize -= Math.min(8, fBufferSize);
            ret |= (fBitsBuffer >> fBitsInBuffer) & masks[nbits];
        }

        return (ret & masks[bitsCount]);
    }
    
    public void putBits(int bitCount, int val) throws Exception {
        if (bitCount <= 0)
            return;
        
        int bitRemain = bitCount;
        int valRemain = val & masks[bitRemain];
        int bitFree = (8 - fBitsInBuffer);
        
        if (bitFree > bitCount) {
            fBitsBuffer = (byte) (fBitsBuffer << bitCount);
            int tmp = valRemain & masks[bitCount];
            fBitsBuffer = (byte) (fBitsBuffer | tmp);
            fBitsInBuffer += bitCount;
        } else {
            if (bitFree == 0) {
                fBufferPos++;
                fBitsInBuffer = 0;
                fBitsBuffer = 0;
            } else {
                fBitsBuffer = (byte) (fBitsBuffer << bitFree);
                int tmp = (valRemain >> (bitRemain-bitFree)) & masks[bitCount];
                fBitsBuffer = (byte) (fBitsBuffer | tmp);
                fBuffer[fBufferOffset+fBufferPos] = fBitsBuffer;
                
                bitRemain = bitCount - bitFree;
                valRemain = valRemain & masks[bitRemain];
                fBufferPos++;
                fBitsInBuffer = 0;
                fBitsBuffer = 0;
            }
            
            while (bitRemain > 8) {
                bitRemain -= 8;
                fBuffer[fBufferOffset+fBufferPos] = (byte)((valRemain >> bitRemain) & 0xFF);
                fBufferPos++;
            }
            
            if (bitRemain > 0) {
                fBitsBuffer = (byte) (valRemain & masks[bitRemain]);
                fBitsInBuffer = bitRemain;
            }
        }
        
        if (fBitsInBuffer > 0) {
            fBuffer[fBufferOffset+fBufferPos] = (byte) (fBitsBuffer << (8-fBitsInBuffer));
        }
    }

    void init(byte[] buffer, int offset, int size) {
        // fBookmarkOn = 0;
        fBuffer = buffer;
        fBufferSize = size;
        fBitsInBuffer = 0;
        fBitsBuffer = 0;
        fBufferPos = 0;
        fBufferOffset = offset;
    }

    void bookmark(int bSet) {
        if (0 != bSet) {
            fBitsInBufferMark = fBitsInBuffer;
            fBufferMark = fBuffer;
            fBufferSizeMark = fBufferSize;
            fBitsBufferMark = fBitsBuffer;
            fBufferPosMark = fBufferPos;
            fBufferOffsetMark = fBufferOffset;
            fBookmarkOn = 1;
        } else {
            fBitsInBuffer = fBitsInBufferMark;
            fBuffer = fBufferMark;
            fBufferSize = fBufferSizeMark;
            fBitsBuffer = fBitsBufferMark;
            fBufferPos = fBufferPosMark;
            fBufferOffset = fBufferOffsetMark;
            fBookmarkOn = 0;
        }
    }

    int PeekBits(int bits) throws BitstreamException {
        int ret;
        bookmark(1);
        ret = GetBits(bits);
        bookmark(0);
        return ret;
    }
}
