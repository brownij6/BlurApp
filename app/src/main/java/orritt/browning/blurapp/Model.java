package orritt.browning.blurapp;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.lang.*;
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



public class Model {

    public static void model(String videopath, CascadeClassifier ccFront, CascadeClassifier ccProf){
        //open video file specified by command-line argument 0
        VideoCapture src = new VideoCapture(videopath);

        //Size frameSize = new Size((int) src.get(Videoio.CAP_PROP_FRAME_WIDTH), (int) src.get(Videoio.CAP_PROP_FRAME_HEIGHT));
        Size frameSize = new Size((int) src.get(Videoio.CAP_PROP_FRAME_HEIGHT), (int) src.get(Videoio.CAP_PROP_FRAME_WIDTH));

        //TODO change from command line specified location to default location in gallery
        String path = getGalleryPath();

        Log.e("##########: ", "GALLERY PATH: " + path);
        VideoWriter vw = new VideoWriter(path + "temp.avi", VideoWriter.fourcc('M', 'J','P','G'), src.get(Videoio.CAP_PROP_FPS), frameSize, true);

        Log.e("##########: ", "trying to open");
        //vw.open(path + "temp.avi", VideoWriter.fourcc('M', 'J','P','G'), src.get(Videoio.CAP_PROP_FPS), frameSize, true);

        Log.e("######: ", "2");
        Mat mat = new Mat();
        int faceSize = 0;
        int frameNum = 0;
        while (src.read(mat)) {

            //make sure video writer opens succesfully
            if (vw.isOpened() == false){
                vw.release();
                Log.e("######: ", "VW NOT OPENED");
                break;
            }

            //Mat tmp = new Mat();
            MatOfRect faces = new MatOfRect();
            //Core.rotate(mat, tmp, Core.ROTATE_90_CLOCKWISE);
            Mat gray = new Mat();
            Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
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
                Imgproc.rectangle(mat, facesArray[i].tl(), facesArray[i].br(), new Scalar(255, 255, 255), -1);
            }

            ccProf.detectMultiScale(gray, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                    new Size(faceSize, faceSize), new Size());

            facesArray = faces.toArray();
            for (int i = 0; i < facesArray.length; i++){
                Imgproc.rectangle(mat, facesArray[i].tl(), facesArray[i].br(), new Scalar(255, 255, 255), -1);
            }

            vw.write(mat);
            Log.e("######: ", "Frame " + frameNum + " completed");
            frameNum++;
        }
        vw.release();
        Log.e("##########: ", "Finished writing file");

        //TODO figure out commands and stuff for audio (android)
        /*String[] commands = (System.getProperty("user.dir")+"\\ffmpeg-4.3.1-win64-static\\bin\\" + "ffmpeg -i "+ args[0]+" -vn -acodec copy .\\tests\\tmp.aac").split(" ");
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
        }*/

    }

    private static String getGalleryPath() {
        return Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/";
    }

}
