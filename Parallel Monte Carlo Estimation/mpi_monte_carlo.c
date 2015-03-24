/* File:     mpi_monte_carlo.c
 * Author:   William Chiang
 * Purpose:  Estimates pi by using the Monte Carlo method in a
 *           parallelized program 
 *
 * Compile:  mpicc -g -D <x> -Wall -o mpi_monte_carlo mpi_monte_carlo.c -lm
 *           User can choose from 1 of 5 compile methods:
 *           [ LOOP, TREE, REDUCE, ALLREDUCE, BFLY ]
 *           If unspecified, program will default compile with ALLREDUCE
 *
 * Run:      mpicc -n <# of processes> ./mpi_monte_carlo <# of darts>
 * Input: n (Number of darts)
 * Output: (To command line)
 *           Total darts(n)
 *           Darts that hit the circle
 *           Estimation of pi
 *           True value of pi
 *           Error estimation 
 *           Time Elapsed  
 * Notes: "timer.h" is still used to get the system time.   
 *        For BFLY method, it is assumed that the user will use processors
 *        that sum up to a power of 2. Otherwise, BFLY structure will not
 *        sum properly.
 */

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "timer.h"
#include <time.h>
#include <mpi.h>

long long int MPI_Monte_carlo(long long int n, int seed);
double Get_max_time(double par_elapsed, int my_rank, int p);
char user_command(void);
long long int sum(long long int my_contrib,int my_rank,int p,
    MPI_Comm comm);
long long int bsum(long long int my_contrib,int my_rank,int p,
    MPI_Comm comm);

int main(int argc, char* argv[]) {
    long long int in_circle_count = 0;
    long long int n, n_divided;
    long long int hitdarts = 0;  
    double pi;   
    double error = 0; 
    double start, finish, my_elapsed, elapsed;
    MPI_Comm comm;
    int p; //number of processes 
    int my_rank; //rank number 
    double totalestimate = 0;
    int fail = 0; // In case of error in executing

    MPI_Init(&argc, &argv);
    comm = MPI_COMM_WORLD;
    MPI_Comm_size(comm, &p);
    MPI_Comm_rank(comm, &my_rank);

    if (argc != 2 || strtol(argv[1], NULL, 10) <= 0){
        if (my_rank == 0){
        printf("Missing argument. Include an int argument > 0 after filename");
        printf("\n");
        }
        MPI_Bcast(&fail, 1, MPI_INT, 0, comm);
        MPI_Finalize();
        exit(0);
    }
    n = strtol(argv[1], NULL, 10);

    #ifdef LOOP 
            double q;
            long long int temp;
            MPI_Barrier(MPI_COMM_WORLD);
            start = MPI_Wtime();
            n_divided = n/p;
            pi = 4.0*atan(1.0); 
            in_circle_count = MPI_Monte_carlo(n_divided, my_rank);
            if (my_rank == 0) {
                hitdarts = in_circle_count;
                for (q = 1; q < p; q++){
                    MPI_Recv(&temp, 1, MPI_LONG_LONG, q, 0, MPI_COMM_WORLD,
                     MPI_STATUS_IGNORE);
                    hitdarts += temp;
                }
                totalestimate = (hitdarts*4.0)/n;
                error = (fabs)(pi - totalestimate);
            } else {
                MPI_Send(&in_circle_count, 1, MPI_LONG_LONG, 0, 0,
                 MPI_COMM_WORLD);
            }
            MPI_Barrier(MPI_COMM_WORLD);
            finish = MPI_Wtime();
            
    #elif TREE
            MPI_Barrier(MPI_COMM_WORLD);
            start = MPI_Wtime();

            n_divided = n/p;
            pi = 4.0*atan(1.0); 
            in_circle_count = MPI_Monte_carlo(n_divided, my_rank); 
            hitdarts = sum(in_circle_count, my_rank, p, comm);
            if (my_rank == 0){
                totalestimate = (hitdarts*4.0)/n;
                error = (fabs)(pi - totalestimate);
            }
            MPI_Barrier(MPI_COMM_WORLD);
            finish = MPI_Wtime();

    #elif REDUCE
            MPI_Barrier(MPI_COMM_WORLD);
            start = MPI_Wtime();

            n_divided = n/p;
            pi = 4.0*atan(1.0); 
            in_circle_count = MPI_Monte_carlo(n_divided, my_rank);
            MPI_Reduce(&in_circle_count, &hitdarts, 1, MPI_LONG_LONG, 
                MPI_SUM, 0, MPI_COMM_WORLD);
            if (my_rank == 0){
                totalestimate = (hitdarts*4.0)/n;
                error = (fabs)(pi - totalestimate);
            }
            MPI_Barrier(MPI_COMM_WORLD);
            finish = MPI_Wtime();

    #elif ALLREDUCE 
            MPI_Barrier(MPI_COMM_WORLD);
            start = MPI_Wtime();

            n_divided = n/p;
            pi = 4.0*atan(1.0); 
            in_circle_count = MPI_Monte_carlo(n_divided, my_rank);
            MPI_Allreduce(&in_circle_count, &hitdarts, 1, MPI_LONG_LONG, 
                MPI_SUM, MPI_COMM_WORLD);
            if (my_rank == 0){
                totalestimate = (hitdarts*4.0)/n;
                error = (fabs)(pi - totalestimate);
            }
            MPI_Barrier(MPI_COMM_WORLD);
            finish = MPI_Wtime();

    // Butterfly Method
    #elif BFLY
            MPI_Barrier(MPI_COMM_WORLD);
            start = MPI_Wtime();

            n_divided = n/p;
            pi = 4.0*atan(1.0); 
            in_circle_count = MPI_Monte_carlo(n_divided, my_rank); 
            hitdarts = bsum(in_circle_count, my_rank, p, comm);
            if (my_rank == 0){
                totalestimate = (hitdarts*4.0)/n;
                error = (fabs)(pi - totalestimate);
            }
            MPI_Barrier(MPI_COMM_WORLD);
            finish = MPI_Wtime();

     // Default Method
     #else
            MPI_Barrier(MPI_COMM_WORLD);
            start = MPI_Wtime();

            n_divided = n/p;
            pi = 4.0*atan(1.0); 
            in_circle_count = MPI_Monte_carlo(n_divided, my_rank);
            MPI_Allreduce(&in_circle_count, &hitdarts, 1, MPI_LONG_LONG, 
                MPI_SUM, MPI_COMM_WORLD);
            if (my_rank == 0){
                totalestimate = (hitdarts*4.0)/n;
                error = (fabs)(pi - totalestimate);
            }
            MPI_Barrier(MPI_COMM_WORLD);
            finish = MPI_Wtime();

    #endif

        my_elapsed = finish - start;
        elapsed = Get_max_time(my_elapsed, my_rank, p);
            
        if (my_rank == 0){
        printf("Total darts: %llu\n", n);
        printf("Darts that hit: %llu\n", hitdarts);
        printf("Estimate of pi: %f\n", totalestimate);
        printf("Actual value of pi: %f\n", pi);
        printf("Error estimate: %f\n", error);
        printf("Time elapsed: %f\n", elapsed);
        }
        MPI_Finalize();
        return 0;
}


