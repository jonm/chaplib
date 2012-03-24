package org.chaplib;

import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.cache.HeaderConstants;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.protocol.HTTP;

public class Response {

    private static final String STALE_WHILE_REVALIDATE = "stale-while-revalidate";
    private static final String STALE_IF_ERROR = "stale-if-error";
    private static final long MAX_AGE = 2147483648L;
    private HttpCacheEntry entry;
    
    public Response(Date requestSent, Date responseReceived, HttpResponse response) {
        entry = new HttpCacheEntry(requestSent, responseReceived, response.getStatusLine(),
                response.getAllHeaders(), new NullCacheEntryResource());
    }
    
    public long getCurrentAgeSecs(Date now) {
        return getCorrectedInitialAgeSecs() + getResidentTimeSecs(now);
    }

    public long getFreshnessLifetimeSecs() {
        long maxage = getMaxAge();
        if (maxage > -1)
            return maxage;

        Date dateValue = getDateValue();
        if (dateValue == null)
            return 0L;

        Date expiry = getExpirationDate();
        if (expiry == null)
            return 0;
        long diff = expiry.getTime() - dateValue.getTime();
        return (diff / 1000);
    }

    public boolean isResponseFresh(Date now) {
        return (getCurrentAgeSecs(now) < getFreshnessLifetimeSecs());
    }

    /**
     * Decides if this response is fresh enough based Last-Modified and Date, if available.
     * This entry is meant to be used when isResponseFresh returns false.  The algorithm is as follows:
     *
     * if last-modified and date are defined, freshness lifetime is coefficient*(date-lastModified),
     * else freshness lifetime is defaultLifetime
     *
     * @param entry the cache entry
     * @param now what time is it currently (When is right NOW)
     * @param coefficient Part of the heuristic for cache entry freshness
     * @param defaultLifetime How long can I assume a cache entry is default TTL
     * @return {@code true} if the response is fresh
     */
    public boolean isResponseHeuristicallyFresh(Date now, float coefficient,
            long defaultLifetime) {
        return (getCurrentAgeSecs(now) < getHeuristicFreshnessLifetimeSecs(coefficient, defaultLifetime));
    }

    public long getHeuristicFreshnessLifetimeSecs(float coefficient, long defaultLifetime) {
        Date dateValue = getDateValue();
        Date lastModifiedValue = getLastModifiedValue();

        if (dateValue != null && lastModifiedValue != null) {
            long diff = dateValue.getTime() - lastModifiedValue.getTime();
            if (diff < 0)
                return 0;
            return (long)(coefficient * (diff / 1000));
        }

        return defaultLifetime;
    }

    public boolean isRevalidatable() {
        return entry.getFirstHeader(HeaderConstants.ETAG) != null
                || entry.getFirstHeader(HeaderConstants.LAST_MODIFIED) != null;
    }

    public boolean mustRevalidate() {
        return hasCacheControlDirective(HeaderConstants.CACHE_CONTROL_MUST_REVALIDATE);
    }

    public boolean proxyRevalidate() {
        return hasCacheControlDirective(HeaderConstants.CACHE_CONTROL_PROXY_REVALIDATE);
    }

    public boolean mayReturnStaleWhileRevalidating(Date now) {
        for (Header h : entry.getHeaders(HeaderConstants.CACHE_CONTROL)) {
            for(HeaderElement elt : h.getElements()) {
                if (STALE_WHILE_REVALIDATE.equalsIgnoreCase(elt.getName())) {
                    try {
                        int allowedStalenessLifetime = Integer.parseInt(elt.getValue());
                        if (getStalenessSecs(now) <= allowedStalenessLifetime) {
                            return true;
                        }
                    } catch (NumberFormatException nfe) {
                        // skip malformed directive
                    }
                }
            }
        }

        return false;
    }

    public boolean mayReturnStaleIfError(HttpRequest request, Date now) {
        long stalenessSecs = getStalenessSecs(now);
        return mayReturnStaleIfError(request.getHeaders(HeaderConstants.CACHE_CONTROL),
                                     stalenessSecs)
                || mayReturnStaleIfError(entry.getHeaders(HeaderConstants.CACHE_CONTROL),
                                         stalenessSecs);
    }

