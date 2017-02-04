/*
 * Copyright <2017> <Stanislas Daniel Claude Dolcini>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.stan.recycler;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines a class that will get a log from irclogs.wildfiregames.com.
 * Created by Stanislas Daniel Claude Dolcini on 02/02/17.
 */
class LogDownloader implements Runnable {
    private final String formatedURL;

    private List<String> nickList;
    private List<String> timeList;
    private List<String> messageList;
    private boolean wildfireRobotEnabled,wildfireBotEnabled;

    /**
     *
     * @param wildfireRobotEnabled Wether you should print WildfireRobot Messages.
     * @param wildfireBotEnabled Wether you should print WildfireBot Messages
     */
    public LogDownloader(boolean wildfireRobotEnabled, boolean wildfireBotEnabled) {
        this.formatedURL = getCurrentLogPath();
        this.nickList = new ArrayList<>();
        this.timeList = new ArrayList<>();
        this.messageList = new ArrayList<>();
        this.wildfireRobotEnabled = wildfireRobotEnabled;
        this.wildfireBotEnabled = wildfireBotEnabled;
    }

    /**
     * @return the formated URL to get the wanted log.
     */
    private String getCurrentLogPath() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return "http://irclogs.wildfiregames.com/" + df.format(c.getTime()) + "-QuakeNet-%230ad-dev.log";
    }

    /**
     * @TODO: 02/02/17 Find a way to be able to swipe down.
     * @return a list of log messages
     */
    public List<LogMessage> getLogMessages() {
        ArrayList<LogMessage> logMessages = new ArrayList<>();
        for (int i = 0; i < messageList.size(); ++i) 
            logMessages.add(new LogMessage(nickList.get(i), messageList.get(i), timeList.get(i) + ": " ));
        
        // Reverse the collection, to be able to swipe up to update.
        Collections.reverse(logMessages);
        return logMessages;
    }

    /**
     * Gets the lastest log, and split it into parts.
     */
    @Override
    public void run() {
        try {
            URL url = new URL(formatedURL);
            // Read all the text returned by the server
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String message;
            while ((message = in.readLine()) != null) {
                if (message.contains("Log opened"))
                    continue;
                else if (message.contains("has joined #0ad-dev"))
                    continue;
                else if (message.contains("Day changed"))
                    continue;
                else if (message.contains("is now known as"))
                    continue;
                else if (message.contains("  * "))
                    continue;
                else if (message.contains("has quit"))
                    continue;
                else if (message.contains("-!-"))
                    continue;
                else if (message.contains("has left"))
                    continue;
                else if (message.contains("WildfireRobot") && !wildfireRobotEnabled )
                    continue;
                else if (message.contains("WildfireBot") && !wildfireBotEnabled )
                    continue;

                timeList.add(message.substring(0, 5));

                int nickStartIdx = message.indexOf("<") + 2;
                int nickStopIdx = message.indexOf(">");
                String nick = message.substring(nickStartIdx, nickStopIdx);

                nickList.add(nick);

                messageList.add(message.substring(nickStopIdx + 2, message.length()));
            }
            in.close();
        } catch (Exception e) {
            Log.w("Error", e);
        }
    }


}
