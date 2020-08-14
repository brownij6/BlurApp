

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.Core;
import org.opencv.videoio.Videoio;
import org.opencv.videoio.VideoWriter;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Desktop{
   
   public static void main(String[] args){
      
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            
      //open video file specified by command-line argument 0
      VideoCapture src = new VideoCapture(args[0]);
      
      CascadeClassifier cc = new CascadeClassifier();
      cc.load("./resources/haarcascade_frontalface_default.xml");
      
       if(!src.isOpened()){
         System.out.println("Failed to open video file.");
         System.exit(0);
      }
      
      Size frameSize = new Size((int) src.get(Videoio.CAP_PROP_FRAME_HEIGHT), (int) src.get(Videoio.CAP_PROP_FRAME_WIDTH));

      //output file specified by command-line argument 1
      VideoWriter vw = new VideoWriter(args[1], VideoWriter.fourcc('x', '2','6','4'),
                                       src.get(Videoio.CAP_PROP_FPS), frameSize, true);
      
      Mat mat = new Mat(); 
      int faceSize = 0;                
      while (src.read(mat)) {
         Mat tmp = new Mat();
         MatOfRect faces = new MatOfRect();
         Core.rotate(mat, tmp, Core.ROTATE_90_CLOCKWISE);
         Mat gray = new Mat();
         Imgproc.cvtColor(tmp, gray, Imgproc.COLOR_BGR2GRAY);
         Imgproc.equalizeHist(gray, gray);
         
        if (faceSize == 0){
   			int height = gray.rows();
   			if (Math.round(height * 0.2f) > 0){
   				faceSize = Math.round(height * 0.2f);
   			}
		  }
        
        cc.detectMultiScale(gray, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(faceSize, faceSize), new Size());
            
        Rect[] facesArray = faces.toArray();
		  for (int i = 0; i < facesArray.length; i++){
			   Imgproc.rectangle(tmp, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
         }
         
         vw.write(tmp);         
      }
      
      
   }
   
}