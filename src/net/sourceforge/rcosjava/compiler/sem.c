/**
 * First test for semaphore.  Run sem2.c next.
 **/
int main()
{
  int semValue, semResult;

  printf("I am sem.c, based on sem.pll\n");
  printf("I will wait for sem2.c to signal me.\n");
  printf("Creating semaphore.\n");

  semValue = semcreate("sem1");

  printf("Created a semaphore with the value: ");
  printf("%i", semValue);
  printf("\n");

  semResult = semwait("sem1");

  printf("Was woken up!");
  printf("Result value was: " );
  printf("%i", semResult);
  printf("\n");
}
