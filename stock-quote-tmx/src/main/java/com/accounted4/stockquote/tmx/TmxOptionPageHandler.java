package com.accounted4.stockquote.tmx;

import com.accounted4.stockquote.tmx.contentHandler.NoOpChainableContentHandler;
import com.accounted4.stockquote.tmx.contentHandler.ChainableContentHandler;
import com.accounted4.stockquote.tmx.contentHandler.CommodityPriceChainableContentHandler;
import com.accounted4.stockquote.tmx.contentHandler.OptionChainChainableContentHandler;
import com.accounted4.stockquote.tmx.contentHandler.UpdateTimestampMessageChainableContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author gheinze
 */
public class TmxOptionPageHandler extends DefaultHandler {

    private ChainableContentHandler currentHandler;
    private final OptionChain result;


    private static final String BID_UPDATE_KEY = "Bid price";
    private static final String SEARCH_STRING = "Bid price:";
    private static final String WRAPPING_ELEMENT = "B";


    public TmxOptionPageHandler(String symbol) {

        result = new OptionChain(symbol);

        ChainableContentHandler updateMsgHandler = new UpdateTimestampMessageChainableContentHandler(result);
        ChainableContentHandler lastPriceHandler = new CommodityPriceChainableContentHandler(result, CommodityPriceChainableContentHandler.Type.Last, "Last price:",  WRAPPING_ELEMENT);
        ChainableContentHandler bidPriceHandler = new CommodityPriceChainableContentHandler(result, CommodityPriceChainableContentHandler.Type.Bid, SEARCH_STRING, WRAPPING_ELEMENT);
        ChainableContentHandler askPriceHandler = new CommodityPriceChainableContentHandler(result, CommodityPriceChainableContentHandler.Type.Ask, "Ask price:", WRAPPING_ELEMENT);
        ChainableContentHandler optionHandler = new OptionChainChainableContentHandler(result);
        ChainableContentHandler noOpHandler = new NoOpChainableContentHandler();

        currentHandler = updateMsgHandler;

        updateMsgHandler
                .setNextHandler(lastPriceHandler)
                .setNextHandler(bidPriceHandler)
                .setNextHandler(askPriceHandler)
                .setNextHandler(optionHandler)
                .setNextHandler(noOpHandler)
                ;

    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentHandler.startElement(uri, localName, qName, attributes);
        nextHandler();
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        currentHandler.endElement(uri, localName, qName);
        nextHandler();
    }


    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        currentHandler.characters(ch, start, length);
        nextHandler();
    }

    private void nextHandler() {
        if (currentHandler.isComplete()) {
            currentHandler = currentHandler.getNextHandler();
        }
    }

    public OptionChain getOptionChain() {
        return result;
    }
    
}
