package org.jenkinsci.plugins.recipe;

import hudson.util.IOException2;
import hudson.util.VersionNumber;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

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

    /**
     * Parses configuration XML files and picks up references to XML files.
     *
     * TODO: switch to Pluginmanager.parseRequestedPlugins in 1.498
     */
    static Map<String,VersionNumber> parseRequestedPlugins(InputStream configXml) throws IOException {
        final Map<String,VersionNumber> requestedPlugins = new TreeMap<String,VersionNumber>();
        try {
            SAXParserFactory.newInstance().newSAXParser().parse(configXml, new DefaultHandler() {
                @Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    String plugin = attributes.getValue("plugin");
                    if (plugin == null) {
                        return;
                    }
                    if (!plugin.matches("[^@]+@[^@]+")) {
                        throw new SAXException("Malformed plugin attribute: " + plugin);
                    }
                    int at = plugin.indexOf('@');
                    String shortName = plugin.substring(0, at);
                    VersionNumber existing = requestedPlugins.get(shortName);
                    VersionNumber requested = new VersionNumber(plugin.substring(at + 1));
                    if (existing == null || existing.compareTo(requested) < 0) {
                        requestedPlugins.put(shortName, requested);
                    }
                }
            });
        } catch (SAXException x) {
            throw new IOException2("Failed to parse XML",x);
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e); // impossible since we don't tweak XMLParser
        }
        return requestedPlugins;
    }
}
