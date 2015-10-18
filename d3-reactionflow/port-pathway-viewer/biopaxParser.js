BiopaxUtilties = {};


//BiopaxUtilties.getAllLinksFromComponent = function(component, links){
//
//};

/**
 * get the whole set of links that start from the reaction
 * or between complexes - component that are involved in that
 * reaction
 */
BiopaxUtilties.getAllLinksFromReaction = function(reaction, links){
    var linksInvolved = [];

    links.forEach(function(l){
        if(l.type == "participant-reaction"){
            if(l.source.node == reaction.node || l.target.node == reaction.node ){
                linksInvolved = _.union(linksInvolved, [l]);

                if(l.source.type == "complex"){
                    linksInvolved = _.union(linksInvolved,
                        BiopaxUtilties.getLinksToProteinsFromComplex(l.source, links));
                } else if(l.target.type == "complex"){
                    linksInvolved = _.union(linksInvolved,
                        BiopaxUtilties.getLinksToProteinsFromComplex(l.target, links));
                }
            }
        } else if(l.type == "reaction-reaction"){
            if(l.source.node == reaction.node) {
                linksInvolved = _.union(linksInvolved, [l]);
            }
        }

    });

    return linksInvolved;
};


BiopaxUtilties.getAllLinksFromComponent = function(component, links){
    var linksInvolved = [];
    if(component.type == 'complex') {
        var reactions = BiopaxUtilties.getReactionsFromComponent(component,
            chart.data().links);

        reactions.forEach(function(r){
            linksInvolved = _.union(linksInvolved,
                BiopaxUtilties.getAllLinksFromReaction(r, chart.data().links));
        });

    } else if(component.type == 'protein'){
        var reactions = BiopaxUtilties.getReactionsFromComponent(component,
            chart.data().links);

        reactions.forEach(function(r){
            linksInvolved = _.union(linksInvolved,
                BiopaxUtilties.getAllLinksFromReaction(r, chart.data().links))
                .filter(function(l){
                    return l.source.side != component.side  || (l.source.node == component.node && l.type == 'participant-reaction')
                });
        });
    }

    return linksInvolved;
};


BiopaxUtilties.getReactionsFromComponent = function(component, links){
    var reactionsInvolved = [];

    links.forEach(function(l){
        if(l.type == "participant-reaction"){
            if(l.source.node == component.node){
                reactionsInvolved = _.union(reactionsInvolved, [l.target]);
            }
        }

    });

    //if the component is a protein, call getReactionFromComponent again with all
    //the complexes involved
    //if(component.type == "protein"){
    //    BiopaxUtilties.getComplexesFromProtein(component, links).forEach(function(c){
    //        reactionsInvolved = _.union(reactionsInvolved,
    //            BiopaxUtilties.getReactionsFromComponent(c, links));
    //    })
    //}

    return reactionsInvolved;
};


BiopaxUtilties.getLeftProteinsFromReaction = function(reaction, links){

    var proteins = [];

    links.forEach(function(l){
        if(l.type = "participant-reaction" && l.source.side == "left"){
            if(l.source.type == "protein")
                proteins.push(l.source);
            else if(l.source.type == "complex"){
                proteins = _.union(proteins,
                    BiopaxUtilties.getProteinsFromComplex(l.source, links));
            }
        }
    });

    return proteins;
};


BiopaxUtilties.getProteinsFromComplex = function(complex, links){
    var proteins = [];

    links.forEach(function(l){
        if(l.target.node == complex.node && l.type == "component-complex" && l.source.side == complex.side){
            if(l.source.type == "protein")
                proteins.push(l.source);
            else{
                console.warn("not a protein in a complex");
            }
        }
    });

    return proteins;
};


BiopaxUtilties.getComplexesFromProtein = function(protein, links){
    var complexes = [];

    links.forEach(function(l){
        if(l.source.node == protein.node && l.type == "component-complex" && l.source.side == protein.side){
            if(l.target.type == "complex"){
                complexes = _.union(complexes, [l.target]);
            }
        }
    });

    return complexes;
};


BiopaxUtilties.getLinksToProteinsFromComplex = function(complex, links){
    var linksInvolved = [];

    links.forEach(function(l){
        if(l.target.node == complex.node && l.type == "component-complex" && l.source.side == complex.side){
            if(l.source.type == "protein")
                linksInvolved.push(l);
            else{
                console.warn("not a protein in a complex");
            }
        }
    });

    return linksInvolved;
};




