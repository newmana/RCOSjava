/**
 * Tests random number generator
 **/

int main()
{
  int counter;
  int result;
  
  for (counter = 0; (counter < 10); counter = (counter + 1))
  {
    result = rand(10);
    printf("Got random number: ");
    printf("%i", result);
    printf("\n");
  }
}