package com.variacode.nxt.commtest;

import java.io.DataOutputStream;
import java.io.IOException;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;

public class USBTest {

    public static void main(String[] args) throws NXTCommException, InterruptedException {
        NXTConnector conn = new NXTConnector();

        conn.addLogListener(new NXTCommLogListener() {

            @Override
            public void logEvent(String message) {
                System.out.println("USBSend Log.listener: " + message);
            }

            @Override
            public void logEvent(Throwable throwable) {
                System.out.println("USBSend Log.listener - stack trace: ");
                throwable.printStackTrace();
            }

        }
        );

        if (!conn.connectTo("usb://")) {
            System.err.println("No NXT found using USB");
            System.exit(1);
        }

        DataOutputStream outDat = new DataOutputStream(conn.getOutputStream());
        for (int i = 0; i < 100; i++) {
            try {
                outDat.writeUTF("MOVE_FACE,DOWN");// + DOWN);
                outDat.flush();
            } catch (IOException ioe) {
                System.out.println(ioe);
                System.err.println("IO Exception writing bytes");
            }
            Thread.sleep(4000);
            System.out.println("Sent " + "LOG," + i);
        }

        try {
            conn.close();
            System.out.println("Closed connection");
        } catch (IOException ioe) {
            System.out.println(ioe);
            System.err.println("IO Exception Closing connection");
        }

    }
}
