package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class FoundationHook {

    private Servo servo;

    public FoundationHook(HardwareMap hardwareMap) {
        servo = hardwareMap.servo.get("foundation");
    }

    public void raise() {
        servo.setPosition(0);
    }

    public void lower() {
        servo.setPosition(1);
    }

}
