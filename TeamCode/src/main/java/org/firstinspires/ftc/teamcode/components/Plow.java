package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Plow {

    public static final double UPPER_ROTATION_LIMIT = 0.25;

    private DcMotor motor;

    public Plow(HardwareMap hardwareMap) {
        motor = hardwareMap.dcMotor.get("plowMotor");

        // TODO: Encoders
    }

    public void raise() {
        motor.setPower(0.5);
    }

    public void lower() {
        motor.setPower(-1);
    }

    public void stop() {
        motor.setPower(0);
    }

}
