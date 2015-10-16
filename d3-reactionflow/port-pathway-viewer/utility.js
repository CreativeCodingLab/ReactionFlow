

////////////////////////////////////////////////
// Helper Functions

function getColor(type, count) {
  var sat = 20;
  //console.log(category+" "+count+" termMaxMax3="+termMaxMax3+" sat="+sat);
  if (type=="complex")
    return "rgb("+sat+", "+255+", "+sat+")" ; // leaf node
  else if (type=="protein")
    return "rgb("+255+", "+sat+", "+sat+")" ; // leaf node
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