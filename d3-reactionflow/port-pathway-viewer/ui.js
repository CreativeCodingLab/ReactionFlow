

function ui() {
    function my(selection) {}

    var dispatch = d3.dispatch(
        "chooseSorting",
        "animateAll",
        "chooseData"
    );

    var defaultSorting = my.defaultSorting = getSet.bind(this)();

    my.initialize = function(selection) {
        var controls = selection.append("div")
            .classed("controls", true);

        var sections = [
            { label: "Sorting", classed: "sort", func: addSortControls },
            // { label: "Animation", func: addAnimationControls }
            { label: "Data", classed: "data", func: addDataControls }
        ];

        var sectionDivs = controls.selectAll(".section").data(sections);
        sectionDivs.enter().append("div")
            .attr("class", function(d) { return d.classed + " section"; })
            .call(function(enter) {
                enter.append("h3").text(function(d) { return d.label; });
                enter.append("div").each(function(d) {
                    d.func(d3.select(this));
                });
            });

        // d3.selectAll("label.sorting.active").on("click")()

        return my;
    }
    
    function addDataControls(selection) {
        var buttonData = [
            { label: "RAF/MAP kinase cascade", type: "xml", query: "../1_RAF-Cascade.xml", usesRdfAbout: false },
            { label: "Mitotic G1-G1/S phases", type: "xml", query: "../mitotic_g1_g1_s_phases.xml", usesRdfAbout: false },
            { label: "Test Model", type: "xml", query: "../testmodel.xml", usesRdfAbout: true }
        ];
        var button = selection.append("div")
            .classed("btn-group-vertical", true)
            .selectAll("button").data(buttonData);

        button.enter().append("button")
            .classed("btn btn-default", true)
            .text(function(d) { return d.label; })
            .on("click", function() {
                dispatch.chooseData.apply(this, arguments);
            });
    }

    my.triggerDefault = function() {
        d3.selectAll("label.sorting.active").each(function() {
            dispatch.chooseSorting.apply(this, arguments);
        })
    }

    function addAnimationControls(selection) {
        var buttonData = [
            { label: "Animate All" }
        ]
        var button = selection.selectAll("button").data(buttonData);

        button.enter().append("button")
            .classed("btn btn-default", true)
            .text(function(d) { return d.label; })
            .on("click", function() {
                dispatch.animateAll.apply(this, arguments);
            });
    }

    function addSortControls(selection) {
        var sortingTypes = [
            // { label: "none", func: undefined },
            { label: "alphabetical", func: "sortParticipantsByName" },
            { label: "edge crossing", func: "sortParticipantsMinimizingEdgesCrossing" },
            { label: "topological", func: "sortParticipantsByTopologicalOrder" }
        ];

        var buttonGroup = selection.append("div")
            .classed("btn-group-vertical", true)
            .attr("data-toggle", "buttons");

        var button = buttonGroup.selectAll("label").data(sortingTypes);

        var label = button.enter()
            .append("label")
            .classed("btn btn-default sorting", true)
            .classed("active",  function(d, i) {return d.label == defaultSorting(); })
            .on("click", function(d, i) {
                dispatch.chooseSorting.apply(this, arguments);
            });

        label.text(function(d) { return d.label; });

        label.append("input")
            .attr({ type: "radio", name: "sortType" });
    }
    return d3.rebind(my, dispatch, "on"); // dispatch.on("chooseEvent")
}
