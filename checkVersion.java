public static boolean checkFile() throws MalformedURLException, IOException {
        String s = "https://solweb.tper.it/web/tools/open-data/open-data-download.aspx?source=solweb.tper.it&filename=opendata-versione&version=1&format=csv";
        HttpURLConnection huc = (HttpURLConnection) new URL(s).openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream(), StandardCharsets.UTF_8));
        String s2;
        do {
            s2 = br.readLine();
        } while (!s2.startsWith("lineefermate"));
        System.out.println(s2);
        s2 = s2.substring(s2.lastIndexOf(";") + 1, s2.length());
        System.out.println(s2);
        File f1 = new File("/home/raphael/Scrivania");
        File [] f2=f1.listFiles();
        if (s2.equals(f2[f2.length].getName().substring(14, (int)f2[f2.length].length()))) {
            return true;
        } else {
            return false;
        }
    }
