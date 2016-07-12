package com.accounted4.stockquote.yahoo;


import com.accounted4.stockquote.api.QuoteAttribute;
import com.accounted4.stockquote.api.QuoteService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


/**
 * An implementation of the stock quote api using a Yahoo REST service.
 *
 * @author Glenn Heinze <glenn@gheinze.com>
 */
public class YahooQuoteService implements QuoteService {

    private static final String SERVICE_NAME = YahooQuoteConfiguration.ServiceName.getConfigValue();;
    private static final String BASE_URL = YahooQuoteConfiguration.BaseUrl.getConfigValue();
    private static final String SECURITY_SEPARATOR = YahooQuoteConfiguration.SecuritySeparator.getConfigValue();
    private static final String RESPONSE_SEPARATOR = YahooQuoteConfiguration.ResponseSeparator.getConfigValue();


    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }


    @Override
    public List<HashMap<QuoteAttribute, String>> executeQuery(List<String> securities, List<QuoteAttribute> quoteAttributes) {

        String queryUrlString = generateQueryString(securities, quoteAttributes);

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(queryUrlString);

        try {

            HttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String stringResponse = EntityUtils.toString(entity);
                return processResponse(stringResponse, quoteAttributes);
            }

        } catch (IOException ex) {
            System.out.println("Error " + ex);
        }

        List<HashMap<QuoteAttribute, String>> result = new ArrayList<>();

        return result;
    }


    /*
     * The response comes back as a csv. Parse it out
     *
     */
    private List<HashMap<QuoteAttribute, String>> processResponse(
            String response,
            List<QuoteAttribute> quoteAttributes
    ) {

        List<HashMap<QuoteAttribute, String>> result = new ArrayList<>();

        // Each line is the response for one security
        String[] lines = response.split("\n");

        for (String line : lines) {

            // Results for the attributes come back in the order requested, each separated by a comma
            String[] items = line.split(RESPONSE_SEPARATOR);

            HashMap<QuoteAttribute, String> lineItem = new HashMap<>();
            int i = 0;
            for (String item : items) {
                lineItem.put(quoteAttributes.get(i++), item);
            }

            result.add(lineItem);

        }

        return result;

    }



    private String generateQueryString(List<String> securities, List<QuoteAttribute> quoteAttributes) {

        String tickerList = securityListToString(securities);
        String attributeList = attributeListToString(quoteAttributes);

        String query = String.format(BASE_URL + "?s=%s&f=%s", tickerList, attributeList);
        System.out.println("Query url: " + query);

        return query;

    }



    /*
     * Convert the list of securities provided by the user into a Yahoo-formated list
     * that can be sent to the Yahoo service: a "+" separated list of Strings
     */
    private String securityListToString(List<String> securityList) {
        return securityList.stream().collect(Collectors.joining(SECURITY_SEPARATOR));
    }


    /*
     * Translate the list of attributes requested into a Yahoo-specific query string to
     * ship off to the Yahoo stock quote service
     *
     * @param quoteAttributes List of generic quote attributes
     * @return Query string to send to Yahoo in order to query for requested attributes.
     */
    private String attributeListToString(List<QuoteAttribute> quoteAttributes) {
        StringBuilder sb = new StringBuilder();
        for (QuoteAttribute attr : quoteAttributes) {
            switch (attr) {
                case SYMBOL:
                    sb.append("s");
                    break;
                case COMPANY_NAME:
                    sb.append("n");
                    break;
                case LAST_TRADE_PRICE:
                    sb.append("l1");
                    break;
                case BOOK_VALUE:
                    sb.append("b4");
                    break;
                case EARNINGS_PS:
                    sb.append("e");
                    break;
                case DIVIDEND_PS:
                    sb.append("d");
                    break;
                case EX_DIVIDEND_DATE:
                    sb.append("q");
                    break;
                case DIVIDEND_DATE:
                    sb.append("r1");
                    break;
                case DIVIDEND_YIELD:
                    sb.append("y");
                    break;
                case PRICE_SALES:
                    sb.append("p5");
                    break;
                case PRICE_BOOK:
                    sb.append("p6");
                    break;
                case PRICE_EARNINGS:
                    sb.append("r");
                    break;
                default: System.out.println("Warning: Unsupported attribute: " + attr);
            }
        }
        return sb.toString();
    }



}
