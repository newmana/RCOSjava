/**
 * For reading in characters and integers.
 **/

int main()
{
  int number;
  int numGuess;
  char ch;
  char chGuess;

  number = 4;
  numGuess = 0;

  ch = 'c';
  chGuess = ' ';

  while ((numGuess != number))
  {
    printf("Whats the answer (4)? ");
    scanf("%i", numGuess);
    printf("\n");
  }

  printf("Correct!\n");

  while ((chGuess != ch))
  {
    printf("Whats the answer (c)? ");
    scanf("%c", chGuess);
    printf("\n");
  }

  printf("Correct!\n");
}


