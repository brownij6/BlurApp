import org.opencv.videoio.VideoCapture;
import org.opencv.core.Core;

public class Desktop{
   
   public static void main(String[] args){
      
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            
      //open video file specified by command-line argument
      VideoCapture src = new VideoCapture(args[0]);
      VideoWriter vw = new VideoWriter();
      
      if(!src.isOpened()){
         System.out.println("Failed to open video file.");
         System.exit(0);
      }
   }
   
}