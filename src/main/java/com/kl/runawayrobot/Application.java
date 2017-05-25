package com.kl.runawayrobot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.*;
import java.util.logging.Level;

public class Application {

    private static final String BASE_URL_TEMPLATE = "http://www.hacker.org/runaway/index.php?name=%s&password=%s";
    private String BASE_URL;
    private String PATH_URL;
    private String LEVEL_URL;
    private String LEVEL;

    public Application(String username, String password, String startLevel) {
        BASE_URL = String.format(BASE_URL_TEMPLATE,username,password);
        PATH_URL = BASE_URL + "&path=%s";
        LEVEL_URL = BASE_URL + "&gotolevel=%s";
        LEVEL = startLevel;
        try {
            start(null, LEVEL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter username: ");
        String username = scanner.next();
        System.out.println("Please enter password: ");
        String password = scanner.next();
        System.out.println("Please enter start level: ");
        String startLevel = scanner.next();
        Application application = new Application(username, password, startLevel);
    }

    public void start (String path, String level) throws Exception {
        Document doc;
        if (path != null) {
            Jsoup.connect(String.format(PATH_URL,path)).get();
        } else {
            Jsoup.connect(BASE_URL).get();
        }
        doc = Jsoup.connect(String.format(LEVEL_URL,level)).get();
        String flashVars;
        try {
            flashVars = doc.select("object").first().children().attr("value");
        } catch (NullPointerException e) {
            flashVars = findObject(doc);
        }

        Map<String, String> params = getParams(flashVars);

        System.out.println();
        System.out.println(params);

        Board board = new Board(params.get("FVterrainString"), Integer.valueOf(params.get("FVboardX")), Integer.valueOf(params.get("FVboardY")),false);

        try {
            CalculateMoves calculateMoves = new CalculateMoves(Integer.valueOf(params.get("FVinsMin")), Integer.valueOf(params.get("FVinsMax")), board);
            if (Integer.valueOf(params.get("FVlevel")) < 513) {
                start(calculateMoves.result,String.valueOf(Integer.valueOf(params.get("FVlevel"))+1));
            } else {
                Jsoup.connect(String.format(PATH_URL,calculateMoves.result)).get();
            }
        } catch (CalculateMoves.PuzzleNotSolvedException e) {
            e.printStackTrace();
        }


    }

    private static String findObject(Node node) {
        String returnVal = null;

        for (int i = 0; i < node.childNodes().size();) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment")) {
                if (child.toString().indexOf("FlashVars VALUE=\"") > 0) {
                    String tmpString = child.toString().substring(child.toString().indexOf("FlashVars VALUE=\"") + 17);
                    return tmpString.substring(0, tmpString.indexOf("\""));
                }
            } else {
                returnVal = findObject(child);
                if (returnVal != null) {
                    return returnVal;
                } else {
                    i++;
                }
            }
        }
        return null;
    }

    public static Map<String, String> getParams (String inputString) {

        Map<String, String> paramsMap = new HashMap<>();
        String[] params = inputString.split("&");

        Arrays.stream(params).forEach(param -> {
            String[] split = param.split("=");
            paramsMap.put(split[0],split[1]);
        });

        return paramsMap;
    }
}
