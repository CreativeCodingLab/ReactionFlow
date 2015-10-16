var temp;
/**
 * 
 */
function sortingUtilities() {
    function my() {};
    
    function sortParticipantsMinimizingEdgesCrossing(participants, reactions, links) {
        var sorted = {
            proteins : [],
            complexes : [],
            reactions : []
        };
        
        var proteins = participants.left.protein;
        var complexes = participants.left.complex;
        
        //sort proteins
        while(proteins.length > 0) {
            var last, next;
            if(sorted.proteins.length != 0)
                last = sorted.proteins[sorted.proteins.length-1];
            else last = proteins.slice(Math.floor(Math.random()*proteins.length))[0];
            
            next = _.max(proteins, function(p){return proteinSimilarity(last,p, links);});
            proteins = _.without(proteins, next);
            sorted.proteins.push(next);
            
        }
        
        //sort complexes
        
        //var complexesToProteinsFactor = complexes.length / proteins.length;
        sorted.proteins.forEach(function(p){
           sorted.complexes = _.union(sorted.complexes, getComplexes(p, links));
        });
        
        //sort reactions
        
        //var complexesToProteinsFactor = complexes.length / proteins.length;
        sorted.complexes.forEach(function(p){
           sorted.reactions = _.union(sorted.reactions, getReactions(p, links));
        });
        
        //add all the remaining
        sorted.reactions = _.union(sorted.reactions, reactions);
        
        //sorted.complexes = sorted.complexes.reverse();
    
        return sorted;
    }
    
    
    /*
     * 
     */
    function sortParticipantsByTopologicalOrder(participants, reactions, links) {
        
        var sorted = {
            proteins : [],
            complexes : [],
            reactions : []
        };
        
        
        var complexes = participants.left.complex;
        
        //start with the reaction
        // var reactions = participants.filter(function(d) {
        //     return d.type === "reaction";
        // });
        
        
        //reactions.forEach( function(r){
        //    //for each possible position, the number of upstream reactions
        //    //we want to insert the reaction in the best position
        //    var numberOfUpstreamsReactions = [];
        //
        //    for(var i = 0; i < sorted.reactions.length; i++){
        //        var countOfUpstream = 0;
        //        for(var j = 0; j < sorted.reactions.length; j++){
        //            if(i < j){
        //                if(existsReactionLink(r, sorted.reactions[j], links))
        //                    countOfUpstream++;
        //            } else {
        //                if(existsReactionLink(sorted.reactions[j], r, links))
        //                    countOfUpstream++;
        //            }
        //        }
        //
        //        numberOfUpstreamsReactions.push(countOfUpstream);
        //    }
        //
        //    //var index = sorted.reactions.indexOf(_.min(numberOfUpstreamsReactions));
        //    var index = numberOfUpstreamsReactions.indexOf(_.min(numberOfUpstreamsReactions));
        //    sorted.reactions.splice(index,0,r);
        //
        //});

        var availLinks = links.slice().filter(function(l){return l.type == "reaction-reaction"});
        var availReactions = reactions.slice(0);

        while(availReactions.length > 0){

            var exiting = [], entering = [];

            for(var i = 0; i < availReactions.length; i++){
                var reaction = availReactions[i];
                entering.push(getEnteringReactionLinks(reaction, availLinks).length);
            }

            var minEntering = _.min(entering);

            var minEnteringReactions = [];
            for(var i = 0; i < availReactions.length; i++){
                var reaction = availReactions[i];
                if(entering[i] == minEntering){
                    minEnteringReactions.push(reaction);
                    exiting.push(getExitingReactionLinks(reaction, availLinks).length);
                }
            }

            var maxExiting = _.max(exiting);
            var candidatesReactions = [];

            for(var i = 0; i < minEnteringReactions.length; i++){
                if(exiting[i] == maxExiting){
                    candidatesReactions.push(minEnteringReactions[i]);
                }
            }

            var r = candidatesReactions[0];
            sorted.reactions.push(r);
            //remove reaction
            availReactions = _.without(availReactions, r);

            //remove links
            availLinks = availLinks.filter(function(l){return l.source.node != r.node});

        }
        
        //sort complexes
        sorted.reactions.forEach( function(r){
            sorted.complexes = _.union(sorted.complexes, getComplexesByReaction(r, links));
        });
        
        //add all the remaining
        sorted.complexes = _.union(sorted.complexes, complexes);
        
        
        //sort proteins
        sorted.complexes.forEach( function(c){
            sorted.proteins = _.union(sorted.proteins, getProteinByComplexes(c, links));
        });
        
        
        temp = sorted;
        return sorted;
    } 
    
    
    /*
     * 
     */
    function sortParticipantsByName(participants, reactions, links) {
          var sorted = {
            proteins : [],
            complexes : [],
            reactions : []
        };
        
        
        var proteins = participants.left.protein;
        // filter(function(d) {
        //     return d.type === "protein" && d.side === "left";
        // });
        
        var complexes = participants.left.complex;
        // filter(function(d) {
        //     return d.type === "complex" && d.side === "left";
        // });
        
        sorted.proteins = _.sortBy(proteins, function(p){
            d3.select(p.node).select("displayName").text();
        });
        
        sorted.complexes = _.sortBy(complexes, function(p){
            d3.select(p.node).select("displayName").text();
        });
        
        sorted.reactions = _.sortBy(reactions, function(p, i){
            var name = d3.select(p.node).select("displayName");
            if (name.size() > 0) return name.text();
            else return "Reaction" + i;
        });
        
        return sorted;
    }
    
    
    
    function proteinSimilarity(p1, p2, links) {
        
        var p1Complexes = [],
            p2Complexes = [];
        
        links.forEach(function(link){
            if(link.source.node === p1.node) {
                p1Complexes.push(link.target.node);
            } else if(link.source.node === p2.node) {
                p2Complexes.push(link.target.node);
            }        
            
        });
        
        return _.intersection(p1Complexes, p2Complexes).length;
    }
    
    
    function getComplexes(protein, links) {
        var complexes = [];
        links.forEach(function(l){
                if(l.source.node === protein.node && l.type == "component-complex" && l.target.side == "left"){
                    complexes.push(l.target);
                }
        })
        
        return complexes;
    }
    
    
    function getProteinByComplexes(complex, links) {
        var proteins = [];
        links.forEach(function(l){
            if(l.target.node === complex.node && l.type == "component-complex" && l.source.side == "left"){
                proteins.push(l.source);
            }
        });
        
        return proteins;
    }
    
    
    function getComplexesByReaction(reaction, links) {
        var complexes = [];
        links.forEach(function(l){
            if(l.target.node === reaction.node && l.type == "participant-reaction" && l.source.side == "left"){
                complexes.push(l.source);
            }
        })
        
        return complexes;
    }
    
    
    function getReactions(complexes, links) {
        var reactions = [];
        links.forEach(function(l){
                if(l.source.node === complexes.node && l.type == "participant-reaction" && l.source.side == "left"){
                    reactions.push(l.target);
                }
        });
        
        return reactions;
    }
    
    function existsReactionLink(r1, r2, links){
        var find = false;
        
        links.forEach(function(l){
            if(l.source.node === r1.node && l.target.node === r2.node && l.type == "reaction-reaction"){
                find = true;
            }
        });
        
        return find;
        
    }

    function getExitingReactionLinks(r, links){
        var reactionsLinks = [];
        links.forEach(function(l){
            if(l.source.node === r.node  && l.target.node !== r.node && l.type == "reaction-reaction"){
                reactionsLinks.push(l);
            }
        });

        return reactionsLinks;
    }

    function getEnteringReactionLinks(r, links){
        var reactionsLinks = [];
        links.forEach(function(l){
            if(l.target.node === r.node  && l.source.node !== r.node && l.type == "reaction-reaction"){
                reactionsLinks.push(l);
            }
        });

        return reactionsLinks;
    }
    
    my.sortParticipantsByName = sortParticipantsByName;
    my.sortParticipantsMinimizingEdgesCrossing = sortParticipantsMinimizingEdgesCrossing;
    my.sortParticipantsByTopologicalOrder = sortParticipantsByTopologicalOrder;
    
    return my;
}
