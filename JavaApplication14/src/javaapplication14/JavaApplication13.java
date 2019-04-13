package javaapplication14;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class JavaApplication13 {

    public static void main(String[] args) throws IOException, URISyntaxException {
        BusReader p = new BusReader();
        p.fileToArrayList("C:/Users/Emanuele/Desktop/lineefermate_20190401 (2).csv");
        p.busCodeToPrint("");
        p.stopCodeToPrint("");
        p.stopNameToPrint("");
    }
}
