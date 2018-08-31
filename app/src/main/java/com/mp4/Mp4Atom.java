package com.mp4;

import java.util.Arrays;
import java.util.Vector;

import android.util.Log;

import cn.ubia.util.StringUtils;

public class Mp4Atom {
private byte[] fType = null;
private long fSize = 0;
private long fStart = 0;
private long fEnd = 0;
private byte fDepth = 0;
private boolean fExpectChild = false;
private Vector<Mp4Property> fProperties;	///< �����б�
private Vector<Mp4Atom> fChildAtoms;	///< �ӽڵ��б�
private Mp4Atom fParentAtom;	///< ����ڵ�ĸ��ڵ�

public Mp4Atom()
{
	Reset();
}

/** 
 * ���� Mp4Atom
 * @param type ��� atom ������, һ�� atom ������Ϊ 4 �� ASCII �ַ�
 *	���, �� "moov".
 */
public Mp4Atom( byte[] type )
{
	Log.e("","Mp4Atom   type:"+StringUtils.getStringFromByte(type));
	Reset();
	System.arraycopy(type, 0, fType, 0, 4);
	AddProperties((byte)0);
}

public Mp4Atom(String type) {
    this(type.getBytes());
}

/** �������г�Ա����. */
void Reset()
{
	fSize	= 0;
	fStart	= 0;
	fEnd	= 0;
	fDepth	= 0;
	fExpectChild = false;
	fType = new byte[5];
	Arrays.fill(fType, (byte)0);
	if (null == fProperties) {
	    fProperties = new Vector<Mp4Property>();
	} else {
	    fProperties.clear();
	}
	
	if (null == fChildAtoms) {
	    fChildAtoms = new Vector<Mp4Atom>();
	} else {
	    fChildAtoms.clear();
	}
}

long GetSize() { return fSize; }

long GetStart() { return fStart; }

/** ��������ڵ�ĳ���. */
void SetSize( long size )
{
    fSize = size; 
    fEnd = fStart + fSize; // �Զ��������λ��
}

/** ��������ڵ�Ŀ�ʼλ��. */
void SetStart( long start )
{
    fStart = start; 
    fEnd = fStart + fSize; // �Զ��������λ��
}

Mp4Atom GetParent() { return fParentAtom; }
void SetParent(Mp4Atom parent) { fParentAtom = parent; }

byte[] GetType() { return fType; }
int GetChildAtomCount() { return fChildAtoms.size(); }
int GetPropertyCount() { return fProperties.size(); }

/** ��� version �� flags ��������������. */
void AddVersionAndFlags() {
	AddProperty(Mp4PropertyType.IntegerProperty, 1, "version");
	AddProperty(Mp4PropertyType.IntegerProperty, 3, "flags");
}

/** 
 * ��ʼ����� atom �������б������. 
 * @param version ��� atom �İ汾��
 * @return ����ɹ��򷵻� MP4_S_OK(0), ���򷵻�һ��С�� 0 �Ĵ���.
 */
int AddProperties(byte version)
{
	if (fType[0] == '\0') {
		return Mp4ErrorCode.MP4_ERR_FAILED; // û������ atom ������
	}

	int type = Mp4Common.ATOMID(fType);

	if (fType[0] == 'a') {
		// AVC atom
		if (type == Mp4Common.ATOMID("avc1")) {
			AddProperty(Mp4PropertyType.BytesProperty,		6, "reserved1");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "dataReferenceIndex");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "version");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "level");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "vendor");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "temporalQuality");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "spatialQuality");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "width");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "height");
			AddProperty(Mp4PropertyType.FloatProperty,		4, "hor");
			AddProperty(Mp4PropertyType.FloatProperty,		4, "ver");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "dataSize");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "frameCount");
			AddProperty(Mp4PropertyType.StringProperty,		32, "compressorName");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "depth");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "colorTable");
			fExpectChild = true;

		// AVC atom
		} else if (type == Mp4Common.ATOMID("avcC")) {
			AddProperty(Mp4PropertyType.IntegerProperty,	1, "configurationVersion");
			AddProperty(Mp4PropertyType.IntegerProperty,	1, "AVCProfileIndication");
			AddProperty(Mp4PropertyType.IntegerProperty,	1, "profile_compatibility");
			AddProperty(Mp4PropertyType.IntegerProperty,	1, "AVCLevelIndication");
			AddProperty(Mp4PropertyType.IntegerProperty,	1, "lengthSizeMinusOne");

			AddProperty(Mp4PropertyType.BitsProperty,		3, "reserved2");
			AddProperty(Mp4PropertyType.BitsProperty,		5, "numOfSequenceParameterSets");
			AddProperty(Mp4PropertyType.SizeTableProperty,	0, "sequenceEntries");
			AddProperty(Mp4PropertyType.IntegerProperty,	1, "numOfPictureParameterSets");
			AddProperty(Mp4PropertyType.SizeTableProperty,	0, "pictureEntries");
		}

	} else if (fType[0] == 'd') {

		// Data information atom
		if (type == Mp4Common.ATOMID("dinf")) {
			fExpectChild = true;

		// AMR audio atom
		} else if (type == Mp4Common.ATOMID("damr")) {
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "vendor");
			AddProperty(Mp4PropertyType.IntegerProperty,	1, "decoderVersion");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "modeSet");
			AddProperty(Mp4PropertyType.IntegerProperty,	1, "modeChangePeriod");
			AddProperty(Mp4PropertyType.IntegerProperty,	1, "framesPerSample");

		// Data reference atom
		} else if (type == Mp4Common.ATOMID("dref")) {
			AddVersionAndFlags();			
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "entryCount");
			fExpectChild = true;
		}

	} else if (fType[0] == 'e') {
		// MPEG-4 elementary stream descriptor atom
		if (type == Mp4Common.ATOMID("esds")) {
			AddVersionAndFlags();
			
			Mp4DescriptorProperty property = new Mp4DescriptorProperty("descriptor");
			property.AddDescriptor(new Mp4Descriptor(Mp4DescriptorType.Mp4ESDescrTag));
			fProperties.add(property);
		}

	} else if (fType[0] == 'f') {
		// File type atom
		if (type == Mp4Common.ATOMID("ftyp")) {
			AddProperty(Mp4PropertyType.StringProperty,		4, "majorBrand");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "minorVersion");
			AddProperty(Mp4PropertyType.StringProperty,		16, "brands");

		// Free atom
		} else if (type == Mp4Common.ATOMID("free")) {
			AddProperty(Mp4PropertyType.BytesProperty,		16, "reserved1");
		}

	} else if (fType[0] == 'h') {
		// Handler reference atom
		if (type == Mp4Common.ATOMID("hdlr")) {
			AddVersionAndFlags();
			AddProperty(Mp4PropertyType.BytesProperty,		4, "type");
			AddProperty(Mp4PropertyType.StringProperty,		4, "handlerType");
			AddProperty(Mp4PropertyType.BytesProperty,      12, "reserved1");
			AddProperty(Mp4PropertyType.StringProperty,		25, "manufacturer");
		}

	} else if (fType[0] == 'i') {
		// Descriptor atom
		if (type == Mp4Common.ATOMID("iods")) {
			AddVersionAndFlags();

			Mp4DescriptorProperty property = new Mp4DescriptorProperty("descriptor");
			property.AddDescriptor(new Mp4Descriptor(Mp4DescriptorType.Mp4FileIODescrTag));
			fProperties.add(property);		
		}

	} else if (fType[0] == 'm') {
		// Movie atom
		if (type == Mp4Common.ATOMID("moov")) {
			fExpectChild = true;

		// Media atom
		} else if (type == Mp4Common.ATOMID("mdia")) {
			fExpectChild = true;			

		// Movie header atom
		} else if (type == Mp4Common.ATOMID("mvhd")) {
			AddVersionAndFlags();

			AddProperty(Mp4PropertyType.IntegerProperty,	4, "creationTime");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "modificationTime");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "timeScale");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "duration");
			AddProperty(Mp4PropertyType.FloatProperty,		4, "rate");
			AddProperty(Mp4PropertyType.FloatProperty,		2, "volume");
			AddProperty(Mp4PropertyType.BytesProperty,		10, "reserved1");
			AddProperty(Mp4PropertyType.BytesProperty,		60, "reserved2");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "nextTrackId");

		// Media header atom
		} else if (type == Mp4Common.ATOMID("mdhd")) {
			AddVersionAndFlags();

			AddProperty(Mp4PropertyType.IntegerProperty,	4, "creationTime");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "modificationTime");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "timeScale");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "duration");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "language");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "quality");

		// Media information atom
		} else if (type == Mp4Common.ATOMID("minf")) {
			fExpectChild = true;

		// MPEG-4 audio atom
		} else if (type == Mp4Common.ATOMID("mp4a")) {
			AddProperty(Mp4PropertyType.BytesProperty,		6, "reserved1");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "dataReferenceIndex");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "version");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "level");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "vendor");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "channels");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "sampleSize");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "packetSize");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "sampleRate");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "reserved2");
			fExpectChild = true;
		}

	} else if (fType[0] == 'r') {
		// Root atom
		if (type == Mp4Common.ATOMID("root")) {
			fExpectChild = true;
		}

	} else if (fType[0] == 's') {
		// Sound media information header atom
		if (type == Mp4Common.ATOMID("smhd")) {
			AddVersionAndFlags();
			AddProperty(Mp4PropertyType.BytesProperty,		4, "reserved");

		// AMR audio atom
		} else if (type == Mp4Common.ATOMID("samr")) {
			AddProperty(Mp4PropertyType.BytesProperty,		6, "reserved1");	
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "dataReferenceIndex");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "version");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "level");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "vendor");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "channels");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "sampleSize");
			AddProperty(Mp4PropertyType.BytesProperty,		4, "reserved2");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "timeScale");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "reserved3");
			fExpectChild = true;

		// Sample table atom
		} else if (type == Mp4Common.ATOMID("stbl")) {
			fExpectChild = true;

		// Sample description atom
		} else if (type == Mp4Common.ATOMID("stsd")) {
			AddVersionAndFlags();
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "entryCount");
			fExpectChild = true;

		// Time-to-sample atom
		} else if (type == Mp4Common.ATOMID("stts")) {
			AddVersionAndFlags();			
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "entryCount");
			
			Mp4TableProperty table = new Mp4TableProperty("entries");
			table.AddColumn("sampleCount");
			table.AddColumn("sampleDelta");
			fProperties.add(table);

		// Sample-to-chunk atom
		} else if (type == Mp4Common.ATOMID("stsc")) {
			AddVersionAndFlags();			
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "entryCount");

			Mp4TableProperty table = new Mp4TableProperty("entries");
			table.AddColumn("firstChunk");
			table.AddColumn("samplesPerChunk");
			table.AddColumn("sampleDescriptionIndex");
			fProperties.add(table);

		// Sample size atom
		} else if (type == Mp4Common.ATOMID("stsz")) {
			AddVersionAndFlags();			
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "sampleSize");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "entryCount");

			Mp4TableProperty table = new Mp4TableProperty("entries");
			table.AddColumn("sampleSize");
			fProperties.add(table);

		// Sync sample atom
		} else if (type == Mp4Common.ATOMID("stss")) {
			AddVersionAndFlags();			
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "entryCount");
			
			Mp4TableProperty table = new Mp4TableProperty("entries");
			table.AddColumn("sampleNumber");
			fProperties.add(table);

		// Chunk offset atom
		} else if (type == Mp4Common.ATOMID("stco")) {
			AddVersionAndFlags();
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "entryCount");
			
			Mp4TableProperty table = new Mp4TableProperty("entries");
			table.AddColumn("chunkOffset");
			fProperties.add(table);
		}

	} else if (fType[0] == 't') {
		// Track atom
		if (type == Mp4Common.ATOMID("trak")) {
			fExpectChild = true;

		// Track header atom
		} else if (type == Mp4Common.ATOMID("tkhd")) {
			AddVersionAndFlags();
			
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "creationTime");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "modificationTime");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "trackId");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "reserved1");
			AddProperty(Mp4PropertyType.IntegerProperty,	4, "duration");
			AddProperty(Mp4PropertyType.BytesProperty,		12, "reserved2");	
			AddProperty(Mp4PropertyType.FloatProperty,		2, "volume");
			AddProperty(Mp4PropertyType.IntegerProperty,	2, "reserved3");
			AddProperty(Mp4PropertyType.BytesProperty,		36, "matrix");
			AddProperty(Mp4PropertyType.FloatProperty,		4, "width");
			AddProperty(Mp4PropertyType.FloatProperty,		4, "height");
		}

	} else if (fType[0] == 'u') {
		// Url atom
		if (type == Mp4Common.ATOMID("url ")) {
			AddVersionAndFlags();			
		}

	} else if (fType[0] == 'v') {
		// Video media information header atom
		if (type == Mp4Common.ATOMID("vmhd")) {
			AddVersionAndFlags();
			AddProperty(Mp4PropertyType.BytesProperty,		8, "reserved");
		}
	}
	return Mp4ErrorCode.MP4_S_OK;
}

