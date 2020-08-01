import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.core.Core;
import org.opencv.core.Size;
import org.opencv.videoio.Videoio;
import org.opencv.core.Mat;

public class Desktop{
   
   public static void main(String[] args){
      
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            
      //open video file specified by command-line argument 0
      VideoCapture src = new VideoCapture(args[0]);
      
      Size frameSize = new Size((int) src.get(Videoio.CAP_PROP_FRAME_WIDTH), (int) src.get(Videoio.CAP_PROP_FRAME_HEIGHT));

      //output file specified by command-line argument 1
      VideoWriter vw = new VideoWriter(args[1], VideoWriter.fourcc('x', '2','6','4'),
                                       src.get(Videoio.CAP_PROP_FPS), frameSize, true);
      
      Mat mat = new Mat();                 
      while (src.read(mat)) {
         vw.write(mat);         
      }
      
      if(!src.isOpened()){
         System.out.println("Failed to open video file.");
         System.exit(0);
      }
      
      
   }
   
}