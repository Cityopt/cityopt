INSERT INTO Algorithm (algorithmId, description) VALUES
 (1, 'grid search'),
 (2, 'genetic algorithm');
INSERT INTO AlgoParam (algorithmID, name, defaultValue) VALUES
 (1, 'max runtime [minutes]', '1000000'),
 (1, 'max parallel evaluations', '100'),
 (1, 'max scenarios', '10000'),
 (2, 'max runtime [minutes]', '1000000'),
 (2, 'max parallel evaluations', '100'),
 (2, 'seed of the random number generator', '1'),
 (2, 'number of generations', '10'),
 (2, 'population size', '100'),
 (2, 'number of parents per generation', '25'),
 (2, 'number of offspring per generation', '25'),
 (2, 'crossover rate', '0.95');
