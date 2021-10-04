package common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    private static final byte markerSendFile = 25;
    private static final byte markerAuthRequest = 26;

    public static void sendFile(Path path, Channel channel, ChannelFutureListener listener) throws IOException {
        // Кладем ссылку на файл
        FileRegion file = new DefaultFileRegion(path.toFile(), 0, Files.size(path));
        ByteBuf buffer = null;
        // Определяем размер буфера
        buffer = ByteBufAllocator.DEFAULT.directBuffer(1);
        // Заворачиваем сигнальный байт
        buffer.writeByte(markerSendFile);
        // Отправляем
        channel.writeAndFlush(buffer);
        // Определяем размер буфера
        buffer = ByteBufAllocator.DEFAULT.directBuffer(4);
        // Кладем в переменную длину имени файла
        int nameLength = path.getFileName().toString().getBytes(StandardCharsets.UTF_8).length;
        // Заворачиваем в буфер
        buffer.writeInt(nameLength);
        // Отправляем ДЛИНУ ИМЕНИ ФАЙЛА
        channel.writeAndFlush(buffer);
        // Получаем из имени файла массив байтов
        byte[] fileNameBytes = path.getFileName().toString().getBytes(StandardCharsets.UTF_8);
        // Определяем размер буфера по количеству элементов в массиве
        buffer = ByteBufAllocator.DEFAULT.directBuffer(fileNameBytes.length);
        // Оборачиваем массив в буфер
        buffer.writeBytes(fileNameBytes);
        // Отправляем ИМЯ ФАЙЛА
        channel.writeAndFlush(buffer);
        buffer = ByteBufAllocator.DEFAULT.directBuffer(8);
        buffer.writeLong(Files.size(path));
        // Отправляем РАЗМЕР ФАЙЛА
        channel.writeAndFlush(buffer);
        // Отправляем файл и кладём инфу по отправке в channel future
        ChannelFuture sendingFile = channel.writeAndFlush(file);
        if (listener != null) {
            sendingFile.addListener(listener);
        }
    }

    public static boolean deleteFile(String fileName) throws IOException {
        Path pathToFile = Paths.get("cloud-storage-client/user1/" + fileName);
        return Files.deleteIfExists(pathToFile);
    }

    public static boolean changeFileName(String oldFileName, String newFileName) {
        Path pathToFile = Paths.get("cloud-storage-client/user1/" + oldFileName);
        if (Files.exists(pathToFile)) {
            File oldFile = new File(pathToFile.toUri());
            File newFile = new File("cloud-storage-client/user1/" + newFileName);
            return oldFile.renameTo(newFile);
        }
        return false;
    }

    /*public static void authRequest(Channel channel,  String login, String password) {
        ByteBuf buffer = null;
        buffer = ByteBufAllocator.DEFAULT.directBuffer(1);
        buffer.writeByte(markerAuthRequest);
        channel.writeAndFlush(buffer);

        // ОТПРАВКА ЛОГИНА
        int loginLength = login.getBytes(StandardCharsets.UTF_8).length;
        buffer.writeInt(loginLength);
        channel.writeAndFlush(buffer);
        byte[] loginBytes = login.getBytes(StandardCharsets.UTF_8);
        buffer = ByteBufAllocator.DEFAULT.directBuffer(loginBytes.length);
        buffer.writeBytes(loginBytes);
        channel.writeAndFlush(buffer);

        // ОТПРАВКА ПАРОЛЯ
        int passwordLength = password.getBytes(StandardCharsets.UTF_8).length;
        buffer.writeInt(passwordLength);
        channel.writeAndFlush(buffer);
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        buffer = ByteBufAllocator.DEFAULT.directBuffer(passwordBytes.length);
        buffer.writeBytes(passwordBytes);
        channel.writeAndFlush(buffer);

    }*/
}
