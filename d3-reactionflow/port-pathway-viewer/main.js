         
//Append a SVG to the body of the html page. Assign this SVG as an object to svg
var width = 1000;
var height = 600;

var svg = d3.select("body").append("svg")
    .attr("width", width)
    .attr("height", height);

//Set up the force layout
/*var force = d3.layout.force()
    .charge(-100)
   // .linkStrength(10)
    .linkDistance(50)
    .gravity(0.05)
    //.friction(0.5)
    .alpha(0.1)
    .size([width, height]);*/
 var force = cola.d3adaptor()
    .linkDistance(30)
    .size([width, height]);   

var cola = cola.d3adaptor()
        .linkDistance(100)
        .avoidOverlaps(true)
        .handleDisconnected(false)
        .size([width, height]);


var nodes, links;
var link, node, nodeText;
var nodeRadius = 10;


var groups2, nodes2, links2;
            
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
            d.name = d3.select(d.node).select("displayName").text();
            nodes.push(d);
        }
            
    });  

    groups2 = [];  
    nodes2 =[]; 
    // Read all pathway information and their direct children 
    chart.data().pathways.forEach(function(d) {
        var node1 = {}; 
        node1.name = d3.select(d.node).attr("rdf:ID");
        node1.displayName = d3.select(d.node).select("displayName").text();
        node1.node = d.node;
        groups2.push(node1);
        
        
        var allCom = d3.select(d.node).selectAll("pathwayComponent")[0];
        allCom.forEach(function(com){
            var node2 ={};
            node2.name = d3.select(com).attr("rdf:resource");
            if (node2.name.indexOf("Pathway") > -1){
                if (!node1.groupNames)
                    node1.groupNames = [];
                node1.groupNames.push(node2); 
            }
            else{
                nodes2.push(node2); // for the node arrayList
                if (!node1.leaves)
                    node1.leaves = [];
                node1.leaves.push(nodes2.length-1); // index of node in nodes2 list    
            }
        });

       
    }); 

    
    // Connect parent-children node
    groups2.forEach(function(d) {
        if (d.groupNames){
            d.groups = [];
            d.groupNames.forEach(function(d2, c){
                var pathwayIndex;
                groups2.forEach(function(d4, i) {
                    var curName = d2.name.substring(1);
                    var name4 = d4.name;
                    if (curName == name4){
                        pathwayIndex = i;
                    }    
                }); 
                if (pathwayIndex>=0){
                    d.groups.push(pathwayIndex);
                   // d.groups[c].isSubPathway = 1;
                }
            });  
        }   
    }); 

    



    // Process links ****************************************************************
    /*
    links = [];    
    chart.data().links.participantReaction.forEach(function(l) {
        var name1= d3.select(l.source.node).select("displayName");  // Participants
        var name2= d3.select(l.target.node).select("displayName");  // Reactions      
      // console.log("side="+l.source.side+" type="+l.source.type+"    name="+name1);
        var node1 =  getNodeByName(nodes,name1);
        var node2 =  getNodeByName(nodes,name2);
        
        var participantSide =  l.source.side;

        if (node1 && node2){
            var newLink = {};
            if (participantSide=="left"){
                newLink.source = node1;
                newLink.target = node2;
            }
            else if (participantSide=="right"){
                newLink.source = node2;
                newLink.target = node1;
            }
            else{
                throw new Error("Something went wrong: Can NOT get side of participant in a reaction");
            }    
            links.push(newLink);
        }
    });  


   
    nodes.forEach(function (v) { v.width = v.height = nodeRadius/5; }); 

    var constraints = {"axis":"y", "left":0, "right":1, "gap":25};
    force.nodes(nodes)
        .links(links)
        .flowLayout("y", 30)
       // .flowLayout("x", 30)
        .symmetricDiffLinkLengths(30)
        .avoidOverlaps(true)
        .start();

    
    force.on("tick", update);    
     
       // define arrow markers for graph links
    svg.append('svg:defs').append('svg:marker')
        .attr('id', 'end-arrow')
        .attr('viewBox', '0 -5 10 10')
        .attr('refX', 8)
        .attr('markerWidth', 5)
        .attr('markerHeight', 5)
        .attr('orient', 'auto')
      .append('svg:path')
        .attr('d', 'M0,-5L10,0L0,5')
        .attr('fill', '#666');

    link = svg.selectAll(".link")
            .data(links)
          .enter().append('svg:path')
            .style("stroke", "#000")
            .attr('class', 'link');




 
    

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

    
       drawColorLegend();*/






    nodes2.forEach(function (v) { 
        v.width = v.height = nodeRadius*4; }); 

    var color = d3.scale.category20();

    svg = d3.select("body").append("svg")
        .attr("width", width)
        .attr("height", height);

    var g;    
    cola
        .nodes(nodes2)
        .links([])
        .groups(groups2)
        .start();
   
        
    var group2 = svg.selectAll(".group2")
        .data(groups2)
      .enter().append("rect")
        .attr("rx", 8).attr("ry", 8)
        .attr("class", "group2")
        .style("fill", function (d, i) { return color(i); });

    var link2 = svg.selectAll(".link2")
        .data([])
      .enter().append("line")
        .attr("class", "link2");

    var pad = 3;
    var node2 = svg.selectAll(".node2")
        .data(nodes2)
      .enter().append("rect")
        .attr("class", "node2")
        .attr("width", function (d) { return d.width - 2 * pad; })
        .attr("height", function (d) { return d.height - 2 * pad; })
        .attr("rx", 5).attr("ry", 5)
        .style("fill", function (d) { return color(groups2.length); })
        .call(cola.drag);

    var label2 = svg.selectAll(".label2")
        .data(nodes2)
       .enter().append("text")
        .attr("class", "label2")
        .text(function (d) { return d.name; })
        .call(cola.drag);

    node2.append("title")
        .text(function (d) { return d.name; });

    cola.on("tick", function () {
       /* link2.attr("x1", function (d) { return d.source.x; })
            .attr("y1", function (d) { return d.source.y; })
            .attr("x2", function (d) { return d.target.x; })
            .attr("y2", function (d) { return d.target.y; });*/

        node2.attr("x", function (d) { return d.x - d.width / 2 + pad; })
            .attr("y", function (d) { return d.y - d.height / 2 + pad; });
        
        group2.attr("x", function (d) { return d.bounds.x; })
             .attr("y", function (d) { return d.bounds.y; })
            .attr("width", function (d) { return d.bounds.width(); })
            .attr("height", function (d) { return d.bounds.height(); });

        label2.attr("x", function (d) { return d.x; })
             .attr("y", function (d) {
                 var h = height;
                 return d.y + h/4;
             });
    });

}