/** ��ʼ����� atom, ��ӱ������ atom �ڵ�, �Լ�����Ĭ�ϵ�����ֵ��. */
int Init( byte version )
{
	if (fType[0] == 0) {
		return Mp4ErrorCode.MP4_ERR_FAILED;
	}

	final byte[] matrix = {
		0x00, 0x01, 0x00, 0x00,  0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00,  0x00, 0x00, 0x00, 0x00, 
		0x00, 0x01, 0x00, 0x00,  0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00,  0x00, 0x00, 0x00, 0x00, 
		0x40, 0x00, 0x00, 0x00, 
	};

	int type = Mp4Common.ATOMID(fType);
	if (fType[0] == 'a') {
		if (type == Mp4Common.ATOMID("avc1")) {
			AddChildAtom("avcC");

			SetIntProperty("dataReferenceIndex", 1);
			SetFloatProperty("hor", 72.0);
			SetFloatProperty("ver", 72.0);
			SetIntProperty("frameCount", 1);
			SetStringProperty("compressorName", "\012AVC Coding");
			SetIntProperty("depth", 24);
			SetIntProperty("colorTable", 65535);

		} else if (type == Mp4Common.ATOMID("avcC")) {
			SetIntProperty("configurationVersion", 1);
			SetIntProperty("lengthSizeMinusOne", 0xFF);
			SetIntProperty("reserved2", 0x07);
		}

	} else if (fType[0] == 'd') {
		if (type == Mp4Common.ATOMID("dinf")) {
			AddChildAtom("dref");
			
		} else if (type == Mp4Common.ATOMID("damr")) {
			SetIntProperty("vendor", 0x6d346970);
			SetIntProperty("decoderVersion", 1);
			SetIntProperty("framesPerSample", 1);

		} else if (type == Mp4Common.ATOMID("dref")) {
			AddChildAtom("url ");
			SetIntProperty("entryCount", 1);
		}

	} else if (fType[0] == 'f') {
		if (type == Mp4Common.ATOMID("ftyp")) {
//			SetStringProperty("majorBrand", "M4V ");
//			SetIntProperty("minorVersion", 1);
//			SetStringProperty("brands", "M4V M4A mp42isom");
			SetStringProperty("majorBrand", "mp42");
			SetStringProperty("brands", "mp42isom");
		}

	} else if (fType[0] == 'h') {
		if (type == Mp4Common.ATOMID("hdlr")) {
			SetStringProperty("handlerType", "vide");
			SetStringProperty("manufacturer", "Apple Video Media Handler");
		}
	} else if (fType[0] == 'm') {
		if (type == Mp4Common.ATOMID("moov")) {
			AddChildAtom("mvhd");
			AddChildAtom("iods");
			
		} else if (type == Mp4Common.ATOMID("mdia")) {
			AddChildAtom("mdhd");
			AddChildAtom("hdlr");
			AddChildAtom("minf");

		} else if (type == Mp4Common.ATOMID("minf")) {
			AddChildAtom("dinf");
			AddChildAtom("stbl");

		} else if (type == Mp4Common.ATOMID("mp4a")) {
			SetIntProperty("dataReferenceIndex", 1);
			SetIntProperty("sampleSize", 16);

			AddChildAtom("esds");

		} else if (type == Mp4Common.ATOMID("mvhd")) {
			SetIntProperty("timeScale", 1000);
			SetFloatProperty("rate", 1.0);
			SetFloatProperty("volume", 1.0);
			SetIntProperty("nextTrackId", 1);
//			SetIntProperty("nextTrackId", 2);

			Mp4Property p = FindProperty("reserved2");
			if (null != p) {
				p.SetBytesValue(matrix, 36);
			}			
		}
	} else if (fType[0] == 'r') {
		if (type == Mp4Common.ATOMID("root")) {
			AddChildAtom("ftyp");
			AddChildAtom("mdat");
			AddChildAtom("moov");
		}

	} else if (fType[0] == 's') {
		if (type == Mp4Common.ATOMID("stbl")) {
			AddChildAtom("stsd");
			AddChildAtom("stts");
			AddChildAtom("stsz");

			AddChildAtom("stsc");
			AddChildAtom("stco");

		} else if (type == Mp4Common.ATOMID("samr")) {
			SetIntProperty("dataReferenceIndex", 1);
			SetIntProperty("channels", 2);
			SetIntProperty("sampleSize", 16);

			AddChildAtom("damr");
		}

	} else if (fType[0] == 't') {
		if (type == Mp4Common.ATOMID("trak")) {
			AddChildAtom("tkhd");
			AddChildAtom("mdia");			

		} else if (type == Mp4Common.ATOMID("tkhd")) {
			SetIntProperty("flags", 1);
			SetIntProperty("trackId", 1);
			Mp4Property p = FindProperty("matrix");
			if (null != p) {
				p.SetBytesValue(matrix, 36);
			}
		}

	} else if (fType[0] == 'u') {
		if (type == Mp4Common.ATOMID("url ")) {
			SetIntProperty("flags", 1);
		}

	} else if (fType[0] == 'v') {
		if (type == Mp4Common.ATOMID("vmhd")) {
			SetIntProperty("flags", 1);
		}
	}

	return Mp4ErrorCode.MP4_S_OK;
}

