int global;

void main()
{
  global = 1;

  printf("Global variable test\n");

  if ((global > 0))
  {
    printf("Global greater than 0\n");
    
  }

  if ((global == 0))
  {
    printf("Global equal to 0\n");
  }

  if ((global == 1))
  {
    printf("Global equal to 1\n");
  }

  if ((global < 1))
  {
    printf("Global less than 1\n");
  }

  if ((global < 2))
  {
    printf("Global less than 2\n");
  }
}

