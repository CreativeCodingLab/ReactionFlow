ReactionFlow
=============
ReactionFlow is a visual analytics application for pathway analysis that emphasizes the structural and causal relationships amongst proteins, complexes, and biochemical reactions within a given pathway. To support the identified causality analysis tasks, user interactions allow an analyst to filter, cluster, and select pathway components across linked views. Animation is used to highlight the flow of activity through a pathway. 

We have defined a causal relationship between two reactions if the output participants of one reaction act as the input to another reaction. Causality is therefore a directed relationship, and in our visualizations causality is depicted through the use of gradient-filled lines, where direction flows from yellow to black as depicted in the following figure. In other words, reaction 2 is downstream of reaction 1.

The following figure shows an example of using ReactionFlow to analyze the Influenza Infection pathway. In the first panel, we show all causal relationships (yellow-black gradient arcs) of 52 biochemical reactions in the pathway. Input and output participants are aligned on the left and right which can be highlighted when rolling over a reaction. In the second panel, we show downstream effects (red arcs) of "knocking out" proteins in the first reaction.

![ScreenShot](http://www.cs.uic.edu/~tdang/ReactionFlow/TearserImage.png)

![ScreenShot](http://www.cs.uic.edu/~tdang/ReactionFlow/Image1-RAF pathway1.png)
![ScreenShot](http://www.cs.uic.edu/~tdang/ReactionFlow/Image1-RAF pathway2.png)

![ScreenShot](http://www.cs.uic.edu/~tdang/ReactionFlow/Image2-ERBB2 pathway1.png)
![ScreenShot](http://www.cs.uic.edu/~tdang/ReactionFlow/Image2-ERBB2 pathway2.png)

![ScreenShot](http://www.cs.uic.edu/~tdang/ReactionFlow/Image3-Rb-E2F Pathway.png)

![ScreenShot](http://www.cs.uic.edu/~tdang/ReactionFlow/Image4-NGF Signaling Pathway.png)

![ScreenShot](http://www.cs.uic.edu/~tdang/ReactionFlow/Image5-Signaling to NOTCH.png)

![ScreenShot](http://www.cs.uic.edu/~tdang/ReactionFlow/Image6-Myochonic pathway.png)

![ScreenShot](http://www.cs.uic.edu/~tdang/ReactionFlow/Image7-Signaling to TGF.png)

![ScreenShot](http://www.cs.uic.edu/~tdang/ReactionFlow/Image8-Signaling to GPCR.png)


Please click to watch the overview video.

[![ScreenShot](http://www.cs.uic.edu/~tdang/ReactionFlow/TeaserVideo.png)](http://www.cs.uic.edu/~tdang/ReactionFlow/video.mp4)

The application (ReactionFlow_1_1.jar) is available in application.Cross-Platform folder.

This work was funded by the DARPA Big Mechanism Program under ARO contract WF911NF-14-1-0395.
