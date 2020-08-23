

import java.lang.*; 
import java.io.*; 
import java.util.Scanner;

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
           
      CascadeClassifier ccFront = new CascadeClassifier();
      ccFront.load("./resources/haarcascade_frontalface_default.xml");
      
      CascadeClassifier ccProf = new CascadeClassifier();
      ccProf.load("./resources/haarcascade_profileface.xml");
      
       if(!src.isOpened()){
         System.out.println("Failed to open video file.");
         System.exit(0);
      }
      
      Size frameSize = new Size((int) src.get(Videoio.CAP_PROP_FRAME_HEIGHT), (int) src.get(Videoio.CAP_PROP_FRAME_WIDTH));

      //output file specified by command-line argument 1
      VideoWriter vw = new VideoWriter("./tests/tmp.avi", VideoWriter.fourcc('x', '2','6','4'),
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
   			if (Math.round(height * 0.15f) > 0){
   				faceSize = Math.round(height * 0.15f);
   			}
		  }
        
        ccFront.detectMultiScale(gray, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(faceSize, faceSize), new Size());
            
        Rect[] facesArray = faces.toArray();
		  for (int i = 0; i < facesArray.length; i++){
			   Imgproc.rectangle(tmp, facesArray[i].tl(), facesArray[i].br(), new Scalar(255, 255, 255), -1);
         }
         
        ccProf.detectMultiScale(gray, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(faceSize, faceSize), new Size());
            
        facesArray = faces.toArray();
		  for (int i = 0; i < facesArray.length; i++){
			   Imgproc.rectangle(tmp, facesArray[i].tl(), facesArray[i].br(), new Scalar(255, 255, 255), -1);
         }
         
         vw.write(tmp);         
      }
      
      String[] commands = (System.getProperty("user.dir")+"\\ffmpeg-4.3.1-win64-static\\bin\\" + "ffmpeg -i "+ args[0]+" -vn -acodec copy .\\tests\\tmp.aac").split(" ");
      ProcessBuilder pb = new ProcessBuilder(commands);
      pb.redirectErrorStream(true);
      
      
      try{
         Process p = pb.start();
         InputStream is = p.getInputStream();
         OutputStream os = p.getOutputStream();
         os.close();
         is.close();
         p.waitFor();
         p.destroy();
      }catch(Exception e){
         System.out.println("exception: " + e);
      }
      
      commands = (System.getProperty("user.dir")+"\\ffmpeg-4.3.1-win64-static\\bin\\" + "ffmpeg -i .\\tests\\tmp.avi -i .\\tests\\tmp.aac -c:v copy -map 0:v:0 -map 1:a:0 -c:a aac -b:a 192k "+ args[1]).split(" ");
      pb = new ProcessBuilder(commands);
      pb.redirectErrorStream(true);
      
      
      try{
         Process p = pb.start();
         InputStream is = p.getInputStream();
         OutputStream os = p.getOutputStream();
         os.close();
         is.close();
         p.waitFor();
         p.destroy();
      }catch(Exception e){
         System.out.println("exception: " + e);
      }
      
      vw.release();
      File tmp = new File(System.getProperty("user.dir")+"\\tests\\tmp.avi");
      File tmp2 = new File(System.getProperty("user.dir")+"\\tests\\tmp.aac");

            
      try{
         System.gc();
         if(tmp.delete()){
          
         }
         if(tmp2.delete()){
           
         }
         
      }catch(Exception e){
         System.out.println(e);
      }
         
   }
   
}

