INSERT INTO Algorithm (algorithmId, description) VALUES
 (1, 'grid search'),
 (2, 'genetic algorithm');
INSERT INTO AlgoParam (aParamsID, algorithmID, name, defaultValue) VALUES
 (1, 1, 'max runtime [minutes]', '1000000'),
 (2, 1, 'max parallel evaluations', '100'),
 (3, 1, 'max scenarios', '10000'),
 (4, 2, 'max runtime [minutes]', '1000000'),
 (5, 2, 'max parallel evaluations', '100'),
 (6, 2, 'seed of the random number generator', '1'),
 (7, 2, 'number of generations', '10'),
 (8, 2, 'population size', '100'),
 (9, 2, 'number of parents per generation', '25'),
 (10, 2, 'number of offspring per generation', '25'),
 (11, 2, 'crossover rate', '0.95');
