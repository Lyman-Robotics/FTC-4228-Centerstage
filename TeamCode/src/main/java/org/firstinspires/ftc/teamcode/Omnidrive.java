package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Omnirive", group = "Driver Controlled")
public class Omnidrive extends LinearOpMode {

  @Override
  public void runOpMode() {
    // Initialize the hardware variables.
    RobotClass robot = new RobotClass(hardwareMap, false);

    // ! Runs upon initialization
    telemetry.addData("Status", "Initialized");
    telemetry.update();

    // Initialize drive variables
    float vertical;
    float horizontal;
    float pivot;
    double speedScalar=1.0;
    boolean slowMode = false;
    boolean clawClosed = false;
    boolean holdingObject = false;

    robot.SlideMotor.setPower(0.4);

    // ! Runs until the end of the match after play is pressed
    waitForStart();
    robot.timeElapsed.reset();

    while (opModeIsActive()) {
      double max;

      vertical = gamepad1.left_stick_y;
      // horizontal = -gamepad1.left_stick_x; for when we actually have omni
      horizontal = 0;
      pivot = -gamepad1.right_stick_x;

      if (gamepad1.right_bumper) {
        slowMode = true;
      } else if (gamepad1.left_bumper) {
        slowMode = false;
      }else{
            speedScalar = slowMode ? 0.2 : 0.5; // used to be .5 for fast and before that .65
      }
      if (gamepad1.left_trigger > 0)
      {
        speedScalar = 1;
      }

      if (gamepad1.dpad_left)
      {
        holdingObject = true;
      }
      else if (gamepad1.dpad_right)
      {
        holdingObject = false;
      }



      
      double FRPower = ((-pivot + (vertical - horizontal)) * speedScalar);
      double BRPower = ((-pivot + vertical + horizontal) * speedScalar);
      double FLPower = ((pivot + vertical + horizontal) * speedScalar);
      double BLPower = ((pivot + (vertical - horizontal)) * speedScalar);

      // ? Nerd stuff to make sure the robot doesn't go too fast
      max = Math.max(Math.abs(FLPower), Math.abs(FRPower));
      max = Math.max(max, Math.abs(BLPower));
      max = Math.max(max, Math.abs(BRPower));

      if (max > 1.0) {
        FLPower /= max;
        FRPower /= max;
        BLPower /= max;
        BRPower /= max;
      }
      // ? Nerd stuff ends here

      robot.setDrivePower(FLPower, FRPower, BLPower, BRPower);

      // ** Actual Servo
      if (gamepad1.x) {
        // clawClosed = false;
        robot.Claw.setPower(-0.4);
      } else if (gamepad1.y) {
        // clawClosed = true;
        robot.Claw.setPower(0.4);
      } else {
        robot.Claw.setPower(0);
      }
      if (holdingObject)
      {
        robot.Claw.setPower(-0.4);
      }
      robot.ClawServo.setPosition(
          clawClosed ? robot.servoClosePos : robot.servoOpenPos);

      //** This is for testing purposes
      // if (gamepad2.x) {
      // robot.ClawServo.setPosition(robot.ClawServo.getPosition() + 0.001);
      // } else if (gamepad2.y) {
      // robot.ClawServo.setPosition(robot.ClawServo.getPosition() - 0.001);
      // }

      // Slide on gamepad
      if (gamepad1.a) {
        robot.SlideMotor.setPower(robot.slidePowerUp);
      } else if (gamepad1.b && robot.SlideTouchSensor.getState()) {
        robot.SlideMotor.setPower(robot.slidePowerDown);
      } else {
        robot.SlideMotor.setPower(0);
      }

      // Show the elapsed game time and wheel power.
      telemetry.addData("Claw Servo Position", robot.ClawServo.getPosition());
      telemetry.addData("Slow Mode", slowMode);
      telemetry.addData(
          "Slide Touch Sensor",
          !(robot.SlideTouchSensor.getState()));
      telemetry.addData("Elapsed Time", robot.timeElapsed.toString());

      telemetry.addData("Front left/Right", "%4.2f, %4.2f", FLPower, FRPower);
      telemetry.addData("Back  left/Right", "%4.2f, %4.2f", BLPower, BRPower);
      telemetry.update();
    }
  }
}
