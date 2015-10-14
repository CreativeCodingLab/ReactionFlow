////////////////////////////////////////////////
// Helper Functions

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