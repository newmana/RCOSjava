int global;
int global2;

/**
 * Tests if/else/elseif for >, ==, !=, < and tunary ? operator.
 **/
int main()
{
  int local;
  int local2;

  global = 1;

  printf("Global variable test\n");

  if ((global > 0))
  {
    printf("Global greater than 0\n");

  }
  else
  {
    printf("Global less than or equal to 0\n");
  }

  if ((global == 1))
  {
    printf("Global equal to 1\n");
  }
  else
  {
    printf("Not equal to 1\n");
  }

  if ((global < 2))
  {
    printf("Global less than 2\n");
  }
  else
  {
    printf("Global greater than or equal to 2\n");
  }

  local = 1;
  global = 2;
  local2 = 123;

  if ((global == 1))
  {
    if ((local != 2))
    {
      printf("Something went wrong\n");
    }
  }
  if ((global == 2))
  {
    if ((local == 1))
    {
      printf("Global is 2 and local is 1\n");
    }
  }
}