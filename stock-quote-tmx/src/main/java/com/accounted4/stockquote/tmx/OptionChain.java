package com.accounted4.stockquote.tmx;


import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.TreeSet;



/**
 * A collection of the available options (CALL and PUT) available for
 * a given company.
 *
 * @author Glenn Heinze <glenn@gheinze.com>
 */
public class OptionChain {



    private String symbol;
    private BigDecimal lastPrice;
    private BigDecimal bidPrice;
    private BigDecimal askPrice;
    private String queryTime;
    private final TreeSet<Option> calls = new TreeSet<>();
    private final TreeSet<Option> puts = new TreeSet<>();

    private final NumberFormat numberFormat = NumberFormat.getInstance();


    public OptionChain(String symbol) {
        this.symbol = symbol;
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
    }


    public void addOption(Option option) {

        if (!option.getSymbol().equals(symbol)) {
            throw new IllegalArgumentException("Attempt to add option with symbol: " + option.getSymbol() +
                    " to an option chain for: " + getSymbol() );
        }

        if (option.getOptionType().equals(OptionType.CALL)) {
            calls.add(option);
        } else {
            puts.add(option);
        }

    }


    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();
        result.append("Symbol: ").append(getSymbol()).append("\n");
        result.append("Last Price: ").append(getDisplayPrice(lastPrice)).append("\n");
        result.append("Bid Price: ").append(getDisplayPrice(bidPrice)).append("\n");
        result.append("Ask Price: ").append(getDisplayPrice(askPrice)).append("\n");
        result.append("Query Time: ").append(getQueryTime()).append("\n");

        result.append("\n").append(OptionType.CALL).append("\n");
        calls.stream().forEach((option) -> {
            result.append("  ").append(option.toString()).append("\n");
        });

        result.append("\n").append(OptionType.PUT).append("\n");
        puts.stream().forEach((option) -> {
            result.append("  ").append(option.toString()).append("\n");
        });

        return result.toString();
    }


    public String getDisplayPrice(BigDecimal price) {
        return numberFormat.format(price);
    }


    /**
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }


    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }


    /**
     * @return the lastPrice
     */
    public BigDecimal getLastPrice() {
        return lastPrice;
    }


    /**
     * @param lastPrice the lastPrice to set
     */
    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = new BigDecimal(lastPrice);
    }


    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = new BigDecimal(bidPrice);
    }


    public BigDecimal getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(BigDecimal askPrice) {
        this.askPrice = askPrice;
    }

    public void setAskPrice(String askPrice) {
        this.askPrice = new BigDecimal(askPrice);
    }


    /**
     * @return the queryTime
     */
    public String getQueryTime() {
        return queryTime;
    }


    /**
     * @param queryTime the queryTime to set
     */
    public void setQueryTime(String queryTime) {
        this.queryTime = queryTime;
    }

    // TODO: return copy. or not.
    public TreeSet<Option> getCalls() {
        return calls;
    }

    // TODO: return copy
    public TreeSet<Option> getPuts() {
        return puts;
    }

}
