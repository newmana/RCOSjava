/**
 * First test for shared memory.  Run smem2.c too.
 **/
int main()
{
  int shrId, shrResult, offset, value;

  shrId = shrcreate("myshr", 10);
  printf("Created share memory with id of: ");
  printf("%i", shrId);
  printf("\n");

  for (value = 0; (value < 10); value = (value + 1))
  {
    for (offset = 0; (offset < 10); offset = (offset + 1))
    {
      shrResult = shrwrite(shrId, offset, value);
      printf("Writing: ");
      printf("%i", value);
      printf(", offset: ");
      printf("%i", offset);
      printf("\n");
    }
  }

  shrResult = shrclose(shrId);
}
