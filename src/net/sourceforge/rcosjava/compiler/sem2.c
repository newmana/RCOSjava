/**
 * First test semaphore.  Run sem.c first.
 **/
int main()
{
  int semValue;
  int semResult;

  printf("I am sem2.c, based on sem2.pll\n");
  printf("I will signal sem.c.\n");
  printf("Opening semaphore.\n");

  semValue = semopen("sem1");

  if ((semValue < 0))
  {
    printf("Semaphore not created. ERROR!\n");
  }
  else
  {
    semResult = semsignal(semValue);
    printf("Sent signal, sem.c should wake up.\n");
  }

  printf("Closing semaphore\n");
  semResult = semclose("sem1");
}
