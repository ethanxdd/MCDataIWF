package org.nycu.mc.iwf.Session;

import java.util.Timer;
import java.util.TimerTask;

import org.nycu.mc.iwf.proxy.Proxy;


public class PermissionState {
    public static final int HAS_PERMISSION = 1;
    public static final int HAS_NO_PERMISSION = 2;
    public static final int PENDING_REQUEST = 3;
    public static final int PENDING_RELEASE = 4;
    public static final int PENDING_REVOKE = 5;
    public static final int QUEUE = 6;
    private static final String TAG = "PermissionState";
    private int currentState = -1;
    private MediaSipSession mediaSipSession;
    private Timer timer;
    private Timer queueHintTimer;
    private Timer queueRequestTimer;
    private int queueNumber = 0;

    public PermissionState(MediaSipSession mediaSipSession) {
        this.mediaSipSession = mediaSipSession;
        this.currentState = 2;
    }

    public void changeState(int state) {
        this.stopReleaseTimer();
        this.stopQueueHintTimer();
        switch (state) {
            case 1: {
                this.currentState = 1;
                break;
            }
            case 2: {
                this.currentState = 2;
                break;
            }
            case 3: {
                this.currentState = 3;
                break;
            }
            case 4: {
                this.startReleaseTimer();
                this.currentState = 4;
                break;
            }
            case 5: {
                this.currentState = 5;
                break;
            }
            case 6: {
                this.startQueueHintTimer();
                this.sendRTCPQueueRequest();
                this.currentState = 3;
                break;
            }
            default: {
                this.currentState = -1;
            }
        }
    }

    public void setQueueNumber(int queueNumber) {
        this.queueNumber = queueNumber;
    }

    private void sendRTCPQueueRequest() {
        if (this.queueRequestTimer == null) {
            this.queueRequestTimer = new Timer();
        }
        this.queueRequestTimer.schedule(new TimerTask(){

            @Override
            public void run() {
                Proxy.getInstance().sendRTCPQueueRequest(PermissionState.this.mediaSipSession);
            }
        }, 5000L);
    }

    private void startQueueHintTimer() {
        this.stopQueueHintTimer();
        this.queueHintTimer = new Timer();
        QueueHintTimerTask timerTask = new QueueHintTimerTask();
        this.queueHintTimer.schedule((TimerTask)timerTask, 0L, 1500L);
    }

    public void stopQueueHintTimer() {
        this.closeTimer(this.queueHintTimer);
        this.closeTimer(this.queueRequestTimer);
        this.queueHintTimer = null;
        this.queueRequestTimer = null;
    }

    private void closeTimer(Timer timer) {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    private void startReleaseTimer() {
        if (this.timer == null && (this.currentState == 4 || this.currentState == 1)) {
            this.timer = new Timer();
            ReleaseTimerTask timerTask = new ReleaseTimerTask(this);
            this.timer.schedule((TimerTask)timerTask, 1000L, 1000L);
        }
    }

    public void stopReleaseTimer() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer.purge();
            this.timer = null;
        }
    }

    public int getCurrentState() {
        return this.currentState;
    }

    private void sendMSGToTalkHandler(int permissionState) {
    }

    private void sendMSGToTalkHandler(int mbcpState, int peerPort) {
    }

    class QueueHintTimerTask
    extends TimerTask {
        QueueHintTimerTask() {
        }

        @Override
        public void run() {
        }
    }

    class ReleaseTimerTask
    extends TimerTask {
        private PermissionState permissionState;
        private int count;

        ReleaseTimerTask(PermissionState permissionState2) {
            this.permissionState = permissionState2;
            this.count = 0;
        }

        @Override
        public void run() {
            String peerIp = "120.101.9.174";
            int peerRtcpPort = 5090;
            if (this.permissionState.getCurrentState() != 2) {
                if (this.count >= 4) {
                    this.permissionState.stopReleaseTimer();
                    this.cancel();
                } else {
                    Proxy.getInstance().sendRtcpRelease(PermissionState.this.mediaSipSession);
                    ++this.count;
                }
            } else {
                this.cancel();
            }
        }
    }
}

