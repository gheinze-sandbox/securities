package com.accounted4.stockquote.tmx.contentHandler;

import com.accounted4.stockquote.tmx.OptionChain;
import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author gheinze
 */
public abstract class ChainableContentHandler {

    private ChainableContentHandler nextHandler;
    protected boolean complete = false;
    private OptionChain result;


    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {}

    public void endElement(String uri, String localName, String qName) throws SAXException {}

    public void characters(char[] ch, int start, int length) throws SAXException {}

    public ChainableContentHandler getNextHandler() {
        return nextHandler;
    }

    public ChainableContentHandler setNextHandler(ChainableContentHandler handler) {
        nextHandler = handler;
        return handler;
    }

    public boolean isComplete() {
        return complete;
    }

    public OptionChain getOptionChain() {
        return result;
    }

    final public void setOptionChain(OptionChain result) {
        this.result = result;
    }

}
