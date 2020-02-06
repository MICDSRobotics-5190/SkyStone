package org.firstinspires.ftc.teamcode.autonomous;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.micdsrobotics.robotplus.autonomous.TimeOffsetVoltage;
import org.firstinspires.ftc.micdsrobotics.robotplus.hardware.MecanumDrive;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.components.FoundationHook;
import org.firstinspires.ftc.teamcode.components.Grabber;
import org.firstinspires.ftc.teamcode.components.Lift;
import org.firstinspires.ftc.teamcode.components.Plow;
import org.firstinspires.ftc.teamcode.components.SkyStoneRobot;

import java.util.List;

@Autonomous(name = "RedBuildSite")
public class RedAutoBuildSite extends LinearOpMode {

    private static final String TFOD_MODEL_ASSET = "Skystone.tflite";
    private static final String STONE_LABEL = "Stone";
    private static final String SKYSTONE_LABEL = "Skystone";

    private static final String VUFORIA_KEY = "Abqz+2L/////AAABma0eOZ1qn0JtoRRtYBon9SdCW2pl+UV/udFm5jGdYGCtEwQGIrRVlelT+8l+8a9loKRX9hN58JSJgQ6XTHzFz7FfECsfDmQpAm2IHWxgA/rRgk4X4TxZV86koMTTIz7bL8ixS2SLqlOwB6qcesZHtJtaIPx5sUsy5rFyXFAQe7foPxD1wcedwAZb92319WcFK+j+dlH1bZkqzQ0OAacAOBs5FThNTM6DwHVzFtgMVo3bCyySE9v1s3XRmN8E0TKBBn4560Tp1YaJOtwfK9jSHiMzjl4Ou1f0azFKbUgPiQax96FmMw/BqereyseL02MvUjL7Lvp8R9a8VINQMZ0RnmQ+S//lbeXMLKLt8SHJ8weV";

    private TFObjectDetector tfod;

    private enum Position {
        LEFT,
        MIDDLE,
        RIGHT
    }

    private SkyStoneRobot robot = new SkyStoneRobot();
    private MecanumDrive drivetrain;
    private Lift lift;
    private Grabber grabber;
    private Plow plow;
    private FoundationHook foundationHook;

    @Override
    public void runOpMode(){
        robot.initHardware(hardwareMap);
        drivetrain = robot.getDrivetrain();
        lift = new Lift(hardwareMap);
        grabber = new Grabber(hardwareMap);
        grabber.close();
        plow = new Plow(hardwareMap);
        foundationHook = new FoundationHook(hardwareMap);
        long adjustDist = 0;
        boolean isLeft = false;

        initTensorFlow();

        waitForStart();

        switch (getSkyStonePosition()) {
            case LEFT:
                moveDistanceCm(MecanumDrive.Direction.LEFT, 18);
                isLeft = true;
                break;
            case MIDDLE:
                moveDistanceCm(MecanumDrive.Direction.RIGHT,20);
                adjustDist = -300;
                break;
            case RIGHT:
                moveDistanceCm(MecanumDrive.Direction.RIGHT, 39);
                adjustDist = -600;
                break;
        }

        //drive forward
        lift.raise();
        sleep(400);
        lift.stop();
        plow.raise();
        sleep(300);
        plow.stop();
        moveDistanceCm(MecanumDrive.Direction.UP,68);
        moveSlowDistance(MecanumDrive.Direction.UP, 15, 0.4);
        sleep(100);

        //grab block
        grabber.extend();
        sleep(2000);
        lift.lower();
        sleep(400);
        lift.stop();
        grabber.clamp();
        sleep(600);

        //drive under skybridge to foundation
        moveDistanceCm(MecanumDrive.Direction.DOWN,36);
        drivetrain.complexDrive(6.205,1,0);
        sleep(4500+adjustDist);
        lift.raise();
        sleep(250);
        lift.stop();
        if (isLeft){
            drivetrain.complexDrive(0,0,-1);
            sleepDistance(11);
        }
        drivetrain.complexDrive(6.205,1,0);
        sleep(650);

        //place block on foundation
        drivetrain.complexDrive(0,0,1);
        sleepDistance(48);
        plow.lower();
        /*sleep(800);
        plow.stop();*/
        moveDistanceCm(MecanumDrive.Direction.UP, 20);
        lift.lower();
        sleep(250);
        lift.stop();
        plow.stop();
        grabber.extend();
        sleep(200);
        lift.raise();
        sleep(500);
        lift.stop();

        //park under bridge
        moveDistanceCm(MecanumDrive.Direction.DOWN, 20);
        lift.lower();
        sleep(400);
        lift.stop();
        grabber.close();
        sleep(400);
        moveDistanceCm(MecanumDrive.Direction.RIGHT,130);


    }

    private void sleepDistance(double distance) {
        double voltage = hardwareMap.voltageSensor.get("Expansion Hub 2").getVoltage();
        long sleepTime = TimeOffsetVoltage.calculateDistance(robot, voltage, distance);
        telemetry.addData("OpMode", "sleep time: " + sleepTime);
        telemetry.update();
        sleep(sleepTime);
        robot.stopMoving();
    }

    private void moveDistanceCm(MecanumDrive.Direction direction, double distance) {
        if (distance <= 0) return;
        drivetrain.complexDrive(direction.angle(), 1, 0);
        sleepDistance(distance);
    }

    private void moveSlowDistance(MecanumDrive.Direction direction, double distance, double velocity) {
        if (distance <= 0) return;
        drivetrain.complexDrive(direction.angle(), velocity, 0);
        sleepDistance(distance);
    }

    private void initTensorFlow() {
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam 1");

        //  Instantiate the Vuforia engine
        VuforiaLocalizer vuforia = ClassFactory.getInstance().createVuforia(parameters);
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence = 0.8;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, STONE_LABEL, SKYSTONE_LABEL);

        tfod.activate();
    }

    private Position getSkyStonePosition() {
        long startTime = System.currentTimeMillis();
        boolean running = true;
        while (running) {
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                for (Recognition recognition : updatedRecognitions) {
                    if (!recognition.getLabel().equals(SKYSTONE_LABEL)) continue;

                    float recogCenter = (recognition.getLeft() + recognition.getRight()) / 2;
                    float positionRatio = recogCenter / recognition.getImageWidth();

                    if (positionRatio >= 0.66) {
                        return Position.RIGHT;
                    }

                    else if (positionRatio <= 0.33) {
                        return Position.LEFT;
                    }

                    else{
                        running = false;
                    }
                }
            }
            /*if (System.currentTimeMillis()-startTime>=3000){
                running = false;
            }*/
        }
        return Position.MIDDLE;
    }

}
