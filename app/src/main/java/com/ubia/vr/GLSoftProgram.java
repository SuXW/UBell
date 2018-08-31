package com.ubia.vr;

  
import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import cn.ubia.UBell.R;

/**
 * Created by guobichuan on 7/20/16.
 */
public class GLSoftProgram {

    public static final String TAG = GLSoftProgram.class.getSimpleName();

    Context context;
    private boolean built = false;
    private int programId = 0;

    public GLSoftProgram(Context context) {
        this.context = context;
    }

    public boolean isProgramBuilt() {
        return built;
    }

    public void build(String[] variables) {
        final String vertexShader ="attribute vec4 a_position;\n"+
       " attribute vec2 a_texCoord;\n"+

        "varying   vec2 v_texCoord;\n"+

       " uniform   mat4 u_MVPMatrix;\n"+

       " void main () {\n"+
          "  v_texCoord = a_texCoord;\n"+
           " gl_Position = u_MVPMatrix * (a_position) ;\n"+
      "  }\n";
        
       // RawReader.readTextFileFromRawResource(context,VRConfig.getInstance().getVertex_shaderRawID());
        final String fragmentShader = 
        		"precision mediump float;\n"+

        "varying vec2      v_texCoord;\n"+

        "uniform sampler2D u_texture;\n"+

       " void main () {\n"+
            "gl_FragColor = texture2D(u_texture, v_texCoord);\n"+
            //gl_FragColor = vec4(0.2, 1.0, 0.129, 0);
       " }\n";
        		
        		//RawReader.readTextFileFromRawResource(context, VRConfig.getInstance().getFragment_shaderRawID());

        Log.d(TAG, vertexShader);
        Log.d(TAG, fragmentShader);

        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
        programId = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, variables);

        GLES20.glUseProgram(programId);
        built = true;
    }

    public int getProgram() {
        return programId;
    }
    /**
     * Releases the program.
     * <p>
     * The appropriate EGL context must be current (i.e. the one that was used to create
     * the program).
     */
    public void release() { 
        GLES20.glDeleteProgram(programId);
        programId = -1;
        built = false;
    }
}
