

////////////////////////////////////////////////
// Helper Functions

  // Add color legend
function drawColorLegend() {
      var xx = 10;
      var y1 = 20;
      var y2 = 34;
      var y3 = 48;
      var y4 = 62;
      var rr = 6;

      svg.append("circle")
        .attr("class", "nodeLegend")
        .attr("cx", xx)
        .attr("cy", y1)
        .attr("r", rr)
        .style("fill", "#f00");
      
      svg.append("text")
        .attr("class", "nodeLegend")
        .attr("x", xx+10)
        .attr("y", y1+2)
       .text("Protein")
        .attr("dy", ".21em")
        .attr("font-family", "sans-serif")
        .attr("font-size", "12px")
        .style("text-anchor", "left")
        .style("fill", "#f00");
   
      svg.append("circle")
        .attr("class", "nodeLegend")
        .attr("cx", xx)
        .attr("cy", y2)
        .attr("r", rr)
        .style("fill", "#00f");  

      svg.append("text")
        .attr("class", "nodeLegend")
        .attr("x", xx+10)
        .attr("y", y2+2)
        .text("Complex")
        .attr("dy", ".21em")
        .attr("font-family", "sans-serif")
        .attr("font-size", "12px")
        .style("text-anchor", "left")
        .style("fill", "#00f");  

       svg.append("circle")
        .attr("class", "nodeLegend")
        .attr("cx", xx)
        .attr("cy", y3)
        .attr("r", rr)
        .style("fill", "#000");  

      svg.append("text")
        .attr("class", "nodeLegend")
        .attr("x", xx+10)
        .attr("y", y3+2)
        .text("Biochemical Reations")
        .attr("dy", ".21em")
        .attr("font-family", "sans-serif")
        .attr("font-size", "12px")
        .style("text-anchor", "left")
        .style("fill", "#000");  
}

function getColor(type, count) {
  var sat = 0;
  //console.log(category+" "+count+" termMaxMax3="+termMaxMax3+" sat="+sat);
  if (type=="complex")
    return "rgb("+sat+", "+sat+", "+255+")" ; // leaf node
  else if (type=="protein")
    return "rgb("+255+", "+sat+", "+sat+")" ; // leaf node
  else if (type=="smallmolecule")
    return "rgb("+255+", "+sat+", "+255+")" ; // leaf node
  else
    return "#000000";
}

function selectionToArray(selection) {
    var array = [];
    selection.each(function() { array.push(this); });
    return array;
}

function translate(x, y) { return "translate(" + x + "," + y + ")"; }

function getSet(defaultValue) {
    var variable = defaultValue;
    return function(value) {
        return (arguments.length) ? (variable = value, this) : variable;
    }
}