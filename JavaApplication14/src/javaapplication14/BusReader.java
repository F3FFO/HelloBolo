package javaapplication14;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class BusReader {

    ArrayList<BusClass> bus = new ArrayList();

    public void fileToArrayList(String file) throws IOException {
        try (Scanner reader = new Scanner(new File(file))) {
            String s = "";
            while (reader.hasNext()) {
                s = reader.nextLine();
                if (!s.startsWith("codice_linea")) {
                    StringTokenizer token = new StringTokenizer(s, ";");
                    if (token.countTokens() == 10) {
                        String buscode = token.nextToken();
                        String stopcode = token.nextToken();
                        String name = token.nextToken();
                        for (int i = 0; i < 6; i++) {
                            token.nextToken();
                        }
                        String zonecode = token.nextToken();
                        bus.add(new BusClass(buscode, stopcode, name, zonecode));
                    } else if (token.countTokens() == 9) {
                        String buscode = token.nextToken();
                        String stopcode = token.nextToken();
                        String name = token.nextToken();
                        for (int i = 0; i < 5; i++) {
                            token.nextToken();
                        }
                        String zonecode = token.nextToken();
                        bus.add(new BusClass(buscode, stopcode, name, zonecode));
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public void printArray() {
        for (int i = 0; i < bus.size(); i++) {
            System.out.println(bus.get(i).getLinecode() + "," + bus.get(i).getStopcode() + "," + bus.get(i).getStopname() + "," + bus.get(i).getZonecode());
        }
    }

    public void stopCodeToPrint(String stop) {
        for (int i = 0; i < bus.size(); i++) {
            if (bus.get(i).getStopcode().equals(stop)) {
                System.out.println(bus.get(i).getLinecode() + "," + bus.get(i).getStopcode() + "," + bus.get(i).getStopname() + "," + bus.get(i).getZonecode());
            }
        }
    }

    public void busCodeToPrint(String buscode) {
        for (int i = 0; i < bus.size(); i++) {
            if (bus.get(i).getLinecode().equals(buscode)) {
                System.out.println(bus.get(i).getLinecode() + "," + bus.get(i).getStopcode() + "," + bus.get(i).getStopname() + "," + bus.get(i).getZonecode());
            }
        }
    }

    public void stopNameToPrint(String stopname) {
        for (int i = 0; i < bus.size(); i++) {
            if (bus.get(i).getStopname().equals(stopname)) {
                System.out.println(bus.get(i).getLinecode() + "," + bus.get(i).getStopcode() + "," + bus.get(i).getStopname() + "," + bus.get(i).getZonecode());
            }
        }
    }
}
