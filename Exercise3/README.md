# TTP
Dynamic Tours
- [x] Generate 1 dynamic benchmark for each of the nine benchmarks of Exercise 1
where each dynamic benchmarks consists of a sequence of instances where every
50 generations the tour is changing by a randomly chosen exchange operation.
To generate the benchmarks only store the generation number and the applied
operation for each change.
- [x] Generate 1 dynamic benchmark for each of the nine benchmarks of Exercise 1
where each dynamic benchmarks consists of a sequence of instances where every
500 generations the tour is changing by a randomly chosen 2-opt step. To generate
the benchmarks only store the generation number and the applied operation for
each change.
- [x] Run your best performing algorithm of Exercise 2 on these benchmarks and show
the average performance over 10 runs in the same way as for Exercise 2.
- [x] Design a new algorithm that is performing as best as possible on these benchmarks.
All algorithms for dynamic problems have to be an online algorithms which can
only take into account the changes made to the instance up to the generation it
has already processed. They can not look into the ”future”. If you are using a
population-based algorithm, the population size has to be at most 20. Show the
performance. Plot the average quality per generation of 10 runs on each benchmark.
