package fr.iut.pathpilotapi.benchmark;

import fr.iut.pathpilotapi.algorithme.Algorithme;
import fr.iut.pathpilotapi.algorithme.BranchAndBound;
import fr.iut.pathpilotapi.algorithme.BruteForce;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(value = 1, warmups = 1)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
public class AlgorithmBenchmark {

    @Param({"3", "5", "8"})  // Différentes tailles de matrices à tester
    private int size;

    private Algorithme bruteForce;
    private Algorithme branchAndBound;
    private List<List<Double>> distances;

    @Setup
    public void setup() {
        bruteForce = new BruteForce();
        branchAndBound = new BranchAndBound();
        distances = generateRandomDistanceMatrix(size);

        bruteForce.setMatrixLocationsRequest(distances);
        branchAndBound.setMatrixLocationsRequest(distances);
    }

    private List<List<Double>> generateRandomDistanceMatrix(int size) {
        List<List<Double>> matrix = new ArrayList<>();
        Random random = new Random(42);  // Seed fixe pour la reproductibilité

        for (int i = 0; i < size; i++) {
            List<Double> row = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    row.add(0.0);  // Distance à soi-même est 0
                } else {
                    row.add(random.nextDouble() * 100);  // Distances aléatoires entre 0 et 100
                }
            }
            matrix.add(row);
        }
        return matrix;
    }

    @Benchmark
    public void benchmarkBruteForce(Blackhole blackhole) {
        bruteForce.computeBestPath();
        blackhole.consume(bruteForce.getBestPath());
    }

    @Benchmark
    public void benchmarkBranchAndBound(Blackhole blackhole) {
        branchAndBound.computeBestPath();
        blackhole.consume(branchAndBound.getBestPath());
    }
}