package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Servo;
// import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
// import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
// import org.firstinspires.ftc.teamcode.common.powerplay.SleeveDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

// import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

// ! npx prettier --write "TeamCode\src\main\java\org\firstinspires\ftc\teamcode/*.java"

@Autonomous(name = "DONT TOUCH", group = "CLASS")
@Disabled
public class RobotClass extends LinearOpMode {

  // ** Declare OpMode members
  public DcMotor FLDrive;
  public DcMotor FRDrive;
  public DcMotor BLDrive;
  public DcMotor BRDrive;
  public DcMotor Claw;
  public DcMotor SlideMotor;
  public Servo ClawServo;
  public DigitalChannel SlideTouchSensor;

  public OpenCvCamera camera;
  public String webcamName = "Webcam 1";
  public String position;
  public SleeveDetection sleeveDetection;

  public float omniRightVal = (float) (Math.PI / 2.0);
  public float omniLeftVal = (float) (3.0 * (Math.PI / 2.0));
  public double slidePowerUp = 0.4; 
  public double slidePowerDown = -slidePowerUp;
  public float servoOpenPos = (float) 0.7477777777;
  public float servoClosePos = (float) 0.338333333;

  public int TickCounts = 1120;
  public double circumference = 3.1415926535 * 3.1496063; // 8cm lappy 10cm scrappy 3.149 is the 8cm to in
  public double gearReduction = .0833333; // used to be .6 this is probably wrong :(
  public double countsPerInch = (TickCounts * gearReduction) / circumference; // gear reduction is < 1 if geared up

  HardwareMap hwMap = null;
  public ElapsedTime timeElapsed = new ElapsedTime();

  public RobotClass(HardwareMap hwMap, Boolean initServo) {
    init(hwMap, initServo);
  }

  /* Initialize standard Hardware interfaces */
  public void init(HardwareMap hwMap, Boolean initServo) {
    // Save reference to Hardware map
    hwMap = hwMap;

    // Define and initialize motors
    FLDrive = hwMap.get(DcMotor.class, "FLDrive");
    FRDrive = hwMap.get(DcMotor.class, "FRDrive");
    BLDrive = hwMap.get(DcMotor.class, "BLDrive");
    BRDrive = hwMap.get(DcMotor.class, "BRDrive");
    Claw = hwMap.get(DcMotor.class, "Claw");

    SlideMotor = hwMap.get(DcMotor.class, "SlideMotor");
    ClawServo = hwMap.get(Servo.class, "ClawServo");
    SlideTouchSensor = hwMap.get(DigitalChannel.class, "SlideTouchSensor");
    // ? ServoPlaceholder = hwMap.get(Servo.class, "ServoPlaceholder");

    SlideTouchSensor.setMode(DigitalChannel.Mode.INPUT);

    // Make robot drive straight
    BRDrive.setDirection(DcMotor.Direction.REVERSE);
    BLDrive.setDirection(DcMotor.Direction.REVERSE);
    ClawServo.setDirection(Servo.Direction.REVERSE);

    FLDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    FRDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    BLDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    BRDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    SlideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    FLDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    FRDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    BLDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    BRDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    FLDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    BLDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    FRDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    BRDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

    // If init servo, set to init position
    if (initServo) {
      ClawServo.setPosition(servoOpenPos);
    }

    int cameraMonitorViewId = hwMap.appContext
        .getResources()
        .getIdentifier(
            "cameraMonitorViewId",
            "id",
            hwMap.appContext.getPackageName());
    camera = OpenCvCameraFactory
        .getInstance()
        .createWebcam(
            hwMap.get(WebcamName.class, webcamName),
            cameraMonitorViewId);
    sleeveDetection = new SleeveDetection();
    camera.setPipeline(sleeveDetection);

    camera.openCameraDeviceAsync(
        new OpenCvCamera.AsyncCameraOpenListener() {
          @Override
          public void onOpened() {
            camera.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
          }

          @Override
          public void onError(int errorCode) {
          }
        });

    position = sleeveDetection.getPosition();
  }

