/**
 * Test local and global variables
 **/
int global;

void printOut()
{
  printf("%i", global);
}

int main()
{
  global = 1;

  printOut();
}