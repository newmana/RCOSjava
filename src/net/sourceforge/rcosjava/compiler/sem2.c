/**
 * First test semaphore.  Run sem.c first.
 **/
int main()
{
  int semValue, semResult;

  printf("I am sem2.c, based on sem2.pll\n");
  printf("I will signal sem.c.\n");
  printf("Opening semaphore.\n");

  semValue = semopen("sem1");

  if ((semValue < 0))
  {
    printf("Semaphore not created. ERROR!");
  }
  else
  {
    semResult = semsignal("sem1");
    printf("Sent signal, sem.c should wake up.");
  }

  printf("Closing semaphore");
  semResult = semclose("sem1");
}
