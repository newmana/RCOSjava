/**
 * First test for shared memory.  Run smem.c too.
 **/
int main()
{
  int shrId;
  int shrSize;
  int shrValue;
  int offset;

  printf("Attempting to read shared memory segment\n");

  shrId = shropen("myshr");

  if ((shrId < 0))
  {
    printf("Error getting shared memory, run smem first.");
  }
  else
  {
    shrSize = shrsize(shrId);
    printf("The size of the memory block is ");
    printf("%i", shrSize);
    printf("\n");

    while ((offset != -1))
    {
      printf("Which value do you want to read (-1 quits)? ");
      scanf("%i", offset);
      shrValue = shrread(shrId, offset);
      printf("\nThe value is: ");
      printf("%i", shrValue);
      printf("\n");
    }

    shrValue = shrclose(shrId);
  }
}
