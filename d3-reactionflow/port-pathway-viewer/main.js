         
//Append a SVG to the body of the html page. Assign this SVG as an object to svg
var width = 1000;
var height = 600;

var svg = d3.select("body").append("svg")
    .attr("width", width)
    .attr("height", height);

//Set up the force layout
var force = d3.layout.force()
    .charge(-100)
   // .linkStrength(10)
    .linkDistance(50)
    .gravity(0.05)
    //.friction(0.5)
    .alpha(0.1)
    .size([width, height]);
var nodes, links;
var link, node, nodeText;

function vis() {
    nodes = [];    
    ["complex", "protein"].forEach(function(type) {
        ["left", "right"].forEach(function(side) {
            chart.data().participants[side][type].forEach(function(d) {
                if (isContainedChild(nodes, d)<0){
                    d.type = type;
                    d.name = d3.select(d.node).select("displayName").text();
                    nodes.push(d);
                }
                    
            });    
        });
    });
    chart.data().reactions.forEach(function(d) {
        if (isContainedChild(nodes, d)<0){
            d.type = d.type;
            d.name = d3.select(d.node).select("displayName").text();
            nodes.push(d);
        }
            
    });    


    links = [];    
    chart.data().links.participantReaction.forEach(function(l) {
        var name1= d3.select(l.source.node).select("displayName");
        var name2= d3.select(l.target.node).select("displayName");
        //console.log("side="+side+" type="+type+" "+d.y+"    name="+name.text());
        var node1 =  getNodeByName(nodes,name1);
        var node2 =  getNodeByName(nodes,name2);
        if (node1 && node2){
            var newLink = {};
            newLink.source = node1;
            newLink.target = node2;
            links.push(newLink);
        }
    });  

    force.nodes(nodes)
        .links(links)
        .start();  
    force.on("tick", update);    
     
    link = svg.selectAll(".link")
      .data(links)
    .enter().append("line")
      .attr("class", "link")
      .style("stroke", "#f00")
      .style("stroke-width", function(d) { return Math.sqrt(d.value); });

    node = svg.selectAll(".node")
      .data(nodes)
    .enter().append("circle")
      .attr("class", "node")
      .attr("r", 5)
      .style("fill", function(d) { return getColor(d.type); })
      .call(force.drag);

    nodeText = svg.selectAll(".nodeText")
      .data(nodes)
    .enter().append("text")
      .attr("class", "nodeText")
      .attr("dx", "5px")
      .style("fill", function(d) { return getColor(d.type); })
      .attr("font-family", "sans-serif")
        .attr("font-size", "11px")
        .style("text-anchor", "left")
       .text(function(d) { return d.name; });

    

}


function update(){
    console.log("update "+link);
    if (link && node){
        link.attr("x1", function(d) { return d.source.x; })
            .attr("y1", function(d) { return d.source.y; })
            .attr("x2", function(d) { return d.target.x; })
            .attr("y2", function(d) { return d.target.y; });

        node.attr("cx", function(d) { return d.x; })
            .attr("cy", function(d) { return d.y; });
        nodeText.attr("x", function(d) { return d.x; })
            .attr("y", function(d) { return d.y; });
    }    
} 

// check if a node already exist.
function isContainedChild(a, element) {
    if (a){
        for (var i=0; i<a.length;i++){
            var name1 = d3.select(a[i].node).select("displayName");
            var name2 = d3.select(element.node).select("displayName");
            if (name1.text()==name2.text())
                return i;
        }
    }
    return -1;
}

// check if a node already exist.
function getNodeByName(a, name2) {
    if (a){
        for (var i=0; i<a.length;i++){
            var name1 = d3.select(a[i].node).select("displayName");
            if (name1.text()==name2.text())
                return a[i];
        }
    }
    return undefined;
}

