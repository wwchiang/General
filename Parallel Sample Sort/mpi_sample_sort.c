/* File: mpi_sample_sort.c
* Author: William Chiang
* Purpose: Sorts a user-entered array using sample sort with butterfly
* 		   distribution. 
* Compile: mpicc -g -Wall -o mpi_sample_sort mpi_sample_sort.c -lm
* Run: mpiexec -n <x> ./mpi_sample_sort
	   <x>: the number of processes

* Input: User specifies sample size, size of list, and integers in list.
* Output: Prints out each process' sorted list.
* Note: Assumes user will specify correct inputs, and assumes that the 
 		number of processes will be a power of 2. Also assumes that the
 		sample size can be evenly divided into the number of processes. 
  Note 2: main() and butterfly() are not the best quality of solution, as
  		  they are extremely long. However, they do work. 
*/
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <mpi.h>
#include <string.h>

const int RMAX = 100;

int* getSample(int local_A[], int my_rank, int local_n_p, int local_s);
int* generateSample(int local_n, int local_lst[], int my_rank);
int Compare(const void* a_p, const void* b_p);
void Butterfly(int my_rank,int p,double splits[],int* local_n,int local_lst[]);
double getSplit(int my_rank, int sample_list[], int size, int p);
void Print_list(int global_lst[], int p, int partitions[]);
// main //
int main(int argc, char* argv[]){
	int my_rank, p, i;
	int* list = NULL;
	int* local_list = NULL;
	int* sample_list = NULL;
	MPI_Comm comm;
	int s, n, localsize;
	double splitter;
	double* splitter_array = NULL;
	MPI_Init(&argc, &argv);
	comm = MPI_COMM_WORLD;
	MPI_Comm_size(comm, &p);
	MPI_Comm_rank(comm, &my_rank);
	int* global_samples = NULL;
	int* sorted_local_samples = NULL;
	int* global_list = NULL;
	int* receive_counts = NULL;
	int* displace = NULL;
	int temp_displace = 0;
	if (my_rank == 0){
		printf("Enter sample size as an int: ");
		scanf("%d", &s);
		printf("Enter size of list: ");
		scanf("%d", &n);
		printf("Enter list of %d elements: ", n);
		list = malloc(n*sizeof(int));
		for (i = 0; i < n; i++){
			scanf("%d", &list[i]);
		}
	}
	MPI_Bcast(&s, 1, MPI_INT, 0, MPI_COMM_WORLD);
	MPI_Bcast(&n, 1, MPI_INT, 0, MPI_COMM_WORLD);
	localsize = n/p;

	local_list = malloc(localsize*sizeof(int));
	MPI_Scatter(list, localsize, MPI_INT, local_list, localsize, 
		MPI_INT, 0, MPI_COMM_WORLD);

	sample_list = malloc(s/p*sizeof(int));
	sample_list = getSample(local_list, my_rank, localsize, s/p);
	
	global_samples = malloc(s*sizeof(int));
	MPI_Allgather(sample_list, s/p, MPI_INT, global_samples, s/p, 
		MPI_INT, MPI_COMM_WORLD);

	qsort(global_samples, s, sizeof(int), Compare);
	sorted_local_samples = malloc(s/p*sizeof(int));
	MPI_Scatter(global_samples, s/p, MPI_INT, sorted_local_samples, s/p, 
		MPI_INT, 0, MPI_COMM_WORLD);
	
	splitter = getSplit(my_rank, sorted_local_samples, s/p, p);
	splitter_array = malloc(p*sizeof(double));
	MPI_Allgather(&splitter, 1, MPI_DOUBLE, splitter_array, 1, 
		MPI_DOUBLE, MPI_COMM_WORLD);
	qsort(splitter_array, p, sizeof(double), Compare);
	Butterfly(my_rank, p, splitter_array, &localsize, 
	local_list);

	if (my_rank == 0){
		global_list = malloc(n*sizeof(int));
	}
	receive_counts = realloc(receive_counts, p*sizeof(int));
	displace = realloc(displace, p*sizeof(int));

	// Creates the receive count list for passing into MPI_Gatherv
	MPI_Allgather(&localsize, 1, MPI_INT, receive_counts, 1, MPI_INT, 
		MPI_COMM_WORLD);

	// Creates the displacement list for passing into MPI_Gatherv	
	for (i = 0; i < p; i++){
		displace[i] = temp_displace;
		temp_displace+= receive_counts[i]; 
	}
	MPI_Gatherv(local_list, localsize, MPI_INT, global_list, receive_counts,
	 displace, MPI_INT, 0, MPI_COMM_WORLD);

	if (my_rank == 0){
		Print_list(global_list, p, receive_counts);
	}
	// Frees everything.
	free(splitter_array);free(sample_list);free(local_list);free(global_list);
	free(displace); free(receive_counts); free(list); free(global_samples);
	free(sorted_local_samples);
	MPI_Finalize();
	return 0;
} /* main */

/* ----------------------------------------------------------------------
* Function: getSample
* Purpose: Gets the samples needed for the sample sort.
* input args: local_A[], my_rank, local_n_p, local_s
* output: returns a list of local samples
		  local_samples[]
*/

int* getSample(int local_A[], int my_rank, int local_n_p, int local_s){
	int* local_samples;
	int subscript;
	int i;
	int samples_left = local_s;
	int valid = 0;
	int j = 0;

	srandom(my_rank);
	local_samples = malloc(local_s*sizeof(int));
	
	do {
		subscript = random() % local_n_p;
		for (i = 0; i < local_s; i++){
			if (local_samples[i] == local_A[subscript]){
				valid = 0;
				break;
			}
			if (i == local_s - 1){
				valid = 1;
			} 
		}
		if (valid == 1){
			local_samples[j] = local_A[subscript];
			samples_left--;
			j++;
		}
	} while (samples_left > 0);
	qsort(local_samples, local_s, sizeof(int), Compare);
	return local_samples;
} /* getSample */

