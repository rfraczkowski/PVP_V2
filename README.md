PVP_V2
======

Co-evolving

The JuneRuns branch has the code based on Rachel's senior project, and edited by Dr. Olsen for swarmfest.
This code will be used for running experiments for the swarmfest presentation, and should be the basis of future work.

The UI code has been ignored in favor of the code that runs on the command line.
The PVP class is the main class that interfaces with MASON.
Animal is the parent class of both agent classes, predator and prey.  Methods in animal are used by each of its children
without being overridden.
Reinforcement learning is built into the agent classes for agents to learn how to preference their movement.
