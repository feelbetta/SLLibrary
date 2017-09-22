package com.sllibrary.util.logging;

import com.sllibrary.util.exceptions.Exceptions;
import com.sllibrary.util.scheduler.Scheduler;
import org.json.simple.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Debugger {

    private final static Logger logger = Logger.getLogger(Debugger.class.getName());

    private Debugger() {}

    public static void setReportingURL(String url) {
        DebugOpts opts = Debugger.DebugUtil.getOpts();
        if (opts != null) {
            opts.setUrl(url);
        }
    }

    public static void toggleOutput(boolean output) {
        DebugOpts opts = Debugger.DebugUtil.getOpts();
        if (opts != null) {
            opts.toggleOutput(output);
        }
    }

    public static void hideErrors(boolean hide) {
        DebugOpts opts = Debugger.DebugUtil.getOpts();
        if (opts != null) {
            opts.hideErrors(hide);
        }
    }

    public static void print(Level level, String format, Object... args) {
        DebugOpts opts = Debugger.DebugUtil.getOpts();
        if (opts == null || !opts.doOutput()) {
            return;
        }
        opts.getLogger().log(level, String.format("[%s]=> %s",
                opts.getPrefix(), String.format(format, args)));
    }

    public static void print(String format, Object... args) {
        //Note, do not overload methods. getOpts() depends on stack location
        DebugOpts opts = Debugger.DebugUtil.getOpts();
        if (opts == null || !opts.doOutput()) {
            return;
        }
        opts.getLogger().log(Level.INFO, String.format("[%s]=> %s",
                opts.getPrefix(), String.format(format, args)));
    }

    public static void error(Throwable error, String message, Object... args) {
        DebugOpts opts = Debugger.DebugUtil.getOpts();
        if (opts == null) {
            return;
        }
        if (!opts.doHideErrors()) {
            opts.getLogger().log(Level.SEVERE, String.format(message, args), error);
        }
        //Send JSON payload
        Debugger.DebugUtil.report(opts, error, String.format(message, args));
    }


    private static JSONObject getPayload(DebugOpts opts, Throwable error, String message) {
        JSONObject back = new JSONObject();
        Map<String, Object> additional = opts.attachInfo();
        back.put("project-type", "standalone");
        JSONObject system = new JSONObject();
        system.put("name", System.getProperty("os.name"));
        system.put("version", System.getProperty("os.version"));
        system.put("arch", System.getProperty("os.arch"));
        back.put("system", system);
        JSONObject java = new JSONObject();
        java.put("version", System.getProperty("java.version"));
        java.put("vendor", System.getProperty("java.vendor"));
        java.put("vendor-url", System.getProperty("java.vendor.url"));
        java.put("bit", System.getProperty("sun.arch.data.model"));
        back.put("java", java);
        back.put("message", message);
        back.put("error", Exceptions.readableStackTrace(error));
        if (!additional.isEmpty()) {
            additional.forEach(back::put);
        }
        return back;
    }

    private static void send(String url, JSONObject payload) throws IOException {
        URL loc = new URL(url);
        HttpURLConnection http = (HttpURLConnection) loc.openConnection();
        http.setRequestMethod("POST");
        http.setRequestProperty("Content-Type", "application/json");
        http.setUseCaches(false);
        http.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(http.getOutputStream())) {
            wr.writeBytes(payload.toJSONString());
            wr.flush();
        }
    }

    public static class DebugOpts {

        protected final String prefix;
        protected boolean output;
        protected boolean hideErrors;
        protected Logger logger = Debugger.logger;
        protected String url;

        public DebugOpts() {
            this.hideErrors = false;
            this.output = true;
            this.prefix = "Debug";
            this.url = null;
        }

        public boolean doOutput() {
            return this.output;
        }

        public void toggleOutput(boolean output) {
            this.output = output;
        }

        public void hideErrors(boolean hide) {
            this.hideErrors = hide;
        }

        public boolean doHideErrors() {
            return this.hideErrors;
        }

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public JSONObject attachInfo() {
            return new JSONObject();
        }

        public Logger getLogger() {
            return this.logger;
        }

        public void setLogger(Logger logger) {
            this.logger = logger;
        }

    }

    public static final class DebugUtil {

        private static final DebugOpts OPTS = new DebugOpts();
        private static Supplier<DebugOpts> getOpts = () -> OPTS;

        public static void report(DebugOpts opts, Throwable error, String message) {
            if (opts == null || opts.getUrl() == null) {
                return;
            }
            Scheduler.runAsyncTask(() -> {
                JSONObject out = Debugger.getPayload(opts, error, message);
                try {
                    Debugger.send(opts.getUrl(), out);
                } catch (IOException ex) {
                    Debugger.logger.log(Level.WARNING, "Unable to report error");
                }
            }, 0);
        }

        public static DebugOpts getOpts() {
            return getOpts.get();
        }

        public static void setOps(Supplier<DebugOpts> getOpts) {
            DebugUtil.getOpts = getOpts;
        }

    }
}