Mp4Atom AddChildAtom( String name ) {
    Mp4Atom atom = AddChildAtom(name, -1);
    return atom;
}

////////////////////////////////////////////////////////////////////////////////
// �ӽڵ����

/**
 * ���һ��ָ�������Ƶ��� ATOM �ڵ�. 
 * @param name Ҫ��ӵ��� ATOM �ڵ������
 * @param index �����λ��, -1 ��ʾ��ӵ������ڵ�ĺ���.
 * @return ������ӵĽڵ��ָ��, ���û����ӳɹ��򷵻�һ����ָ��.
 */
Mp4Atom AddChildAtom( String name, int index )
{
	if (name == null || 0 == name.length()) {
		return null;
	}

	int p = name.indexOf('.');
	if (p >= 0) {
	    String subName = name.substring(p+1);
		Mp4Atom child = GetChildAtom(name);
		if (null != child) {
			return child.AddChildAtom(subName, index);
		}
	} else {
		Mp4Atom atom = new Mp4Atom(name);
		atom.SetParent(this);
		atom.Init((byte)0);

		if (index < 0 || index >= fChildAtoms.size()) {
			// ��ӵ����������ڵ�ĺ���
			fChildAtoms.add(atom);
		} else {
			// ���뵽ָ����λ��
			fChildAtoms.insertElementAt(atom, index);
		}
		return atom;
	}

	return null;
}

