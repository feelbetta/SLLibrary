package com.sllibrary.util.language;

import com.google.common.base.Preconditions;
import com.sllibrary.util.colors.Colors;
import com.sllibrary.util.time.Time;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Lang {

    public static final char COLOR_CHAR = '&';

    public enum MessageType {
        CHAT,
        TITLE
    }

    public enum DefaultFontInfo {

        A('A', 5),
        a('a', 5),
        B('B', 5),
        b('b', 5),
        C('C', 5),
        c('c', 5),
        D('D', 5),
        d('d', 5),
        E('E', 5),
        e('e', 5),
        F('F', 5),
        f('f', 4),
        G('G', 5),
        g('g', 5),
        H('H', 5),
        h('h', 5),
        I('I', 3),
        i('i', 1),
        J('J', 5),
        j('j', 5),
        K('K', 5),
        k('k', 4),
        L('L', 5),
        l('l', 1),
        M('M', 5),
        m('m', 5),
        N('N', 5),
        n('n', 5),
        O('O', 5),
        o('o', 5),
        P('P', 5),
        p('p', 5),
        Q('Q', 5),
        q('q', 5),
        R('R', 5),
        r('r', 5),
        S('S', 5),
        s('s', 5),
        T('T', 5),
        t('t', 4),
        U('U', 5),
        u('u', 5),
        V('V', 5),
        v('v', 5),
        W('W', 5),
        w('w', 5),
        X('X', 5),
        x('x', 5),
        Y('Y', 5),
        y('y', 5),
        Z('Z', 5),
        z('z', 5),
        NUM_1('1', 5),
        NUM_2('2', 5),
        NUM_3('3', 5),
        NUM_4('4', 5),
        NUM_5('5', 5),
        NUM_6('6', 5),
        NUM_7('7', 5),
        NUM_8('8', 5),
        NUM_9('9', 5),
        NUM_0('0', 5),
        EXCLAMATION_POINT('!', 1),
        AT_SYMBOL('@', 6),
        NUM_SIGN('#', 5),
        DOLLAR_SIGN('$', 5),
        PERCENT('%', 5),
        UP_ARROW('^', 5),
        AMPERSAND('&', 5),
        ASTERISK('*', 5),
        LEFT_PARENTHESIS('(', 4),
        RIGHT_PERENTHESIS(')', 4),
        MINUS('-', 5),
        UNDERSCORE('_', 5),
        PLUS_SIGN('+', 5),
        EQUALS_SIGN('=', 5),
        LEFT_CURL_BRACE('{', 4),
        RIGHT_CURL_BRACE('}', 4),
        LEFT_BRACKET('[', 3),
        RIGHT_BRACKET(']', 3),
        COLON(':', 1),
        SEMI_COLON(';', 1),
        DOUBLE_QUOTE('"', 3),
        SINGLE_QUOTE('\'', 1),
        LEFT_ARROW('<', 4),
        RIGHT_ARROW('>', 4),
        QUESTION_MARK('?', 5),
        SLASH('/', 5),
        BACK_SLASH('\\', 5),
        LINE('|', 1),
        TILDE('~', 5),
        TICK('`', 2),
        PERIOD('.', 1),
        COMMA(',', 1),
        SPACE(' ', 3),
        DEFAULT('a', 4);

        private final char character;
        private final int length;

        DefaultFontInfo(char character, int length) {
            this.character = character;
            this.length = length;
        }

        public char getCharacter() {
            return this.character;
        }

        public int getLength() {
            return this.length;
        }

        public int getBoldLength() {
            if (this == DefaultFontInfo.SPACE) return this.getLength();
            return this.length + 1;
        }

        public static DefaultFontInfo getDefaultFontInfo(char c) {
            for (DefaultFontInfo dFI : DefaultFontInfo.values()) {
                if (dFI.getCharacter() == c) return dFI;
            }
            return DefaultFontInfo.DEFAULT;
        }
    }

    private static final String ALPHA_FILLER_CONTENT = ChatColor.DARK_GRAY + "  \u23B9";
    private static final char PIXEL_CHAR = '\u2588';

    private final static int CENTER_PX = 154;

    private Lang() {
    }

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes(Lang.COLOR_CHAR, string);
    }

    public static String uncolor(String string) {
        return ChatColor.stripColor(string);
    }

    public static String shuffle(String string) {
        List<Character> randomChars = new ArrayList<>();
        Collections.addAll(randomChars, ArrayUtils.toObject(string.toCharArray()));
        Collections.shuffle(randomChars);
        return StringUtils.join(randomChars, "");
    }

    public static String toReadable(String string) {
        String[] names = string.split("_");
        for (int i = 0; i < names.length; i++) {
            names[i] = names[i].substring(0, 1) + names[i].substring(1).toLowerCase();
        }
        return (StringUtils.join(names, " "));
    }

    public static String[] imageToText(BufferedImage image, boolean trim) {
        int height = Preconditions.checkNotNull(image, "Image").getHeight();
        int width = image.getWidth();

        String[][] message = new String[height][width];
        LinkedList<Integer> pendingAlpha = new LinkedList<>();
        for (int y = 0; y < height; y++) {
            boolean fillAlpha = !trim;
            boolean left = false;

            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y), true);
                if (trim) {
                    if (color.getAlpha() < 1) {
                        pendingAlpha.add(x);
                        left = (left || x == 0);
                    } else {
                        if (!left) {
                            Lang.applyPendingAlpha(pendingAlpha, message[y]);
                        } else {
                            pendingAlpha.clear();
                            left = false;
                        }
                    }
                }
                ChatColor minecraftColor = rgbToMinecraft(Colors.closestColorMatch(color, Colors.COLOR_MAP.keySet()));
                message[y][x] = minecraftColor == null ? (fillAlpha ? Lang.ALPHA_FILLER_CONTENT : "") : minecraftColor.toString() + Lang.PIXEL_CHAR;
            }
            if (!trim) {
                Lang.applyPendingAlpha(pendingAlpha, message[y]);
            }
        }
        String[] messageFinal = new String[height];
        for (int y = 0; y < height; y++) {
            messageFinal[y] = StringUtils.join(message[y]);
        }
        return messageFinal;
    }

    public static ChatColor rgbToMinecraft(Color color) {
        return Colors.COLOR_MAP.get(color);
    }

    private static void applyPendingAlpha(LinkedList<Integer> pendingAlpha, String[] message) {
        IntStream.range(0, pendingAlpha.size()).forEach(value -> message[value] = Lang.ALPHA_FILLER_CONTENT);
        pendingAlpha.clear();
    }

    public static void send(String message, MessageType messageType) {
        if (messageType == MessageType.TITLE) {
            Bukkit.getOnlinePlayers().forEach(o -> o.sendTitle("", Lang.color(message), 20, (int) Time.toTicks(TimeUnit.SECONDS, 4), 20));
            return;
        }
        Bukkit.broadcastMessage(Lang.color(message));
    }

    public static void send(String message, MessageType messageType, boolean centered) {
        Bukkit.getOnlinePlayers().forEach(o -> Lang.send(o, message, messageType, centered));
    }

    public static void send(Player player, String message, MessageType messageType) {
        Lang.send(player, message, messageType, false);
    }

    public static void send(Player player, String message, MessageType messageType, boolean centered) {
        message = message == null ? "" : ChatColor.translateAlternateColorCodes(Lang.COLOR_CHAR, message);
        if (messageType == MessageType.TITLE) {
            player.sendTitle("", message, 20, (int) Time.toTicks(TimeUnit.SECONDS, 4), 20);
            return;
        }
        if (!centered) {
            player.sendMessage(message);
            return;
        }
        int messagePxSize = 0;
        boolean previousCode = false, isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = Lang.CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        player.sendMessage(sb.toString() + message);
    }
}
