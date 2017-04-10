/*
 * Copyright (C) 2013 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.common.util.captcha;

import com.github.cage.Cage;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author cuongdv3@viettel.com.vn
 * @since 07-01-2013
 * @version 1.0
 */
public class CaptchaServlet extends HttpServlet {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CaptchaServlet.class);
    private static final long serialVersionUID = 1490947492185481844L;
    private static final Cage cage = new VCage();

    /**
     * Generates a captcha token and stores it in the session.
     *
     * @param session where to store the captcha.
     */
    public static void generateToken(HttpSession session) {

//        int car = token_char.length - 1;
//        int numText = showRandomInteger(4, 6, new Random());
//        String token = "";
//        for (int i = 0; i < numText; i++) {
//            token += token_char[generator.nextInt(car) + 1];
//        }
        String token = cage.getTokenGenerator().next();

        session.setAttribute("codeImg", token);
//        markTokenUsed(session, false);
    }

    /**
     * Used to retrieve previously stored captcha token from session.
     *
     * @param session where the token is possibly stored.
     * @return token or null if there was none
     */
    public static String getToken(HttpSession session) {
        Object val = session.getAttribute("codeImg");

        return val != null ? val.toString() : null;
    }

    public static boolean isTokenOk(HttpSession session, String code) {
        String sessionToken = getToken(session);
        if (sessionToken == null || "".equals(sessionToken.trim())) {
            return false;
        }
        return sessionToken.equals(code == null ? "" : code.trim());
    }

    /**
     * Marks token as used/unused for image generation.
     *
     * @param session where the token usage flag is possibly stored.
     * @param used false if the token is not yet used for image generation
     */
    protected static void markTokenUsed(HttpSession session, boolean used) {
        session.setAttribute("captchaTokenUsed", used);
    }

    /**
     * Checks if the token was used/unused for image generation.
     *
     * @param session where the token usage flag is possibly stored.
     * @return true if the token was marked as unused in the session
     */
    protected static boolean isTokenUsed(HttpSession session) {
        return !Boolean.FALSE.equals(session.getAttribute("captchaTokenUsed"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        generateToken(session);
        String token = session != null ? getToken(session) : null;
        if (token == null) {
            try {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Captcha not found.");
            } catch (Exception ex) {
                log.error(ex, ex);
            }
            return;
        }

        setResponseHeaders(resp);
//        markTokenUsed(session, true);
        try {
            cage.draw(token, resp.getOutputStream());
        } catch (Exception ex) {
            log.error(ex, ex);
        }
    }

    /**
     * Helper method, disables HTTP caching.
     *
     * @param resp response object to be modified
     */
    protected void setResponseHeaders(HttpServletResponse resp) {
        resp.setContentType("image/" + cage.getFormat());
        resp.setHeader("Cache-Control", "no-cache, no-store");
        resp.setHeader("Pragma", "no-cache");
        long time = System.currentTimeMillis();
        resp.setDateHeader("Last-Modified", time);
        resp.setDateHeader("Date", time);
        resp.setDateHeader("Expires", time);
    }
}
