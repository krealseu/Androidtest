#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/log.h>
#include <malloc.h>
#include "png.h"
#include <time.h>

#define IFLOG true
#define TAG "keyboard"
#define LOGV(...) if(IFLOG) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)


png_structp png_ptr=NULL;//libpng的结构体
png_infop   info_ptr=NULL;//libpng的信息
#define PNG_BYTES_TO_CHECK 4
#define HAVE_ALPHA	1
#define NO_ALPHA	0

typedef struct _pic_data pic_data;
struct _pic_data
{
    int	width, height;	/* 尺寸 */
    int	bit_depth;		/* 位深 */
    int	flag;			/* 一个标志，表示是否有alpha通道 */

    unsigned char	*rgba;	/* 图片数组 */
};
char test[123] = "rewer";

int detect_png(JNIEnv* env,char *filepath, pic_data *out)
/* 用于解码png图片 */
{
    sprintf(test,"a11231");
    FILE *pic_fp;
    pic_fp = fopen(filepath, "rb");
    if(pic_fp == NULL) { /* 文件打开失败 */
        sprintf(test,"qqq");
        return -1;
    }

    /* 初始化各种结构 */
    png_structp png_ptr;
    png_infop   info_ptr;
    unsigned char        buf[PNG_BYTES_TO_CHECK];
    int         temp;

    png_ptr  = png_create_read_struct(PNG_LIBPNG_VER_STRING, 0, 0, 0);
    info_ptr = png_create_info_struct(png_ptr);

    setjmp(png_jmpbuf(png_ptr)); // 这句很重要

    temp = fread(buf,1,PNG_BYTES_TO_CHECK,pic_fp);
    temp = png_sig_cmp(buf, (png_size_t)0, PNG_BYTES_TO_CHECK);

    /*检测是否为png文件*/
    if (temp!=0) {
        sprintf(test,"asxxx");
        return 1;
    }

    rewind(pic_fp);
    /*开始读文件*/
    png_init_io(png_ptr, pic_fp);
    // 读文件了
    png_read_png(png_ptr, info_ptr, PNG_TRANSFORM_EXPAND, 0);

    int color_type,channels;

    /*获取宽度，高度，位深，颜色类型*/
    channels       = png_get_channels(png_ptr, info_ptr);	/*获取通道数*/
    out->bit_depth = png_get_bit_depth(png_ptr, info_ptr);	/* 获取位深 */
    color_type     = png_get_color_type(png_ptr, info_ptr);	/*颜色类型*/

    int i,j;
    int size, pos = 0;
    /* row_pointers里边就是rgba数据 */
    png_bytep* row_pointers;
    row_pointers = png_get_rows(png_ptr, info_ptr);
    out->width = png_get_image_width(png_ptr, info_ptr);
    out->height = png_get_image_height(png_ptr, info_ptr);
    size = out->width * out->height; /* 计算图片的总像素点数量 */

    __android_log_print(ANDROID_LOG_VERBOSE,"asdaa","sdf%d %d %d %d",out->width,out->height,size,
                        sizeof(row_pointers));

    if(channels == 4 || color_type == PNG_COLOR_TYPE_RGB_ALPHA)
    {/*如果是RGB+alpha通道，或者RGB+其它字节*/
        size *= (4*sizeof(unsigned char));	/* 每个像素点占4个字节内存 */
        out->flag = HAVE_ALPHA;				/* 标记 */
        out->rgba = (unsigned char*) malloc(size);
        if(out->rgba == NULL)
        {/* 如果分配内存失败 */
            fclose(pic_fp);
            LOGV("sdfaad");
            sprintf(test,"误(png):无法分配足够的内");
            return 1;
        }
        __android_log_print(ANDROID_LOG_VERBOSE,"asdaa","%d %d",size,sizeof(out->rgba));
        temp = (4 * out->width);/* 每行有4 * out->width个字节 */
        for(i = 0; i < out->height; i++)
        {
            for(j = 0; j < temp; j += 4)
            {/* 一个字节一个字节的赋值 */
                out->rgba[pos++] = row_pointers[i][j]; // red
                out->rgba[pos++] = row_pointers[i][j+1]; // green
                out->rgba[pos++] = row_pointers[i][j+2];   // blue
                out->rgba[pos++] = row_pointers[i][j+3]; // alpha
            }
        }
    }
    else if(channels == 3 || color_type == PNG_COLOR_TYPE_RGB)
    {/* 如果是RGB通道 */
        size *= (3*sizeof(unsigned char));	/* 每个像素点占3个字节内存 */
        out->flag = NO_ALPHA;				/* 标记 */
        out->rgba = (unsigned char*) malloc(size);
        if(out->rgba == NULL)
        {/* 如果分配内存失败 */
            LOGV("sdf");
            fclose(pic_fp);
            sprintf(test,"asdfasdfasdf");
            return 1;
        }

        temp = (3 * out->width);/* 每行有3 * out->width个字节 */
        for(i = 0; i < out->height; i++)
        {
            for(j = 0; j < temp; j += 3)
            {/* 一个字节一个字节的赋值 */
                out->rgba[pos++] = row_pointers[i][j]; // red
                out->rgba[pos++] = row_pointers[i][j+1]; // green
                out->rgba[pos++] = row_pointers[i][j+2];   // blue

            }
        }
    }
    else {
        sprintf(test,"12312312312");
        return 1;
    }

    /* 撤销数据占用的内存 */
    png_destroy_read_struct(&png_ptr, &info_ptr, 0);
    return 0;
}

int test_i = 0;
#ifdef   __cplusplus
extern   "C"{
#endif
jstring Java_org_kreal_ndktest_test_testq1( JNIEnv* env,
jobject thiz ) {
//    int iRetVal;
//    pic_data pic_data1;
//
//    struct timeval start, end;
//
//    gettimeofday( &start, NULL );
//    iRetVal = detect_png(env,"/sdcard/123.png",&pic_data1);
//    gettimeofday( &end, NULL );
//    int timeuse = 1000000 * ( end.tv_sec - start.tv_sec ) + end.tv_usec - start.tv_usec;
//
//    __android_log_print(ANDROID_LOG_VERBOSE,"asdaa","time: %d us\n", timeuse) ;
//
//    if (iRetVal==0)
//        return (*env).NewStringUTF("ADS from JNI !");
    char tt[132];
    sprintf(tt,"ss %d",test_i);
    return (env)->NewStringUTF(tt);
}
JNIEXPORT jstring JNICALL
Java_org_kreal_ndktest_test_testq12(JNIEnv *env, jobject instance, jint i) {
    char tt[132];
    test_i= i;
    static int tti = 0;
    tti+=i;
    sprintf(tt,"ss %d",tti);
    return (env)->NewStringUTF(tt);
}

JNIEXPORT jobject JNICALL
Java_org_kreal_ndktest_test_getss(JNIEnv *env, jobject instance, jint i) {
    char *ss;
    ss = (char *) malloc(i);
    return env->NewDirectByteBuffer(ss,i);
}

JNIEXPORT void JNICALL
Java_org_kreal_ndktest_test_ddd(JNIEnv *env, jobject instance, jobject eBuffer) {
    char * address = (char *) env->GetDirectBufferAddress(eBuffer);
//    sprintf(address,"wo de shi jie ni hao ma asdf dfasdf");
//    free(env->GetDirectBufferAddress(eBuffer));
}
#ifdef   __cplusplus
}
#endif