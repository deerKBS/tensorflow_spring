package com.example.tensorflowTest.test;

import org.apache.commons.io.IOUtils;
import org.tensorflow.Graph;
import org.tensorflow.Session;

import java.io.IOException;
import java.io.InputStream;

public class testService {
    private Graph graph;
    private Session session;

    public testService() {
        try (InputStream is = getClass().getResourceAsStream("/model.pb")) {
            graph = new Graph();
            graph.importGraphDef(IOUtils.toByteArray(is));
            session = new Session(graph);
        } catch (IOException e) {
            throw new RuntimeException("모델 파일을 불러올 수 없습니다.");
        }
    }
}