    private boolean mayReturnStaleIfError(Header[] headers, long stalenessSecs) {
        boolean result = false;
        for(Header h : headers) {
            for(HeaderElement elt : h.getElements()) {
                if (STALE_IF_ERROR.equals(elt.getName())) {
                    try {
                        int staleIfErrorSecs = Integer.parseInt(elt.getValue());
                        if (stalenessSecs <= staleIfErrorSecs) {
                            result = true;
                            break;
                        }
                    } catch (NumberFormatException nfe) {
                        // skip malformed directive
                    }
                }
            }
        }
        return result;
    }

    private Date getDateValue() {
        Header dateHdr = entry.getFirstHeader(HTTP.DATE_HEADER);
        if (dateHdr == null)
            return null;
        try {
            return DateUtils.parseDate(dateHdr.getValue());
        } catch (DateParseException dpe) {
            // ignore malformed date
        }
        return null;
    }

    private Date getLastModifiedValue() {
        Header dateHdr = entry.getFirstHeader(HeaderConstants.LAST_MODIFIED);
        if (dateHdr == null)
            return null;
        try {
            return DateUtils.parseDate(dateHdr.getValue());
        } catch (DateParseException dpe) {
            // ignore malformed date
        }
        return null;
    }

    private long getApparentAgeSecs() {
        Date dateValue = getDateValue();
        if (dateValue == null)
            return MAX_AGE;
        long diff = entry.getResponseDate().getTime() - dateValue.getTime();
        if (diff < 0L)
            return 0;
        return (diff / 1000);
    }

    private long getAgeValue() {
        long ageValue = 0;
        for (Header hdr : entry.getHeaders(HeaderConstants.AGE)) {
            long hdrAge;
            try {
                hdrAge = Long.parseLong(hdr.getValue());
                if (hdrAge < 0) {
                    hdrAge = MAX_AGE;
                }
            } catch (NumberFormatException nfe) {
                hdrAge = MAX_AGE;
            }
            ageValue = (hdrAge > ageValue) ? hdrAge : ageValue;
        }
        return ageValue;
    }

    private long getCorrectedReceivedAgeSecs() {
        long apparentAge = getApparentAgeSecs();
        long ageValue = getAgeValue();
        return (apparentAge > ageValue) ? apparentAge : ageValue;
    }

    private long getResponseDelaySecs() {
        long diff = entry.getResponseDate().getTime() - entry.getRequestDate().getTime();
        return (diff / 1000L);
    }

    private long getCorrectedInitialAgeSecs() {
        return getCorrectedReceivedAgeSecs() + getResponseDelaySecs();
    }

    private long getResidentTimeSecs(Date now) {
        long diff = now.getTime() - entry.getResponseDate().getTime();
        return (diff / 1000L);
    }

    private long getMaxAge() {
        long maxage = -1;
        for (Header hdr : entry.getHeaders(HeaderConstants.CACHE_CONTROL)) {
            for (HeaderElement elt : hdr.getElements()) {
                if (HeaderConstants.CACHE_CONTROL_MAX_AGE.equals(elt.getName())
                        || "s-maxage".equals(elt.getName())) {
                    try {
                        long currMaxAge = Long.parseLong(elt.getValue());
                        if (maxage == -1 || currMaxAge < maxage) {
                            maxage = currMaxAge;
                        }
                    } catch (NumberFormatException nfe) {
                        // be conservative if can't parse
                        maxage = 0;
                    }
                }
            }
        }
        return maxage;
    }

    private Date getExpirationDate() {
        Header expiresHeader = entry.getFirstHeader(HeaderConstants.EXPIRES);
        if (expiresHeader == null)
            return null;
        try {
            return DateUtils.parseDate(expiresHeader.getValue());
        } catch (DateParseException dpe) {
            // malformed expires header
        }
        return null;
    }

    public boolean hasCacheControlDirective(final String directive) {
        for (Header h : entry.getHeaders(HeaderConstants.CACHE_CONTROL)) {
            for(HeaderElement elt : h.getElements()) {
                if (directive.equalsIgnoreCase(elt.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public long getStalenessSecs(Date now) {
        long age = getCurrentAgeSecs(now);
        long freshness = getFreshnessLifetimeSecs();
        if (age <= freshness) return 0L;
        return (age - freshness);
    }

}
