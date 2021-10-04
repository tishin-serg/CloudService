package common;

import io.netty.channel.ChannelHandlerContext;

public interface FileHandlerListener {
    void onFileStartRead(FileHandler fileHandler);

    void onGetFileNameLength(FileHandler fileHandler);

    void onGetFileName(FileHandler fileHandler, String fileName);

    void onGetFileSize(FileHandler fileHandler, long fileSize);

    void onReceiveFileFinished(FileHandler fileHandler);

    void onGetFileException(FileHandler fileHandler, Throwable cause, ChannelHandlerContext ctx);
}
