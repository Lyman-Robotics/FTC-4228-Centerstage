package org.firstinspires.ftc.teamcode;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

public class SleeveDetection extends OpenCvPipeline {

  /*
   * YELLOW = Parking Left
   * CYAN = Parking Middle
   * MAGENTA = Parking Right
   */

  // TOPLEFT anchor point for the bounding box
  private static Point SLEEVE_TOPLEFT_ANCHOR_POINT1 = new Point(0, 0);
  private static Point SLEEVE_TOPLEFT_ANCHOR_POINT2 = new Point(0, 0);
  private static Point SLEEVE_TOPLEFT_ANCHOR_POINT3 = new Point(0, 0);

  // Width and height for the bounding box
  public static int REGION_WIDTH = 30;
  public static int REGION_HEIGHT = 50;

  // Color definitions
  private final Scalar CYAN = new Scalar(
    0,
    255,
    255
  );

  // Anchor point definitions LEFT
  Point sleeve_pointA = new Point(
    SLEEVE_TOPLEFT_ANCHOR_POINT1.x,
    SLEEVE_TOPLEFT_ANCHOR_POINT1.y
  );
  Point sleeve_pointB = new Point(
    SLEEVE_TOPLEFT_ANCHOR_POINT1.x + REGION_WIDTH,
    SLEEVE_TOPLEFT_ANCHOR_POINT1.y + REGION_HEIGHT
  );

  // Anchor point definitions MIDDLE
  Point sleeve_pointC = new Point(
    SLEEVE_TOPLEFT_ANCHOR_POINT2.x,
    SLEEVE_TOPLEFT_ANCHOR_POINT2.y
  );
  Point sleeve_pointD = new Point(
    SLEEVE_TOPLEFT_ANCHOR_POINT2.x + REGION_WIDTH,
    SLEEVE_TOPLEFT_ANCHOR_POINT2.y + REGION_HEIGHT
  );

  // Anchor point definitions RIGHT
  Point sleeve_pointE = new Point(
    SLEEVE_TOPLEFT_ANCHOR_POINT3.x,
    SLEEVE_TOPLEFT_ANCHOR_POINT3.y
  );
  Point sleeve_pointF = new Point(
    SLEEVE_TOPLEFT_ANCHOR_POINT3.x + REGION_WIDTH,
    SLEEVE_TOPLEFT_ANCHOR_POINT3.y + REGION_HEIGHT
  );

  // Running variable storing the parking position
  private String position = "Left";

  @Override
  public Mat processFrame(Mat input) {
    // Get the submat frame for each rectangle and sum all the values
    Mat areaMatLeft = input.submat(new Rect(sleeve_pointA, sleeve_pointB));
    Mat areaMatMiddle = input.submat(new Rect(sleeve_pointC, sleeve_pointD));
    Mat areaMatRight = input.submat(new Rect(sleeve_pointE, sleeve_pointF));

    Scalar sumColorsLeft = Core.sumElems(areaMatLeft);
    Scalar sumColorsMiddle = Core.sumElems(areaMatMiddle);
    Scalar sumColorsRight = Core.sumElems(areaMatRight);

    double maxColorValue = sumColorsLeft.val[0];

    if (sumColorsMiddle.val[0] > maxColorValue) {
      maxColorValue = sumColorsMiddle.val[0];
    }

    if (sumColorsRight.val[0] > maxColorValue) {
      maxColorValue = sumColorsRight.val[0];
    }

    if (maxColorValue == sumColorsLeft.val[0]) {
      position = "Left";
      Imgproc.rectangle(input, sleeve_pointA, sleeve_pointB, CYAN, 2);
    } else if (maxColorValue == sumColorsMiddle.val[0]) {
      position = "Middle";
      Imgproc.rectangle(input, sleeve_pointC, sleeve_pointD, CYAN, 2);
    } else if (maxColorValue == sumColorsRight.val[0]) {
      position = "Right";
      Imgproc.rectangle(input, sleeve_pointE, sleeve_pointF, CYAN, 2);
    }

    areaMatLeft.release();
    areaMatMiddle.release();
    areaMatRight.release();
    return input;
  }

  // Returns an enum being the current position where the robot will park
  public String getPosition() {
    return position;
  }
}
