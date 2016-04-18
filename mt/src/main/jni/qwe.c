#include <jni.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string.h>
#include <fcntl.h>
#include <linux/input.h>
#include <linux/uinput.h>
static int uinp_fd = -1;
struct uinput_user_dev uinp; // uInput device structure
struct input_event event; // Input device structure
int setup_uinput_device()
{
    // Temporary variable
    int i=0;

    // Open the input device
    uinp_fd = open("/dev/uinput", O_WRONLY | O_NDELAY);
    if (uinp_fd <=0)
    {
        //printf("Unable to open /dev/uinput/n");
        return uinp_fd;
    }

    return 1;
}
int send_a_event(int fd, uint16_t type, uint16_t keycode, int32_t value)
{
    struct input_event ev;
    memset(&ev, 0, sizeof(struct input_event));
    ev.type = type;
    ev.code = keycode;
    ev.value = value;
    if (write(fd, &ev, sizeof(struct input_event))<0) {
        printf("report key error!\n");
        return -1;
    }
    return 0;
}

int send_keyEvent(int fd,uint16_t keycode)
{
    int result=0;
    result += send_a_event(fd, EV_KEY, keycode, 1);
    result += send_a_event(fd, EV_SYN, SYN_REPORT, 0);
    result += send_a_event(fd, EV_KEY, keycode, 0);
    result += send_a_event(fd, EV_SYN, SYN_REPORT, 0);
    if(result!=0)
        return -1;
    else
        return 1;
}
static int i=0;
JNIEXPORT jint JNICALL
Java_org_kreal_mt_MainActivity_create(JNIEnv *env, jobject instance) {

    //setup_uinput_device();
    return setup_uinput_device();

}