/*-----------------------------------------------------------------
 * Function:      MPI_Monte_carlo
 * Purpose:       Uses the Monte Carlo method to calculate the 
 *                estimated pi
 * Input:         n - total number of darts thrown
 *                seed - A changing seed for the randomizer 
 * Return value:  in_circle_count - The number of darts that 
 *                landed inside the dartboard.
 *
 * Note:          Randomizer does not work properly without "time.h"
 */
long long int MPI_Monte_carlo(long long int n, int seed) {
    long long int in_circle_count = 0;
    long long int i;
    double x, y;
    if (seed == 0){
        srand(time(NULL));
    }
    else {
        srand(seed);
    }
    for (i = 0; i < n; i++) {
        x = rand()/((double) RAND_MAX);
        x = 2.0*x - 1.0;
        y = rand()/((double) RAND_MAX);
        y = 2.0*y - 1.0;
        if ((x*x + y*y) <= 1) {
            in_circle_count++;
        }
    }
    return in_circle_count;
}
/* MPI_Monte_carlo */

/*------------------------------------------------------------------
 * Function:   Get_max_time
 * Purpose:    Find the maximum elapsed time across the processes
 * In args:    my_rank:       calling process' rank
 *             p:             total number of processes
 *             par_elapsed:   elapsed time on calling process
 * Ret val:    Process 0:     max of all processes times
 *             Other procs:   input value for par_elapsed
 * Note: Sampled from Prof. Pacheco's code.
 */
double Get_max_time(double par_elapsed, int my_rank, int p) {
   int source;
   MPI_Status status;
   double temp;

   if (my_rank == 0) {
      for (source = 1; source < p; source++) {
         MPI_Recv(&temp, 1, MPI_DOUBLE, source, 0, MPI_COMM_WORLD, &status);
         if (temp > par_elapsed) par_elapsed = temp;
      }
   } else {
      MPI_Send(&par_elapsed, 1, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD);
   }
   return par_elapsed;
}  /* Get_max_time */

/*-----------------------------------------------------------------*/
/* Function:    sum
 * Purpose:     Compute the global sum of ints stored on the processes
 *
 * Input args:  my_contrib = process's contribution to the global sum
 *              my_rank = process's rank
 *              p = number of processes
 *              comm = communicator
 * Return val:  Sum of each process's my_contrib:  valid only
 *              on process 0.
 * Note: Uses Tree Structure communication - Sampled from Prof. Pacheco's
 *       code.
 */
long long int sum(long long int my_contrib,int my_rank,int p,MPI_Comm comm){
    long long int sum = my_contrib;
    long long int temp;
    int        partner;
    int        done = 0;
    unsigned bitmask = (unsigned) 1;

    while (!done && bitmask < p) {
        partner = my_rank ^ bitmask;
        if (my_rank < partner) {
            if (partner < p) {
                MPI_Recv(&temp, 1, MPI_LONG_LONG, partner, 0, comm, 
                      MPI_STATUS_IGNORE);
                sum += temp;
            }
            bitmask <<= 1;
        } else {
            MPI_Send(&sum, 1, MPI_LONG_LONG, partner, 0, comm); 
            done = 1;
        }
    }
    return sum;
}  /* Global_sum */


/*-----------------------------------------------------------------*/
/* Function:    bsum
 * Purpose:     Compute the global sum of ints stored on the processes
 *
 * Input args:  my_contrib = process's contribution to the global sum
 *              my_rank = process's rank
 *              p = number of processes
 *              comm = communicator
 * Return val:  Sum of each process's my_contrib on the global sum. 
 *              Each processor has the same end sum.
 * Note: Uses butterfly structure communication, only works for 
 *       values of p that are powers of 2. 
 */
long long int bsum(long long int my_contrib,int my_rank,int p,
    MPI_Comm comm){
    long long int sum = my_contrib;
    long long int temp;
    int           partner;
    unsigned bitmask = (unsigned) 1;

    while (bitmask < p){
        partner = my_rank ^ bitmask;
        MPI_Send(&sum, 1, MPI_LONG_LONG, partner, 0, comm);
        MPI_Recv(&temp, 1, MPI_LONG_LONG, partner, 0, comm, 
            MPI_STATUS_IGNORE);
        sum += temp;
        bitmask <<= 1;
    }

    return sum;
}