package com.accounted4.stockquote;

import com.accounted4.stockquote.api.QuoteAttribute;
import com.accounted4.stockquote.api.QuoteService;
import com.accounted4.stockquote.api.QuoteServiceException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.cli.*;


/**
 * Command Line Interface utility for interactive quick quotes.
 *
 * @author Glenn Heinze <glenn@gheinze.com>
 */
public class Query {


    private static final String VERSION = "0.1";

    private final Option helpOption = new Option("help", "print this message");
    private final Option versionOption = new Option("version", "print the version information and exit");
    private final Option showServicesOption = new Option("showServices", "list the stock quoting services configured for queries");
    private final Option showAttributesOption = new Option("showAttributes", "list attributes that may be retrieved");

    private final Option serviceOption = Option.builder("service")
            .desc("query the named service")
            .hasArg()
            .argName("serviceName")
            .build()
            ;

    private final Option symbolsOption = Option.builder("symbols")
            .desc("comma separated list of ticker symbols to query for (example: ORCL")
            .hasArg()
            .argName("tickerSymbols")
            .build()
            ;

    private final Option attributesOption = Option.builder("attributes")
            .desc("comma separated list of attributes to query for (example: LAST_TRADE_PRICE")
            .hasArg()
            .argName("queryAttributes")
            .build()
            ;

    private final Options options;


    private final HelpFormatter formatter = new HelpFormatter();
    private final CommandLineParser parser = new GnuParser();
    private final QueryBuilder queryBuilder = new QueryBuilder();



    public Query() {
        options = createOptions();
    }


    private Options createOptions() {
        Options result = new Options();
        result.addOptionGroup(createOptionGroup());
        result.addOption(serviceOption);
        result.addOption(symbolsOption);
        result.addOption(attributesOption);
        return result;
    }


    private OptionGroup createOptionGroup() {
        OptionGroup group = new OptionGroup();
        group.addOption(helpOption);
        group.addOption(versionOption);
        group.addOption(showServicesOption);
        group.addOption(showAttributesOption);
        return group;
    }



    public CommandLine parseCommandLine(String[] args) throws ParseException {
        return parser.parse(options, args);
    }


    public void processCommandLine(String[] args) {

        String errorMsg;

        try {

            CommandLine cmdLine = parseCommandLine(args);

            if ( commandHasHelpRequest(cmdLine) ||
                 commandHasVersionRequest(cmdLine) ||
                 commandHasShowSupportedServicesRequest(cmdLine) ||
                 commandHasShowSupportedQueryAttributesRequest(cmdLine) ||
                 executeQuery(cmdLine)
            ) {
                // Command line was processed via short-circuit
                return;
            }

            errorMsg = "Failure to process command line";

        } catch(ParseException pe) {
            errorMsg = "Malformed command";
        } catch(QuoteServiceException qse) {
            errorMsg = "Failure executing service command";
        }

        System.out.println(errorMsg);
        formatter.printHelp( "Query", options );

    }


    private boolean commandHasHelpRequest(CommandLine cmdLine) {
        // No options or asking for help, print help and escape
        if (cmdLine.getOptions().length <= 0 || cmdLine.hasOption(helpOption.getOpt())) {
            formatter.printHelp( "Query", options );
            return true;
        }
        return false;
    }



    private boolean commandHasVersionRequest(CommandLine cmdLine) {
        if (cmdLine.hasOption(versionOption.getOpt())) {
            System.out.println("version: " + VERSION);
            return true;
        }
        return false;
    }



    private boolean commandHasShowSupportedServicesRequest(CommandLine cmdLine) {

        if (cmdLine.hasOption(showServicesOption.getOpt())) {
            List<QuoteService> supportedQuoteServices = queryBuilder.getSupportedQuoteServices();
            System.out.println("Discovered services: ");
            supportedQuoteServices.stream()
                    .map(s -> "  " + s.getServiceName())
                    .forEach(System.out::println)
                    ;
            return true;
        }

        return false;
    }



    private boolean commandHasShowSupportedQueryAttributesRequest(CommandLine cmdLine) {

        if (cmdLine.hasOption(showAttributesOption.getOpt())) {
            System.out.println("Supported query attributes: ");
            for (QuoteAttribute attribute : QuoteAttribute.values()) {
                System.out.println("  " + attribute);
            }
            return true;
        }

        return false;

    }



    private boolean executeQuery(CommandLine cmdLine) throws QuoteServiceException {

        String selectedSymbols = cmdLine.getOptionValue(symbolsOption.getOpt());

        // If no query attributes were specified, default to LAST_TRADE_PRICE
        String selectedAttributes = cmdLine.getOptionValue(attributesOption.getOpt(), QuoteAttribute.LAST_TRADE_PRICE.toString());

        QuoteService quoteService = findRequestedService(cmdLine);

        List<String> enteredSymbols = Arrays.stream(selectedSymbols.split(","))
                .map(s -> s.trim())
                .collect(Collectors.toList())
                ;

        // Build the list of attributes based on entered csv
        ArrayList<QuoteAttribute> attrList = new ArrayList<>();
        for (String s : selectedAttributes.split(",")) {
            try {
                attrList.add(QuoteAttribute.valueOf(s));
            } catch (IllegalArgumentException iae) {
                System.out.println("Ignoring unrecognized attribute: " + s);
            }
        }

        // Query the service
        List<HashMap<QuoteAttribute, String>> result = quoteService.executeQuery(enteredSymbols, attrList);

        // Dump the result
        result.stream().map((line) -> {
            System.out.println();
            return line;
        }).forEach((line) -> {
            line.entrySet().stream()
                    .forEach((entry) -> {
                        System.out.println("  " + entry.getKey().toString() + " = " + entry.getValue());
            });
        });

        return true;

    }


    private QuoteService findRequestedService(CommandLine cmdLine) throws QuoteServiceException {

        String requestedService = cmdLine.getOptionValue(serviceOption.getOpt());

        // Find the named quote service
        List<QuoteService> supportedQuoteServices = queryBuilder.getSupportedQuoteServices();

        Optional<QuoteService> discoveredService = supportedQuoteServices.stream()
                .filter(s -> s.getServiceName().equalsIgnoreCase(requestedService))
                .findAny();

        return discoveredService.orElseThrow(
                () -> new QuoteServiceException(
                        String.format(
                                "Service %s was not discovered. Check classpath?",
                                requestedService
        )));

    }



    /* ---------------------------
     * Interactive
     *     -showServices
     *     -showAtributes
     *     -service <serviceName> -symbols <symbol>[,<symbol>] -attributes <attr>[,<attr>]
     *     -help
     * ---------------------------
     */
    public static void main(String[] args) {

        Query query = new Query();
        query.processCommandLine(args);

    }


}
