package com.split;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

public class CreateInstances {


    public Instances getInstances(int features, List<double[]> list) {
        ArrayList<Attribute> attr = new ArrayList<>();
        for (int feature = 0; feature < features; feature++) {
            attr.add(new Attribute(String.valueOf(feature)));

        }
        Instances env = new Instances(String.valueOf(System.currentTimeMillis()), attr, list.size());
        for (int i = 0; i < list.size(); i++) {
            DenseInstance denseInstance = new DenseInstance(1, list.get(i));
            env.add(denseInstance);
        }
        return env;


    }


}
