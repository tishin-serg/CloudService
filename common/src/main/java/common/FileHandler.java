package common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class FileHandler extends ChannelInboundHandlerAdapter {

    private final FileHandlerListener listener;
    private Status currentStatus = Status.INACTION;
    private int nameLength;
    private String fileName;
    private long fileSize;
    private long receivedFileBytes;
    private BufferedOutputStream outputStream;

    public FileHandler(FileHandlerListener listener) {
        this.listener = listener;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuffer = (ByteBuf) msg;
        while (byteBuffer.readableBytes() > 0) {
            if (currentStatus == Status.INACTION) {
                if (byteBuffer.readByte() == 25) {
                    currentStatus = Status.FILE_NAME_LENGTH;
                    receivedFileBytes = 0L;
                    System.out.println("Status: Start reading file");
                    // listener.onFileStartRead(this);
                } else {
                    System.out.println("Wrong first byte: " + byteBuffer.readByte());
                }
            }

            if (currentStatus == Status.FILE_NAME_LENGTH) {
                if (byteBuffer.readableBytes() >= 4) {
                    nameLength = byteBuffer.readInt();
                    currentStatus = Status.NAME;
                    System.out.println("Status: Get name file length");
                    // listener.onGetFileNameLength(this);
                }
            }

            if (currentStatus == Status.NAME) {
                if (byteBuffer.readableBytes() >= nameLength) {
                    byte[] nameFileBytes = new byte[nameLength];
                    byteBuffer.readBytes(nameFileBytes);
                    fileName = new String(nameFileBytes, StandardCharsets.UTF_8);
                    System.out.println("Received file: " + fileName);
                    // listener.onGetFileName(this, fileName);
                    outputStream = new BufferedOutputStream(new FileOutputStream(fileName));
                    currentStatus = Status.FILE_SIZE;
                }
            }

            if (currentStatus == Status.FILE_SIZE) {
                if (byteBuffer.readableBytes() >= 8) {
                    fileSize = byteBuffer.readLong();
                    // listener.onGetFileSize(this, fileSize);
                    System.out.println("Status: Get file size: " + fileSize / 1024 + " kb");
                    currentStatus = Status.FILE;
                }
            }

            if (currentStatus == Status.FILE) {
                while (byteBuffer.readableBytes() > 0) {
                    outputStream.write(byteBuffer.readByte());
                    receivedFileBytes++;
                    if (receivedFileBytes == fileSize) {
                        currentStatus = Status.INACTION;
                        // listener.onReceiveFileFinished(this);
                        outputStream.close();
                        break;
                    }
                }
            }
        }
        if (byteBuffer.readableBytes() == 0) {
            byteBuffer.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // listener.onGetFileException(this, cause, ctx);
        ctx.close();
    }
}
