package com.accounted4.stockquote.tmx.contentHandler;

import com.accounted4.stockquote.tmx.OptionChain;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Ex: "<th title="Bid price: 83.400">Bid price: <b>83.400</b></th>"
 * 1. Look for string "Bid price:"
 * 2. Skip to next element "B"
 * 3. Grab text from element
 *
 * @author gheinze
 */
public class CommodityPriceChainableContentHandler extends ChainableContentHandler {

    public enum Type {
        Last, Bid, Ask;
    }


    private final Type resultKey;
    private final String searchString;
    private final String wrappingElement;

    private boolean searchingForLocation = true;
    private boolean foundWrappingElement = false;


    public CommodityPriceChainableContentHandler(OptionChain result, Type resultKey, String searchString, String wrappingElement) {
        setOptionChain(result);
        this.resultKey = resultKey;
        this.searchString = searchString;
        this.wrappingElement = wrappingElement;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (!searchingForLocation && qName.equalsIgnoreCase(wrappingElement)) {
            foundWrappingElement = true;
        }
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
    }


    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        String text = new String(ch, start, length).trim();

        if (foundLocationFor(text)) {
            extractValueFrom(text);
        }

    }


    private boolean foundLocationFor(String text) {
        if (searchingForLocation && text.startsWith(searchString)) {
            searchingForLocation = false;
        }
        return !searchingForLocation;
    }


    private boolean extractValueFrom(String text) {
        if (foundWrappingElement) {
            switch(resultKey) {
                case Ask: getOptionChain().setAskPrice(text); break;
                case Bid: getOptionChain().setBidPrice(text); break;
                case Last: getOptionChain().setLastPrice(text); break;
            }
            complete = true;
        }
        return complete;
    }

}