/*
function update(){
   // console.log("update "+link);
    if (link && node){
            link.each(function (d) {
                if (isIE()) this.parentNode.insertBefore(this, this);
            });
            // draw directed edges with proper padding from node centers
            link.attr('d', function (d) {
                var deltaX = d.target.x - d.source.x,
                    deltaY = d.target.y - d.source.y,
                    dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY),
                    normX = deltaX / dist,
                    normY = deltaY / dist,
                    sourcePadding = nodeRadius/1.5,
                    targetPadding = nodeRadius/1.5,
                    sourceX = d.source.x + (sourcePadding * normX),
                    sourceY = d.source.y + (sourcePadding * normY),
                    targetX = d.target.x - (targetPadding * normX),
                    targetY = d.target.y - (targetPadding * normY);
                return 'M' + sourceX + ',' + sourceY + 'L' + targetX + ',' + targetY;
            });

            node.attr("cx", function (d) { return d.x; })
                .attr("cy", function (d) { return d.y; });
          //  nodeText.attr("x", function(d) { return d.x; })
         //   .attr("y", function(d) { return d.y; });
    }    
} */
 function isIE() { return ((navigator.appName == 'Microsoft Internet Explorer') || ((navigator.appName == 'Netscape') && (new RegExp("Trident/.*rv:([0-9]{1,}[\.0-9]{0,})").exec(navigator.userAgent) != null))); }

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

