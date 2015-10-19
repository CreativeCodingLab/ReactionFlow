var d3 = window.d3, _ = window._, Bacon = window.Bacon;

var dispatch = d3.dispatch(
    "foundBioPax"
);

(function main() {
    d3.ns.prefix.rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    var main = d3.select("body").append("main");
    main.append("row").append("h1").text("ReactionFlow");

    // Mappings (to reduce magic text)
    ////////////////////////////////////////////////

    var idMapping = {
        links: {
            participantReaction: "participant-reaction",
            reactionReaction: "reaction-reaction",
            componentComplex: "component-complex"
        },
        reactions: {
            biochemicalReaction: "reaction"
        }
    };

    // Chart
    /////////////////////////////////////////////////

    var columns = [
        { id: "left-protein", label: "Proteins" },
        { id: "left-complex", label: "Complexes" },
        { id: "column-3", label: null },
        { id: "reaction", label: "Reactions" },
        { id: "column-5", label: null},
        { id: "right-complex", label: "Complexes" },
        { id: "right-protein", label: "Proteins" },
    ];

    var chartRow = main.append("row").classed("chart", true);

    var chartColumn = chartRow.append("div").classed("col-xs-10", true);

    var chart = window.chart = pathwayChart() // FIXME: window.chart for dev only
        .margin({ top: 50, right: 120, bottom: 50, left: 100 })
        .outerWidth(1200).outerHeight(700)
        .columns(columns)
        .initialize(chartColumn);

    // Parser
    /////////////////////////////////////////////////
    var parser = biopaxParser().idMapping(idMapping);

    // User Interface
    /////////////////////////////////
    var uiColumn = chartRow.append("div").classed("col-xs-2", true);
    var controlsUi = ui().defaultSorting("alphabetical").initialize(uiColumn);
   
    // Sorting Utility
    /////////////////////////////////
    var sortUtility = sortingUtilities();

    // Events
    /////////////////////////////////
    dispatch.on("foundBioPax", function(biopax) {
        parser.biopax(biopax);

        var data = {
            reactions: parser.reactions(),
            participants: parser.participants(),
            links: parser.links(),
            pathways: parser.pathways()
        };
        chart.data(data).draw();
        controlsUi.triggerDefault();
    });


    // Tuan's code
    // var d = {label: "RAF/MAP kinase cascade", type: "xml", query: "../mitotic_g1_g1_s_phases.xml", usesRdfAbout: false};
    var d = {label: "RAF/MAP kinase cascade", type: "xml", query: "../1_RAF-Cascade.xml", usesRdfAbout: false};
    parser.usesRdfAbout(d.usesRdfAbout);
    d3.selectAll(".controls .data button").attr("disabled", true);
    if (d.type == "xml") {
        var callback = function(d) { 
            dispatch.foundBioPax(d3.select(d)); 
            vis();  
        };
        d3.xml(d.query, "application/xml", callback);
    }
   
       
    
    controlsUi.on("chooseSorting", function(d) {
        var data = chart.data(),
            participants = data.participants,
            reactions = data.reactions,
            links = data.links;
        // Get the function based on the button event data
        var func = sortUtility[d.func];

        if (func) {
            var newOrder = func(participants, reactions, links);
            chart.setOrder(newOrder);
            chart.syncColumnOrders();
            chart.draw();
            chart.resetPathLinesLength();
        }
        // var f = sortUtility[d.func];
        // sortingObject[d.signal]    
    });
    
    controlsUi.on("chooseData", function(d) {
        parser.usesRdfAbout(d.usesRdfAbout);
        d3.selectAll(".controls .data button").attr("disabled", true);
        if (d.type == "xml") {
            var callback = function(d) { dispatch.foundBioPax(d3.select(d)); };
            d3.xml(d.query, "application/xml", callback);
        }
        //debugger;
        // if (d.type == "json") d3.json
        // var request = d3[d.type](d.query)
    });

    controlsUi.on("animateAll", function() {
        //TODO TESTING PURPOSES
        // chart.animation();
        chart.startAnimation(chart.data().participants.right.complex[0]);
    });
    
    // To use the API:
    /////////////////////////////////
    // parser.attributeName("rdf:about");
    // var query = "http://www.pathwaycommons.org/pc2/search.json?q=raf%20cascade&type=pathway";
    // var request = d3.json(query)
    //     .on("load", function(d) { getBioPaxFromUri(d.searchHit[0].uri); });

    // To use static file:
    /////////////////////////////////
    // parser.usesRdfAbout(false); // ID");
    // var query = "../mitotic_g1_g1_s_phases.xml"; //"../testmodel.xml"; // "../1_RAF-Cascade.xml";
    // var request = d3.xml(query, "application/xml", function(d) {
    //     dispatch.foundBioPax(d3.select(d));
    // });

    // Launch
    /////////////////////////////////
    // request.get();


}());

function getBioPaxFromUri(uri) {
    var query = "http://www.pathwaycommons.org/pc2/get?uri=" + uri;
    d3.xml(query, "application/xml", function(d) {
        dispatch.foundBioPax(d3.select(d));
    });
}
