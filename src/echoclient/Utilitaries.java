/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoclient;

import java.time.LocalTime;

/**
 *
 * @author asrianCron
 */
public class Utilitaries {

    public static String getTime() {
        return String.format("[%s:%s:%s]", zeroOne(LocalTime.now().getHour()), zeroOne(LocalTime.now().getMinute()), zeroOne(LocalTime.now().getSecond()));
    }

    public static String zeroOne(int second) {
        if (second < 10) {
            return "0" + second;
        }
        return String.valueOf(second);
    }
}
