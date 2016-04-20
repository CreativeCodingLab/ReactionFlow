

////////////////////////////////////////////////
// Helper Functions

  // Add color legend
function drawColorLegend() {
      var xx = 10;
      var y1 = 20;
      var y2 = 36;
      var y3 = 52;
      var y4 = 62;
      var rr = 6;

      svg.append("circle")
        .attr("class", "nodeLegend")
        .attr("cx", xx)
        .attr("cy", y1)
        .attr("r", rr)
        .style("fill", "#080");
      
      svg.append("text")
        .attr("class", "nodeLegend")
        .attr("x", xx+10)
        .attr("y", y1+2)
       .text("Protein")
        .attr("dy", ".21em")
        .attr("font-family", "sans-serif")
        .attr("font-size", "11px")
        .style("text-anchor", "left")
        .style("fill", "#080");
   
      svg.append("circle")
        .attr("class", "nodeLegend")
        .attr("cx", xx)
        .attr("cy", y2)
        .attr("r", rr+2)
        .style("fill", "#800");  

      svg.append("text")
        .attr("class", "nodeLegend")
        .attr("x", xx+10)
        .attr("y", y2+2)
        .text("Complex")
        .attr("dy", ".21em")
        .attr("font-family", "sans-serif")
        .attr("font-size", "11px")
        .style("text-anchor", "left")
        .style("fill", "#900");  

       svg.append("rect")
        .attr("class", "nodeLegend")
        .attr("x", xx-rr)
        .attr("y", y3-rr)
        .attr("rx", 2)
        .attr("ry", 2)
        .attr("width", rr*2)
        .attr("height", rr*2)
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

function colorGroup(d) {
  var minSat = 150;
  var maxSat = 240;
  var step = (maxSat-minSat)/maxDepth;
  console.log(d.deep+" step="+step);
  var sat = Math.round(maxSat-d.deep*step);
  return "rgb("+sat+", "+sat+", "+sat+")" ; 
}

function getColor(type, count) {
  var sat = 0;
  //console.log(category+" "+count+" termMaxMax3="+termMaxMax3+" sat="+sat);
  if (type=="complex")
    return "rgb("+sat+", "+sat+", "+200+")" ; // leaf node
  else if (type=="protein")
    return "rgb("+sat+", "+150+", "+sat+")" ; // leaf node
  else if (type=="smallmolecule")
    return "rgb("+200+", "+sat+", "+200+")" ; // leaf node
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