/* ----------------------------------------------------------------------
* Function: getSplit
* Purpose: Gets the splitters needed from the sample list taken in main.
* input args: my_rank, sample_list[], size, p
* output: returns local_split as a double
*/
double getSplit(int my_rank, int sample_list[], int size, int p){
	double local_split;
	int end, biggest_p;
	end = size-1;
	int temp;

	biggest_p = p - 1;
	if (my_rank != biggest_p){
		MPI_Send(&sample_list[end], 1, MPI_INT, my_rank + 1, 0, 
			MPI_COMM_WORLD);
	}
	if (my_rank != 0){
		MPI_Recv(&temp, 1, MPI_INT, my_rank-1, 0, MPI_COMM_WORLD, 
			MPI_STATUS_IGNORE);
		local_split = (temp + sample_list[0])/(2.0);
	}
	if (my_rank == 0){
		return sample_list[0];
	}
	return local_split;
} /* getSplit */

/*-------------------------------------------------------------------
 * Function:    Compare
 * Purpose:     Compare 2 ints, return -1, 0, or 1, respectively, when
 *              the first int is less than, equal, or greater than
 *              the second.  Used by qsort.
 */
int Compare(const void* a_p, const void* b_p) {
   int a = *((int*)a_p);
   int b = *((int*)b_p);

   if (a < b)
      return -1;
   else if (a == b)
      return 0;
   else /* a > b */
      return 1;
}  /* Compare */

/* ----------------------------------------------------------------
 * Function: Butterfly
 * Purpose: Uses a butterfly structure to sample sort a given local
 * 			list, and uses pass-by-reference to reflect it back into main.
 * Input args: rank, p, splits[], local_n, local_lst
 * Output: Returns nothing, but changes local_n and local_lst[]
 * Notes: Extremely long code, but can't figure out a way to condense. 
 *		  Almost 80 lines long. Yay!
 */
void Butterfly(int my_rank, int p, double splits[], int* local_n, int 
	local_lst[]){
	int partner, i;
	int midpoint = p/2;
	unsigned bitmask = (unsigned) (p/2);
	int* my_list = NULL;
	int local_size = *local_n;
	my_list = malloc(*local_n*sizeof(int));
	memcpy(my_list, local_lst, *local_n*sizeof(int));

	while (bitmask > 0 && midpoint > 0 && midpoint < p && bitmask < p){
		int copy_iterator = 0, keep_iterator = 0, num_of_receive = 0;
		int* copy_list = NULL;
		int* keep_list = NULL;
		partner = my_rank ^ bitmask;
		if (my_rank < midpoint){
			for (i = 0; i < local_size; i++){
				if (my_list[i] > splits[midpoint]){;
					copy_iterator++;
					copy_list = realloc(copy_list, 
					copy_iterator*sizeof(int));
					copy_list[copy_iterator-1] = my_list[i];
				} else {
					keep_iterator++;
					keep_list = realloc(keep_list, 
					keep_iterator*sizeof(int));
					keep_list[keep_iterator-1] = my_list[i];
				}
			}
			midpoint = midpoint - 1;
		} 
		else if (my_rank >= midpoint){
			for (i = 0; i < local_size; i++){
				if (my_list[i] < splits[midpoint]) {
					copy_iterator++;
					copy_list = realloc(copy_list, 
					copy_iterator*sizeof(int));
					copy_list[copy_iterator-1] = my_list[i];
				} else {
					keep_iterator++;
					keep_list = realloc(keep_list, 
					keep_iterator*sizeof(int));
					keep_list[keep_iterator-1] = my_list[i];
				}
			}
			midpoint = midpoint + 1;
		}
		MPI_Status status;
		MPI_Send(copy_list,copy_iterator,MPI_INT,partner,0,MPI_COMM_WORLD);

		MPI_Probe(partner, 0, MPI_COMM_WORLD, &status);
		MPI_Get_count(&status, MPI_INT, &num_of_receive);
		int* temp_list = NULL;
		temp_list = malloc(num_of_receive*sizeof(int));
		MPI_Recv(temp_list, num_of_receive, MPI_INT, partner, 0, 
			MPI_COMM_WORLD, &status);
		my_list = realloc(my_list, 
			(keep_iterator + num_of_receive)*sizeof(int));
		local_size = (keep_iterator + num_of_receive);
		for (i = 0; i < keep_iterator; i++){
			my_list[i] = keep_list[i];
		}
		for (i = keep_iterator; i < (keep_iterator + num_of_receive); i++){
			my_list[i] = temp_list[i - keep_iterator];
		}
		free(copy_list); free(keep_list); free(temp_list);
		bitmask >>= 1;
	}

	qsort(my_list, local_size, sizeof(int), Compare);
	local_lst = realloc(local_lst, local_size*sizeof(int));
	memcpy(local_lst, my_list, local_size*sizeof(int));
	*local_n = local_size;
} /* Butterfly */

  /*---------------------------------------------------------------------
 * Function:  Print_list
 * Purpose:   Prints each process's corresponding list w/ partitions.
 * In args:   global_lst[], p, partitions[]
 * Output: Prints to stdio
 */
void Print_list(int global_lst[], int p, int partitions[]) {
   int i;
   int j;
   int temp_count = 0;
   int temp_count2 = 0;
   for (i = 0; i < p; i++){
   		temp_count2 += partitions[i];
   		printf("Proc %d > ", i);
   		for (j = temp_count; j < temp_count2; j++){
   			printf(" %d ", global_lst[j]);
   		}
   		temp_count += partitions[i];	
   		printf("\n");
   }
}  /* Print_list */