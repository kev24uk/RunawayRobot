package com.kl.runawayrobot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.*;

/**
 * Created by Kev on 19/05/2017.
 */
public class main {

    public static final String BASE_URL = "http://www.hacker.org/runaway/index.php?name=Kev24uk&password=xxxx";

    public static void main(String[] args) throws Exception{
        start(BASE_URL);
    }

    public static void start (String url) throws Exception {
        Document doc = Jsoup.connect(url).get();
        String flashVars = findObject(doc);
        //.select("object").first().children().attr("value");

        Map<String, String> params = getParams(flashVars);
        /*Map<String, String> params = new HashMap<>();

        params.put("FVlevel", "55");
        params.put("FVterrainString","....XXX.X........X.X...X..X.XX..X.X...X.X....XX.XX.X............X.X.....X....X..X....X....X...X..X.X.......X.X.......X...X..XX....XX..X...X...X.X.X..X.......X.XX...X..XX..XX.XXXX....X..XXX......X.X...XX......XX..X....X.X.X..X....X.....X.....X....X.X...X..XX....XXX.XXX....XX..X....XX.....X.X.X....X.....X......X...XX........X.......X.........XX.XX..........X.X.XXX......X..X.........XX...............X.XXX......XX.X.X......X....X..XXXXXX.........X..X.X.XX.....X..X.....X...XXX................X.........X...........X..X...X.......X...X...X........X...X.X..........X..X.X.X...X.X.X...X..X..........X..X.X.X.XX..X...XX....X.X...X...X.X..XX.XX.....X......X.XX.X.X....X.XX......X.....................X...XX..............XX..X....XX..XX.X....X....X....X..X..............X..X..X....XX.....X.XXX....XX..XX.X.X.X....X.....X..X............X...X..X......X.....X.....XXX..X....XX....X..X.X..XX..X...X.X.X.....X.X");
        params.put("FVinsMax","18");
        params.put("FVboardY","30");
        params.put("FVboardX","30");
        params.put("FVinsMin", "11");*/

        System.out.println(params);

        Board board = new Board(params.get("FVterrainString"), Integer.valueOf(params.get("FVboardX")), Integer.valueOf(params.get("FVboardY")), true);

        Board testingBoard = new Board(params.get("FVterrainString"), Integer.valueOf(params.get("FVboardX")), Integer.valueOf(params.get("FVboardY")), false);

        CalculateMoves movesToMake = new CalculateMoves(Integer.valueOf(params.get("FVinsMin")), Integer.valueOf(params.get("FVinsMax")), board, testingBoard);

        String moveToSend = movesToMake.result != null ? movesToMake.result : movesToMake.testRemainingPossible();

        //String moveToSend = movesToMake.getTrieSolution();

        start(BASE_URL + "&path="+moveToSend);
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
        return returnVal;
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
