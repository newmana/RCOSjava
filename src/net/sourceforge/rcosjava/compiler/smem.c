/**
 * First test for shared memory.  Run smem2.c too.
 **/
int main()
{
  int shrId, shrResult, offset, value;

  shrId = shrcreate("myshr", 10);

  for (value = 0; (value < 10); value = (value + 1))
  {
    for (offset = 0; (offset < 10); offset = (offset + 1))
    {
      shrResult = shrwrite(shrId, offset, value);
    }
  }

  shdResult = shrclose(shrId);
}
