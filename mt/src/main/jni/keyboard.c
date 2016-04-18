//
// Created by lthee on 2016/2/16.
//
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

int sfp,nfp;
struct sockaddr_in s_add,c_add;
int sin_size;
char buffer[1024]={0};
int recbytes;
unsigned short portnum=8088;

int setup_uinput_device()
{
    // Temporary variable
    int i=0;

    // Open the input device
    uinp_fd = open("/dev/uinput", O_WRONLY | O_NDELAY);
    if (uinp_fd <=0)
    {
        printf("Unable to open /dev/uinput/n");
        return -1;
    }

    memset(&uinp,0,sizeof(uinp)); // Intialize the uInput device to NULL
    strncpy(uinp.name, "CC KeyBoard", UINPUT_MAX_NAME_SIZE);
    uinp.id.version = 4;
    uinp.id.bustype = BUS_USB;

    // Setup the uinput device
    ioctl(uinp_fd, UI_SET_EVBIT, EV_KEY);
    ioctl(uinp_fd, UI_SET_EVBIT, EV_SYN);
    for (i=0; i < 256; i++) {
        ioctl(uinp_fd, UI_SET_KEYBIT, i);
    }
    /* Create input device into input sub-system */
    write(uinp_fd, &uinp, sizeof(uinp));
    if (ioctl(uinp_fd, UI_DEV_CREATE))
    {
        printf("Unable to create UINPUT device.");
        return -1;
    }
    return 1;
}

void close_uinput_device()
{
    /* Destroy the input device */
    ioctl(uinp_fd, UI_DEV_DESTROY);
    /* Close the UINPUT device */
    close(uinp_fd);
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

int main()
{
    daemon(0,0);
    sfp = socket(AF_INET, SOCK_STREAM, 0);
    if(-1 == sfp)
    {
        printf("socket fail ! \r\n");
        return -1;
    }
    printf("socket ok !\r\n");

    bzero(&s_add,sizeof(struct sockaddr_in));
    s_add.sin_family=AF_INET;
    s_add.sin_addr.s_addr=htonl(INADDR_ANY);
    s_add.sin_port=htons(portnum);

    if(-1 == bind(sfp,(struct sockaddr *)(&s_add), sizeof(struct sockaddr)))
    {
        printf("bind fail !\r\n");
        return -1;
    }
    printf("bind ok !\r\n");

    if(-1 == listen(sfp,1))
    {
        printf("listen fail !\r\n");
        return -1;
    }
    printf("listen ok\r\n");
    int cmd=0;
    while(1)
    {
        sin_size = sizeof(struct sockaddr_in);
        printf("wait a client\r\n");
        nfp = accept(sfp, (struct sockaddr *)(&c_add), &sin_size);
        if(-1 == nfp)
        {
            printf("accept fail !\r\n");
            return -1;
        }
        if(send_keyEvent(uinp_fd,0)<0) {
            if (setup_uinput_device() < 0) {
                printf("Unable to find uinput device/n");
                return -1;
            }
        }
        //printf("accept ok!\r\nServer start get connect from %#x : %#x\r\n",ntohl(c_add.sin_addr.s_addr),ntohs(c_add.sin_port));
        while (1){
            if(-1 == (recbytes = read(nfp,buffer,1024)))
            {
                printf("read data fail ! close link to client\r\n");
                break;
            }
            buffer[recbytes]='\0';
            cmd = atoi(buffer);
            if(cmd>=0&&cmd<=255)
                send_keyEvent(uinp_fd,cmd);
            else break;
        }
        close(nfp);
        if(send_keyEvent(uinp_fd,0)>0) {
            close_uinput_device();
        }
        if(cmd<0||cmd>255){
            break;
        }
    }
    close(sfp);
    return 0;
}
