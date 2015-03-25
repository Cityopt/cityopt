INSERT INTO Algorithm (algorithmId, description) VALUES
 (1, 'grid search'),
 (2, 'genetic algorithm');
INSERT INTO AlgoParam (algorithmID, name) VALUES
 (1, 'max runtime [minutes]'),
 (1, 'max parallel evaluations'),
 (1, 'max scenarios'),
 (2, 'max runtime [minutes]'),
 (2, 'max parallel evaluations'),
 (2, 'seed of the random number generator'),
 (2, 'number of generations'),
 (2, 'population size'),
 (2, 'number of parents per generation'),
 (2, 'number of offspring per generation'),
 (2, 'crossover rate');
