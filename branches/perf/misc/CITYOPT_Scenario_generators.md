CITYOPT scenario generators
===========================


Grid search
-----------

The grid search is a basic algorithm that iterates over all possible values of
all decision variables.  The decision variables must be of integer type.  This
is practical only if the number of decision variables is low and they only
have a small range of values.

Users can control the grid search algorithm via the following parameters:

- `max runtime [minutes]` forces the algorithm to stop after the specified
  time.  Default 1000000 minutes.

- `max scenarios` forces the algorithm to stop after the specified number
  of scenarios has been generated.  The remaining combinations of decision
  variable values are ignored.  Default 10000.

- `max parallel evaluations` is currently not useful, because the number
  of parallel simulations is limited by the simulation server back-end.


Genetic algorithm
-----------------

The genetic algorithm seeks solutions (scenarios) that are as good as possible
(Pareto-optimal) with respect to one or more objective functions, and that
satisfy the specified algebraic constraints.

If unlimited, the runtime of the algorithm is proportional to the number of
generations times the number of offspring per generation.

- `max runtime [minutes]` forces the algorithm to stop after the specified
  time.  The time is only checked at the end of each generation, which
  may take relatively long depending on population size.  Default 1000000.

- `max parallel evaluations` is currently not useful, because the number
  of parallel simulations is limited by the simulation server back-end.

- `seed of the random number generator` defines the random number sequence
  used by the algorithm, so that the same results can be repeated in
  a new genetic algorithm run. Default 1.

- `number of generations` specifies the number of generations to be
  produced.  Default 10.  Hundreds or thousands of generations are needed
  for the genetic algorithm to converge to optimal solutions.

- `population size` specifies the number of solutions to keep in the
  population.  On each generation, newly generated offspring replace
  worse members of the population. Default 100.

- `number of parents per generation` specifies the number of best solutions
  to pick as parent solutions for the next generation.  Default 25.

- `number of offspring per generation` specifies the number of new solutions
  to create from parents on each generation.  Default 25.

- `crossover rate` specifies the fraction of input parameter values to take
  from one parent solution, with the rest taken from another parent solution.
  Default 0.95


Algorithm reference
-------------------

The genetic algorithm implementation is from the Opt4J package.
http://opt4j.sourceforge.net/

M. Lukasiewycz, M. Glass, F. Reimann and Jürgen Teich.
Opt4J - A Modular Framework for Meta-heuristic Optimization.
Proceedings of the Genetic and Evolutionary Computing Conference (GECCO 2011),
pp. 1723--1730. Dublin, Ireland, 2011.
