package org.kreal.photoview;


import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;


/**
 * Created by Kreal on 2015/9/17.
 */
public class GLhelp {
    public static String readfromfile(String path) {
        String fileRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + path;
        try {
            return read(new FileInputStream(fileRoot));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String readfromAssets(Context context, String path) {
        try {
            return read(context.getAssets().open(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String read(InputStream in) {
        StringBuffer out = new StringBuffer();
        String result = new String();
        byte[] b = new byte[1024];
        int len = 0;
        try {
            while ((len = in.read(b)) != -1) {
                out.append(new String(b, 0, len));
            }
            result = out.toString();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int loadProgram(String vertexSource, String fragmentSource) {

// Load the vertex shaders
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                vertexSource);
// Load the fragment shaders
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentSource);
// Create the program object
        int program = GLES20.glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("Error create program.");
        }
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
// Link the program
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
// Check the link status
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(program);
            throw new RuntimeException("Error linking program: " +
                    GLES20.glGetProgramInfoLog(program));
        }
// Free up no longer needed shader resources
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);
        return program;
    }

    public static int loadShader(int shaderType, String source) {

// Create the shader object
        int shader = GLES20.glCreateShader(shaderType);
        if (shader == 0) {
            throw new RuntimeException("Error create shader.");
        }
        int[] compileStatus = new int[1];
// Load the shader source
        GLES20.glShaderSource(shader, source);
// Compile the shader
        GLES20.glCompileShader(shader);
// Check the compile status
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            GLES20.glDeleteShader(shader);
            throw new RuntimeException("Error compile shader: " +
                    GLES20.glGetShaderInfoLog(shader));
        }
        return shader;
    }

    public static int loadTexture(Bitmap bitmap) {
        int[] texture_ID = new int[1];

        //创建纹理,得到ID
        GLES20.glGenTextures(1, texture_ID, 0);
        if (texture_ID[0] != 0) {
            //将生成的纹理ID绑定到指定的纹理上
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture_ID[0]);
            //设置纹理属性
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE);

            //绑定纹理数据,传入指定图片
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            //防止后面的纹理操作修改其属性
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0 );
        }
        if (texture_ID[0]==0)
            throw new RuntimeException("Error Loading Textue");
        return texture_ID[0];
    }
}