/** 
 * ����ָ�������Ƶ��� atom �ڵ�. 
 * @param name Ҫ���ҵĽڵ������, �༶�ڵ������� "." ����, �� "moov.trak".
 * @return �����ҵ��Ľڵ��ָ��, ���û���ҵ��򷵻�һ����ָ��.
 */
Mp4Atom FindAtom( String name )
{
	if (name == null || 0 == name.length()) {
		return null;
	}
	
	int p = name.indexOf('.');
	if (p >= 0) {
	    String subName = name.substring(p+1);
		Mp4Atom child = GetChildAtom(name);
		if (null != child) {
			return child.FindAtom(subName);
		}
	} else {
		return GetChildAtom(name);
	}

	return null;
}

/** 
 * ����ָ�������Ƶ��ӽڵ�, ע���������ֻ�᷵����һ���ڵ���ӽڵ�.
 * @param name Ҫ���ҵĽڵ������. 
 * @return �����ҵ��Ľڵ��ָ��, ���û���ҵ��򷵻�һ����ָ��.
 */
Mp4Atom GetChildAtom( String name )
{
	if (name == null || 0 == name.length()) {
		return null;
	}

	int type = Mp4Common.ATOMID(name);
	int count = fChildAtoms.size();
	for (int i = 0; i < count; i++) {
		Mp4Atom atom = fChildAtoms.get(i);
		if (null != atom && Mp4Common.ATOMID(atom.fType) == type) {
			return atom;
		}
	}

	return null;
}

