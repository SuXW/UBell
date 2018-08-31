package com.ubia.vr;

import android.opengl.Matrix;
import android.util.Log;

//存储系统矩阵状态的类
public class MatrixState {
  private static float[] mProjMatrix = new float[16];//4x4矩阵 投影用
  private static float[] mVMatrix = new float[16];//摄像机位置朝向9参数矩阵

  private static float[] currMatrix;   //当前变换矩阵
  static float[][] mStack = new float[10][16];   //用于保存变换矩阵的类
  static int stackTop = -1; //标识栈顶的索引



  /**
   * 初始化变换矩阵
   */
  public static void setInitStack() {
      currMatrix = new float[16];
      Matrix.setRotateM(currMatrix, 0, 0, 1, 0, 0);  //除初始化无变换内容的矩阵
  }

  /**
   * 把变换矩阵保存到栈中
   */
  public static void pushMatrix() {
      stackTop++;
      if(stackTop>=10){
		  Log.e("popMatrix", "   popMatrix  stackTop:"+stackTop);
		  return;
	  }
      for (int i = 0; i < 16; i++) {
          mStack[stackTop][i] = currMatrix[i];
      }
  }

  /**
   * 从栈中读取变换矩阵
   */
  public static void popMatrix() {
	  if(stackTop<0){
		  Log.e("popMatrix", "   popMatrix  stackTop:"+stackTop);
		  return;
	  }
      for (int i = 0; i < 16; i++) {
          currMatrix[i] = mStack[stackTop][i];
      }
      stackTop--;
  }

  /**
   * 平移变换
   */
  public static void translate(float x, float y, float z) {
      Matrix.translateM(currMatrix, 0, x, y, z);
  }

  /**
   * 旋转变换
   *
   * @param angle
   * @param x
   * @param y
   */
  public static void rotate(float angle, float x, float y, float z) {
      Matrix.rotateM(currMatrix, 0, angle, x, y, z);
  }

  /**
   * 缩放变换
   */
  public static void scale(float x, float y, float z) {
      Matrix.scaleM(currMatrix, 0, x, y, z);
  }

  //设置摄像机
  static float[] cameraLocation=new float[3];//摄像机位置
  /**
   * 设置摄像机
   *
   * @param cx  摄像机位置x
   * @param cy  摄像机位置y
   * @param cz  摄像机位置z
   * @param tx  摄像机目标点x
   * @param ty  摄像机目标点y
   * @param tz  摄像机目标点z
   * @param upx 摄像机UP向量X分量
   * @param upy 摄像机UP向量Y分量
   * @param upz 摄像机UP向量Z分量
   */
  public static void setCamera(float cx, float cy, float cz, float tx, float ty, float tz, float upx, float upy, float upz) {
      Matrix.setLookAtM(mVMatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz);
  }

  /**
   * 设置正交投影参数
   *
   * @param left   near面的left
   * @param right  near面的right
   * @param bottom near面的bottom
   * @param top    near面的top
   * @param near   near面距离
   * @param far    far面距离
   */
  public static void setProjectOrtho(float left, float right, float bottom, float top, float near, float far) {
      Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
  }

  /**
   * 设置透视投影参数
   *
   * @param left   near面的left
   * @param right  near面的right
   * @param bottom near面的bottom
   * @param top    near面的top
   * @param near   near面距离
   * @param far    far面距离
   */
  public static void setProjectFrustum(float left, float right, float bottom, float top, float near, float far) {
      Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
  }
   /**
   * 获取具体物体的变换之后的矩阵
   *
   * @return
   */
  public static float[] mMVPMatrix = new float[16];
  public static float[] getFinalMatrix() {
      Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
      Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
      return mMVPMatrix;
  }



  //获取具体物体的变换矩阵
  public static float[] getMMatrix()
  {
      return currMatrix;
  }
}