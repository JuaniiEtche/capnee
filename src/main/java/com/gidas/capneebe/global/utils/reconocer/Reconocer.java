package com.gidas.capneebe.global.utils.reconocer;


import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.opencv.core.CvType;

import java.io.File;
import java.nio.IntBuffer;

import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_GRAYSCALE;

public class Reconocer {

    private LBPHFaceRecognizer lbphRecognizer;


    public Reconocer() {
    }


    public void entrenarImagenes(){
        File rostros = new File("rostros");
        File imageFiles[] = rostros.listFiles();

        MatVector images = new MatVector(imageFiles.length);

        Mat labels = new Mat(imageFiles.length, 1, CV_32SC1);

        IntBuffer labelsBuf = labels.createBuffer();

        int counter = 0;

        for(File image : imageFiles){
            System.out.println("Entrenando "+image.getName());
            Mat img = opencv_imgcodecs.imread(image.getAbsolutePath(), IMREAD_GRAYSCALE);
            org.bytedeco.javacpp.opencv_imgproc.equalizeHist(img, img);
            int label = Integer.parseInt(image.getName().split("\\-")[0]);

            images.put(counter, img);

            labelsBuf.put(counter, label);

            counter++;
        }
        lbphRecognizer = LBPHFaceRecognizer.create();
        lbphRecognizer.train(images, labels);
    }

    public void loadDatabase(){
        lbphRecognizer = LBPHFaceRecognizer.create();
        lbphRecognizer.read("dataBase.yml");
    }

    public void saveDatabase(){
        Mat floatImage =lbphRecognizer.getHistograms().get(0);
        floatImage.convertTo(floatImage, CvType.CV_64F);

        //System.out.println(lbphRecognizer.getHistograms().get(0).col(0).data().get);
        System.out.println(floatImage.col(0).row(0).data().getFloat());
        lbphRecognizer.save("dataBase.yml");
    }

    public long reconocer(opencv_core.Mat img){
        IntPointer label = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(1);
        lbphRecognizer.predict(img, label, confidence);
        int predictedLabel = label.get(0);

        System.out.println(confidence.get(0)+"\t"+predictedLabel);

        if(confidence.get(0)>15){
            return -1;
        }

        return predictedLabel;
    }

    public static void crearBD(){
        Reconocer r = new Reconocer();
        r.entrenarImagenes();
        r.saveDatabase();
    }

}
