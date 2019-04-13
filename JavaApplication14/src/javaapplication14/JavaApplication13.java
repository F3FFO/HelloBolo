package javaapplication14;

import java.io.IOException;

public class JavaApplication13 {

    public static void main(String[] args) throws IOException {
        BusReader p = new BusReader();
        UrlBusElaboration n = new UrlBusElaboration();

        p.fileToArrayList("C:/Users/Emanuele/Desktop/lineefermate_20190401 (2).csv");
        p.busCodeToPrint("");
        p.stopCodeToPrint("");
        p.stopNameToPrint("");

        n.httpExtractOnlyStop("8");
        n.httpExtractStopAndHour("8", "12");
        n.httpExtractStopAndBusLine("8", "36");
    }
}
