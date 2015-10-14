var d3 = window.d3, _ = window._;

d3.ns.prefix.rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

var query = "http://www.pathwaycommons.org/pc2/search.json?q=raf%20cascade&type=pathway";

var dispatch = d3.dispatch(
    "foundPathwayUri", 
    "foundBioPax",
    "foundPathwaySteps",
    "foundReactions",
    "reactionsUpdated",
    "foundAllParticipants"
);

var margin = { top: 50, right: 50, bottom: 10, left: 50 },
    width = 900 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;
    
var columns = [
    { id: "left-proteins", label: "Proteins" },
    { id: "left-complexes", label: "Complexes" },
    { id: "column-3", label: null },
    { id: "reactions", label: "Reactions" },
    { id: "column-5", label: null},
    { id: "right-complexes", label: "Complexes" },
    { id: "right-proteins", label: "Proteins" },
];
    
var colScale = d3.scale.ordinal()
    .domain(columns.map(function(d) { return d.id }))
    .rangePoints([0, width]);

dispatch.on("foundPathwayUri", getBioPaxFromUri);
// dispatch.on("foundBioPax.steps", getAllPathwaySteps); // TODO
dispatch.on("foundBioPax", getAllReactionsArray);
// dispatch.on("foundPathwaySteps", updatePathwaySteps);
// dispatch.on("foundReactions.a", updateReactions);
dispatch.on("foundReactions", getAllParticipants);
dispatch.on("foundAllParticipants", positionParticipants);

// dispatch.on("reactionsUpdated", updateParticipants);

initializeElements();
searchForPathway(query);

function positionParticipants(allParticipants) {
    
}

function getAllParticipants(nodes) {
    var allParticipants = [];
    var links = [];
    var reactionsArray = nodes.filter(function(d) { return d.type == "reaction"; });
    
    reactionsArray.forEach(function(reaction) {
        ["left", "right"].forEach(function(side) {
            var participantsOneSide = getParticipantsForReactionSide(reaction.node, side);
            
            // Create links
            participantsOneSide.forEach(function(participant) {
                var found = _.find(allParticipants, function(p) { return p.node === participant.node});
                var source = found ? found : participant;
                links.push({ source: source, target: reaction });
            });
            
            allParticipants = allParticipants.concat(participantsOneSide);
        });
    });
    
    dispatch.foundAllParticipants(allParticipants);
}

// function updateParticipants(reactionSelection) {
//     var allParticipants = [];
//     reactionSelection.each(function(reaction) {
//         // var participants = getParticipants(reaction, "left");
//         allParticipants = allParticipants
//             .concat(
//                 getParticipants(reaction, "left"), 
//                 getParticipants(reaction, "right")
//             );
//     });
//     allParticipants = _.uniq(allParticipants);
// }

function getParticipantsForReactionSide(reaction, side) {
    var participantSel = d3.select(reaction).selectAll(side);
        
    var participants = [];
        
    participantSel.each(function() {
            var resourceId = d3.select(this).attr("rdf:resource");
            var participantNode = findParticipantNode(resourceId);
            participants.push({ node: participantNode, side: side });
        });
        
    return participants;
}

// Given a resource identifier, find participant nodes.
function findParticipantNode(resourceId) {
    // FIXME: Better way of finding participants?
    // The problem is selecting based on namespaced attributes.
    var participant = biopax
        .selectAll('Protein, Complex, SmallMolecule, Dna') // FIXME: Hard coded selection.
        .filter(function() { 
            return d3.select(this).attr("rdf:about") == resourceId;
        });
    if (participant.size() !== 1) 
        throw new Error("participants length !== 1", resourceId);
    return participant.node();
}

// function pushParticipants(participants) {

