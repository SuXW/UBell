package com.ubia.vr;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.YuvImage;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import cn.ubia.util.StringUtils;

import com.ubia.IOTC.Packet;
import com.ubia.vr.SphereMath.Point3F;

/**
 * step to use:<br/>
 * 1. new GLProgram()<br/>
 * 2. buildProgram()<br/>
 * 3. buildTextures()<br/>
 * 4. drawFrame()<br/>
 */
public class GLProgram {

    // program id
    private int _program;
    // window position
//    public final int mWinPosition;
    // texture id
    private int _textureI;
    private int _textureII;
    private int _textureIII;
    // texture index in gles
    private int _tIindex;
    private int _tIIindex;
    private int _tIIIindex;
    // vertices on screen
  
    // handles
    private int _positionHandle = -1, _coordHandle = -1;
    private int _yhandle = -1, _uhandle = -1, _vhandle = -1;
    private int _ytid = -1, _utid = -1, _vtid = -1;
    // vertices buffer

    // video width and height
    private int _video_width = -1;
    private int _video_height = -1;
    // flow control
    private boolean isProgBuilt = false;
    private int width;
    private int height;
    private int a_texCoord_h;
    private int a_position_h;
    private int u_texture_h;
    private int u_MVPMatrix_h;
    private FloatBuffer vertices;
    private int vertexBufferID[] = new int[1];
    private int textureBufferID[] = new int[1];
    private int vertexIndicesBufferID[] = new int[1];
    ByteBuffer indicesBuffer;
    FloatBuffer vextbuffer;
    FloatBuffer textbuffer;
    int numVertices,numTexcoord,numIndice;
    private float cam_scale = 1.0f;

    private int vCount;
    private static final int BYTES_PER_FLOAT = 4;
    private static final int GEO_COORDS_PER_VERTEX = 3;
    private static final int TEX_COORDS_PER_VERTEX = 2;
    private static final int VERTEX_PER_SQUARE = 6;
    private Bitmap bitmap;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    float fingerRotationX = 0,fingerRotationY = 0,fingerRotationZ = 0;
    float translationX = 0;
    float overture = 100.0f;   //default
    float maxFinger = 720;
    float minFinger = 0;
    int surfaceMode = VRConfig.surfaceHemiSpherePano;    
	int cameraPutModel = VRConfig.CameraPutModelFaceFront;
    private boolean hasBuilded = false;
    private boolean shallUpdateGLES = true;
    float bitmapRatio = 1.0f;
    public static final float MAX_SCALE = 1.5f;
    public static final float MIN_SCALE = 0.6f;
    public static final float MAX_OVERTURE = 100.0f;
    public static final float MIN_OVERTURE = 70.0f;
    
    
    private float geo_r = 10.0f;
    private int   geo_angleStep = 10;
    private int   geo_vrange = 180;
    private int   geo_hrange = 180; 
    private Point3F cam_eye = new Point3F(0f, 1.0f, 0f);
    private Point3F cam_head = new Point3F(0f, 0f, 1.0f); 
    
    /**
     * position can only be 0~4:<br/>
     * fullscreen => 0<br/>
     * left-top => 1<br/>
     * right-top => 2<br/>
     * left-bottom => 3<br/>
     * right-bottom => 4
     */
    public GLProgram(int position) {
        if (position < 0 || position > 4) {
            throw new RuntimeException("Index can only be 0 to 4");
        }
   
    }



    public boolean isProgramBuilt() {
        return isProgBuilt;
    }
    public int getProgram(){
    	return _program;
    }
    
    public int buildProgram() {
        // TODO createBuffers(_vertices, coordVertices);
        if (_program <= 0) {
            _program = createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        }
        Utils.LOGD("_program = " + _program);

     
        isProgBuilt = true;
        return _program;
    }

 
 
    /**
     * create program and load shaders, fragment shader is very important.
     */
    public int createProgram(String vertexSource, String fragmentSource) {
        // create shaders
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        // just check
        Utils.LOGD("vertexShader = " + vertexShader);
        Utils.LOGD("pixelShader = " + pixelShader);
        Log.e("ubiaGLES", "createProgram===>");
        
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Utils.LOGE("Could not link program: ");
                Utils.LOGE(GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
             
        }
        return program;
    }

    /**
     * create shader with given source.
     */
    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Utils.LOGE("Could not compile shader " + shaderType);
                Utils.LOGE(GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }


    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Utils.LOGE("***** " + op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }
    /**
     * Releases the program.
     * <p>
     * The appropriate EGL context must be current (i.e. the one that was used to create
     * the program).
     */
    public void release() { 
        GLES20.glDeleteProgram(_program);
        _program = -1;
        isProgBuilt = false;
    }
    static float[] squareVertices = { -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, }; // fullscreen

    private static final String VERTEX_SHADER = 
    		"attribute vec4 a_position;\n" + 
    		"attribute vec2 a_texCoord;\n" +
            "varying vec2 v_texCoord;\n" + 
            "uniform mat4 u_MVPMatrix;\n" +
    		"void main() {\n" + 
            "gl_Position = u_MVPMatrix * (a_position) ;\n" + 
    		"v_texCoord = a_texCoord;\n" + "}\n";

    private static final String FRAGMENT_SHADER = 
    		"precision mediump float;\n" + 
    		"uniform sampler2D tex_y;\n" +
            "uniform sampler2D tex_u;\n" + 
    		"uniform sampler2D tex_v;\n" + 
            "varying vec2 v_texCoord;\n" + 
    		"void main() {\n" + 
            	"vec4 c = vec4((texture2D(tex_y, v_texCoord).r - 16./255.) * 1.164);\n" +
            	"vec4 U = vec4(texture2D(tex_u, v_texCoord).r - 128./255.);\n" +
            	"vec4 V = vec4(texture2D(tex_v, v_texCoord).r - 128./255.);\n" + 
            	"c += V * vec4(1.596, -0.813, 0, 0);\n" +
            	"c += U * vec4(0, -0.392, 2.017, 0);\n" + 
            	"c.a = 1.0;\n" + 
            	"gl_FragColor = c;\n" + "}\n";

}