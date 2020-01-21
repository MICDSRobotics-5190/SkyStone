package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@TeleOp(name = "TensorFlow Test")
public class TensorFlowTest extends LinearOpMode {
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

    @Override
    public void runOpMode() {
        initTensorFlow();

        waitForStart();

        while (opModeIsActive()) {
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                for (Recognition recognition : updatedRecognitions) {
                    if (!recognition.getLabel().equals(SKYSTONE_LABEL)) continue;

                    float recogCenter = (recognition.getLeft() + recognition.getRight()) / 2;
                    float positionRatio = recogCenter / recognition.getImageWidth();

                    Position position = Position.MIDDLE;
                    if (positionRatio >= 0.66) {
                        position = Position.RIGHT;
                    } else if (positionRatio <= 0.33) {
                        position = Position.LEFT;
                    }

                    telemetry.addData("Position", position.toString());
                    telemetry.update();
                }
            }
        }
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
}
