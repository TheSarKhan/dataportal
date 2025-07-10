package org.example.dataprotal.service;

import java.io.IOException;

public interface TranslateService {
    String translate(String from, String to, String text);

    String translateFiles() throws IOException, InterruptedException;
}
