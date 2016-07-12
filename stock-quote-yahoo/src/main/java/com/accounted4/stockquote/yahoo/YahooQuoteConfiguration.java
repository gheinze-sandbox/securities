package com.accounted4.stockquote.yahoo;

/**
 * Interface for retrieving configuration information for accessing the Yahoo quote service.
 * Configuration lookup is performed in a hierarchical manner until a value is found:
 *   o check the system environment variable
 *   o default value
 *
 * @author gheinze
 */
public enum YahooQuoteConfiguration {

    ServiceName("quoteService.yahoo.name", "Yahoo"),
    BaseUrl("quoteService.yahoo.url", "http://finance.yahoo.com/d/quotes.csv"),
    SecuritySeparator("quoteService.yahoo.securitySeparator", "+"),
    ResponseSeparator("quoteService.yahoo.responseSeparator", ",")
    ;

    private final String key;
    private final String defaultValue;

    private YahooQuoteConfiguration(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }


    public String getConfigValue() {
        String value = System.getenv(key);
        return null == value ? defaultValue : value;
    }


}
