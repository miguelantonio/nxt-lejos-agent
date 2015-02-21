
import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.USB;

public class NXTAgent {

    private static final int LCD_MAX_ROWS = 8;
    private static final int ALIVE_LOOP_WAIT_MILIS = 500;
    private static final int LCD_MAX_COLUMNS = 20;

    private static DataInputStream spineIn = null;
    private static NXTConnection spine = null;
    private static final List<String> lcdLog = new LinkedList<String>();
    private static final List<Pair<Action, String>> actions = new LinkedList<Pair<Action, String>>();

    private static boolean alive = true;

    public static void main(String[] args) throws InterruptedException {
        awake();
        ///TEST////
        //test();
        ///TEST////
        be();
    }

    private static void test() {
        /*
         actionQueue(new Pair<Action, Object>(Action.MOVE_FACE, Face.DOWN));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.MOVE_FACE, Face.MID));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.MOVE_FACE, Face.UP));*/
        /*
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.MOVE_FACE, Face.MID));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.MOVE_FACE, Face.DOWN));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.MOVE_FACE, Face.MID));*/
        actionQueue(new Pair<Action, String>(Action.MOVE_FORWARD, null));
        actionQueue(new Pair<Action, String>(Action.NOTHING, null));
        actionQueue(new Pair<Action, String>(Action.NOTHING, null));
        actionQueue(new Pair<Action, String>(Action.MOVE_BACKWARD, null));
        actionQueue(new Pair<Action, String>(Action.NOTHING, null));
        actionQueue(new Pair<Action, String>(Action.NOTHING, null));
        actionQueue(new Pair<Action, String>(Action.MOVE_STOP, null));
        actionQueue(new Pair<Action, String>(Action.TURN_LEFT, null));
        actionQueue(new Pair<Action, String>(Action.NOTHING, null));
        actionQueue(new Pair<Action, String>(Action.TURN_RIGHT, null));
        actionQueue(new Pair<Action, String>(Action.NOTHING, null));
        actionQueue(new Pair<Action, String>(Action.MOVE_STOP, null));

    }

    public static void actionQueue(Pair<Action, String> action) {
        actions.add(action);
    }

    private static void actionDo() {
        if (!actions.isEmpty()) {
            Pair<Action, String> action = actions.get(0);
            actions.remove(0);
            if (action != null) {
                if (action.first.equals(Action.MOVE_FORWARD)) {
                    Pilot.moveForward(true);
                } else if (action.first.equals(Action.MOVE_BACKWARD)) {
                    Pilot.moveForward(false);
                } else if (action.first.equals(Action.TURN_RIGHT)) {
                    Pilot.turnRight(true);
                } else if (action.first.equals(Action.TURN_LEFT)) {
                    Pilot.turnRight(false);
                } else if (action.first.equals(Action.MOVE_STOP)) {
                    Pilot.stopMoving();
                } else if (action.first.equals(Action.LOG)) {
                    if (action.second != null && action.second instanceof String) {
                        log((String) action.second);
                    }
                } else if (action.first.equals(Action.MOVE_FACE)) {
                    if (action.second != null && action.second instanceof String) {
                        face((String) action.second);
                    }
                } else {
                    log("NO SE: " + action.first);
                    log("EJ: " + Action.LOG);
                }
            }
        }
    }

    private static void be() throws InterruptedException {
        while (alive) {
            listen();
            actionDo();
            Thread.sleep(ALIVE_LOOP_WAIT_MILIS);
        }
    }

    private static void listen() {
        if (spineIn == null) {
            log("CONN NOT INIT");
            return;
        }
        
        try {
            if (spineIn != null) {
                String b = spineIn.readUTF();
                log(b);                //actionQueue(null);
            }
        } catch (Exception e) {
            log("ERROR READING");
        }

    }

    private static void logClear() {
        LCD.clear();
    }

    private static void log(String log) {
        if (log != null) {
            if (log.length() > LCD_MAX_COLUMNS) {
                log = log.substring(0, LCD_MAX_ROWS);
            }
            lcdLog.add(log);
            if (lcdLog.size() > LCD_MAX_ROWS) {
                lcdLog.remove(0);
            }
            LCD.clear();
            for (int i = lcdLog.size() - 1; i >= 0; i--) {
                LCD.drawString(lcdLog.get(i), 0, i);
            }
        } else {
            log("");
        }
    }

