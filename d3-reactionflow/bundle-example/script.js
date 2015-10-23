
d3.ns.prefix.rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"

var diameter = 960,
radius = diameter / 2,
innerRadius = radius - 120;

function separation(a, b) {
    return (a.parent == b.parent ? 1 : 2) / a.depth;
}

var cluster = d3.layout.cluster()
    .size([360, innerRadius])
    .separation(separation);

var bundle = d3.layout.bundle();

var line = d3.svg.line.radial()
    .interpolate("bundle")
    .tension(.85)
    .radius(function(d) { return d.y; })
    .angle(function(d) { return d.x / 180 * Math.PI; });

var svg = d3.select("body").append("svg")
.attr("width", diameter)
.attr("height", diameter)
.append("g")
.attr("transform", "translate(" + radius + "," + radius + ")");



var owl, root = {};
d3.xml("../mitotic_g1_g1_s_phases.xml", function(data) {
    owl = d3.select(data);

    var pathwayOne = { "rdf": "Pathway1", "children": [], "moreChildren": true };
    root = { "rdf": "", "children": [pathwayOne], "moreChildren": false };

    update(root);

});


function getLinks(nodes) {
    var links = [];

    nodes.forEach(function(node) {
        if (node.parent) {
            var source = node;
            node.parent.children.forEach(function(target) {
                links.push({ source: source, target: target })
            })
        }
    })

    return links;
}

function update(root) {

    var nodes = cluster.nodes(root);

    var links = getLinks(nodes);

    var link = svg.selectAll(".link")
        .data(
            bundle(links),
            function(array) { return array.map(function(d) { return d.rdf }).join("-"); }
        )

    link.transition()
        .attr("d", line);

    link.enter().append("path")
        .attr("class", "link")
        .attr("d", line);

    link.exit().remove();

    var node = svg.selectAll(".node")
        .data(nodes, function(d) { return d.rdf });

    node.transition()
        .attr("transform", function(d) { return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")"; });

    node.enter().append("g")
        .attr("class", "node")
        .attr("transform", function(d) { return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")"; })
        .on("mouseover", mouseover).on("mouseout", mouseout)
        .append("text")
        .attr("dx", function(d) { return d.x < 180 ? 8 : -8; })
        .attr("dy", ".31em")
        .attr("text-anchor", function(d) { return d.x < 180 ? "start" : "end"; })
        .attr("transform", function(d) { return d.x < 180 ? null : "rotate(180)"; })
        .text(function(d) { return (d.rdf) ? d.rdf : d.name ; })
        .on("click", function(d) {
            if (! d.children) {
                d.children = getChildren(selectByRDF(d.rdf));
                if (d.children.length > 0) d.moreChildren = false;
                update(root);
            } else if (d.children && d.children.length > 0) {
                d.children = [];
                d.moreChildren = true;
                update(root);
            }
        })

        function mouseover(d) {
            var node = d3.select(this);
            if (d.children)
                node.classed("fade", false);
        }

        function mouseout(d) {
            var node = d3.select(this);
            if (d.children)
                node.classed("fade", true);
        }

    node
        .style({ fill: function(d) { return (d.moreChildren) ? "red" : "black" } })
        .classed("fade", function(d) { return (d.children) })

    node.exit().remove();
}

var childTags = { // This is a Map(tagName -> childrenAccessor)
    "bp:Pathway": ["pathwayOrder"],
    "bp:PathwayStep": ["stepProcess"],
    "bp:BiochemicalReaction": ["left", "right"],
    "bp:Complex": ["component"]
};

function hasChildren(selection) {
    var node = selection.node();
    var tagName = node.tagName;
    var childSelectors = childTags[tagName];

    if (childSelectors) {
        return childSelectors.some(function(childSelector) {
            var sel = d3.select(node).selectAll(childSelector);
            if (! sel.empty()) return true;
        })
    }
}

var visited = [];
function getChildren(selection) {
    var node = selection.node();
    var tagName = node.tagName;
    var childSelectors = childTags[tagName];

    if (childSelectors) {
        var children = [];
        childSelectors.forEach(function(childSelector) {
            d3.select(node).selectAll(childSelector)
                .each(function(d) {
                    var resource = d3.select(this).attr("rdf:resource");
                    var rdfId = resource.substr(1);
                    visited.push(rdfId);
                    var selection = selectByRDF(rdfId);
                    children.push({
                        "rdf": rdfId, "children": [],
                        "moreChildren": hasChildren(selection)
                    });
                })
        })
        return children;
    } else {
        console.info("No child selector for %s", tagName);
    }
}

var visited = [];
function traverseSelection(selection) {
    var node = selection.node();
    var tagName = node.tagName;
    var childSelector = childTag[tagName];

    if (childSelector) {
        d3.select(node).selectAll(childSelector)
            .each(function(d) {
                var resource = d3.select(this).attr("rdf:resource");
                var rdfId = resource.substr(1);
                // if (visited.indexOf(rdfId) > -1) {
                //     console.info("Already visited this resource.");
                //     return;
                // }
                visited.push(rdfId);
                var selection = selectByRDF(rdfId);
                traverseSelection(selection);
            })
    } else {
        console.info("No child selector for %s", tagName);
    }
}

function selectByRDF(rdfId) {
    var tagName = rdfId.replace(/\d+/g, '');
    return owl.selectAll(tagName)
        .filter(function(element) {
            return d3.select(this).attr("rdf:ID") == rdfId;
        });
}
