package org.kreal.ftpserver.virtualfilesystem;

import org.apache.ftpserver.ftplet.FtpFile;

/**
 * Created by lthee on 2016/10/12.
 */
public interface FtpFileCC extends FtpFile {
    public String getRealPath();
    public String getType();
}
