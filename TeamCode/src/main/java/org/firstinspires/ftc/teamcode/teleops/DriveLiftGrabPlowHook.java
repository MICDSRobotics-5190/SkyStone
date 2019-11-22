package org.firstinspires.ftc.teamcode.teleops;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.micdsrobotics.robotplus.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.components.FoundationHook;
import org.firstinspires.ftc.teamcode.components.Grabber;
import org.firstinspires.ftc.teamcode.components.Lift;
import org.firstinspires.ftc.teamcode.components.Plow;
import org.firstinspires.ftc.teamcode.components.SkyStoneRobot;

@TeleOp(name = "Drivetrain + Lift + Grabber + Plow + Hook")
public class DriveLiftGrabPlowHook extends OpMode {

    private SkyStoneRobot robot = new SkyStoneRobot();
    private MecanumDrive drivetrain;
    private Lift lift;
    private Grabber grabber;
    private Plow plow;
    private FoundationHook foundationHook;

    @Override
    public void init() {
        robot.initHardware(hardwareMap);
        drivetrain = robot.getDrivetrain();
        lift = new Lift(hardwareMap);
        grabber = new Grabber(hardwareMap);
        grabber.close();
        plow = new Plow(hardwareMap);
        foundationHook = new FoundationHook(hardwareMap);
    }

    @Override
    public void loop() {
        drivetrain.complexDrive(gamepad1, telemetry);

        if (gamepad2.a) {
            lift.raise();
        } else if (gamepad2.b) {
            lift.lower();
        }

        if (gamepad2.x) {
            grabber.extend();
        } else if (gamepad2.y) {
            grabber.clamp();
        }

        if (gamepad1.a) {
            plow.raise();
        } else if (gamepad1.b) {
            plow.lower();
        }

        if (gamepad1.x) {
            foundationHook.lower();
        } else if (gamepad1.y) {
            foundationHook.raise();
        }
    }

}