/** ����ָ�����������ӽڵ�. */
Mp4Atom GetChildAtom( int index )
{
	if (index < 0 || index >= fChildAtoms.size()) {
		return null;
	}

	return fChildAtoms.get(index);
}

/** ������е��ӽڵ�. */
void ClearChildAtoms()
{
	fChildAtoms.clear();	
}

////////////////////////////////////////////////////////////////////////////////
// ���Բ���

/** 
 * ���ָ�����ͺ����Ƶ�����. 
 * @param type Ҫ��ӵ����Ե�����
 * @param size Ҫ��ӵ����ԵĴ�С, ��λΪ�ֽ�
 * @param name Ҫ��ӵ����Ե�����
 * @return ������ӵ����Ե�ָ��, ���û����ӳɹ��򷵻�һ����ָ��.
 */
Mp4Property AddProperty(int type, int size, String name)
{
	Mp4Property property = null;
	if (type == Mp4PropertyType.SizeTableProperty) {
		property = new Mp4SizeTableProperty(name);

	} else if (type == Mp4PropertyType.DescriptorProperty) {
		property = new Mp4DescriptorProperty(name);

	} else if (type == Mp4PropertyType.TableProperty) {
		return null; // ��֧��ͨ������������������͵�����

	} else if (type == Mp4PropertyType.IntegerArrayProperty) {
		return null; // ��֧��ͨ������������������͵�����

	} else {
		property = new Mp4Property(type, size, name);
	}

	fProperties.add(property);
	return property;
}

