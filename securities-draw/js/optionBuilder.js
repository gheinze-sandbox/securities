/* global d3 */


var a4 = {};



// Bootstrap

$(document).ready(function () {
    a4.init();
});



(function(exports) {

    "use strict";

    /* Sizing attributes specifying the generated svg element */
    var optionSvgExtents = defaultOptionSvgExtents();


    exports.optionType = optionType();
    exports.Option = Option;
    exports.generateOptionStateSvg = generateOptionStateSvg;
    exports.init = init;



    function optionType() {
        return { CALL: 'CALL', PUT: 'PUT' };
    }


    function Option(type, underlyingCommodityPriceAtPurchase, strikePrice, premium) {

        this.optionType =
                type === exports.optionType.CALL ?
                         exports.optionType.CALL :
                         exports.optionType.PUT
                 ;

        this.underlyingCommodityPriceAtPurchase = underlyingCommodityPriceAtPurchase;
        this.strikePrice = strikePrice;
        this.premium = premium;
    }


    function init() {
    }




    /* A fixed size for the svg element representing a CALL or PUT option */
    function defaultOptionSvgExtents() {
        var full = {w: 100, h: 25};
        var margin = {top: 0, right: 0, bottom: 0, left: 0};
        var dataArea = {
            x: margin.left,
            y: margin.bottom,
            w: full.w - margin.left - margin.right,
            h: full.h - margin.top - margin.bottom
        };
        return {full: full, margin: margin, dataArea: dataArea};
    }



    /**
     * Add an svg element to a container representing a "spark" diagram of the
     * current state wrt strike price of a CALL or PUT option. The state is
     * represented by a price line:
     *   - solid line indicates ownership of the underlying commodity on expiry
     *   - dashed line indicates non-ownership of the underlying commodity on expiry
     *   - black dot was the price of the underlying commodity at time of option purchase
     *   - green triagle represents current price of underlying commodity
     *
     * @param {Element} container The container into which the svg element will be placed.
     * @param {Option} option The Option object to plot.
     * @param {number} currentPrice The current price of the underlying commodity of the Option.
     * @param {start: number, end: number} plotRange The price range of the underlying commodity that should
     * be inclued in the graph, specified by "start" and "end" attributes.
     * @returns {undefined}
     */
    function generateOptionStateSvg(container, option, currentPrice, plotRange) {

        var xScale = d3.scaleLinear()
                .domain([plotRange.start, plotRange.end])
                .rangeRound([
                    optionSvgExtents.dataArea.x,
                    optionSvgExtents.dataArea.x + optionSvgExtents.dataArea.w
                ])
                ;

        var svg = container
                .append("svg")
                .attr("width", optionSvgExtents.full.w)
                .attr("height", optionSvgExtents.full.h)
                ;

        var midHeight = optionSvgExtents.dataArea.y + optionSvgExtents.dataArea.h / 2;

        var lineUpToStrikePrice = svg.append("line")
                .attr("x1", function(d) { return xScale(plotRange.start); })
                .attr("y1", midHeight)
                .attr("x2", function(d) { return xScale(option.strikePrice); })
                .attr("y2", midHeight)
                .attr("stroke-width", 1)
                .attr("stroke", "black")

        ;

        var lineAfterStrikePrice = svg.append("line")
                .attr("x1", function(d) { return xScale(option.strikePrice); })
                .attr("y1", midHeight)
                .attr("x2", function(d) { return xScale(plotRange.end); })
                .attr("y2", midHeight)
                .attr("stroke-width", 1)
                .attr("stroke", "black")
        ;


        if (option.optionType === 'CALL') {
            lineAfterStrikePrice.style("stroke-dasharray", ("3, 3"));
        } else {
            lineUpToStrikePrice.style("stroke-dasharray", ("3, 3"));
        }

        // underlying price at time of purchase
        svg.append("circle")
                .attr("cx", function(d) { return xScale(option.underlyingCommodityPriceAtPurchase); })
                .attr("cy", midHeight)
                .attr("r", 3)
        ;

        // current price

        var color = "green";
        var triangleSize = 25;
        var sqrt3 = Math.sqrt(3);
        var verticalTransform = midHeight + Math.sqrt(triangleSize / (sqrt3 * 3)) * 2;

        var triangle = d3.symbol()
                .type(d3.symbolTriangle)
                .size(triangleSize)
        ;

        svg.append("path")
                .attr("d", triangle)
                .attr("stroke", color)
                .attr("fill", color)
                .attr("transform", function(d) { return "translate(" + xScale(currentPrice) + "," + verticalTransform + ")"; });
        ;

    }



})(a4);
