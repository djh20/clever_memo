package com.github.irshulx.wysiwyg.NLP;

import java.util.ArrayList;
import java.util.Vector;

public class NLP_math {
    static double baseLog(double top, double bot) {
        return Math.log(top)/ Math.log(bot);
    }

    public static double getCosineSimilarity(ArrayList<Double> v1, ArrayList<Double> v2){
        double result = 0;
        result = InnerProduct(v1, v2) / (VectorSize(v1) * VectorSize(v2));
        return result;
    }

    private static double InnerProduct(ArrayList<Double> v1, ArrayList<Double> v2) {
        double Inner = 0;
        for (int i = 0; i < v1.size(); i++) {
            Inner += v1.get(i) * v2.get(i);
        }
        return Inner;
    }

    private static double VectorSize(ArrayList<Double> vector) {
        double vector_size = 0;
        for (int i = 0; i < vector.size(); i++) {
            vector_size += Math.pow(vector.get(i), 2);
        }
        vector_size = Math.sqrt(vector_size);
        return vector_size;
    }
}
