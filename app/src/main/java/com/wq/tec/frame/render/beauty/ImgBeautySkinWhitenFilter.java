package com.wq.tec.frame.render.beauty;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by NoName on 2016/10/26.
 */
public class ImgBeautySkinWhitenFilter extends GPUImageFilter {

    private static final String var = "precision highp float;\n" +
            "\n" +
            "uniform sampler2D curve; \n" +
            "\n" +
            "uniform float texelWidthOffset; \n" +
            "uniform float texelHeightOffset; \n" +
            "\n" +
            "varying highp vec2 vTextureCoord; \n" +
            "\n" +
            "const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721); \n" +
            "\n" +
            "void main() { \n" +
            "vec4 blurColor = vec4(0.); \n" +
            "lowp vec4 textureColor; \n" +
            "lowp float strength = -1.0 / 510.0; \n" +
            "float xCoordinate = vTextureCoord.x; \n" +
            "float yCoordinate = vTextureCoord.y;\n" +
            "\n" +
            "lowp float satura = 0.7; \n" +
            "textureColor = texture2D(sTexture, vTextureCoord); \n" +
            "\n" +
            "lowp float strengthTemp = 1.; \n" +
            "vec2 step  = vec2(0.); \n" +
            "blurColor += texture2D(sTexture,vTextureCoord)* 0.25449 ; \n" +
            "\n" +
            "step.x = 1.37754 * texelWidthOffset  * strengthTemp; \n" +
            "step.y = 1.37754 * texelHeightOffset * strengthTemp;\n" +
            "blurColor += texture2D(sTexture,vTextureCoord+step) * 0.24797; \n" +
            "blurColor += texture2D(sTexture,vTextureCoord-step) * 0.24797; \n" +
            "\n" +
            "step.x = 3.37754 * texelWidthOffset  * strengthTemp; \n" +
            "step.y = 3.37754 * texelHeightOffset * strengthTemp; \n" +
            "blurColor += texture2D(sTexture,vTextureCoord+step) * 0.09122; \n" +
            "blurColor += texture2D(sTexture,vTextureCoord-step) * 0.09122; \n" +
            "\n" +
            "step.x = 5.37754 * texelWidthOffset  * strengthTemp; \n" +
            "step.y = 5.37754 * texelHeightOffset * strengthTemp; \n" +
            "blurColor += texture2D(sTexture,vTextureCoord+step) * 0.03356; \n" +
            "blurColor += texture2D(sTexture,vTextureCoord-step) * 0.03356; \n" +
            "\n" +
            "//saturation \n" +
            "    lowp float luminance = dot(blurColor.rgb, luminanceWeighting); \n" +
            "lowp vec3 greyScaleColor = vec3(luminance); \n" +
            "\n" +
            "blurColor = vec4(mix(greyScaleColor, blurColor.rgb, satura), blurColor.w); \n" +
            "     \n" +
            "lowp float redCurveValue = texture2D(curve, vec2(textureColor.r, 0.0)).r; \n" +
            "lowp float greenCurveValue = texture2D(curve, vec2(textureColor.g, 0.0)).r; \n" +
            "    lowp float blueCurveValue = texture2D(curve, vec2(textureColor.b, 0.0)).r; \n" +
            "\n" +
            "redCurveValue = min(1.0, redCurveValue + strength); \n" +
            "greenCurveValue = min(1.0, greenCurveValue + strength); \n" +
            "blueCurveValue = min(1.0, blueCurveValue + strength); \n" +
            "\n" +
            "    mediump vec4 overlay = blurColor;\n" +
            "\n" +
            "mediump vec4 base = vec4(redCurveValue, greenCurveValue, blueCurveValue, 1.0); \n" +
            "\n" +
            "    // step4 overlay blending \n" +
            "mediump float ra; \n" +
            "if (base.r < 0.5) { \n" +
            "ra = overlay.r * base.r * 2.0; \n" +
            "} else { \n" +
            "ra = 1.0 - ((1.0 - base.r) * (1.0 - overlay.r) * 2.0); \n" +
            "} \n" +
            "\n" +
            "    mediump float ga; \n" +
            "if (base.g < 0.5) { \n" +
            "ga = overlay.g * base.g * 2.0; \n" +
            "} else { \n" +
            "ga = 1.0 - ((1.0 - base.g) * (1.0 - overlay.g) * 2.0); \n" +
            "} \n" +
            "\n" +
            "mediump float ba; \n" +
            "if (base.b < 0.5) { \n" +
            "ba = overlay.b * base.b * 2.0;\n" +
            "} else { \n" +
            "ba = 1.0 - ((1.0 - base.b) * (1.0 - overlay.b) * 2.0); \n" +
            "} \n" +
            "\n" +
            "textureColor = vec4(ra, ga, ba, 1.0); \n" +
            "\n" +
            "    gl_FragColor = vec4(textureColor.r, textureColor.g, textureColor.b, 1.0); \n" +
            "}\n";

