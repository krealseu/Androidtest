
#include <jni.h>
#include <android/log.h>
#include <string.h>
#define TAG "ccFFmpeg"
#define LOGI(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)

#include "../ffmpeg/include/libavcodec/avcodec.h"
#include "../ffmpeg/include/libavformat/avformat.h"
#include "../ffmpeg/include/libswscale/swscale.h"

typedef struct VideoInfo{
    AVFormatContext *pFormatCtx;
    int             videoStream, audioStream;
    AVStream        *audio_st;
    AVCodecContext  *audio_ctx;
    AVStream        *video_st;
    AVCodecContext  *video_ctx;
    AVCodec *video_codec;
    char            filename[1024];
}VideoInfo;
typedef struct VideoDecode{
    AVCodec *pcodec;
    AVPacket *packet;
    AVFrame *pFrame;
    AVPicture *pFrameYUV;
    uint8_t *out_buffer;
    struct SwsContext *img_convert_ctx;
}VideoDecode;
typedef struct AudioDecode{

}AudioDecode;

void VideoDecodeInit(VideoDecode *vd,AVCodecContext *pCodecCtx){
    vd->packet=(AVPacket *)av_malloc(sizeof(AVPacket));
    vd->pFrame=av_frame_alloc();
    vd->pFrameYUV = av_malloc(sizeof(AVPicture));
    vd->out_buffer=(uint8_t *)av_malloc(avpicture_get_size(AV_PIX_FMT_YUV420P, pCodecCtx->width, pCodecCtx->height));
    avpicture_fill((AVPicture *)vd->pFrameYUV, vd->out_buffer, AV_PIX_FMT_YUV420P, pCodecCtx->width, pCodecCtx->height);
    vd->img_convert_ctx = sws_getContext(pCodecCtx->width, pCodecCtx->height, pCodecCtx->pix_fmt,
                                     pCodecCtx->width, pCodecCtx->height, AV_PIX_FMT_YUV420P, SWS_BICUBIC, NULL, NULL, NULL);
    if(pCodecCtx->pix_fmt=AV_PIX_FMT_YUV420P)
      LOGI("%s",pCodecCtx->pix_fmt);
}

VideoInfo *videoInfo;
VideoDecode videoDecode;

int videoinfoload(VideoInfo *info, const char * path){
    strcpy(info->filename,path);
    videoInfo->pFormatCtx=avformat_alloc_context();
    if(avformat_open_input(&info->pFormatCtx,info->filename,NULL,NULL)!=0){
        LOGI("Couldn't open input stream.");
        return -1;
    }
    if(avformat_find_stream_info(info->pFormatCtx,NULL)<0){
        LOGI("Couldn't find stream information.");
        return -1;
    }
    int i;
    for(i=0; i<info->pFormatCtx->nb_streams; i++) {
        switch (info->pFormatCtx->streams[i]->codec->codec_type){
            case AVMEDIA_TYPE_VIDEO:
                info->videoStream = i;
                info->video_st = info->pFormatCtx->streams[i];
                info->video_ctx = info->video_st->codec;
                break;
            case AVMEDIA_TYPE_AUDIO:
                info->audioStream = i;
                info->audio_st = info->pFormatCtx->streams[i];
                info->audio_ctx = info->audio_st->codec;
                break;
            default:
                break;
        }
    }
    info->video_codec=avcodec_find_decoder(info->video_ctx->codec_id);
    if(info->video_codec==NULL){
        LOGI("Codec not found.\n");
        return -1;
    }
    if(avcodec_open2(info->video_ctx, info->video_codec,NULL)<0){
        LOGI("Could not open codec.\n");
        return -1;
    }
    VideoDecodeInit(&videoDecode,videoInfo->video_ctx);
    decode_one_frame();
    return 0;
}

int decode_one_frame(){
    int got_picture,ret,y_size,i;
    FILE *fp_yuv;
    fp_yuv=fopen("/sdcard/sdd.yuv","wb+");
    if(fp_yuv==NULL){
        LOGI("file open file");
    }
    i=0;
    for(;;) {
        LOGI("q");
        if (av_read_frame(videoInfo->pFormatCtx, videoDecode.packet) >= 0) {
            LOGI("w");
            if (videoDecode.packet->stream_index == videoInfo->videoStream) {
                LOGI("e");
                ret = avcodec_decode_video2(videoInfo->video_ctx, videoDecode.pFrame, &got_picture,
                                            videoDecode.packet);
                if (ret < 0) {
                    LOGI("Decode Error.");
                    return -1;
                }
                if (got_picture) {
                    LOGI("r");
                    LOGI("%d",i++);
                    sws_scale(videoDecode.img_convert_ctx,
                              (const uint8_t *const *) videoDecode.pFrame->data,
                              videoDecode.pFrame->linesize, 0, videoInfo->video_ctx->height,
                              videoDecode.pFrameYUV->data, videoDecode.pFrameYUV->linesize);
                    y_size=videoInfo->video_ctx->width*videoInfo->video_ctx->height;
                    //fwrite(videoDecode.pFrameYUV->data[0],1,y_size,fp_yuv);    //Y
                    //fwrite(videoDecode.pFrameYUV->data[1],1,y_size/4,fp_yuv);  //U
                    //fwrite(videoDecode.pFrameYUV->data[2],1,y_size/4,fp_yuv);  //V
                    //fwrite(videoDecode.out_buffer,1, avpicture_get_size(AV_PIX_FMT_YUV420P, videoInfo->video_ctx->width, videoInfo->video_ctx->height),fp_yuv);
                    if(i>2)
                        break;
                }
            }
            av_free_packet(videoDecode.packet);
        }
    }
    fclose(fp_yuv);
}

JNIEXPORT void JNICALL
Java_org_kreal_ccffmpeg_ccFFmpeg_native_1init(JNIEnv *env, jobject instance) {
    av_register_all();
    videoInfo = av_mallocz(sizeof(VideoInfo));
    LOGI("%d", sizeof(AVPicture));
    LOGI("%d", sizeof(videoInfo));
    LOGI("%d", sizeof(AVFormatContext));
    LOGI("Register_all");
}

JNIEXPORT jint JNICALL
Java_org_kreal_ccffmpeg_ccFFmpeg_loadfile(JNIEnv *env, jobject instance, jstring string_) {
    const char *string = (*env)->GetStringUTFChars(env, string_, 0);
    int ret=videoinfoload(videoInfo,string);
    (*env)->ReleaseStringUTFChars(env, string_, string);
    return ret;
}


