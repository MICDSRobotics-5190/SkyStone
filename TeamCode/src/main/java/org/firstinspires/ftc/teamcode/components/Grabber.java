package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Grabber {

    private Servo servo;

    public Grabber(HardwareMap hardwareMap) {
        servo = hardwareMap.servo.get("grabber");
    }

    // Brings the grabber to fully closed position, should not be used in driving.
    public void close() {
        servo.setPosition(0);
    }

    public void extend() {
        servo.setPosition(1);
    }

    // Brings the grabber to optimal grabbing position.
    public void clamp() {
        // TODO: Test position values
        servo.setPosition(0.8);
    }

}