    private int d;
    private int[] e = new int[]{-1};
    byte[] a = new byte[1024];
    int[] b = new int[]{95, 95, 96, 97, 97, 98, 99, 99, 100, 101, 101, 102, 103, 104, 104, 105, 106, 106, 107, 108, 108, 109, 110, 111, 111, 112, 113, 113, 114, 115, 115, 116, 117, 117, 118, 119, 120, 120, 121, 122, 122, 123, 124, 124, 125, 126, 127, 127, 128, 129, 129, 130, 131, 131, 132, 133, 133, 134, 135, 136, 136, 137, 138, 138, 139, 140, 140, 141, 142, 143, 143, 144, 145, 145, 146, 147, 147, 148, 149, 149, 150, 151, 152, 152, 153, 154, 154, 155, 156, 156, 157, 158, 159, 159, 160, 161, 161, 162, 163, 163, 164, 165, 165, 166, 167, 168, 168, 169, 170, 170, 171, 172, 172, 173, 174, 175, 175, 176, 177, 177, 178, 179, 179, 180, 181, 181, 182, 183, 184, 184, 185, 186, 186, 187, 188, 188, 189, 190, 191, 191, 192, 193, 193, 194, 195, 195, 196, 197, 197, 198, 199, 200, 200, 201, 202, 202, 203, 204, 204, 205, 206, 207, 207, 208, 209, 209, 210, 211, 211, 212, 213, 213, 214, 215, 216, 216, 217, 218, 218, 219, 220, 220, 221, 222, 223, 223, 224, 225, 225, 226, 227, 227, 228, 229, 229, 230, 231, 232, 232, 233, 234, 234, 235, 236, 236, 237, 238, 239, 239, 240, 241, 241, 242, 243, 243, 244, 245, 245, 246, 247, 248, 248, 249, 250, 250, 251, 252, 252, 253, 254, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255};
    int[] c = new int[]{0, 0, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 8, 8, 8, 9, 9, 10, 10, 10, 11, 11, 11, 12, 12, 13, 13, 13, 14, 14, 14, 15, 15, 16, 16, 16, 17, 17, 17, 18, 18, 18, 19, 19, 20, 20, 20, 21, 21, 21, 22, 22, 23, 23, 23, 24, 24, 24, 25, 25, 25, 25, 26, 26, 27, 27, 28, 28, 28, 28, 29, 29, 30, 29, 31, 31, 31, 31, 32, 32, 33, 33, 34, 34, 34, 34, 35, 35, 36, 36, 37, 37, 37, 38, 38, 39, 39, 39, 40, 40, 40, 41, 42, 42, 43, 43, 44, 44, 45, 45, 45, 46, 47, 47, 48, 48, 49, 50, 51, 51, 52, 52, 53, 53, 54, 55, 55, 56, 57, 57, 58, 59, 60, 60, 61, 62, 63, 63, 64, 65, 66, 67, 68, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 88, 89, 90, 91, 93, 94, 95, 96, 97, 98, 100, 101, 103, 104, 105, 107, 108, 110, 111, 113, 115, 116, 118, 119, 120, 122, 123, 125, 127, 128, 130, 132, 134, 135, 137, 139, 141, 143, 144, 146, 148, 150, 152, 154, 156, 158, 160, 163, 165, 167, 169, 171, 173, 175, 178, 180, 182, 185, 187, 189, 192, 194, 197, 199, 201, 204, 206, 209, 211, 214, 216, 219, 221, 224, 226, 229, 232, 234, 236, 239, 241, 245, 247, 250, 252, 255};

    public ImgBeautySkinWhitenFilter() {
        super("uniform mat4 uTexMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n    gl_Position = aPosition;\n    vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n}\n", var);
    }

    public void onInitialized() {
        this.d = GLES20.glGetUniformLocation(this.getProgram(), "curve");
        int var1 = GLES20.glGetUniformLocation(this.getProgram(), "texelWidthOffset");
        int var2 = GLES20.glGetUniformLocation(this.getProgram(), "texelHeightOffset");
        if(this.d >= 0) {
//            GLES20.glUniform1f(var1, mImgLevel / (float)this.f.width);
//            GLES20.glUniform1f(var2, mImgLevel / (float)this.f.height);
            setFloat(var1, 1 / 460);
            setFloat(var2, 1 / 720);
            GLES20.glActiveTexture(33987);
            GLES20.glGenTextures(1, this.e, 0);
            GLES20.glBindTexture(3553, this.e[0]);
            GLES20.glTexParameterf(3553, 10240, 9729.0F);
            GLES20.glTexParameterf(3553, 10241, 9729.0F);
            GLES20.glTexParameterf(3553, 10242, 33071.0F);
            GLES20.glTexParameterf(3553, 10243, 33071.0F);

            for(int var3 = 0; var3 < 256; ++var3) {
                this.a[var3 * 4] = (byte)this.b[var3];
                this.a[1 + var3 * 4] = (byte)this.b[var3];
                this.a[2 + var3 * 4] = (byte)this.c[var3];
                this.a[3 + var3 * 4] = -1;
            }

            GLES20.glTexImage2D(3553, 0, 6408, 256, 1, 0, 6408, 5121, ByteBuffer.wrap(this.a));
        }

    }

    protected void onDrawArraysPre() {
        if(this.e[0] != -1) {
            GLES20.glActiveTexture(33987);
            GLES20.glBindTexture(3553, this.e[0]);
            GLES20.glUniform1i(this.d, 3);
        }

    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        onDrawArraysAfter();
    }

    protected void onDrawArraysAfter() {
        if(this.e[0] != -1) {
            GLES20.glActiveTexture(33987);
            GLES20.glBindTexture(3553, 0);
            GLES20.glActiveTexture(33984);
        }

    }

//    protected void onRelease() {
//        super.onRelease();
//        GLES20.glDeleteTextures(1, this.e, 0);
//        this.e[0] = -1;
//    }
}
