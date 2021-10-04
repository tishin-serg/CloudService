package gui;

import common.FileUtils;
import core.ClientNetwork;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class ControllerClientGui implements EventHandler<ActionEvent>, Initializable {

    private FileUtils fileUtils = new FileUtils();
    private ClientNetwork clientNetwork = new ClientNetwork();

    @FXML
    private Button btnDisconnect;
    @FXML
    private ListView<String> clientFileList;
    @FXML
    private ListView<String> serverFileList;
    @FXML
    private Button btnUpload;
    @FXML
    private Button btnDownload;
    @FXML
    private Button btnRename;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnConnect;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;


    /*
    public static void main(String[] args) {


        FileSender fileSender = new FileSender();
        try {
            fileSender.sendFile(Paths.get("/Users/mac/IdeaProjects/CloudService/cloud-storage-client/Шилдт джава.pdf"),
                    clientNetwork.getCurrentChannel(),
                    new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                future.cause().printStackTrace();
//                Network.getInstance().stop();
                            }
                            if (future.isSuccess()) {
                                System.out.println("Файл успешно передан");
//                Network.getInstance().stop();
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     */

    public static void connect() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ClientNetwork clientNetwork = new ClientNetwork();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientNetwork.start(countDownLatch);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void auth() {

    }

    public static void showFileNames(String user) {
        File[] array = Paths.get("cloud-storage-client/" + user).toFile().listFiles();
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i].getName());
        }
    }

    @Override
    public void handle(ActionEvent event) {
        Object src = event.getSource();
        if (src.equals(btnUpload)) {

        } else {
            showErrorAlert("Unexpected source: " + src);
            throw new RuntimeException("Unexpected source: " + src);
        }
    }

    private void showErrorAlert(String s) {
    }

    private void showFileList() {
        String user = loginField.getText();
        File[] array = Paths.get("cloud-storage-client/" + user).toFile().listFiles();
        String[] files = new String[array.length];
        for (int i = 0; i < files.length; i++) {
            files[i] = array[i].getName();
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clientFileList.getItems().setAll(files);
            }
        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showFileList();
    }
}
