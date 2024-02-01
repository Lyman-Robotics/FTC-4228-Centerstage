package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "drbak", group = "Driver Controlled")
public class tedt extends LinearOpMode {

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
      robot.setDrivePower(-.5,-.5,-.5,-.5);
      sleep(1000);
      robot.setDrivePower(0,0,0,0);
      sleep(9999999);
    }
  }
}
