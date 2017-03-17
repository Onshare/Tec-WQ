package com.wq.tec.frame.render.beauty;

import android.opengl.GLES20;
import android.util.Log;


import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * Created by NoName on 2016/10/26.
 */
public class ImgBeautySmoothFilter extends GPUImageFilter {

    private static final String var = "precision highp float;\n" +
            "varying vec2 vTxCoord[16];   \n" +
            "\n" +
            "uniform float distanceNormalizationFactor;   \n" +
            "const float lightenRatio = - 0.44;\n" +
            "\n" +
            "float hardlight(float color)   \n" +
            "{   \n" +
            "for (int i = 0; i < 4; ++i)    \n" +
            "{   \n" +
            "    if(color <= 0.5)   \n" +
            "    {   \n" +
            "     color = color * color * 2.0;   \n" +
            "    }else   \n" +
            "    {   \n" +
            "     color = 1.0 - ((1.0 - color)*(1.0 - color) * 2.0);   \n" +
            "    }   \n" +
            "}   \n" +
            "   return color; \n" +
            "}   \n" +
            "   \n" +
            "float lightAdjust( float x, float intensity) \n" +
            "{   \n" +
            "float k = - 0.52;   \n" +
            "float fx = x;   \n" +
            "return (((fx * (k * (fx-1.) + 1.)) * intensity + (100./255. - intensity)*x) / 100./255.); \n" +
            "}   \n" +
            "   \n" +
            "float SkinWhitening(float x)   \n" +
            "{   \n" +
            "    highp float beta = 3.35;   \n" +
            "    highp float logbeta = log(beta);   \n" +
            "    highp float pixel = x;   \n" +
            "    highp float alpha = 256. * log(pixel*(beta-1.)+1.)/logbeta;   \n" +
            "    float ynew = alpha / 255.;   \n" +
            "    return ynew;   \n" +
            "}\n" +
            "\n" +
            "void main()   \n" +
            "{   \n" +
            "vec3 centralColor = texture2D(sTexture, vTxCoord[0]).rgb;  \n" +
            "vec2 blurCoordinates[12];   \n" +
            "vec2 blurStep;         \n" +
            "float gaussianWeightTotal;   \n" +
            "vec3 sum;   \n" +
            "vec3 RgbSampleColor;   \n" +
            "float distanceFromCentralColor;   \n" +
            "float distanceFromCentralPosition;   \n" +
            "float gaussianWeight;    \n" +
            "                   \n" +
            "gaussianWeightTotal = 0.4;   \n" +
            "sum = centralColor * 0.4;   \n" +
            "\n" +
            "RgbSampleColor = texture2D(sTexture, vTxCoord[1]).rgb;   \n" +
            "distanceFromCentralColor = min(distance(centralColor,RgbSampleColor) * distanceNormalizationFactor, 1.0);   \n" +
            "distanceFromCentralPosition = min(distance(vTxCoord[0], vTxCoord[1]) * distanceNormalizationFactor, 1.0);   \n" +
            "gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor) * (1.0 - distanceFromCentralPosition);   \n" +
            "gaussianWeightTotal += gaussianWeight;   \n" +
            "sum += RgbSampleColor * gaussianWeight;   \n" +
            "   \n" +
            "RgbSampleColor = texture2D(sTexture, vTxCoord[2]).rgb;   \n" +
            "distanceFromCentralColor = min(distance(centralColor, RgbSampleColor) * distanceNormalizationFactor, 1.0);   \n" +
            "distanceFromCentralPosition = min(distance(vTxCoord[0], vTxCoord[2]) * distanceNormalizationFactor, 1.0);   \n" +
            "gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor) * (1.0 - distanceFromCentralPosition);   \n" +
            "gaussianWeightTotal += gaussianWeight;   \n" +
            "sum += RgbSampleColor * gaussianWeight;   \n" +
            "   \n" +
            "RgbSampleColor = texture2D(sTexture, vTxCoord[3]).rgb;   \n" +
            "distanceFromCentralColor = min(distance(centralColor, RgbSampleColor) * distanceNormalizationFactor, 1.0);   \n" +
            "distanceFromCentralPosition = min(distance(vTxCoord[0], vTxCoord[3]) * distanceNormalizationFactor, 1.0);   \n" +
            "gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor) * (1.0 - distanceFromCentralPosition);   \n" +
            "gaussianWeightTotal += gaussianWeight;   \n" +
            "sum += RgbSampleColor * gaussianWeight;   \n" +
            "   \n" +
            "RgbSampleColor = texture2D(sTexture, vTxCoord[4]).rgb;   \n" +
            "distanceFromCentralColor = min(distance(centralColor, RgbSampleColor) * distanceNormalizationFactor, 1.0);   \n" +
            "distanceFromCentralPosition = min(distance(vTxCoord[0], vTxCoord[4]) * distanceNormalizationFactor, 1.0);   \n" +
            "gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor) * (1.0 - distanceFromCentralPosition);   \n" +
            "gaussianWeightTotal += gaussianWeight;   \n" +
            "sum += RgbSampleColor * gaussianWeight;   \n" +
            "   \n" +
            "RgbSampleColor = texture2D(sTexture, vTxCoord[5]).rgb;   \n" +
            "distanceFromCentralColor = min(distance(centralColor, RgbSampleColor) * distanceNormalizationFactor, 1.0);   \n" +
            "distanceFromCentralPosition = min(distance(vTxCoord[0], vTxCoord[5]) * distanceNormalizationFactor, 1.0);   \n" +
            "gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor) * (1.0 - distanceFromCentralPosition);   \n" +
            "gaussianWeightTotal += gaussianWeight;   \n" +
            "sum += RgbSampleColor * gaussianWeight;   \n" +
            "   \n" +
            "RgbSampleColor = texture2D(sTexture, vTxCoord[6]).rgb;   \n" +
            "distanceFromCentralColor = min(distance(centralColor, RgbSampleColor) * distanceNormalizationFactor, 1.0);   \n" +
            "distanceFromCentralPosition = min(distance(vTxCoord[0], vTxCoord[6]) * distanceNormalizationFactor, 1.0);   \n" +
            "gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor) * (1.0 - distanceFromCentralPosition);   \n" +
            "gaussianWeightTotal += gaussianWeight;   \n" +
            "sum += RgbSampleColor * gaussianWeight;   \n" +
            "   \n" +
            "RgbSampleColor = texture2D(sTexture, vTxCoord[7]).rgb;   \n" +
            "distanceFromCentralColor = min(distance(centralColor, RgbSampleColor) * distanceNormalizationFactor, 1.0);   \n" +
            "distanceFromCentralPosition = min(distance(vTxCoord[0], vTxCoord[7]) * distanceNormalizationFactor, 1.0);   \n" +
            "gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor) * (1.0 - distanceFromCentralPosition);   \n" +
            "gaussianWeightTotal += gaussianWeight;   \n" +
            "sum += RgbSampleColor * gaussianWeight;   \n" +
            "   \n" +
            "RgbSampleColor = texture2D(sTexture, vTxCoord[8]).rgb;   \n" +
            "distanceFromCentralColor = min(distance(centralColor, RgbSampleColor) * distanceNormalizationFactor, 1.0);   \n" +
            "distanceFromCentralPosition = min(distance(vTxCoord[0], vTxCoord[8]) * distanceNormalizationFactor, 1.0);   \n" +
            "gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor) * (1.0 - distanceFromCentralPosition);   \n" +
            "gaussianWeightTotal += gaussianWeight;   \n" +
            "sum += RgbSampleColor * gaussianWeight;   \n" +
            "   \n" +
            "RgbSampleColor = texture2D(sTexture, vTxCoord[9]).rgb;   \n" +
            "distanceFromCentralColor = min(distance(centralColor, RgbSampleColor) * distanceNormalizationFactor, 1.0);   \n" +
            "distanceFromCentralPosition = min(distance(vTxCoord[0], vTxCoord[9]) * distanceNormalizationFactor, 1.0);   \n" +
            "gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor) * (1.0 - distanceFromCentralPosition);   \n" +
            "gaussianWeightTotal += gaussianWeight;   \n" +
            "sum += RgbSampleColor * gaussianWeight;   \n" +
            "   \n" +
            "RgbSampleColor = texture2D(sTexture, vTxCoord[10]).rgb;   \n" +
            "distanceFromCentralColor = min(distance(centralColor, RgbSampleColor) * distanceNormalizationFactor, 1.0);   \n" +
            "distanceFromCentralPosition = min(distance(vTxCoord[0], vTxCoord[10]) * distanceNormalizationFactor, 1.0);   \n" +
            "gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor) * (1.0 - distanceFromCentralPosition);   \n" +
            "gaussianWeightTotal += gaussianWeight;   \n" +
            "sum += RgbSampleColor * gaussianWeight;   \n" +
            "   \n" +
            "RgbSampleColor = texture2D(sTexture, vTxCoord[11]).rgb;   \n" +
            "distanceFromCentralColor = min(distance(centralColor, RgbSampleColor) * distanceNormalizationFactor, 1.0);   \n" +
            "distanceFromCentralPosition = min(distance(vTxCoord[0], vTxCoord[11]) * distanceNormalizationFactor, 1.0);   \n" +
            "gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor) * (1.0 - distanceFromCentralPosition);   \n" +
            "gaussianWeightTotal += gaussianWeight;   \n" +
            "sum += RgbSampleColor * gaussianWeight;   \n" +
            "   \n" +
            "RgbSampleColor = texture2D(sTexture, vTxCoord[12]).rgb;   \n" +
            "distanceFromCentralColor = min(distance(centralColor, RgbSampleColor) * distanceNormalizationFactor, 1.0);   \n" +
            "distanceFromCentralPosition = min(distance(vTxCoord[0], vTxCoord[12]) * distanceNormalizationFactor, 1.0);   \n" +
            "gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor) * (1.0 - distanceFromCentralPosition);   \n" +
            "gaussianWeightTotal += gaussianWeight;   \n" +
            "sum += RgbSampleColor * gaussianWeight;   \n" +
            "    \n" +
            "RgbSampleColor = texture2D(sTexture, vTxCoord[13]).rgb;   \n" +
            "distanceFromCentralColor = min(distance(centralColor, RgbSampleColor) * distanceNormalizationFactor, 1.0);   \n" +
            "distanceFromCentralPosition = min(distance(vTxCoord[0], vTxCoord[13]) * distanceNormalizationFactor, 1.0);   \n" +
            "gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor) * (1.0 - distanceFromCentralPosition);   \n" +
            "gaussianWeightTotal += gaussianWeight;   \n" +
            "sum += RgbSampleColor * gaussianWeight;   \n" +
            "    \n" +
            "RgbSampleColor = texture2D(sTexture, vTxCoord[14]).rgb;   \n" +
            "distanceFromCentralColor = min(distance(centralColor, RgbSampleColor) * distanceNormalizationFactor, 1.0);   \n" +
            "distanceFromCentralPosition = min(distance(vTxCoord[0], vTxCoord[14]) * distanceNormalizationFactor, 1.0);   \n" +
            "gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor) * (1.0 - distanceFromCentralPosition);   \n" +
            "gaussianWeightTotal += gaussianWeight;   \n" +
            "sum += RgbSampleColor * gaussianWeight;   \n" +
            "    \n" +
            "RgbSampleColor = texture2D(sTexture, vTxCoord[15]).rgb;   \n" +
            "distanceFromCentralColor = min(distance(centralColor, RgbSampleColor) * distanceNormalizationFactor, 1.0);   \n" +
            "distanceFromCentralPosition = min(distance(vTxCoord[0], vTxCoord[15]) * distanceNormalizationFactor, 1.0);   \n" +
            "gaussianWeight = 0.05 * (1.0 - distanceFromCentralColor) * (1.0 - distanceFromCentralPosition);   \n" +
            "gaussianWeightTotal += gaussianWeight;   \n" +
            "sum += RgbSampleColor * gaussianWeight;   \n" +
            "    \n" +
            "vec3  weightMean  = sum / gaussianWeightTotal;\n" +
            "float MaskColorR = hardlight(clamp((weightMean[0] - centralColor[0] + 0.5),0.0,1.0));\n" +
            "float MaskColorG = hardlight(clamp((weightMean[1] - centralColor[1] + 0.5),0.0,1.0));\n" +
            "float MaskColorB = hardlight(clamp((weightMean[2] - centralColor[2] + 0.5),0.0,1.0));\n" +
            "    \n" +
            "vec3 curveLighten =mix(centralColor*(lightenRatio*(centralColor - vec3(1.0,1.0,1.0)) +  vec3(1.0,1.0,1.0)),centralColor,0.1);\n" +
            "    \n" +
            "float blendMaskR =mix(centralColor[0],curveLighten[0],MaskColorR);\n" +
            "float blendMaskG =mix(centralColor[1],curveLighten[1],MaskColorG);\n" +
            "float blendMaskB =mix(centralColor[2],curveLighten[2],MaskColorB);\n" +
            "    \n" +
            "vec3 res = vec3(blendMaskR,blendMaskG,blendMaskB);\n" +
            "gl_FragColor =  vec4(res,1.0);\n" +
            "}";