/** ����ָ�������Ե�ֵ. */
void SetIntProperty( String name, long value )
{
	Mp4Property property = FindProperty(name);
	if (null != property) {
		property.SetIntValue(value);
	}
}

/** ����ָ�������Ե�ֵ. ����������򷵻� 0. */
long GetIntProperty( String name )
{
	Mp4Property property = FindProperty(name);
	if (null != property) {
		return property.GetIntValue();
	}
	
	return 0;
}

/** ����ָ�������Ե�ֵ. */
void SetStringProperty( String name, String value )
{
	Mp4Property property = FindProperty(name);
	if (null != property) {
		property.SetStringValue(value);
	}	
}

/** ����ָ�������Ե�ֵ. */
int GetStringProperty( String name, byte[] buf, int bufLen )
{
	Mp4Property property = FindProperty(name);
	if (property == null || buf == null || bufLen <= 1) {
		return 0;
	}

	return property.GetStringValue(buf, bufLen);
}

void SetFloatProperty( String name, double value ) {
    SetFloatProperty(name, (float)value);
}
/** ����ָ�������Ե�ֵ. */
void SetFloatProperty( String name, float value )
{
	Mp4Property property = FindProperty(name);
	if (null != property) {
		property.SetFloatValue(value);
	}
}

/** ����ָ�������Ե�ֵ. ����������򷵻� 0.0. */
float GetFloatProperty( String name )
{
	Mp4Property property = FindProperty(name);
	if (null != property) {
		return property.GetFloatValue();
	}
	
	return 0;
}

/** ����ָ��������������. */
Mp4Property GetProperty( int index )
{
	if (index < 0 || index >= fProperties.size()) {
		return null;
	}
	
	return fProperties.get(index);
}

/** ���Ҳ�����ָ�������Ƶ�����. */
Mp4Property FindProperty( String name )
{
	if (name == null || 0 == name.length()) {
		return null;
	}

	int p = name.indexOf('.');

	// ����ָ�����Ƶ��ӽڵ�
	if (p >= 0) {
	    String subName = name.substring(p+1);
		Mp4Atom child = GetChildAtom(name);
		if (null != child) {
			return child.FindProperty(subName);
		}

		return null;
	}
	
	// ����ָ�����Ƶ�����
	int count = fProperties.size();
	for (int i = 0; i < count; i++) {
		Mp4Property property = fProperties.get(i);
		if (property == null) {
			return null;
		}
		
		if (property.GetType() == Mp4PropertyType.DescriptorProperty) {
			return ((Mp4DescriptorProperty)property).GetProperty(name);
		}

		if (null != property.GetName() && name.equalsIgnoreCase(property.GetName())) {
			return property;
		}
	}
	
	return null;
}

/** 
 * ����ָ���ı������ָ��������. 
 * @param name �����������.
 * @param col ����������
 */
