/**
 * Test for do loop.
 **/

int main()
{
  char a;
  int index;
  char hello[80];

  hello = "hello world";

  printf("Down from 10:");
  index = 10;
  do
  {
    printf("%i", index);
    index = (index - 1);
  }
  while ((index != 0));
}
