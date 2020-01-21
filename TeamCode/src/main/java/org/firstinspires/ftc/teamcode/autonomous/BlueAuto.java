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

@Autonomous(name = "Blue")
public class BlueAuto extends LinearOpMode {

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

        waitForStart();

        switch (getSkyStonePosition()) {
            case LEFT:
                moveDistanceCm(MecanumDrive.Direction.LEFT, 15);
                break;
            case RIGHT:
                moveDistanceCm(MecanumDrive.Direction.RIGHT, 15);
                break;
            // default position is middle, no need to move
        }

        //drive forward
        lift.raise();
        sleep(400);
        lift.stop();
        plow.raise();
        sleep(400);
        plow.stop();
        moveDistanceCm(MecanumDrive.Direction.UP,68);
        moveSlowDistance(MecanumDrive.Direction.UP, 15, 0.4);
        sleep(100);

        //grab block
        grabber.extend();
        sleep(2000);
        lift.lower();
        sleep(600);
        lift.stop();
        grabber.clamp();
        sleep(500);

        //drive under skybridge to foundation
        //moveDistanceCm(MecanumDrive.Direction.DOWN, 10);
        //grabber.clamp();
        //sleep(300);
        moveDistanceCm(MecanumDrive.Direction.DOWN,50);
        //drivetrain.complexDrive(0,0,1);
        //sleepDistance(32.5);
        moveDistanceCm(MecanumDrive.Direction.LEFT, 220);
        //drivetrain.complexDrive(0,0,-1);
        //sleepDistance(32.5);

        //place block on foundation
        lift.raise();
        sleep(200);
        lift.stop();
        plow.lower();
        sleep(400);
        plow.stop();
        moveDistanceCm(MecanumDrive.Direction.UP, 62);
        lift.lower();
        sleep(200);
        lift.stop();
        grabber.extend();
        sleep(200);
        lift.raise();
        sleep(500);
        lift.stop();

        //latch onto foundation
        moveDistanceCm(MecanumDrive.Direction.DOWN, 20);
        moveDistanceCm(MecanumDrive.Direction.LEFT, 30);
        drivetrain.complexDrive(0,0,1);
        sleepDistance(48);
        lift.lower();
        sleep(500);
        lift.stop();
        moveDistanceCm(MecanumDrive.Direction.DOWN, 22);
        foundationHook.lower();
        sleep(1000);
        grabber.close();
        sleep(1000);

        //drop off foundation at ?depot?
        moveSlowDistance(MecanumDrive.Direction.UP, 170, 0.5);
        foundationHook.raise();

        //drive to under the bridge
        moveDistanceCm(MecanumDrive.Direction.LEFT, 160);


    }

    private void sleepDistance(double distance) {
        double voltage = hardwareMap.voltageSensor.get("Expansion Hub 2").getVoltage();
        long sleepTime = TimeOffsetVoltage.calculateDistance(robot, voltage, distance);
        Log.d("OpMode", "sleep time: " + sleepTime);
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
        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        if (updatedRecognitions != null) {
            for (Recognition recognition : updatedRecognitions) {
                if (!recognition.getLabel().equals(SKYSTONE_LABEL)) continue;

                float recogCenter = (recognition.getLeft() + recognition.getRight()) / 2;
                float positionRatio = recogCenter / recognition.getImageWidth();

                if (positionRatio >= 0.66) {
                    return Position.RIGHT;
                }

                if (positionRatio <= 0.33) {
                    return Position.LEFT;
                }
            }
        }
        return Position.MIDDLE;
    }

}
