#include <jni.h>
#include <android/log.h>
#include <string.h>

#define TAG "ccFFmpeg"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR , TAG, __VA_ARGS__)

#include "../ffmpeg/include/libavcodec/avcodec.h"
#include "../ffmpeg/include/libavformat/avformat.h"
#include "../ffmpeg/include/libswscale/swscale.h"

AVFormatContext *pFormatCtx;
int videoStream, audioStream;
AVStream *audio_st;
AVCodecContext *audio_ctx;
AVStream *video_st;
AVCodecContext *video_ctx;
AVCodec *video_codec;
char filename[1024];

AVCodec *pcodec;
AVPacket *packet;
AVFrame *pFrame;
AVPicture *pFrameYUV;
uint8_t *out_buffer;
jlong buffen_len;
struct SwsContext *img_convert_ctx;

jobject outputBuffer;
int CurrentPosition;

void VideoDecodeInit(AVCodecContext *pCodecCtx) {
    packet = (AVPacket *) av_malloc(sizeof(AVPacket));
    pFrame = av_frame_alloc();
    pFrameYUV = av_malloc(sizeof(AVPicture));
    buffen_len = avpicture_get_size(AV_PIX_FMT_YUV420P, pCodecCtx->width, pCodecCtx->height);
    out_buffer = (uint8_t *) av_malloc(avpicture_get_size(AV_PIX_FMT_YUV420P, pCodecCtx->width, pCodecCtx->height));
    avpicture_fill((AVPicture *) pFrameYUV, out_buffer, AV_PIX_FMT_YUV420P, pCodecCtx->width,pCodecCtx->height);
    if(AV_PIX_FMT_YUV420P==pCodecCtx->pix_fmt)
          LOGI("sds");
          else LOGI("adsf");
          LOGI("ad1111sf");
    img_convert_ctx = sws_getContext(pCodecCtx->width, pCodecCtx->height, pCodecCtx->pix_fmt,
                                     pCodecCtx->width, pCodecCtx->height, AV_PIX_FMT_YUV420P,
                                     SWS_BICUBIC, NULL, NULL, NULL);
}


int videoinfoload(const char *path) {
    strcpy(filename, path);
    pFormatCtx = avformat_alloc_context();
    if (avformat_open_input(&pFormatCtx, filename, NULL, NULL) != 0) {
        LOGI("Couldn't open input stream.");
        return -1;
    }
    if (avformat_find_stream_info(pFormatCtx, NULL) < 0) {
        LOGI("Couldn't find stream information.");
        return -1;
    }
    int i;
    for (i = 0; i < pFormatCtx->nb_streams; i++) {
        switch (pFormatCtx->streams[i]->codec->codec_type) {
            case AVMEDIA_TYPE_VIDEO:
                videoStream = i;
                video_st = pFormatCtx->streams[i];
                video_ctx = video_st->codec;
                break;
            case AVMEDIA_TYPE_AUDIO:
                audioStream = i;
                audio_st = pFormatCtx->streams[i];
                audio_ctx = audio_st->codec;
                break;
            default:
                break;
        }
    }
    video_codec = avcodec_find_decoder(video_ctx->codec_id);
    if (video_codec == NULL) {
        LOGI("Codec not found.\n");
        return -1;
    }
    if (avcodec_open2(video_ctx, video_codec, NULL) < 0) {
        LOGI("Could not open codec.\n");
        return -1;
    }
    VideoDecodeInit(video_ctx);

    decode_one_frame();
    return 0;
}

int decode_one_frame() {
    int got_picture, ret;
    for (; ;) {
        if (av_read_frame(pFormatCtx, packet) >= 0) {
            if (packet->stream_index == videoStream) {
                ret = avcodec_decode_video2(video_ctx, pFrame, &got_picture, packet);
                if (ret < 0) {
                    return -1;
                }
                if (got_picture) {
                    CurrentPosition = packet->pts*av_q2d(video_st->time_base);
                    sws_scale(img_convert_ctx, (const uint8_t *const *) pFrame->data, pFrame->linesize, 0, video_ctx->height, pFrameYUV->data, pFrameYUV->linesize);
                    break;
                }
            }
            av_free_packet(packet);
        }
        else return 0;
    }
    return 1;
}

