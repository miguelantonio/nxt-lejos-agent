
import java.util.LinkedList;
import java.util.List;
import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

public class NXTAgent {

    private static final int LCD_MAX_ROWS = 10;
    private static final int ALIVE_LOOP_WAIT_MILIS = 500;
    private static final int LCD_MAX_COLUMNS = 14;

    private static final UltrasonicSensor distanceCM = new UltrasonicSensor(SensorPort.S1);
    private static final List<String> lcdLog = new LinkedList<String>();
    private static final List<Pair<Action, Object>> actions = new LinkedList<Pair<Action, Object>>();

    private static boolean alive = true;

    public static void main(String[] args) throws InterruptedException {
        awake();
        ///TEST////
        test();
        ///TEST////
        be();
    }

    private static void test() {
        /*actionQueue(new Pair<Action, Object>(Action.LOG, "PARTIMOS :) PRUEBA HOLA"));
         actionQueue(new Pair<Action, Object>(Action.LOG, ""));
         actionQueue(new Pair<Action, Object>(Action.LOG, ""));
         actionQueue(new Pair<Action, Object>(Action.MOVE_FORWARD, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.MOVE_BACKWARD, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.MOVE_STOP, null));
         actionQueue(new Pair<Action, Object>(Action.TURN_LEFT, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.NOTHING, null));
         actionQueue(new Pair<Action, Object>(Action.MOVE_STOP, null));*/
        actionQueue(new Pair<Action, Object>(Action.LOG, "1 PRUEBA HOLA"));
        actionQueue(new Pair<Action, Object>(Action.LOG, "2 PRUEBA HOLA"));
        actionQueue(new Pair<Action, Object>(Action.LOG, "3 PRUEBA HOLA"));
        actionQueue(new Pair<Action, Object>(Action.LOG, "4 PRUEBA HOLA"));
        actionQueue(new Pair<Action, Object>(Action.LOG, "5 PRUEBA HOLA"));
        actionQueue(new Pair<Action, Object>(Action.LOG, "6 PRUEBA HOLA"));
        actionQueue(new Pair<Action, Object>(Action.LOG, "7 PRUEBA HOLA"));
        actionQueue(new Pair<Action, Object>(Action.LOG, "8 PRUEBA HOLA"));
        actionQueue(new Pair<Action, Object>(Action.LOG, "9 PRUEBA HOLA"));
        actionQueue(new Pair<Action, Object>(Action.LOG, "10 PRUEBA HOLA"));
        actionQueue(new Pair<Action, Object>(Action.LOG, "11 PRUEBA HOLA"));
        actionQueue(new Pair<Action, Object>(Action.LOG, "12 PRUEBA HOLA"));
    }

    //TODO: recibir
    public static void actionQueue(Pair<Action, Object> action) {
        actions.add(action);
    }

    private static void actionDo() {
        if (!actions.isEmpty()) {
            Pair<Action, Object> action = actions.get(0);
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
                    if (action.second instanceof String) {
                        log((String) action.second);
                    }
                } else {
                    //NOTHING
                }
            }
        }
    }

    private static void perception() {
        //log("ultra: " + distanceCM.getDistance() + " cms");
        //TODO: Enviar
    }

    private static void be() throws InterruptedException {
        while (alive) {
            actionDo();
            perception();
            Thread.sleep(ALIVE_LOOP_WAIT_MILIS);
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

    public static void armMoveAngle(int angle) {
        Motor.C.rotate(angle);
    }

    private static void awake() {
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
                logClear();
                alive = false;
            }
        });
    }

    public static enum Action {

        NOTHING, MOVE_FORWARD, MOVE_BACKWARD, TURN_RIGHT, TURN_LEFT, MOVE_STOP, LOG
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
