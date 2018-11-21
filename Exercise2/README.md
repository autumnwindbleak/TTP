# TTP
Dynamic items
- [x] Each item can either be available or unavailable. Start with the whole set of items
available and the best solution obtained in the previous exercise. Every 50 genera-
tions each item changes its status (from available to unavailable or vice versa) with
probability 5/m where m is the total number of items.
- [x] In order to be able to compare algorithmic performance, you have to generate the
sequence of sets of items available at each generation first and run the algorithms
afterwards taking into account this sequence. You should generate 2 dynamic bench-
marks for each of the nine benchmarks where each dynamic benchmarks consists of
a sequence of sets of items generated as outlined above. To generate the benchmarks
only store the generation number and the applied operation for each change.
- [x] To evaluate the performance of your algorithms plot the quality of solutions ob-
tained in each generation. Do 10 repeated runs on each instance for each algorithm
considered in Exercise 1 and plot the average solution quality for each number of
generations and each algorithm. Produce for each benchmark one figure showing
the results of all algorithms.
- [x] Design a new algorithm that is performing as best as possible on these benchmarks.
All algorithms for dynamic problems have to be an online algorithms which can
only take into account the changes made to the instance up to the generation it
has already processed. They can not look into the ”future”. If you are using a
population-based algorithm, the population size has to be at most 20. Plot the
average quality per generation of 10 runs on each benchmark.
