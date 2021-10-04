package gui;

import core.ServerNetwork;

public class ControllerServerGui {
    public static void main(String[] args) {
        ServerNetwork serverNetwork = new ServerNetwork();
        serverNetwork.start();
    }
}