    private static final String a = "uniform mat4 uTexMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nuniform vec2 singleStepOffset;    \nvarying vec2 vTxCoord[16];   \nvoid main() {\ngl_Position = aPosition;\nvec2 vTxCoordCenter = (uTexMatrix * aTextureCoord).xy;\nvTxCoord[0] = vTxCoordCenter.xy;\nvTxCoord[1] = vTxCoordCenter.xy + singleStepOffset * vec2(0.,-5.5);   \nvTxCoord[2] = vTxCoordCenter.xy + singleStepOffset * vec2(5.5,0.);   \nvTxCoord[3] = vTxCoordCenter.xy + singleStepOffset * vec2(-5.5,0);   \nvTxCoord[4] = vTxCoordCenter.xy + singleStepOffset * vec2(0.,5.5);    \n\nvTxCoord[5] = vTxCoordCenter.xy + singleStepOffset * vec2(0.,-1.5);   \nvTxCoord[6] = vTxCoordCenter.xy + singleStepOffset * vec2(1.5,0.);   \nvTxCoord[7] = vTxCoordCenter.xy + singleStepOffset * vec2(-1.5,0);   \nvTxCoord[8] = vTxCoordCenter.xy + singleStepOffset * vec2(0.,1.5);   \n\nvTxCoord[9] = vTxCoordCenter.xy + singleStepOffset * vec2(0.,-3.5);   \nvTxCoord[10] = vTxCoordCenter.xy + singleStepOffset * vec2(3.5,0.);   \nvTxCoord[11] = vTxCoordCenter.xy + singleStepOffset * vec2(-3.5,0);   \nvTxCoord[12] = vTxCoordCenter.xy + singleStepOffset * vec2(0.,3.5);  \n\nvTxCoord[13] = vTxCoordCenter.xy + singleStepOffset * vec2(4.0,-4.0);  \nvTxCoord[14] = vTxCoordCenter.xy + singleStepOffset * vec2(4.0,4.0);  \nvTxCoord[15] = vTxCoordCenter.xy + singleStepOffset * vec2(-4.0,4.0);  \n}\n";

