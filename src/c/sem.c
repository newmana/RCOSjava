/**
 * First test for semaphore.  Run sem2.c next.
 **/
int main()
{
  int semValue;
  int semResult;

  printf("I am sem.c, based on sem.pll\n");
  printf("I will wait for sem2.c to signal me.\n");
  printf("Creating semaphore.\n");

  /** Create a semaphore and get its id. **/
  semValue = semcreate("sem1");

  printf("Created a semaphore with the value: ");
  printf("%i", semValue);
  printf("\n");

  /** Wait for a signal **/
  semResult = semwait(semValue);

  printf("Was woken up!\n");
  printf("Result value was: " );
  printf("%i", semResult);
  printf("\n");

  /** Close semaphore **/
  semResult = semclose(semValue);
}
