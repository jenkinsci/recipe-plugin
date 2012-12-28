package org.jenkinsci.plugins.recipe;

import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_MOVED_TEMPORARILY;

/**
 * @author Kohsuke Kawaguchi
 */
public class Util {
    /**
     * @deprecated
     *      Should switch to {@link StaplerResponse#sendRedirect(int,String)} (Stapler>=1.203)
     */
    public static void sendRedirect(StaplerResponse rsp, int statusCode, String url) throws IOException {
        if (statusCode==SC_MOVED_TEMPORARILY) {
            rsp.sendRedirect(url);  // to be safe, let the servlet container handles this default case
            return;
        }

        if(url.startsWith("http://") || url.startsWith("https://")) {
            // absolute URLs
        } else {
            StaplerRequest req = Stapler.getCurrentRequest();

            if (!url.startsWith("/")) {
                // WebSphere doesn't apparently handle relative URLs, so
                // to be safe, always resolve relative URLs to absolute URLs by ourselves.
                // see http://www.nabble.com/Hudson%3A-1.262%3A-Broken-link-using-update-manager-to21067157.html

                // example: /foo/bar/zot + ../abc -> /foo/bar/../abc
                String base = req.getRequestURI();
                base = base.substring(0,base.lastIndexOf('/')+1);
                if(!url.equals("."))
                    url = base+url;
                else
                    url = base;

                assert url.startsWith("/");
            }

            StringBuilder buf = new StringBuilder(req.getScheme()).append("://").append(req.getServerName());
            if ((req.getScheme().equals("http") && req.getServerPort()!=80)
            || (req.getScheme().equals("https") && req.getServerPort()!=443))
                buf.append(':').append(req.getServerPort());
            url = buf.append(url).toString();
        }

        rsp.setStatus(statusCode);
        rsp.setHeader("Location",url);
        rsp.getOutputStream().close();
    }
}
