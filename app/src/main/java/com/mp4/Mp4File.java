package com.mp4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.ubia.IOTC.Packet;

public class Mp4File {
	private File fFile = null;
	private FileInputStream fInput = null;
	private FileOutputStream fOutput = null;
	//private long fFileSize = 0;
	//private boolean fIsWriting = false;
	private long fBitsBuffer = 0;
	private int fBitsCount = 0;
	private Object fIsWriting;
	private long fFileSize;
	Mp4File()
	{
		fFile			= null;
		 fFileSize		= 0;
		fBitsBuffer		= 0;
		fBitsCount		= 0;
	}

	int Open( String name, String mode )
	{
		if (fFile != null) {
			return Mp4ErrorCode.MP4_ERR_ALREADY_OPEN;
		}

		fFile = new File(name);
		if (fFile == null) {
			return Mp4ErrorCode.MP4_ERR_OPEN;
		}
		if (mode.contains("r")) {
		 
			fFileSize	= fFile.length();
			fIsWriting	= false;
			try {
				fInput = new FileInputStream(fFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (mode.contains("w")) {
			fIsWriting	= true;
			fFileSize	= 0;
			try {
				fOutput = new FileOutputStream(fFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


	
		fBitsBuffer	= 0;
		fBitsCount	= 0;

		return Mp4ErrorCode.MP4_S_OK;
	}

	public long getfFileSize() {
		return fFileSize;
	}

	public void setfFileSize(long fFileSize) {
		this.fFileSize = fFileSize;
	}

	void Close()
	{
		try {
			if (null != fOutput) {
				fOutput.close();
				fOutput = null;
			}
			
			if (null != fInput) {
				fInput.close();
				fInput = null;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if (null != fFile) {
			fFile = null;
		}

		 fIsWriting	= false;
		 fFileSize	= 0;
		fBitsBuffer	= 0;
		fBitsCount	= 0;
	}
	
	void SetPosition(long position) {
		try {
			if (null != fInput)
				fInput.getChannel().position(position);

			if (null != fOutput)
			    fOutput.getChannel().position(position);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	long GetPosition() {
		try {
			if (null != fInput)
				return fInput.getChannel().position();
			
			if (null != fOutput)
				return fOutput.getChannel().position();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}

	int WriteBytes( byte[] bytes, int numBytes )
	{
		if (null == fFile)
			return 0;
		
		try {
			if (null == fOutput) {
				fOutput = new FileOutputStream(fFile);
			}
			
			int bytesLen = (bytes != null) ? bytes.length : 0;
			if (bytesLen >= numBytes) {
			    fOutput.write(bytes, 0, numBytes);
			} else {
			    fOutput.write(bytes, 0, bytesLen);
			    byte[] zeroBuf = new byte[numBytes-bytesLen];
			    Arrays.fill(zeroBuf, (byte)0);
			    fOutput.write(zeroBuf);
			}
			return numBytes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	int ReadBytes( byte[] bytes, int numBytes) {
		if (null == fFile)
			return 0;
		
		try {
			if (null == fInput) {
				fInput = new FileInputStream(fFile);
			}
			
			int result = fInput.read(bytes, 0, numBytes);
			return result;
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return 0;
	}

	/*int ReadBytes( byte* bytes, int numBytes, FILE* file )
	{
		if (file == NULL) {
			file = fFile;
		}
		
		if (file == NULL || bytes == NULL || numBytes <= 0) {
			return 0;
		}

		return fread(bytes, 1, numBytes, file);
	}*/

	/*int PeekBytes( byte* bytes, int numBytes, FILE* file )
	{
		long position = GetPosition(file);
		int size = ReadBytes(bytes, numBytes, file);
		if (size > 0) {
			SetPosition(position, file);
		}
		return size;
	}*/

	void StartReadBits()
	{
		fBitsCount  = 0;
		fBitsBuffer = 0;
	}

	long ReadBits( int size )
	{
		if (size <= 0 || size >= 8) {
			return 0;
		}

		if (fBitsCount == 0) {
			fBitsBuffer = ReadInt(1);
			fBitsCount = 8;
		}

		fBitsCount -= size;
		if (fBitsCount <= 0) {
			fBitsCount = 0;
		}

		long ret = fBitsBuffer;
		if (fBitsCount > 0) {
			ret >>= fBitsCount;
		}

		long mask = 0;
		for (int i = 0; i < size; i++) {
			mask |= 0x00000001 << i;
		}
		
		return ret & mask;
	}

	int WriteBits( long value, int size )
	{
		if (size <= 0 || size >= 8) {
			return 0;
		}
		
		if (fBitsCount == 0) {
			fBitsBuffer = 0;
		}

		long mask = 0;
		for (int i = 0; i < size; i++) {
			mask |= 0x00000001 << i;
		}
		value = value & mask;
		
		fBitsCount += size;
		if (fBitsCount < 8) {
			fBitsBuffer |= value << (8 - fBitsCount);
		} else {
			fBitsBuffer |= value;
		}
		
		if (fBitsCount >= 8) {
			WriteInt(fBitsBuffer, 1);
			fBitsCount = 0;
		}
		
		return 0;
	}

	int ReadMpegLength()
	{
		int length = 0;
		byte numBytes = 0;
		byte b = 0;
		
		do {
			b = (byte)ReadInt(1);
			length = (length << 7) | (b & 0x7F);
			numBytes++;
		} while (((b & 0x80) != 0 ) && numBytes < 4);
		
		return length;
	}

	int WriteInt( long value, int size )
	{
		byte[] data = new byte[9];
		switch (size) {
		case 1:	
			data[0] = (byte)(value & 0xFF); 
			break;
		case 2:	
			data[0] = (byte)(value >> 8); 
			data[1] = (byte)(value & 0xFF); 
			break;
		case 3:	
			data[0] = (byte)(value >> 16);
			data[1] = (byte)(value >> 8);
			data[2] = (byte)(value & 0xFF);
			break;
		case 4: 
			data[0] = (byte)(value >> 24);
			data[1] = (byte)(value >> 16);
			data[2] = (byte)(value >> 8);
			data[3] = (byte)(value & 0xFF);		 
			break;
		case 5: 
			{
			for (int i = 7; i >= 0; i--) {
				data[i] = (byte)(value & 0xFF);
				value >>= 8;
			}
			}
			break;
		default: return 0;
		}
		
		return WriteBytes(data, size);
	}

	long ReadInt( int size )
	{
		if (size < 0 || size > 8) {
			return 0;
		}
		
		byte[] data = new byte[9];
		int read = ReadBytes(data, size);
		if (read != size) {
			return 0;
		}

		long ret = 0;
		long temp = 0;
		switch (size) {
		case 1:	
			ret  = data[0];
			break;
		case 2:
			ret = (data[0] << 8) | data[1];
			break;
		case 3:
			ret = (data[0] << 16) | (data[1] << 8) | data[2];
			break;
		case 4:
			ret=	Packet.byteArrayToInt_Big(data );
//			ret = (data[0] << 24) | (data[1] << 16) | (data[2] << 8) | data[3];
			break;
		case 8: 
			{
				for (int i = 0; i < 8; i++) {
					temp = data[i];
					ret |= temp << ((7 - i) * 8);
				}
			}
			break;
		}
		return ret;
	}
}
