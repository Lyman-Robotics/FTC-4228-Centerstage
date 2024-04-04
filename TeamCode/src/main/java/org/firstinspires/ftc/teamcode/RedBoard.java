package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

// import org.openftc.easyopencv.OpenCvCamera;
// import org.openftc.easyopencv.OpenCvCameraFactory;
// import org.openftc.easyopencv.OpenCvCameraRotation;

@Autonomous(name = "Red Board-Side", group = "Autonomous")
// @Disabled
public class RedBoard extends LinearOpMode {

  @Override
  public void runOpMode() {
    // Initialize the hardware variables.
    RobotClass robot = new RobotClass(hardwareMap, true);

    // ! Runs upon initialization
    telemetry.addData("Status", "Initialized");
    telemetry.update();

    while (!isStarted()) {
      telemetry.addData("Position", robot.sleeveDetection.getPosition());
      telemetry.addData("PRELOAD PIXEL", "PRELOAD IT DUMBASS");
      telemetry.update();
      robot.position = robot.sleeveDetection.getPosition();
    }

    // ! Runs until the end of the match after play is pressed
    waitForStart();
    robot.timeElapsed.reset();

    while (opModeIsActive()) {
      robot.ArmFlipper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

      robot.resetDrive();

      robot.encoderDrive(0.4, -797, -619, -796, -927);
      robot.encoderDrive(0.4, -751, 547, 745, -772);
      robot.encoderDrive(0.4, -500, -378, -553, -494);

      robot.SlideRaiser.setTargetPosition(6000);
      robot.SlideRaiser2.setTargetPosition(6000);
      robot.SlideRaiser.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      robot.SlideRaiser2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      robot.ArmFlipper.setTargetPosition(-240);
      robot.ArmFlipper.setMode(DcMotor.RunMode.RUN_TO_POSITION);
      robot.SlideRaiser.setPower(1);
      robot.SlideRaiser2.setPower(1);
      robot.ArmFlipper.setPower(1);
      robot.SlideRaiser.setTargetPosition(12700);
      robot.SlideRaiser2.setTargetPosition(12700);
      sleep(5000);
      robot.encoderDrive(0.4, -498, -337, -508, -514);
      robot.ArmIntakeServo.setPower(-1);
      sleep(11000);
      robot.ArmIntakeServo.setPower(0);

      sleep(9999999);
      // ! New Stuff
    }
  }

  public void realSleep(int n, String customAdd, RobotClass robot) { // better sleep method, dont use other crappy
    // stuffs
    telemetry.addData("Status", customAdd);
    telemetry.addData("Elapsed Time", robot.timeElapsed.toString());

    sleep(n);

    // telemetry.addData("Front left/Right", "%4.2f, %4.2f", robot.FLPower,
    // robot.FRPower);
    // telemetry.addData("Back left/Right", "%4.2f, %4.2f", robot.BLPower,
    // robot.BRPower);
    telemetry.update();
  }
}
