package com.accounted4.stockquote.tmx.contentHandler;

/**
 *
 * @author gheinze
 */
public class NoOpChainableContentHandler extends ChainableContentHandler {

    @Override
    public ChainableContentHandler getNextHandler() {
        return this;
    }

}