Mp4ArrayProperty GetTableProperty( String name, String col )
{
	if (col == null || 0 == col.length()) {
		return null;
	}

	Mp4Property property = FindProperty(name);
	if (property == null || property.GetType() != Mp4PropertyType.TableProperty) {
		return null; // û��������Ի���������Ե����Ͳ��Ǳ����������
	}

	Mp4TableProperty table = (Mp4TableProperty)property;
	int count = table.GetColCount();
	for (int i = 0; i < count; i++) {
		Mp4ArrayProperty array = table.GetColumn(i);
		if (null != array && null != array.GetName() && col.equalsIgnoreCase(array.GetName())) {
			return array;
		}
	}
	return null;
}

////////////////////////////////////////////////////////////////////////////////
// д����

/** ��ʼд���ļ�, д atom ������ͷ��. */
int BeginWrite( Mp4File file )
{
	fStart = file.GetPosition(); // ��¼��� atom ���ļ��еĿ�ʼλ��

	int ret = file.WriteInt(fSize, 4);		// atom �Ĵ�С
	ret += file.WriteBytes(fType, 4);	// atom ������

	return (ret == 8) ? Mp4ErrorCode.MP4_S_OK : Mp4ErrorCode.MP4_ERR_WRITE;
}

/** ���д��, ��Ҫ��д����� atom ��ʵ�ʳ���. */
int FinishWrite( Mp4File file )
{
	fEnd = file.GetPosition();	// ��¼��� atom ���ļ��еĽ���λ��
	fSize = fEnd - fStart;

	// ����д����ڵ�ĳ���
	file.SetPosition(fStart);
	int ret = file.WriteInt(fSize, 4);

	// �ƻؽڵ�Ľ���λ��
	file.SetPosition(fEnd);

	return (ret == 4) ? Mp4ErrorCode.MP4_S_OK : Mp4ErrorCode.MP4_ERR_WRITE;
}

/** д����ڵ���������Ե��ļ���. */
int WriteProperties(Mp4File file)
{
	int ret = Mp4ErrorCode.MP4_S_OK;
	int count = fProperties.size();
	for (int i = 0; i < count; i++) {
		Mp4Property property = fProperties.get(i);
		if (property == null) {
			ret = Mp4ErrorCode.MP4_ERR_NULL_PROPERTY;
			break;
		}

		ret = property.Write(file);
		if (ret != Mp4ErrorCode.MP4_S_OK) {
			break;
		}
	}

	return ret;
}

/** д����ڵ�������ӽڵ㵽�ļ���. */
int WriteChildAtoms(Mp4File file)
{
	int ret = Mp4ErrorCode.MP4_S_OK;
	int count = fChildAtoms.size();
	for (int i = 0; i < count; i++) {
		Mp4Atom atom = fChildAtoms.get(i);
		if (atom == null) {
			ret = Mp4ErrorCode.MP4_ERR_NULL_ATOM;
			break;
		}

		ret = atom.Write(file);
		if (ret != Mp4ErrorCode.MP4_S_OK) {
			break;
		}
	}
	return ret;
}

/** 
 * ����� atom ������д��ָ�����ļ���ǰλ��. 
 * @param file Ҫд��� MP4 �ļ�
 * @return ����ɹ��򷵻� MP4_S_OK(0), ���򷵻�һ�����������ĸ���.
 */
int Write( Mp4File file )
{
	if (file == null) {
		return Mp4ErrorCode.MP4_ERR_NULL_FILE;
	}

	int type = Mp4Common.ATOMID(fType);
	if (type == Mp4Common.ATOMID("root") || type == Mp4Common.ATOMID("mdat")) {
		return Mp4ErrorCode.MP4_ERR_FAILED; // ������ֱ��д������ atom 
	}

	// д��� ATOM ��ͷ��
	int ret = BeginWrite(file);

	// д����ڵ������
	if (ret == Mp4ErrorCode.MP4_S_OK) {
		ret = WriteProperties(file);
	}

	// д����ڵ���ӽڵ�
	if (ret == Mp4ErrorCode.MP4_S_OK) {
		ret = WriteChildAtoms(file);
	}

	// д����ڵ�ʵ�ʵĳ��ȵ�, ��������ڵ��д����.
	if (ret == Mp4ErrorCode.MP4_S_OK) {
		ret = FinishWrite(file);
	}
	return ret;
}