    public static void face(String face) {
        log("CARA: " + face);
        if (face.equals(Face.DOWN)) {
            Motor.C.rotate(-(((-Motor.C.getPosition()) + 180) % 360));
        } else if (face.equals(Face.MID)) {
            Motor.C.rotate(-(((-Motor.C.getPosition()) + 90) % 360));
        } else if (face.equals(Face.UP)) {
            Motor.C.rotate(-(((-Motor.C.getPosition()) - 360) % 360));
            Motor.C.rotate(-(((-Motor.C.getPosition()) - 360) % 360));
        } else {
            log("NO SE");
        }
    }

    private static void awake() {
        Motor.C.setSpeed(30);
        Motor.A.setSpeed(180);
        Motor.B.setSpeed(180);
        for (int i = 0; i < LCD_MAX_ROWS; i++) {
            lcdLog.add("");
        }
        Button.ESCAPE.addButtonListener(new ButtonListener() {
            @Override
            public void buttonPressed(Button b) {
                log("");
                log("BYE BYE !! :)");
                log("");
            }

            @Override
            public void buttonReleased(Button b) {
                face(Face.UP);
                try {
                    spineIn.close();
                } catch (IOException ex) {
                    log("cant close input");
                }
                spine.close();
                logClear();
                alive = false;
            }
        });
        log("INICIANDO CONEXION");
        log("IZQ USB, DER BL");
        if (Button.waitForAnyPress() == Button.ID_RIGHT) {
            log("... esperando BT");
            spine = Bluetooth.waitForConnection();
        } else if (Button.waitForAnyPress() == Button.ID_LEFT) {
            log("... esperando USB");
            spine = USB.waitForConnection();
        } else {
            log("NO CONNECTION");
            return;
        }
        spineIn = spine.openDataInputStream();
        /*
         conn = new Thread() {
         @Override
         public void run() {
         if (spine != null) {
         DataInputStream input = spine.openDataInputStream();
         while (alive) {
         try {
         String command = input.readUTF();
         if (command != null) {
         log("RCVD: " + command);
         int comma = command.indexOf(",");
         String[] actPr = new String[2];
         if (comma > 0) {
         actPr[0] = command.substring(0, comma);
         actPr[1] = command.substring(comma);
         } else {
         actPr[0] = command;
         actPr[1] = null;
         }
         try {
         Pair<Action, String> act = new Pair<Action, String>(Action.valueOf(actPr[0]), actPr[1]);
         actionQueue(act);
         } catch (IllegalArgumentException e) {
         log("NO SE: " + actPr[0]);
         log("EJ: " + Action.NOTHING.name());
         }
         try {
         Thread.sleep(ALIVE_LOOP_WAIT_MILIS);
         } catch (InterruptedException ex) {
         log("COMM THREAD ERROR");
         }

         } else {
         log("waiting..");
         }
         } catch (IOException ex) {
         log("CANT READ INPUT ERROR");
         }
         }
         try {
         input.close();
         } catch (IOException ex) {
         log("ERR CLOSING");
         }
         }
         }
         };
         conn.setDaemon(true);
         conn.start();*/
    }

    public static class Face {

        public final static String MID = "MID";
        public final static String DOWN = "DOWN";
        public final static String UP = "UP";
    }

    public static enum Action {

        NOTHING, MOVE_FORWARD, MOVE_BACKWARD, TURN_RIGHT, TURN_LEFT, MOVE_STOP, LOG, MOVE_FACE
    }

    public static class Pilot {

        public static void stopMoving() {
            Motor.A.stop();
            Motor.B.stop();
        }

        public static void turnRight(boolean right) {
            if (right) {
                Motor.A.backward();
                Motor.B.forward();
            } else {
                Motor.A.forward();
                Motor.B.backward();
            }
        }

        public static void moveForward(boolean forward) {
            if (forward) {
                Motor.A.forward();
                Motor.B.forward();
            } else {
                Motor.A.backward();
                Motor.B.backward();
            }
        }
    }

    /////// UTIL CLASSES
    public static class Pair<F, S> {

        public final F first;
        public final S second;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
    }

}
