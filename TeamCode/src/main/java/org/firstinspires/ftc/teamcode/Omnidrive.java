package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Omnidrive", group = "Driver Controlled")
public class Omnidrive extends LinearOpMode {

  @Override
  public void runOpMode() {
    // Initialize the hardware variables.
    // ? Boolean is init servo
    RobotClass robot = new RobotClass(hardwareMap, false);

    // ! Runs upon initialization
    telemetry.addData("Status", "Initialized");
    telemetry.update();

    // Initialize drive variables
    float vertical;
    float horizontal;
    float pivot;
    double speedScalar = 1.0;
    boolean slowMode = false;
    //true- single button false- trigger
    boolean singleIntakeSelect = false;
    // Season specific variables
    boolean clawClosed = false;

    // ! Runs until the end of the match after play is pressed
    waitForStart();
    robot.timeElapsed.reset();

    while (opModeIsActive()) {
      double max;

      vertical = gamepad1.left_stick_y;
      horizontal = -gamepad1.right_stick_x; // for when we actually have omni
      pivot = -gamepad1.left_stick_x;

      // Speed Changer
      if (gamepad1.right_bumper) {
        slowMode = true;
      } else if (gamepad1.left_bumper) {
        slowMode = false;
      } else {
        speedScalar = slowMode ? 0.5 : 1; // used to be .5 for fast and before that .65
      }

      // Emergency speed mode
      if (gamepad1.left_trigger > 0) {
        speedScalar = 1;
      }

      // Arm Flipper
      if (gamepad2.left_stick_y != 0 && !singleIntakeSelect) {
        robot.ArmFlipper.setPower(gamepad2.left_stick_y);
      } else if (!singleIntakeSelect){
        robot.ArmFlipper.setPower(0);
      }

      // Intake motor
      if (gamepad2.right_trigger > 0) {
        robot.Intake.setPower(-1);
      } else if (gamepad2.left_trigger > 0) {
        robot.Intake.setPower(1);
      } else {
        robot.Intake.setPower(0);
      }

      // Arm intake motor
      if (gamepad2.right_bumper) {
        robot.ArmIntakeServo.setPower(-1);
      } else if (gamepad2.left_bumper) {
        robot.ArmIntakeServo.setPower(1);
      } else {
        robot.ArmIntakeServo.setPower(0);
      }

      // Raise Slides
      if (gamepad2.a) {
        robot.SlideRaiser.setPower(1);
        robot.SlideRaiser2.setPower(1);
      } else if (gamepad2.b) {
        robot.SlideRaiser.setPower(-1);
        robot.SlideRaiser2.setPower(-1);
      } else {
        robot.SlideRaiser.setPower(0);
        robot.SlideRaiser2.setPower(0);
      }

      // Airplane Servo
      if (gamepad2.x) {
        robot.AirplaneServo.setPosition(-0.5);
      } else if (gamepad2.y) {
        robot.AirplaneServo.setPosition(0);
      }

      if (gamepad2.start)
      {
        robot.ArmFlipper.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                singleIntakeSelect = true;

        robot.ArmFlipper.setTargetPosition(-248);
        robot.ArmFlipper.setPower(1);
      }
      if (gamepad2.back)
      {
        robot.ArmFlipper.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                singleIntakeSelect = true;
        robot.ArmFlipper.setTargetPosition(0);
        robot.ArmFlipper.setPower(0.5);
      }
      if (gamepad2.dpad_left)
      {
        robot.ArmFlipper.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        singleIntakeSelect = false;
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

      // // ? Servo position measurer
      // if (gamepad2.x) {
      // robot.PixelClaw.setPosition(robot.PixelClaw.getPosition() + 0.0001);
      // } else if (gamepad2.y) {
      // robot.PixelClaw.setPosition(robot.PixelClaw.getPosition() - 0.0001);
      // }
      // telemetry.addData("Servo Pos", robot.PixelClaw.getPosition());

      // Telemetry
      telemetry.addData(
        "Hex motor value",
        robot.ArmFlipper.getCurrentPosition()
      );
      telemetry.addData("Slide Value",robot.SlideRaiser.getCurrentPosition());
      telemetry.update();
    }
  }
}