////////////////////////////////////////////////////////////////////////////////
// ������

/** ����û�ж�ȡ�������. */
void Skip( Mp4File file )
{
	if (null != file && (fEnd > 0) && (fEnd != file.GetPosition())) {
		file.SetPosition(fEnd);
	}
}

/** ��ȡ����ڵ���������Ե�ֵ. */
int ReadProperties(Mp4File file)
{
	int ret = Mp4ErrorCode.MP4_S_OK;
	int count = fProperties.size();
	try {
		Log.e("","fProperties.size():"+count);
	for (int i = 0; i < count; i++) {
	
			
	
		Mp4Property property = fProperties.get(i);
		if (property == null) {
			ret = Mp4ErrorCode.MP4_ERR_NULL_PROPERTY;
			break;
		}

		int type = property.GetType();
		if (type == Mp4PropertyType.TableProperty || type == Mp4PropertyType.SizeTableProperty) {
			if (i <= 0) {
				// Table ����ǰ������һ����ʾ����/����������
				ret = Mp4ErrorCode.MP4_ERR_FAILED;
				break;
			}

			Mp4Property countProperty = fProperties.get(i-1);
			count = (int)countProperty.GetIntValue();
			property.SetExpectSize(count);
			
		} else if (type == Mp4PropertyType.StringProperty) {
			if (property.GetSize() == 0) {
				count = (int)((fStart + fSize) - file.GetPosition());
				property.SetExpectSize(count);
			}

		} else if (type == Mp4PropertyType.DescriptorProperty) {
			count = (int)((fStart + fSize) - file.GetPosition());
			property.SetExpectSize(count);
		}

		ret = property.Read(file);
		if (ret != Mp4ErrorCode.MP4_S_OK) {
			break;
		}
	}
	} catch (Exception e) {
		// TODO: handle exception
	}
	return ret;
}

/** ��ȡ���е��ӽڵ�. */
int ReadChildAtoms(Mp4File file)
{
	long leftover = fEnd - file.GetPosition(); // ʣ������ݵĳ���
	int ret = Mp4ErrorCode.MP4_S_OK;

	// ��ȡ���е��ӽڵ�
	while (leftover > 8) {
		long start = file.GetPosition();	
		long size = file.ReadInt(4);	// ATOM ����
		if (size < 8) {
			ret = Mp4ErrorCode.MP4_ERR_READ;
			break;
		}
		
		// ATOM ����
		byte[] type = new byte[5];
		ret = file.ReadBytes(type, 4);
		if (ret != 4) {
			ret = Mp4ErrorCode.MP4_ERR_READ;
			break;
		}
		type[4] = '\0';
		Log.e("","type:"+StringUtils.getStringFromByte(type));
		Mp4Atom atom = new Mp4Atom(type);
		fChildAtoms.add(atom);
		
		atom.SetParent(this);
		atom.SetStart(start);
		atom.SetSize(size);
		
		// ��ȡ��� ATOM �ڵ�����Ժ��ӽڵ�
		ret = atom.Read(file);
		if (ret != Mp4ErrorCode.MP4_S_OK) {
			break;
		}
		
		// ����û�ж�ȡ�������
		atom.Skip(file);
		leftover -= size;
	}

	return ret;
}

/** ��ȡ ATOM ������. */
int Read( Mp4File file )
{
	if (file == null) {
		return Mp4ErrorCode.MP4_ERR_NULL_FILE;
	}

	int ret = Mp4ErrorCode.MP4_S_OK;
	if (fProperties.size() > 0) {
		ret = ReadProperties(file);
	}

	if ((ret == Mp4ErrorCode.MP4_S_OK) && fExpectChild) {
		ret = ReadChildAtoms(file);
	}

	return ret;
}

/** ������е���Դ. */
void Clear()
{
	fParentAtom = null;	
	for (int i = 0; i < fChildAtoms.size(); i++) {
		Mp4Atom atom = fChildAtoms.get(i);
		if (null != atom) {
			atom.Clear();
		}
	}

	fProperties.clear();
	fChildAtoms.clear();

	fStart	= 0;
	fEnd	= 0;
	fSize	= 0;
	fDepth	= 0;

	fExpectChild = false;
}
}