  public void setToEncoderMode() {
    FLDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    FRDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    BLDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    BRDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
  }

  public void setToPowerMode() {
    FLDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    FRDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    BLDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    BRDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
  }

  public void resetDrive() {
    FLDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    FRDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    BLDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    BRDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    FLDrive.setTargetPosition(0);
    FRDrive.setTargetPosition(0);
    BLDrive.setTargetPosition(0);
    BRDrive.setTargetPosition(0);

    FLDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    FRDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    BLDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    BRDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
  }

  public void runToPos() {
    // Runs to position set by setTargetPosition
    FLDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    FRDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    BLDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    BRDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
  }

  public void stopDrive() {
    FLDrive.setPower(0);
    FRDrive.setPower(0);
    BLDrive.setPower(0);
    BRDrive.setPower(0);
  }

  public void setDrivePower(double FL, double FR, double BL, double BR) {
    FLDrive.setPower(FL);
    FRDrive.setPower(FR);
    BLDrive.setPower(BL);
    BRDrive.setPower(BR);
  }

  // Radians
  public void omnidrive(double power, double angle, double rotation) {
    double FL = power * Math.sin(angle + Math.PI / 4) + rotation;
    double FR = power * Math.cos(angle + Math.PI / 4) - rotation;
    double BL = power * Math.cos(angle + Math.PI / 4) + rotation;
    double BR = power * Math.sin(angle + Math.PI / 4) - rotation;

    FLDrive.setPower(FL);
    FRDrive.setPower(FR);
    BLDrive.setPower(BL);
    BRDrive.setPower(BR);
  }

  public void setPos(int FLPos, int FRPos, int BLPos, int BRPos) {
    FLDrive.setTargetPosition((int) FLPos);
    FRDrive.setTargetPosition((int) FRPos);
    BLDrive.setTargetPosition((int) BLPos);
    BRDrive.setTargetPosition((int) BRPos);
  }

  public void encoderDrive(
      double power,
      int FLPos,
      int FRPos,
      int BLPos,
      int BRPos) {
    setPos(
        FLDrive.getCurrentPosition() + FLPos,
        FRDrive.getCurrentPosition() + FRPos,
        BLDrive.getCurrentPosition() + BLPos,
        BRDrive.getCurrentPosition() + BRPos);
    runToPos();
    setDrivePower(power, power, power, power);
    while (FLDrive.isBusy() ||
        FRDrive.isBusy() &&
            BLDrive.isBusy()
        ||
        BRDrive.isBusy()) {
    }
    // sleep(20);
    stopDrive();
    // sleep(25);
  }

  public void encoderDrive(
      double power,
      int FLPos,
      int FRPos,
      int BLPos,
      int BRPos,
      double slideSpeed,
      double slideTime) {
    double startTime = timeElapsed.milliseconds();
    setPos(
        FLDrive.getCurrentPosition() + FLPos,
        FRDrive.getCurrentPosition() + FRPos,
        BLDrive.getCurrentPosition() + BLPos,
        BRDrive.getCurrentPosition() + BRPos);
    runToPos();
    setDrivePower(power, power, power, power);
    SlideMotor.setPower(slideSpeed);
    while (FLDrive.isBusy() ||
        FRDrive.isBusy() &&
            BLDrive.isBusy()
        ||
        BRDrive.isBusy()) {
      if (timeElapsed.milliseconds() >= startTime + slideTime) {
        SlideMotor.setPower(0);
      }
    }
    SlideMotor.setPower(0);
    sleep(20); // for wobble ending porpuses
    stopDrive();
    while (timeElapsed.milliseconds() < startTime + slideTime) {
      SlideMotor.setPower(slideSpeed);
    }
    SlideMotor.setPower(0);
  }

  // ! DONT TOUCH THIS I NEEDED THIS FOR OPMODE FUNCTIONS
  @Override
  public void runOpMode() {
    sleep(999999);
  }
}
