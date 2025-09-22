package com.micmoe.simple_qr.Utils;

import java.awt.*;

public class ColorUtils {
    public static int parseColor(String hex) {
        return Color.decode(hex).getRGB();
    }

    public static boolean isValidHexColor(String color) {
        if (color == null) return false;
        return color.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    }

}
