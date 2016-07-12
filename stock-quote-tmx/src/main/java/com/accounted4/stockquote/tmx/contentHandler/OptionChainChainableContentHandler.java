package com.accounted4.stockquote.tmx.contentHandler;

import com.accounted4.stockquote.tmx.Option;
import com.accounted4.stockquote.tmx.OptionChain;
import com.accounted4.stockquote.tmx.OptionType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * 1. Find <tbody>
 *      1a.Find <td>
 *          <td> 1: attributes data-expiry="20160722" data-strike="78"
 *          <td> 2: character bid
 *          <td> 3: character ask
 *          <td> 4: character last
 *
 *          <td> 9:  attributes data-expiry="20160722" data-strike="78"
 *          <td> 10: character bid
 *          <td> 11: character ask
 *          <td> 12: character last
 *
 * @author gheinze
 */
public class OptionChainChainableContentHandler extends ChainableContentHandler {

    private enum State {

        SearchForTbody
        ,SearchForTd1Name
        ,SearchForTd2Bid
        ,Td2BidFound
        ,SearchForTd3Ask
        ,Td3AskFound
        ,SearchForTd4Last
        ,Td4LastFound

        ,SearchForTd9
        ,SearchForTd10Bid
        ,Td10BidFound
        ,SearchForTd11Ask
        ,Td11AskFound
        ,SearchForTd12Last
        ,Td12LastFound

        ,DoneRow
        ,DoneTable

        ;

    }

    private State currentState = State.SearchForTbody;
    private boolean optionProcessing = false;

    private Option currentOption;

    public OptionChainChainableContentHandler(OptionChain result) {
        setOptionChain(result);
    }

    private final DateTimeFormatter optionDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

        switch(currentState) {

            case SearchForTbody:
                if (qName.equalsIgnoreCase("TBODY")) {
                    currentState = State.SearchForTd1Name;
                    optionProcessing = true;
                }
                break;

            case SearchForTd1Name:
                if (qName.equalsIgnoreCase("TD")) {

                    String expiryDate = atts.getValue("data-expiry");
                    String strikePrice = atts.getValue("data-strike");

                    currentOption = new Option(getOptionChain().getSymbol());
                    currentOption.setOptionType(OptionType.CALL);
                    currentOption.setExpiryDate(LocalDate.parse(expiryDate, optionDateFormatter));
                    currentOption.setStrikePrice(strikePrice);

                    //System.out.print(String.format("%s, %s, ", expiryDate, strikePrice));
                    currentState = State.SearchForTd2Bid;
                }
                break;

            case SearchForTd2Bid:
                if (qName.equalsIgnoreCase("TD")) {
                    currentState = State.Td2BidFound;
                }
                break;

            case SearchForTd3Ask:
                if (qName.equalsIgnoreCase("TD")) {
                    currentState = State.Td3AskFound;
                }
                break;

            case SearchForTd4Last:
                if (qName.equalsIgnoreCase("TD")) {
                    currentState = State.Td4LastFound;
                }
                break;

            case SearchForTd9:
                if (qName.equalsIgnoreCase("TD")) {
                    String expiryDate = atts.getValue("data-expiry");
                    if (null != expiryDate && !expiryDate.isEmpty()) {
                        String strikePrice = atts.getValue("data-strike");

                        currentOption = new Option(getOptionChain().getSymbol());
                        currentOption.setOptionType(OptionType.PUT);
                        currentOption.setExpiryDate(LocalDate.parse(expiryDate, optionDateFormatter));
                        currentOption.setStrikePrice(strikePrice);

                        //System.out.print(String.format("%s, %s, ", expiryDate, strikePrice));
                        currentState = State.SearchForTd10Bid;
                    }
                }
                break;

            case SearchForTd10Bid:
                if (qName.equalsIgnoreCase("TD")) {
                    currentState = State.Td10BidFound;
                }
                break;

            case SearchForTd11Ask:
                if (qName.equalsIgnoreCase("TD")) {
                    currentState = State.Td11AskFound;
                }
                break;

            case SearchForTd12Last:
                if (qName.equalsIgnoreCase("TD")) {
                    currentState = State.Td12LastFound;
                }
                break;

        }


    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (currentState.equals(State.DoneRow) && qName.equalsIgnoreCase("TR")) {
            currentState = State.SearchForTd1Name;
        }

        if (optionProcessing && qName.equalsIgnoreCase("TBODY")) {
            currentState = State.DoneTable;
        }

    }


    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        switch(currentState) {

            case Td2BidFound:
                String bid = new String(ch, start, length).trim();
                currentOption.setBidPrice(bid);
                //System.out.print(String.format("%s, ", bid));
                currentState = State.SearchForTd3Ask;
                break;

            case Td3AskFound:
                String ask = new String(ch, start, length).trim();
                currentOption.setAskPrice(ask);
                //System.out.print(String.format("%s, ", ask));
                currentState = State.SearchForTd4Last;
                break;

            case Td4LastFound:
                String last = new String(ch, start, length).trim();
                currentOption.setLastPrice(last);
                getOptionChain().addOption(currentOption);
                currentOption = null;
                //System.out.print(String.format("%s, ", last));
                currentState = State.SearchForTd9;
                break;

            case Td10BidFound:
                bid = new String(ch, start, length).trim();
                currentOption.setBidPrice(bid);
                //System.out.print(String.format("%s, ", bid));
                currentState = State.SearchForTd11Ask;
                break;

            case Td11AskFound:
                ask = new String(ch, start, length).trim();
                currentOption.setAskPrice(ask);
                //System.out.print(String.format("%s, ", ask));
                currentState = State.SearchForTd12Last;
                break;

            case Td12LastFound:
                last = new String(ch, start, length).trim();
                currentOption.setLastPrice(last);
                getOptionChain().addOption(currentOption);
                currentOption = null;
                //System.out.println(String.format("%s ", last));
                currentState = State.DoneRow;
                break;

        }

    }


}