    public ImgBeautySmoothFilter() {
        super("uniform mat4 uTexMatrix;\nattribute vec4 aPosition;\nattribute vec4 textureCoordinate;\nuniform vec2 singleStepOffset;    \nvarying vec2 vTxCoord[16];   \nvoid main() {\ngl_Position = aPosition;\nvec2 vTxCoordCenter = (uTexMatrix * textureCoordinate).xy;\nvTxCoord[0] = vTxCoordCenter.xy;\nvTxCoord[1] = vTxCoordCenter.xy + singleStepOffset * vec2(0.,-5.5);   \nvTxCoord[2] = vTxCoordCenter.xy + singleStepOffset * vec2(5.5,0.);   \nvTxCoord[3] = vTxCoordCenter.xy + singleStepOffset * vec2(-5.5,0);   \nvTxCoord[4] = vTxCoordCenter.xy + singleStepOffset * vec2(0.,5.5);    \n\nvTxCoord[5] = vTxCoordCenter.xy + singleStepOffset * vec2(0.,-1.5);   \nvTxCoord[6] = vTxCoordCenter.xy + singleStepOffset * vec2(1.5,0.);   \nvTxCoord[7] = vTxCoordCenter.xy + singleStepOffset * vec2(-1.5,0);   \nvTxCoord[8] = vTxCoordCenter.xy + singleStepOffset * vec2(0.,1.5);   \n\nvTxCoord[9] = vTxCoordCenter.xy + singleStepOffset * vec2(0.,-3.5);   \nvTxCoord[10] = vTxCoordCenter.xy + singleStepOffset * vec2(3.5,0.);   \nvTxCoord[11] = vTxCoordCenter.xy + singleStepOffset * vec2(-3.5,0);   \nvTxCoord[12] = vTxCoordCenter.xy + singleStepOffset * vec2(0.,3.5);  \n\nvTxCoord[13] = vTxCoordCenter.xy + singleStepOffset * vec2(4.0,-4.0);  \nvTxCoord[14] = vTxCoordCenter.xy + singleStepOffset * vec2(4.0,4.0);  \nvTxCoord[15] = vTxCoordCenter.xy + singleStepOffset * vec2(-4.0,4.0);  \n}\n", var);
    }

    public void onInitialized() {
        int var1 = GLES20.glGetUniformLocation(this.getProgram(), "singleStepOffset");
        int var2 = GLES20.glGetUniformLocation(this.getProgram(), "distanceNormalizationFactor");
        float[] var3 = new float[]{1 / 460, 1 / 720};
        GLES20.glUniform1f(var2, 2.6F);
//        checkGlError("glUniform1f");//glUniform1f
        GLES20.glUniform2fv(var1, 1, FloatBuffer.wrap(var3));
        setFloatVec2(var1, var3);
//        checkGlError("glUniform2fv");
    }

    public static void checkGlError(String var0) {
        int var1 = GLES20.glGetError();
        if(var1 != 0) {
            String var2 = var0 + ": glError 0x" + Integer.toHexString(var1);
            Log.e("GlUtil", var2);
            throw new RuntimeException(var2);
        }
    }
}
