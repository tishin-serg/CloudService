package common;

import java.nio.file.Path;

public interface FileUtilsListener {
    void onSendFile(Path path);
}