var mainG;
function initializeElements() {
        
    var svg = d3.select("body").selectAll("svg").data([1]);
    
    mainG = svg.enter()
        .append("svg")
        .style({
            border: "1px solid #ccc",
            width: width + margin.left + margin.right,
            height: height + margin.top + margin.bottom
        })
        .append("g")
        .attr({
            transform: translate(margin.left, margin.top)
        });
        
    mainG.append("rect").attr({ width: width, height: height })
        .style({ fill: "none", stroke: "#444" });
        
    var column = mainG.selectAll(".column").data(columns);
    
    var yOffset = -10;
                                                                      
    column.enter().append("g")
        .attr({
            class: "column", id: function(d) { return d.id; },
            transform: function(d) { return translate(colScale(d.id), 0) }
        })
        .append("text")
        .attr({
            "text-anchor": "middle", dy: yOffset
        })
        .text(function(d) { return d.label; });
        
    // dispatch.ready(mainG);
}

// function updatePathwaySteps(steps) {
//     console.log("steps", steps);
// }

function updateReactions(reactionsArray) {
    // console.log("reactions", reactions);
    
    // var reactionsArray = selectionToArray(reactionsSelection);
    
    var reaction = mainG//.select(".column#reactions")
        .selectAll(".reaction").data(reactionsArray);
        
    var rowScale = d3.scale.linear()
        .domain([0, reactionsArray.length - 1])
        .range([0, height]);
    
    reaction.enter().append("g").attr({ class: "reaction" })
        .append("circle")
        .attr({ r: 2 });
    
    reaction
        .each(function(d, i) {
            d.x = colScale("reactions"); 
            d.y = rowScale(i);
        })
        .attr({ 
            transform: function(d) { return translate(d.x, d.y); }
        });
        
    dispatch.reactionsUpdated(reaction);
        
    // var links = [];
    
    // reactionsArray.forEach(function(reaction) {
    //     var id = d3.select(reaction).attr("rdf:about");
    //     var stepProcess = biopax.selectAll("stepProcess")
    //         .filter(function() { 
    //             return d3.select(this).attr("rdf:resource") == id 
    //         });
    //     var pathwayStep = stepProcess.node().parentNode;
    //     var nextSteps = d3.select(pathwayStep).selectAll("nextStep");
    //     // console.log(nextSteps.size());
    //     if (nextSteps.size())
    //         nextSteps.each(function() {
    //             var pathwayStepId = d3.select(this).attr("rdf:resource").substr(1);
    //             var nextPathwayStep = biopax.selectAll("PathwayStep")
    //                 .filter(function() { 
    //                     return d3.select(this).attr("rdf:ID") == pathwayStepId; 
    //                 });
    //             // console.log(nextPathwayStep.node());
    //             // *************************
    //             // AND SO the next pathway step might be another pathway altogether...
    //         });
    // })
}

function getAllReactionsArray(biopax) {
    var reactionsSelection = biopax.selectAll("BiochemicalReaction");
    var nodes = selectionToArray(reactionsSelection)
        .map(function(d) { return { node: d, type: "reaction" } });
    dispatch.foundReactions(nodes);
}

function getAllPathwaySteps(biopax) {
    var steps = biopax.selectAll("PathwayStep");
    dispatch.foundPathwaySteps(steps);
}

var biopax;
function getBioPaxFromUri(uri) {
    var query = "http://www.pathwaycommons.org/pc2/get?uri=" + uri;
    d3.xml(query, "application/xml", function(d) {
        biopax = d3.select(d);
        dispatch.foundBioPax(biopax);
    });
}

function searchForPathway(query) {
    d3.json(query, function(d) { 
        dispatch.foundPathwayUri(d.searchHit[0].uri);
    });
}

////////////////////////////////////////////////
// Helper Functions

function selectionToArray(selection) {
    var array = [];
    selection.each(function() { array.push(this); });
    return array;
}

function translate(x, y) { return "translate(" + x + "," + y + ")"; }

// var d3 = window.d3;

// var width = 900,
//     height = 500;

// var svg = d3.select("body").append("svg")
//     .attr({ width: width, height: height })
//     .style({ border: "1px solid #ccc" });

// d3.xml("../mitotic_g1_g1_s_phases.xml", function(d) {
//     console.log(d);
//     // var pathway11 = $(d).find("Pathway").filter(function(i, d) { return $(d).attr("rdf:ID") == "Pathway11"; });
//     //
//     // var pathwayOrders = pathway11.find("pathwayOrder");
//     //
//     // console.log(pathwayOrders);

// });
