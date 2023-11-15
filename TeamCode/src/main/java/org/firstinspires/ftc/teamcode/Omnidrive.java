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
    boolean cc = false;

    // Season specific variables
    boolean clawClosed = false;

    // ! Runs until the end of the match after play is pressed
    waitForStart();
    robot.timeElapsed.reset();

    while (opModeIsActive()) {
      double max;

      vertical = gamepad1.left_stick_y;
      horizontal = -gamepad1.left_stick_x; //for when we actually have omni
      pivot = -gamepad1.right_stick_x;

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

       // Close Claw
      //  if (gamepad2.dpad_left)
      //  {
      //   cc = true;
      //  }
      //  if (gamepad2.dpad_right)
      //  {
      //   cc = false;
      //  }
      //  if (cc) {
      //    robot.PixelClaw.setPosition(robot.closeClawPos);
      //  } else if (!cc) {
      //    robot.PixelClaw.setPosition(robot.openClawPos);
      //  }

      // Arm Flipper
      if (gamepad2.dpad_up) {
        robot.ArmFlipper.setPower(0.5);
      } else if (gamepad2.dpad_down) {
        robot.ArmFlipper.setPower(-0.5);
      } else {
        robot.ArmFlipper.setPower(0);
      }

      // // Raise Slides
      // if (gamepad2.right_trigger > 0) {
      //   robot.SlideRaiser.setPower(gamepad2.right_trigger * robot.slideSpeedScalar);
      // } else if (gamepad2.left_trigger > 0) {
      //   robot.SlideRaiser.setPower(gamepad2.left_trigger * robot.slideSpeedScalar * -1);
      // } else {
      //   robot.SlideRaiser.setPower(0);
      // }

      // Intake motor
      if (gamepad2.a) {
        robot.Intake.setPower(-0.6);
      } else if (gamepad2.b) {
        robot.Intake.setPower(1);
      } else {
        robot.Intake.setPower(0);
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
      //   robot.PixelClaw.setPosition(robot.PixelClaw.getPosition() + 0.0001);
      // } else if (gamepad2.y) {
      //   robot.PixelClaw.setPosition(robot.PixelClaw.getPosition() - 0.0001);
      // }
      // telemetry.addData("Servo Pos", robot.PixelClaw.getPosition());

      // Telemetry
      telemetry.addData("Status", "Initialized");
      telemetry.update();
    }
  }
}
