package com.accounted4.stockquote.tmx.contentHandler;

import com.accounted4.stockquote.tmx.OptionChain;
import org.xml.sax.SAXException;

/**
 *
 * @author gheinze
 */
public class UpdateTimestampMessageChainableContentHandler extends ChainableContentHandler {


    private static final String SEARCH_STRING = "Last update:";

    public UpdateTimestampMessageChainableContentHandler(OptionChain result)  {
        setOptionChain(result);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        String text = new String(ch, start, length).trim();

        if (text.startsWith(SEARCH_STRING)) {
            String lastUpdateMsg = text.substring(SEARCH_STRING.length()).trim();
            getOptionChain().setQueryTime(lastUpdateMsg);
            complete = true;
        }

    }


}
