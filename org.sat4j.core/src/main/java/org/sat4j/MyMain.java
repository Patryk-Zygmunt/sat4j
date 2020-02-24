package org.sat4j;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class MyMain {
    private static  final String PROBLEM  =
            " p cnf 6 3\n" +
            "1 -3 7 5 0\n" +
            "-4 -5 7 2 0\n" +
            "-2 3 -1 -5 0";

    private static  final String PROBLEM2  =
                    " p cnf 3 8\n" +
                    "1 2 3 0\n" +
                    "1 2 -3 0\n" +
                    "1 -2 3 0\n" +
                    "-1 2 3 0\n" +
                    "1 -2 -3 0\n" +
                    "-1 2 -3 0\n" +
                    "-1 -2 3 0\n" +
                    "-1 -2 -3 0";



    public static void main(String[] args) {
        ISolver solver = SolverFactory.newLight();
        solver.setTimeout(3600); // 1 hour timeout
        Reader reader = new DimacsReader(solver);
        PrintWriter out = new PrintWriter(System.out,true);
        try {
            InputStream stream = new ByteArrayInputStream(PROBLEM2.getBytes(StandardCharsets.UTF_8));

            IProblem problem = reader.parseInstance(stream);
            if (problem.isSatisfiable()) {
                System.out.println("Satisfiable !");
                reader.decode(problem.model(),out);
            } else {
                System.out.println("Unsatisfiable !");
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
        } catch (ParseFormatException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        } catch (ContradictionException e) {
            System.out.println("Unsatisfiable (trivial)!");
        } catch (TimeoutException e) {
            System.out.println("Timeout, sorry!");
        }
    }




    }