void relase(JNIEnv *env) {
    av_free(out_buffer);
    av_free(pFrameYUV);
    av_frame_free(pFrame);
    avcodec_close(video_ctx);
    avformat_close_input(pFormatCtx);
    avformat_free_context(pFormatCtx);
    (*env)->DeleteGlobalRef(env, outputBuffer);
}

void Java_org_kreal_ccffmpeg_ccFFmpeg_native_1init(JNIEnv *env, jobject instance) {
    av_register_all();
}

jint Java_org_kreal_ccffmpeg_ccFFmpeg_loadfile(JNIEnv *env, jobject instance, jstring string_) {
    const char *string = (*env)->GetStringUTFChars(env, string_, 0);
    int ret = videoinfoload(string);
    return ret;
}

jobject Java_org_kreal_ccffmpeg_ccFFmpeg_getByteBuffer(JNIEnv *env, jobject instance) {
    jobject buf = (*env)->NewDirectByteBuffer(env, out_buffer, buffen_len);
    outputBuffer = (*env)->NewGlobalRef(env, buf);
    return outputBuffer;
    // TODO
}

jint Java_org_kreal_ccffmpeg_ccFFmpeg_decoderOne(JNIEnv *env, jobject instance) {
    return decode_one_frame();
}

jint native_getVideoWidth(JNIEnv *env, jobject instance) {
    return video_ctx->width;
}

jint native_getVideoHeight(JNIEnv *env, jobject instance) {
    return video_ctx->height;
}
jint native_getDuration(JNIEnv *env, jobject instance) {
    int i = pFormatCtx->duration/1000;
    return i;
}

jint native_getCurrentPosition(JNIEnv *env, jobject instance) {
    return CurrentPosition;
}

void native_seekTo(JNIEnv *env, jobject instance , jint pos) {
     av_seek_frame(pFormatCtx,-1,pos*AV_TIME_BASE+pFormatCtx->start_time,AVSEEK_FLAG_BACKWARD);
}
static JNINativeMethod const gMethods[] =
        {
                {"native_init",   "()V", (void *) Java_org_kreal_ccffmpeg_ccFFmpeg_native_1init},
                {"loadfile",      "(Ljava/lang/String;)I", (void *) Java_org_kreal_ccffmpeg_ccFFmpeg_loadfile},
                {"getByteBuffer", "()Ljava/nio/ByteBuffer;",                      (void *) Java_org_kreal_ccffmpeg_ccFFmpeg_getByteBuffer},
                {"decoderOne",    "()I",   (void *) Java_org_kreal_ccffmpeg_ccFFmpeg_decoderOne},
                {"native_getVideoWidth","()I",(void *) native_getVideoWidth},
                {"native_getVideoHeight","()I",(void *)native_getVideoHeight},
                {"native_getDuration","()I",(void *)native_getDuration},
                {"native_getCurrentPosition","()I",(void *)native_getCurrentPosition},
                {"native_seekTo","(I)V",(void *)native_seekTo},
        };

jint JNI_OnLoad(JavaVM *jvm, void *reserved) {
    JNIEnv *env = NULL;
    if ((*jvm)->GetEnv(jvm,(void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGD(">>>Get env faild!!!");
        return -1;
    }
    jclass clazz = (*env)->FindClass(env,"org/kreal/akvideoplayer/decoder/CCMediaDecoder");
    if (!clazz) {
        LOGE(">>>>Can't find org/kreal/akvideoplayer/decoder/CCMediaDecoder!!! ");
    }
    (*env)->RegisterNatives(env,clazz, gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
    LOGD(">>>>JNI_OnLoad Called!!!");
    return JNI_VERSION_1_4;
}

void JNI_OnUnload(JavaVM *jvm, void *reserved)
{
    JNIEnv *env = NULL;
    if ((*jvm)->GetEnv(jvm,(void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGD(">>>Get env faild!!!");
        return;
    }
    relase(env);
    LOGD(">>>>JNI_OnUnload Called!!!");
    return;
}