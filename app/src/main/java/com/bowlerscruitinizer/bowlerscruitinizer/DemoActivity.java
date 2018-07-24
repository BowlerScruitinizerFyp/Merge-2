package com.bowlerscruitinizer.bowlerscruitinizer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.goodFeaturesToTrack;
import static org.opencv.imgproc.Imgproc.line;

public class DemoActivity extends AppCompatActivity {


    //initialization

    String TAG = "MainActivity";
    ImageView imageView;
    Uri ImageUri;
    Bitmap imageBitMap;
    Bitmap grayBitMap;
    Bitmap cannyMap;
    Bitmap blurMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);


        //check the loading of opencv

        if (!OpenCVLoader.initDebug()) {

            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }

        init();

    }

    //initialization

    private void init() {


        imageView = (ImageView) findViewById(R.id.image_view);


    }


    //to open the phone gallery
    public void OpenGallery(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, 100);

    }

    //after image is selected from phone gallery


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            ImageUri = data.getData();

            try {

                imageBitMap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), ImageUri);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            imageView.setImageBitmap(imageBitMap);

        }
    }

    public void cord(View view) {

        int width = imageBitMap.getWidth();
        int height = imageBitMap.getHeight();

        //Mat initialization

        Mat Rgba = new Mat();
        Mat gray = new Mat();
        Mat cannyEdges = new Mat();
        Mat hierarchy = new Mat();


        //Poins saved in a list

        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>(); //A list to store all the contours
        Bitmap currentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);


        BitmapFactory.Options o = new BitmapFactory.Options();

        o.inDither = false;
        o.inSampleSize = 4;


        grayBitMap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);


        //convert bitmap to Mat

        Utils.bitmapToMat(imageBitMap, Rgba);

        //To convert in to gray

        Imgproc.cvtColor(Rgba, gray, Imgproc.COLOR_RGB2GRAY);

        Imgproc.Canny(gray, cannyEdges, 10, 100);


        //finding contours
        Imgproc.findContours(cannyEdges, contourList, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        //Drawing contours on a new image
        Mat contours = new Mat();
        contours.create(cannyEdges.rows(), cannyEdges.cols(), CvType.CV_8UC3);
        Random r = new Random();
        for (int i = 0; i < contourList.size(); i++) {
            Imgproc.drawContours(contours, contourList, i, new Scalar(r.nextInt(255), r.nextInt(255), r.nextInt(255)), -1);

        }


        MatOfPoint corners = new MatOfPoint();
        int maxCorners;
        double qualityLevel;
        Mat mask = new Mat();
        int blockSize;
        TextView tx1 = (TextView) findViewById(R.id.textView);


        // Getting good features

        goodFeaturesToTrack(gray, corners, 400, 0.1, 50);


        //Getting cordinates

        Point[] cornerpoints = corners.toArray();

        for (Point points : cornerpoints) {
            circle(Rgba, points, 1, new Scalar(100, 100, 100), 2);

            //for line

            for (int i = 0 ; i < cornerpoints.length ; i++) {

                for(int j = 1 ; j < cornerpoints.length ; j++) {


                    // if(cornerpoints[i].x == 1 && cornerpoints[i].x == 8 && cornerpoints[i].y == 15 && cornerpoints[i].y == 22 && cornerpoints[i].x == 29 && cornerpoints[i].y == 53  && cornerpoints[i].x == 37 && cornerpoints[i].y == 45 ) {

                    line(Rgba, cornerpoints[i], cornerpoints[j], new Scalar(0, 255, 0), 2);
                    //}

                }

            }
            //for line



            //for (int i = 0 ; i < cornerpoints.length ; i++) {


            double x1 = cornerpoints[0].x;
            double y1 = cornerpoints[0].y;

            double x2 = cornerpoints[1].x;
            double y2 = cornerpoints[1].y;

            double x3 = cornerpoints[2].x;
            double y3 = cornerpoints[2].y;

            double x4 = cornerpoints[3].x;
            double y4 = cornerpoints[3].y;


            double m1 = y2-y1/x2-x1;
            //  double m2 = y4-y3/x4-x3;

            m1 = m1 - pow(m1,3)/3 + pow(m1,5)/5;
            m1 = ((int)(m1*180/3.14)) % 360; // Convert the angle in radians to degrees
            if(x1 < x2) m1+=180;
            if(m1 < 0) m1 = 360 + m1;


            //  double tangent = m2-m1/1-m1*m2;

            //   tx1.setText("tangent = " +m1);

            // }




            Double xx = points.x;
            Double yy = points.y;

            Double sine = yy / Math.sqrt(pow(xx, 2) + pow(yy, 2));
            Double cos = xx / Math.sqrt(pow(xx, 2) + pow(yy, 2));
            Double tan = yy / xx;
            double theta = Math.atan2(yy , xx) * (180/3.14);

            tx1.setText("x = " + Double.toString(xx) + "\n y = " + Double.toString(yy) + "\n Sine= " + Double.toString(sine) + "\n Cose= " + Double.toString(cos) + "\n Tan = "
                    + Double.toString(tan)+"\n theta = "+Double.toString(theta));


//            for (int i = 0; i <= cornerpoints.length; i++) {

//            String a = null;
//
//
//
//
//                 a = cornerpoints[1].toString();
//
//                tx1.setText("cordinates "+a+"Sine= "+Double.toString(sine)+"Cose= "+Double.toString(cos));
//
//            }


            // For greater than 1 sine

            if(tan >= 1 ) {

                String angle = "0." + Long.toString(Math.round(tan));
                float a = Float.parseFloat(angle);
                Double theta1 = Math.atan(a)*(180/3.14);



                tx1.setText("x = " + Double.toString(xx) + "\n y = " + Double.toString(yy) + "\n Sine= " + Double.toString(sine) + "\n Cose= " + Double.toString(cos) + "\n Tan = "
                        + Double.toString(tan)+"\n Theta = "+theta1);

            }

        }


        //Converting Mat back to Bitmap
        Utils.matToBitmap(Rgba, currentBitmap);
        imageView.setImageBitmap(currentBitmap);

    }


    //to view only the red part of the image

    public void red1(View view) {

        int width = imageBitMap.getWidth();
        int height = imageBitMap.getHeight();

        //initializing mat

        Mat Rgba = new Mat();
        Mat hsv = new Mat();
        Mat gray = new Mat();

        BitmapFactory.Options o = new BitmapFactory.Options();

        o.inDither = false;
        o.inSampleSize = 4;

        grayBitMap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        //convert bitmap to Mat

        Utils.bitmapToMat(imageBitMap, Rgba);


        //getting HSV image for red colour

        Imgproc.cvtColor(Rgba,hsv , Imgproc.COLOR_BGR2HSV);


        // range of red colour

        Scalar lower_red = new Scalar(110,50,50);
        Scalar upper_red = new Scalar(130,255,255);

        Mat red = new Mat(); // mask

        Core.inRange(hsv, lower_red, upper_red , red);

        //getting the red part of image

        Mat redSkin = new Mat(red.rows(), red.cols(), CvType.CV_8U, new Scalar(3));

        Core.bitwise_and(Rgba,Rgba, redSkin , red);

        Bitmap currentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        MatOfPoint corners = new MatOfPoint();

        Imgproc.cvtColor(redSkin, gray, Imgproc.COLOR_RGB2GRAY);

        //only getting 3 points that is the elbow , shoulder and wrist

        goodFeaturesToTrack(gray, corners, 3, 0.1, 50);


        TextView tx1 = (TextView) findViewById(R.id.textView);


        Point[] cornerpoints = corners.toArray();

        for (Point points : cornerpoints) {
            circle(redSkin, points, 1, new Scalar(100, 100, 100), 6);

            //for line

            for (int i = 0 ; i < cornerpoints.length ; i++) {

                for(int j = 1 ; j < cornerpoints.length ; j++) {


                    // if(cornerpoints[i].x == 1 && cornerpoints[i].x == 8 && cornerpoints[i].y == 15 && cornerpoints[i].y == 22 && cornerpoints[i].x == 29 && cornerpoints[i].y == 53  && cornerpoints[i].x == 37 && cornerpoints[i].y == 45 ) {

                    line(Rgba, cornerpoints[i], cornerpoints[j], new Scalar(0, 255, 0), 2);
                    //}

                }

            }
            //for line



            //getting the coordinates

            double x1 = cornerpoints[0].x;
            double y1 = cornerpoints[0].y;

            double x2 = cornerpoints[1].x;
            double y2 = cornerpoints[1].y;

            double x3 = cornerpoints[2].x;
            double y3 = cornerpoints[2].y;


            //slope method

            double m1 = x2-x1/y2-y1;
//                 double m2 = y4-y3/x4-x3;

            m1 = m1 - pow(m1,3)/3 + pow(m1,5)/5;
            m1 = ((int)(m1*180/3.14)) % 360; // Convert the angle in radians to degrees
            if(x1 < x2) m1+=180;
            if(m1 < 0) m1 = 360 + m1;


//                 double tangent = m2-m1/1-m1*m2;


            //distance method

            double dis1 = sqrt(pow(x2-x1 , 2) + pow(y2-y1 , 2));   //Distance = √(x2−x1)2+(y2−y1)2

            double dis2 = sqrt(pow(x3-x2 , 2) + pow(y3-y2 , 2));   //Distance = √(x2−x1)2+(y2−y1)2

            double sin = dis1 / dis2 ; // Sinθ = Perpendicular/hypotenuse

            double theta = Math.asin(sin) * (180/3.14);


            //   tx1.setText("Distance 1 = " +dis1+ "\n Distance 2 ="+dis2+ "\n sine ="+sin + "\n theta = " +theta);


            if(sin >= 1){

                String angle = "0." + Long.toString(Math.round(sin));
                float a = Float.parseFloat(angle);
                Double theta1 = Math.asin(a)*(180/3.14);

                tx1.setText("Distance 1 = " +dis1+ "\n Distance 2 ="+dis2+ "\n sine ="+sin + "\n theta = " +theta1);


            }

            else{

                tx1.setText("Distance 1 = " +dis1+ "\n Distance 2 ="+dis2+ "\n sine ="+sin + "\n theta = " +theta);
            }
        }


        //Converting Mat back to Bitmap
        Utils.matToBitmap(redSkin, currentBitmap);
        imageView.setImageBitmap(currentBitmap);


    }
}
