/**
 * Tests arrays of characters and ints with while and for loops
 * as well as printing them out.
 **/
int main()
{
  char a;
  int ten[10];
  int one;
  int counter;
  int index;
  char *hello;

  hello = "hello world";
  a = 'a';
  ten[0] = 1;
  ten[1] = 10;
  ten[2] = 100;
  one = 1;

  for (counter = 0; (counter <= 2); counter = (counter + 1))
  {
    printf("Element ");
    printf("%i", counter);
    printf(" in array ten: ");
    printf("%i", ten[counter]);
    printf("\n");
    ten[counter] = (ten[0]) + 1;
  }

  counter = 0;
  while ((counter < 3))
  {
    printf("After addition element ");
    printf("%i", counter);
    printf(" in array ten: ");
    printf("%i", ten[counter]);
    printf("\n");
    counter = (counter + 1);
  }

  printf("Print out in reverse character by character\n");
  index = 11;
  do
  {
    printf("%c", hello[index]);
    a = hello[index];
    index = (index - 1);
  }
  while ((a != 'h'));

  printf("\nFirst Char:");
  printf("%c", hello[0]);
  printf("\n");
}
