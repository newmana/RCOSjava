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
    printf("The shared id is ");
    printf("%i", shrId);
    printf("\n");
    offset = 1;

    while ((offset != 0))
    {
      printf("Which value do you want to read 1-9 (0 quits)? ");
      scanf("%i", offset);
      shrValue = shrread(shrId, offset);
      printf("\nThe value is: ");
      printf("%i", shrValue);
      printf("\nThe offset is: ");
      printf("%i", offset);
      printf("\n");
    }

    shrValue = shrclose(shrId);
  }
}
