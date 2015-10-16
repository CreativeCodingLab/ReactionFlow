var d3 = window.d3;

var width = 1000,
    height = 500,
    visitedResources = [];

var tagAttributeMap = [
    { tagName: "bp:pathwayOrder", attribute: "rdf:resource" },
    { tagName: "bp:stepProcess", attribute: "rdf:resource" },
    { tagName: "bp:left", attribute: "rdf:resource" },
    { tagName: "bp:right", attribute: "rdf:resource" },
    { tagName: "bp:product", attribute: "rdf:resource" },
    { tagName: "bp:component", attribute: "rdf:resource" },
    { tagName: "bp:nextStep", attribute: "rdf:resource" }
];

var tagsToExpand = [
    "bp:pathwayOrder",
    "bp:stepProcess",
    "bp:left",
    "bp:right",
    "bp:product",
    "bp:component",
    "bp:nextStep"
]

var pushResources = [
    "BiochemicalReaction"
]

d3.ns.prefix.owl = "http://www.w3.org/2002/07/owl#";

// var svg = d3.select("body").append("svg").attr({ width: width, height: height });

// var filename = "../mitotic_g1_g1_s_phases.xml";
var filename = "../1_RAF-Cascade.xml"

d3.xml(filename, function(d) {
    
    var firstPathwayID = "Pathway1";
    // var pathway11 = $(d).find("Pathway").filter(function(i, d) { return $(d).attr("rdf:ID") == "Pathway11"; });
    var firstPathway = $(d).find("Pathway").filter(function(i, d) { return $(d).attr("rdf:ID") == firstPathwayID; });
    // console.group("Pathway11");

    var pathwayOrders = firstPathway.find("pathwayOrder");

    // pathwayOrders.each( showAll(d) );

    console.groupEnd();
    
    var children = [];

    pathwayOrders.each( processElement(d, children));

    traverse(children);

    $("body").append("<pre>" + JSON.stringify(children, null, 1) + "<pre>");

    visitedResources = [];

    // pathwayOrders.each( showAll(d) );

});


function traverse(children) {
    var result = [];
    function trav(parent) {
        if (parent.resourceType == "BiochemicalReaction") {

            var leftChildren = parent.children.filter(function(d) { return d.type == "bp:left"});
            var rightChildren = parent.children.filter(function(d) { return d.type == "bp:right"});
            var displayName = parent.children.filter(function(d) { return d.type == "bp:displayName"})[0];


            leftChildren.forEach(function(left) {
                var displayLeft = left.children.filter(function(d) { return d.type == "bp:displayName"})[0];
                rightChildren.forEach(function(right) {
                    var displayRight = right.children.filter(function(d) { return d.type == "bp:displayName"})[0];
                    console.log(parent.name, "-", displayName.text, "-", left.name, "-", displayLeft.text, "-", right.name, "-", displayRight.text);
                })
            })

        }
        if (parent.children) traverse(parent.children);
        // if (child.resourceType == "BiochemicalReaction") console.log(child);
    }

    children.forEach(trav);
}

function processElement(d, pushTo) {
    return function(i, element) {
        var tagName = element.tagName;

        var node = { type: tagName };

        var index = tagsToExpand.indexOf(tagName);

        if (element.textContent) {
            node.text = element.textContent;
        }

        if (index < 0) {
            node.ignored = true;
            pushTo.push(node);
            return;
        }

        var attribute = "rdf:resource";

        var attributeValue = $(element).attr(attribute);

        if (! attributeValue)
            throw new Error(tagName + " does not have attribute " + attribute + ".");

        var resource = attributeValue.substr(1);
        var resourceTagName = resource.replace(/\d+/g, '');

        node.name = resource;
        node.resourceType = resourceTagName;

        // if

        pushTo.push(node);

        var visited = visitedResources.indexOf(resource);
        /* Don't re-visit PathwaySteps */
        if (visited > 0 && resourceTagName === "PathwayStep") {
            node.alreadyVisited = true;
            return;
        }
        if (visited < 0) visitedResources.push(resource);

        var found = $(d).find(resourceTagName).filter(rdfFilter("ID", resource));
        if (found.size() > 1) throw new Error("Found more than one " + resource);
        if (found.size() < 1) throw new Error(resource + " not found");

        var children = found.children();

        node.children = [];

        // if (children.size() > 0) node.children = [];

        children.each(processElement(d, node.children));

    };
}

function showAll(d) {
    return function(i, element) {

        var tagName = element.tagName;

        var index = tagsToExpand.indexOf(tagName);

        if (index < 0) {
            if (element.textContent) console.info(tagName + ": " + element.textContent);
            else console.log(tagName); // + ": No tag mapping found.");
            return;
        }

        var attribute = "rdf:resource";

        var attributeValue = $(element).attr(attribute);

        if (! attributeValue)
            throw new Error(tagName + " does not have attribute " + attribute + ".");

        var resource = attributeValue.substr(1);
        var resourceTagName = resource.replace(/\d+/g, '');

        var text = tagName + ": " + resource;

        console.groupCollapsed(text);

        var visited = visitedResources.indexOf(resource);
        if (visited > 0 && resourceTagName === "PathwayStep") {
            // console.warn("Already visited %s", resource);
            console.groupEnd();
            return;
        }
        if (visited < 0) visitedResources.push(resource);

        found = $(d).find(resourceTagName).filter(rdfFilter("ID", resource));

        if (found.size() > 1) throw new Error("Found more than one " + resource);
        if (found.size() < 1) throw new Error(resource + " not found");

        found.children().each(showAll(d));

        console.groupEnd();

    };
}

function rdfFilter(name, value) {
    return function(i, d) {
        return $(d).attr("rdf:" + name) === value;
    };
}
