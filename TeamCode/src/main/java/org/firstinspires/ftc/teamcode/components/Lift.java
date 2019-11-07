package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Lift {

    private DcMotor motor;

    public Lift(HardwareMap hardwareMap) {
        motor = hardwareMap.dcMotor.get("liftMotor");
    }

    public void raise() {
        motor.setPower(1);
    }

    public void lower() {
        motor.setPower(-1);
    }

}
