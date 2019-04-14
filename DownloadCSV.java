package javaapplication14;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadCSV {

    public static void prova() throws MalformedURLException, IOException {
        String urlpath = "https://solweb.tper.it/web/tools/open-data/open-data-download.aspx?source=solweb.tper.it&filename=lineefermate&version=20190401&format=csv";
        InputStream input = new URL(urlpath).openStream();
        Reader reader = new InputStreamReader(input, "UTF-8");
        String[] urldivided = urlpath.split("&");;
        String nome = urldivided[1].substring(urldivided[1].lastIndexOf("="), urldivided[1].length());
        nome = nome.substring(1);
        String version = urldivided[2].substring(urldivided[2].lastIndexOf("="), urldivided[2].length());
        version = version.substring(1);
        String extension = urldivided[3].substring(urldivided[3].lastIndexOf("="), urldivided[3].length());
        extension = extension.substring(1);
        String path = nome + "_" + version + "." + extension;
        if (versionControl(version)) {
            boolean b = true;
            char c = ' ';
            String util = "";
            while (b) {
                int s = reader.read();
                c = (char) s;
                if (s == -1) {
                    b = false;
                } else {
                    util += c;
                }
            }
            System.out.println(util);
            BufferedWriter br = new BufferedWriter(new FileWriter(fileCreation(path)));
            br.append(util);
            br.close();
        } else {
            System.out.println("UltimaVersioneFileCSV");
        }

    }

    private static File fileCreation(String filename) {
        String path = "C:/Users/Emanuele/Desktop/" + filename; //TODO percorso file row
        try {
            File file = new File(path);
            if (file.exists()) {
                System.out.println("File " + filename + " gia esistene");
            } else if (file.createNewFile()) {
                System.out.println("File " + filename + " creato");
            } else {
                System.out.println("Il file " + filename + " non può essere creato");
            }
            return file;
        } catch (IOException e) {
            return null;
        }
    }

    private static boolean versionControl(String version) {
        String path = ""; //TODO percorso file row
        
        if (!path.isEmpty()) {
            String util = path.substring(path.lastIndexOf("_"), path.lastIndexOf("."));
            if (Integer.parseInt(version) > Integer.parseInt(util.substring(1))) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}