function biopaxParser() {
    
    var usesRdfAbout = my.usesRdfAbout = getSet.bind(this)();
    var idMapping = my.idMapping = getSet.bind(this)();
    
    function my() {}
    
    var reactions = [];
    my.reactions = function() { return reactions; };
    
    var participants = [];
    my.participants = function() {
        var nested = d3.nest()
            .key(function(d) { return d.side; })
            .key(function(d) { return d.type; })
            .map(participants);
        
        ["protein", "complex"].forEach(function(type) {
            balanceParticipants(nested, type);
        });
        
        return nested; 
    };
    
    var links = [];
    my.links = function() { 
        for (var key in idMapping().links) {
            links[key] = links.filter(function(link) {
                return link.type == idMapping().links[key];
            });
        }
        return links; 
    };
    
    // var biopax = my.biopax = getSet.bind(this)();
    var biopax;
    my.biopax = function(bp) {
        biopax = d3.functor(bp);
        
        reactions = getReactionsArray(biopax());
        setParticipantsAndLinks(reactions);
        links = findReactionCasuality(links);
         debugger;
    

        

        participants.forEach(function(participant) {
            if (participant.type == "complex") getComplexComponents(participant, participant);
        })
        
        // FIXME only for testing purposes
        // sortParticipantsByTopologicalOrder(participants, reactions, links);
    };
    
    function balanceParticipants(_participants, type) {
        
        if (!_participants.left[type]) _participants.left[type] = [];
        if (!_participants.right[type]) _participants.right[type] = [];
        
        var leftArray = _participants.left[type],
            rightArray = _participants.right[type];

        function getNode(p) { return p.node };
        
        var nodes = {};

        nodes = {
            left: leftArray.map(getNode),
            right: rightArray.map(getNode)
        };
        allNodes = _.union(nodes.left, nodes.right);
            
        // var diff = _.difference
        ["left", "right"].forEach(function(side) {
            _.difference(allNodes, nodes[side]).forEach(function(outsider) {
                _participants[side][type].push({
                    node: outsider,
                    side: side,
                    type: type
                });
            });
            // var thisSide = _participants[side][type];
            // var otherSide = _participants[]
        });
        // debugger;
    }
    
    function getReactionsArray(biopax) {
        var reactionsSelection = biopax.selectAll("BiochemicalReaction");
        return selectionToArray(reactionsSelection)
            .map(function(node) { return { node: node, type: "reaction" }; });
    };
    
    function setParticipantsAndLinks(_reactions) {
        _reactions.forEach(function(reaction) {
            
            var pushReactionLink = pushLinkToTarget(
                reaction, idMapping().links.participantReaction, links.participantReaction
            );
            
            ["left", "right"].forEach(function(side) {
                getParticipantsOneSide(reaction.node, side)
                    .forEach(function (participant) {
                        var composed = _.compose(pushReactionLink, findOrPushParticipant);
                        composed(participant);
                    });
            });
        });
    }
    
    function getComplexComponents(participant, parent) {

        //recursion
        if( participant.type == "protein" ){
            // Add participant and link
            var p = findOrPushParticipant(participant);
            
            links.push({ 
                source: p,
                target: parent,
                type: idMapping().links.componentComplex
            });
            
        } else { // Participant is a complex
            
            var componentSelection = d3.select(participant.node).selectAll("component");
            
            componentSelection.each(function(component) {
                var resourceId = d3.select(this).attr("rdf:resource");
                var participantNode = findParticipantNode(resourceId);
                var obj = { 
                    node: participantNode, 
                    type: participantNode.tagName.substr(3).toLowerCase(),
                    side: participant.side
                };
                
                getComplexComponents(obj, parent);
        
            });
        }
    }
    
    function pushLinkToTarget(target, type) {
        return function(source) {
            links.push({
                source: source, target: target, type: type 
            });
        };
    }
    
    function findOrPushParticipant(participant) {
        var found = _.find(participants, participantComparator(participant));
        if (!found) {
            participants.push(participant);
            return participant;
        }
        return found;
    }
    
    function participantComparator(other) {
        return function(p) { 
            return (p.node === other.node && p.side === other.side); 
        };
    }
    
    function getParticipantsOneSide(reaction, side) {
        var participantSel = d3.select(reaction).selectAll(side);
        var _participants = [];
        participantSel.each(function() {
            var resourceId = d3.select(this).attr("rdf:resource");
            var participantNode = findParticipantNode(resourceId);
            // Create participant object
            var type = participantNode.tagName.substr(3).toLowerCase();
            _participants.push({ node: participantNode, side: side, type: type });
        });
        return _participants;
    }
    
    // Given a resource identifier, find participant nodes.
    function findParticipantNode(resourceId) {
        // Remove potential hash from resourceId
        if (resourceId.charAt(0) === "#") resourceId = resourceId.substr(1);
        
        var participantTagNames = ["Protein", "Complex", "SmallMolecule", "Dna", "PhysicalEntity"];
        
        // FIXME: Better way of finding participants?
        // The problem is selecting based on namespaced attributes.
        var participant = biopax()
            .selectAll(participantTagNames.join())
            .filter(function() { 
                var attr = usesRdfAbout() ? "rdf:about" : "rdf:ID";
                return d3.select(this).attr(attr) == resourceId;
            });
        if (participant.size() !== 1) {
            debugger;
            throw new Error("participants length !== 1", resourceId);
        }
        return participant.node();
    }
    
    //iterate over all the links and finds out the casuality between reactions
    function findReactionCasuality(_links) {
        var reactionsLinks = [];
        
        var linksToRight = _links.filter(function(l) {
            return (l.source.side == "right");
        });
        
        var linksFromLeft = _links.filter(function(l) {
            return (l.source.side == "left");
        });
        
        linksToRight.forEach(function(startLink) {
            linksFromLeft.forEach(function(endLink) {
                if (startLink.source.node === endLink.source.node) {
                    reactionsLinks.push({
                        source: startLink.target,
                        target: endLink.target,
                        type: "reaction-reaction"
                    });
                }
            });
        });
        
        return _links.concat(reactionsLinks);

    }

    return my;
}