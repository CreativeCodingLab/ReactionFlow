var mainG;
    
function pathwayChart() {
    function chart() {}

    // Getters/Setters
    ////////////////////////////////////////////////////

    var margin = chart.margin = getSet.bind(this)(),
        outerWidth = chart.outerWidth = getSet.bind(this)(),
        outerHeight = chart.outerHeight = getSet.bind(this)(),
        columns = chart.columns = getSet.bind(this)(),
        data = chart.data = getSet.bind(this)(),
        nodeRadius = chart.nodeRadius = getSet.bind(this)(3),
        linkOpacity = chart.linkOpacity = getSet.bind(this)(),
        nodeOpacity = chart.nodeOpacity = getSet.bind(this)();

    var width = chart.width = function() {
        return outerWidth() - margin().left - margin().right;
    };

    var height = chart.height = function() {
        return outerHeight() - margin().top - margin().bottom;
    };

    var colScale = chart.colScale = function() {
        return d3.scale.ordinal()
            .domain(columns().map(function(d) { return d.id; }))
            .rangePoints([0, width()]);
    };

    // Events
    /////////////////////////////////////////////////////

    var dispatch = d3.dispatch(
        "mouseenterNode",
        "mouseleaveNode",
        "clickNode"
    );

    dispatch
        .on("mouseenterNode", function(d, i) {
            hoverNode(d3.select(this), d3.event.type);
        })
        .on("mouseleaveNode", dispatch.on("mouseenterNode"))
        .on("clickNode", function(d, i) {
            chart.startAnimation(d);
        });

    function hoverNode(nodeSelection, eventType) {

        var entering = eventType == "mouseenter";

        nodeSelection.each(function(nodeObject) {
            d3.selectAll(".link").select("path")
                .filter(function(path) {
                    return path.source == nodeObject || path.target == nodeObject;
                })
                .each(function(link) {
                    var other = getOtherVertex(link, nodeObject);
                    var otherNode = d3.selectAll(".node").filter(function(d) {
                        return d == other;
                    });
                    if (otherNode.size() != 1) {
                        console.error("Could not find other node for link (probably a smallMolecule).");
                        return;
                    }
                    otherNode
                        .call(function(t) { toggleHighlightNode(t, entering) });
                })
                .call(toggleHighlightPath(entering));
        })
        .call(function(t) { toggleHighlightNode(t, entering); });
    }

    function getOtherVertex(link, nodeObject) {
        if (link.source == nodeObject) return link.target;
        if (link.target == nodeObject) return link.source;
        throw new Error("nodeObject does not match source or target of link.");
    }

    function toggleHighlightPath(highlight) {
        return function(paths) {
            paths
                .transition("highlight")
                .each(function(d) {
                    if (highlight) pushPrevious(this, "style", "stroke-opacity")(d);
                })
                .style({
                    "stroke-opacity": function(d) { 
                        var foo = highlight ? 0.9 : popPrevious(this, "style", "stroke-opacity")(d);
                        return foo;
                    }
                });
        };
    }
    
    function pushPrevious(node, type, name) {
        var id = getId(node, type, name); // node.tagName + '-' + type + '-' + name;
        var value = d3.select(node)[type](name);
        return function(d) {
            if (!d._previous) d._previous = {};
            if (!d._previous[id]) d._previous[id] = [];
            d._previous[id].push(value);
        }
    }
    
    function popPrevious(node, type, name) {
        var id = getId(node, type, name);
        return function(d) {
            if (!d._previous[id]) throw new Error("d._previous[id] not defined.");
            return d._previous[id].pop();
        }
    }
    
    function getId(node, type, name) {
        return node.tagName + '-' + type + '-' + name;
    }

    function toggleHighlightNode(selection, highlight) {
        var transition = selection.transition("highlight");

        transition.select("text")
            .each(function(d) {
                if (!d._oldDx) d._oldDx = d3.select(this).attr("dx");
                if (!d._oldOpacity) d._oldOpacity = d3.select(this).style("fill-opacity");
                if (!d._oldFontSize) d._oldFontSize = d3.select(this).style("font-size");
            })
            .style({
                "fill-opacity": function(d) { return highlight ? 0.9 : d._oldOpacity },
                "font-size": function(d) {
                    var num = parseInt(d._oldFontSize, 10);
                    var newSize = d._oldFontSize.replace(/\d+/, num * 1.5);
                    return highlight ? newSize : d._oldFontSize;
                }
            })
            .attr({
                dx: function(d) {
                    var move = d.side == "left" ? -8 : 8;
                    return highlight ? move : d._oldDx;
                }
            });

        transition.select("circle")
            .each(function(d) {
                if (!d._oldOpacity) d._oldOpacity = d3.select(this).style("fill-opacity");
                if (!d._oldRadius) d._oldRadius = d3.select(this).attr("r");
            })
            .attr({
                r: function(d) { return highlight ? 6 : d._oldRadius; }
            })
            .style({
                "fill-opacity": function(d) { 
                    return highlight ? 0.9 : d._oldOpacity; 
                }
            });
    }

    // Private: Update Nodes and Link Elements
    /////////////////////////////////////////////////////

    function updateLinks(links) {

        var linkGroups = mainG.selectAll(".link").data(links);
        var dash_size = 5;

        linkGroups.enter().append("g").attr({ class: "link" })
            .append("path")
            .style({
                fill: "none",
                "stroke-width": function(d) { return (d.type == "reaction-reaction") ? 3 : 1; }
            }).attr("stroke-dasharray",
                function(d) {
                    if (d.type == "component-complex") {
                        return "5 5";
                    } else return "0";
                }
            );

        linkGroups.each(function(d) {
            d3.select(this)
                .classed(d.source.side, function() { d.source.side ? true : false })
                .classed("source-" + d.source.type, true)
                .classed("target-" + d.target.type, true);
        });

        return linkGroups;
    }

    /**
     * Update one column, adding nodes if necessary.
     * This function does not set node positions.
     */
    function updateColumn(array, type, side) {
        var selector = getSelector(type, side);
        var groups = mainG.selectAll(selector).data(array);

        var groupsEnter = groups.enter().append("g")
            .classed("node", true)
            .classed(type, true)
            .classed(side, side ? true : false)
            .on("mouseenter", function(d, i) {
                dispatch.mouseenterNode.apply(this, arguments);
            })
            .on("mouseleave", function(d, i) {
                dispatch.mouseleaveNode.apply(this, arguments);
            })
            .on("click", function(d, i) {
                dispatch.clickNode.apply(this, arguments);
            })

        groupsEnter
            .append("circle").attr({ r: nodeRadius() });

        var xOffset = 5;

        groupsEnter.append("text")
            .text(function(d, i) {
                var name = d3.select(d.node).select("displayName");
                if (name.size() > 0) return name.text();
                else return type + i;
            })
            .attr({
                // "font-size": "0.5em"
                dx: function(d) { return d.side == "left" ? -xOffset : xOffset },
                "text-anchor": function(d) { return d.side == "left" ? "end" : "start" }
            });
    }

    // Position-Setting Functions
    ////////////////////////////////////////////

    function setAllPositions() {
        var reactions = data().reactions;

        var links = data().links;
        var linkGroups = updateLinks(links);

        updateColumn(reactions, "reaction", null);
        setPositionsForColumn("reaction", null);

        ["complex", "protein"].forEach(function(type) {
            ["left", "right"].forEach(function(side) {
                var thisSide = data().participants[side][type];
                updateColumn(thisSide, type, side);
                setPositionsForColumn(type, side);
            })
        })
    }

    function setPositionsForColumn(type, side) {
        var foundScale = findScale(type, side);
        var rowScale = foundScale ? foundScale.scale : pushNewScale(type, side);

        var columnId = getColumnId(type, side);
        var selector = getSelector(type, side);

        var groups = mainG.selectAll(selector);

        groups
            .each(function(d, i) {
                if (typeof rowScale(i) === "undefined")  debugger;

                d.x = colScale()(columnId);
                d.y = rowScale(i);
            });

        return groups;
    }

    function setPositionsForGroupsWithScale(groups, rowScale) {
        groups
            .each(function(d, i) {
                d.x = colScale()(columnId);
                d.y = rowScale(i); // TODO: This has no order.
            });

        return groups;
    }

    // Scale and Ordering Functions
    /////////////////////////////////////

    var scales = [];
    function pushNewScale(type, side) {
        var rowSize = d3.selectAll(getSelector(type, side)).size();

        var rowScale = d3.scale.ordinal()
            .domain(d3.range(rowSize))
            .rangePoints([0, height()]);

        scales.push({ scale: rowScale, type: type, side: side });

        return rowScale;
    }

    function findScale(type, side) {
        return _.find(scales, function(scale) {
            return (scale.type == type && scale.side == side);
        });
    }

    function setOrderForColumn(type, side, ordering) {
        var oldColumn = getColumnData(type, side).map(function(p) {
            return p.node;
        });
        var newDomain = ordering.map(function(orderedParticipant) {
            return oldColumn.indexOf(orderedParticipant.node);
        });
        if (newDomain.some(function(d) { return d == -1; })) {
            console.error("Ordered participant not found in  %s / %s.", type, side);
        }
        if (newDomain.length != oldColumn.length) {
            console.error("Old (%s) and new (%s) domain length mismatch for %s / %s. Aborting domain update", oldColumn.length, newDomain.length, type, side);
            return;
        }
        setScaleDomain(type, side, newDomain);
    }

    function setScaleDomain(type, side, array) {
        if (array.some(function(d) { return d == -1; })) debugger;
        var found = findScale(type, side).scale;
        found.domain(array);
    }

    var syncColumnOrders = chart.syncColumnOrders = function() {
        ["protein", "complex"].forEach(function(type) {
            var column = {};
            ["left", "right"].forEach(function(side) {
                column[side] = getColumnData(type, side)
                    .map(function(p) { return p.node });
            });
            var newDomain = column.left.map(function(left) {
                return column.right.indexOf(left);
            });
        });
    };

    chart.setOrder = function(ordering) {
        setOrderForColumn("reaction", undefined, ordering.reactions);
        ["complex", "protein"].forEach(function(type) {
            ["left", "right"].forEach(function(side) {
                var t = type == "complex" ? "complexes" : "proteins";
                setOrderForColumn(type, side, ordering[t]);
            });
        });
    };

    chart.getScaleDomain = function(type, side) {
        return findScale(type, side).scale.domain();
    };

    // Helper Functions
    //////////////////////////////////////////////

    function getSelector(type, side) {
        return side ? "." + type + "." + side : "." + type;
    }

    function getColumnId(type, side) {
        return (type == "reaction") ? type : side + "-" + type;
    }

    function getColumnData(type, side) {
        if (type == "reaction") return chart.data().reactions;
        else  return chart.data().participants[side][type];
    }

    // Move Functions: Move elements into position.
    ////////////////////////////////////////////////////////////

    function moveGroups(groups) {
        groups.attr({
            transform: function(d) { return translate(d.x, d.y); }
        });
    }

    function moveAllGroupsAndLinks() {
        var groups = d3.selectAll(".reaction, .protein, .complex");
        moveGroups(groups.transition());

        var paths = d3.selectAll(".link").select("path");
        movePaths(paths.transition());
    }

    function movePaths(paths) {
        var path = d3.svg.line()
            .x(function(d) { return d.x })
            .y(function(d) { return d.y });

        // Based on: https://gist.github.com/mbostock/4163057
        function sample(d, precision) {
            var path = document.createElementNS(d3.ns.prefix.svg, "path");
            path.setAttribute("d", d);

            var length = path.getTotalLength(),
                distance = 0,
                t = [0],
                dt = length / precision;

            while ((distance += dt) <= length) t.push(distance);

            return t.map(function(t) {
                var point = path.getPointAtLength(t);
                return { x: point.x, y: point.y, t: t / length }
            });
        }

        var arcPath = function(startEnd) {
            var start = startEnd[0], end = startEnd[1];
            var radius = Math.sqrt(Math.pow(start.x - end.x, 2) + Math.pow(start.y - end.y, 2)) / 2;
            var pathData = "M" + [start.x, start.y] + " A" + [radius, radius] + " 0 0,1 " + [end.x, end.y+3];

            // Alternative array of points based on path
            // var pointArray = sample(pathData, 50);

            return pathData;

        };

        paths.filter(function(d) { return d.source.x > -1; })
            .attr("d", function(d) {
                return (d.type == "reaction-reaction") ?
                    arcPath([d.source, d.target]) :
                    path([ d.source, d.target ]);
            })
            .style({
                stroke: function(d) {
                    if (d.type != "reaction-reaction") return "black";
                    if (d.target.y > d.source.y) return "url(#arcGradientDown)";
                    return "url(#arcGradientUp)";
                },
            });
    }

    // Animation Functions
    //////////////////////////////////////

    var animationState;
    var executeAnimation = function(node, reverse){
        var speed = 0.3;
        var pathLength = node.getTotalLength();

        var transition = d3.select(node).transition("animate")
            .duration(pathLength/speed)
            .ease("linear")
            .attr("stroke-dashoffset", reverse ? pathLength * 2 : 0);

        animationState.animatedNodes.push(node);

        return transition;
    };

    /*
     * Animate the next series of links, starting from the left proteins to the right proteins.
     * The set of links to animate must be stored in animationState.nextLinks
     */
    var animateNextLinks = function(animateReaction){
        animationState.previousLinks = _.union(animationState.previousLinks, animationState.currentLinks);
        animationState.reactionToReactionLinks = animationState.nextLinks.filter(function(l){return l.type == "reaction-reaction"});
        animationState.currentLinks = animationState.nextLinks.filter(function(l){return l.type != "reaction-reaction"});

        if(animateReaction){
            animationState.currentLinks = animationState.reactionToReactionLinks;
            animationState.reactionToReactionLinks = [];

        }

        animationState.nextLinks = [];

        d3.selectAll("path").each(function(d){
            var dash_size = 5;

            if(animationState.previousLinks.indexOf(d) == -1){
                var pathLength = this.getTotalLength();
                var dash_array = '';
                var dash_count = 0;

                if(d.type == "component-complex"){
                    dash_count = Math.floor(pathLength / dash_size);
                    dash_count = dash_count % 2 ? dash_count : dash_count + 1;
                }

                for(var i = 0; i < dash_count; i++){
                    dash_array += ' ' + dash_size;
                }

                d3.select(this)
                    .attr("stroke-dasharray", dash_array + " " + pathLength)
                    .attr("stroke-dashoffset", pathLength)
                    // .style("stroke-opacity", 1);
                    
                toggleHighlightPath(true)(d3.select(this));
                    
                

            } else {
                //FADE OUT the previous ones
                // d3.select(this)
                    // .attr("stroke-opacity", 0.35);
                // toggleHighlightPath(true)(d3.select(this));
                toggleHighlightPath(false)(d3.select(this));
            }

            if(animationState.currentLinks.indexOf(d) > -1 && d.source.side == "left" && d.source.type == "protein"){
                executeAnimation(this,false).each("end", animateNextLink);
            } else if(animateReaction && animationState.currentLinks.indexOf(d) > -1){
                executeAnimation(this,false).each("end", animateNextLink);
                animationState.nextReactions = _.union(animationState.nextReactions,[d.target]);
            }


        });
    };

    var animateNextLink = function(currentLinkData){
        animationState.animatedNodes = _.without(animationState.animatedNodes, this);


        d3.selectAll("path").each(function(d){

            if(animationState.currentLinks.indexOf(d) > -1){
                if( d.source.side == "left" &&
                    d.source.node == currentLinkData.target.node && d.source.side == currentLinkData.target.side
                    && d.type != "reaction-reaction"){
                    executeAnimation(this, false).each("end", animateNextLink);
                }

                //if(currentLinkData.type != "reaction-reaction" && d.type == "reaction-reaction" && d.source.node == currentLinkData.target.node){
                //    executeAnimation(this, false).each("end", endLinkAnimation);
                //    animationState.nextReactions = _.union(animationState.nextReactions,[d.target]);
                //
                //}


                if(d.source.side == "right"){
                    if(currentLinkData.type == "participant-reaction" && currentLinkData.source.side == "left"){
                        if(d.target.node == currentLinkData.target.node){
                            executeAnimation(this, true).each("end", animateNextLink);
                        }
                    }

                    //complex to proteins right side
                    if(currentLinkData.type == "participant-reaction" && currentLinkData.source.type == "complex"
                        && currentLinkData.source.side == "right"){
                        if(d.target.node == currentLinkData.source.node){
                            executeAnimation(this, true).each("end", endLinkAnimation);
                        }
                    }
                }
            }

        });

        checkForEndOfAllTransitions();
    };

    var endLinkAnimation = function(){
        animationState.animatedNodes = _.without(animationState.animatedNodes, this);
        checkForEndOfAllTransitions();
    };

    var checkForEndOfAllTransitions = function(){
        if(animationState.animatedNodes.length == 0){

            if(animationState.reactionToReactionLinks.length != 0){
                animationState.nextLinks = animationState.reactionToReactionLinks;
                animationState.reactionToReactionLinks = [];
                animateNextLinks(true);

            } else {

                var links = [];

                animationState.nextReactions.forEach(function(r){

                    var newLinks = _.difference(BiopaxUtilties.getAllLinksFromReaction(r, chart.data().links),
                        animationState.previousLinks);
                    links = _.union(links, newLinks);
                });

                animationState.nextLinks = links;
                //Animate the next series
                animateNextLinks();
            }


        }
    };

    chart.resetPathLinesLength = function() {
         d3.selectAll("path").each(function(d){
            var p = this;
            var pathLength = p.getTotalLength();
            d3.select(p)
                .attr("stroke-dasharray", 0)
                .attr("stroke-dashoffset", 0)
                .style("stroke-opacity", 0.2);
                
            // toggleHighlightPath(true)(d3.select(this));
            // toggleHighlightPath(false)(d3.select(this)); // FIXME: this is just covering up a bug
        });
    };

    /* starts a chain of animations */
    chart.startAnimation = function(startParticipant) {
        animationState = {
            nextReactions : [],
            animatedNodes : [],
            previousLinks : [],
            currentLinks : [],
            reactionToReactionLinks : [],
            nextLinks : []
        };

        if(startParticipant.type == 'protein' || startParticipant.type == 'complex') {
            animationState.nextLinks = BiopaxUtilties.getAllLinksFromComponent(startParticipant,
                chart.data().links);
        } else if(startParticipant.type == 'reaction' ) {
            animationState.nextLinks = BiopaxUtilties.getAllLinksFromReaction(startParticipant,
                chart.data().links);
        }

        animateNextLinks();

    };

    // Public
    ///////////////////////////////////////////////////////////

    chart.draw = function() {
        setAllPositions();
        moveAllGroupsAndLinks();

    };

    
    chart.initialize = function(selection) {
        var svg = selection.selectAll(".svg2").data([1]);
        var vbWidth = width() + margin().left + margin().right,
            vbHeight = height() + margin().top + margin().bottom;

        svg.enter().append("svg")
            .attr("class", "svg2");

        svg.append("defs")
            .call(function(d) {
                d.append("linearGradient").attr({
                    id: "arcGradientDown", x1: 0, x2: 0, y1: 1, y2: 0
                })
                .call(setGradientStops);

                d.append("linearGradient").attr({
                    id: "arcGradientUp",   x1: 0, x2: 0, y1: 0, y2: 1
                })
                .call(setGradientStops);
            })


        function setGradientStops(l) {
            l.append("stop").attr({
                offset: "0%",
                "stop-color": "#00f",
                "stop-opacity": 1
            });
            l.append("stop").attr({
                offset: "100%",
                "stop-color": "red",
                "stop-opacity": 0.2
            });
        }

        mainG = svg.style({
                border: "1px solid #ccc",
                width: "100%"
            })
            .attr({ viewBox: "0 0 " + vbWidth + " " + vbHeight })
            .append("g")
            .attr({ transform: translate(margin().left, margin().top) });

        // mainG.append("rect").attr({ width: width(), height: height() })
        //     .style({ fill: "none", stroke: "#444" });

        var column = mainG.selectAll(".column").data(columns());

        var yOffset = -20;

        column.enter().append("g")
            .attr({
                class: "column", id: function(d) { return d.id; },
                transform: function(d) { return translate(colScale()(d.id), 0) }
            })
            .append("text")
            .attr({
                "text-anchor": "middle", dy: yOffset
            })
            .text(function(d) { return d.label; });

        return chart;
    };

    d3.rebind(chart, dispatch, "on")
    return chart;
}
