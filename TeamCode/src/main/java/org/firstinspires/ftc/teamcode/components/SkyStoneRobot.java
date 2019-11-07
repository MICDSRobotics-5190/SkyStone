package org.firstinspires.ftc.teamcode.components;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.micdsrobotics.robotplus.hardware.MecanumDrive;
import org.firstinspires.ftc.micdsrobotics.robotplus.hardware.Robot;

public class SkyStoneRobot extends Robot<MecanumDrive> {
    @Override
    public void initHardware(HardwareMap hardwareMap) {
        DcMotor main1 = hardwareMap.dcMotor.get("main1");
        DcMotor main2 = hardwareMap.dcMotor.get("main2");
        DcMotor minor1 = hardwareMap.dcMotor.get("minor1");
        DcMotor minor2 = hardwareMap.dcMotor.get("minor2");

        MecanumDrive mecanumDrive = new MecanumDrive(main1, main2, minor1, minor2);

        setDrivetrain(mecanumDrive);

        main2.setDirection(DcMotorSimple.Direction.REVERSE);
        minor2.setDirection(DcMotorSimple.Direction.REVERSE);

        main1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        main2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        minor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        minor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        mecanumDrive.setModes(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    @Override
    public double voltageToDistance(double voltage) {
        // TODO
        return 0;
    }
}
